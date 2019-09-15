springcloud-k8s-ci
========
springcloud、k8s单机微服务集群持续集成开发模板


## 环境
MacOS           本机系统
vmware+vagrant  用vmware+vagrant模拟集群环境

## 目的
为想在单机上构建微服务集群系统的开发者提供一个可用环境

## 步骤
一. vagrant搭建集群主机  
二. 在node4上安装本地Docker Registry  
三. 在node4上安装GitLab  

~~四. 在另外三台主机上配置docker、kubernetes环境及安装其他一些必要软件，模拟集群3台主机~~

五. 编写项目springboot项目  
六. 编写GitLab-ci脚本  

## 详细内容
### Vagrant搭建集群主机
使用Vagrant搭建4个CentOS7的虚拟机模拟集群主机，4台主机名称分别为:node1、node2、node3、node4，其中node4用来安装gitlab实现代码托管、CI/CD

1.创建本机工作目录:  
>$ mkdir ~/works/vagrant/

2.安装VirtualBox:  
>到http://www.virtualbox.org/wiki/Downloads 下载虚拟机程序并安装。安装过程这里不做详细说明

3.安装vagrant:  
>到https://www.vagrantup.com/downloads.html 下载vagrant。vagrant可在本地利用VirtualBox快速创建多个虚拟机。安装过程不做详细说明

4.下载Vagrant Boxes:  
>Vagrant Boxes是Vagrant的基础镜像。$ vagrant box add centos/7 可下载centos7的镜像。如果下载速度过慢，可从命令行中把下载地址copy到浏览器中,下载的镜像文件放到~/works/vagrant/路径。  

5.编辑Vagrantfile集群配置文件,并创建虚拟机使用的共享目录:  
>$ vim mkdir ~/works/vagrant/share  
将项目中的vagrant/Vagrantfile文件copy到 ~/works/vagrant/目录下。Vagrantfile是ruby编写的配置文件，其中包括了创建4个CentOS7的虚拟机、关闭每个虚拟机的防火墙、为虚拟机安装docker的一些操作。

6.创建虚拟机:
>$ vagrant up

7.虚拟机常用的一些命令:
>vagrant up:			启动所有虚拟机  
>vagrant up node1 node3:	启动名称为node1、node3多个虚拟机  
>vagrant halt:			关闭虚拟机
>vagrant destroy:		删除虚拟机  
>vagrant ssh-config:		查看虚拟机的ssh配置

### 在node4上安装本地Docker镜像库 Docker Registry
本地的Docker镜像库用来保存项目打包出来的镜像文件。具体详情可参考Docker Registry的官方文档:：https://docs.docker.com/registry/
我们执行ip addr命令查询到node4的ip地址为192.168.56.104,可执行以下命令来安装并运行Docker Registry
>$ docker run -d -p 192.168.56.104:5000:5000 --restart=always --name registry -v /mnt/registry:/var/lib/registry registry:latest

另外Registry要求使用https通信，我们在内部使用可以配置为使用http即可。编辑以下文件将我们的docker仓库认证为信任仓库。  
如果是MacOs系统可以在Docker Desktop中的Preferences中选择Daemon->Basic->Insecure registries中增加一条数据:http://192.168.56.104:5000

>$ vim /etc/docker/daemon.json  
>{  
>   "insecure-registries" : ["192.168.56.104:5000"]  
>}  

接下来我们可以用busybox来实验一下  
>$ docker pull busybox                                              #拉一个busybox做实验  
>$ docker tag busybox:latest 192.168.56.104:5000/busybox:v1.0       #本地打标签一个自己的busybox镜像  
>$ docker push 192.168.56.104:5000/busybox:v1.0                     #将镜像push到本地私有仓库中  
>$ curl -XGET http://192.168.56.104:5000/v2/_catalog                #查看私有仓库中已push的镜像  

### 在node4上安装gitlab 用来托管代码与CI/CD  
1.首先拉取gitlab ce镜像到本地  
>$ docker pull gitlab/gitlab-ce  

2.到home目录下执行以下命令，创建共享本地的卷  
>$ mkdir -vp gitlab/{data,logs,config}  

3.执行以下命令启动gitlab,其中192.168.56.104是当前node4的ip，--volume参数后的路径要对应刚刚创建的gitlab目录  
>$ docker run --detach --hostname 192.168.56.104 --publish 192.168.56.104:443:443 --publish 192.168.56.104:80:80 --publish 192.168.56.104:1022:22 --name gitlab --restart always --volume /home/gitlab/config:/etc/gitlab --volume /home/gitlab/logs:/var/log/gitlab --volume /home/gitlab/data:/var/opt/gitlab gitlab/gitlab-ce:latest  

4.安装GitLab-Runner。GitLab-Runner是配合GitLab-CI一起使用，用来执行CI的自动化脚本。

(1)我们参考GitLab官方文档，先添加GitLab官方库，然后使用yum安装  
>$ curl -L https://packages.gitlab.com/install/repositories/runner/gitlab-runner/script.rpm.sh | sudo bash  
>$ yum -y install gitlab-runner  

(2)向GitLab注册GitLab-Runner,执行命令后会要求输入一系列参数，我们根据当前环境可做如下填写,其中gitlab-ci token是我们访问http://192.168.56.104 页面->管理中心->概览->Runner中获得的。
>$ gitlab-runner register  
Please enter the gitlab-ci coordinator URL (e.g. https://gitlab.com/):  
http://192.168.56.104  
Please enter the gitlab-ci token for this runner:  
xxx-xxxxxxxxxxxxxxx  
Please enter the gitlab-ci description for this runner:  
(此处填入你对该runner的描述)  
Please enter the gitlab-ci tags for this runner (comma separated):  
shared-runner  
Please enter the executor: docker-ssh, parallels, ssh, docker+machine, kubernetes, docker, shell, virtualbox, docker-ssh+machine, custom:  
docker  
Please enter the default Docker image (e.g. ruby:2.6):  
192.168.56.104:5000/ali-maven-docker:3.5.4-jdk-8-alpine  

(3)再次访问GitLab的管理界面Runner模块，我们就能看到注册成功的runner了。  

(4)非常重要的一步来了。由于我们在编写GitLab-ci脚本[.gitlab-ci.yml](https://github.com/sorcerer310/springcloud-k8s-ci/blob/master/springcloud-k8s-ci-demo/.gitlab-ci.yml)时，会在stage中执行docker命令，这条命令需要在gitlab的docker容器中执行，但容器中并没有安装docker。虽然我们可以通过在容器中再继续安装docker的方式(docker-in-docker)来执行，但这会引起其他的一些问题，所以我们要在这里采用socket binding方式来执行，详细见[浅析 docker-in-docker 和 socket-binding](https://qqzeng.top/2019/07/07/%E6%B5%85%E6%9E%90-docker-in-docker-%E5%92%8C-socket-binding/)这篇文章，这篇文章详细的讲述了如何在容器中执行命令，以及分析了几种方式的优缺点。  
要启用socket binding方式我们需要修改宿主机gitlab-runner的配置文件/etc/gitlab-runner/config.toml，需要为容器绑定宿主机的docker.sock，并绑定宿主机的maven缓存目录:  
~~~toml
concurrent = 1
check_interval = 0

[session_server]
  session_timeout = 1800

[[runners]]
  name = "springboot-k8s-gitlab-ci"
  url = "http://192.168.56.104"
  token = "bE76Z58Zc4As5M1cyXHz"
  executor = "docker"
  [runners.custom_build_dir]
  [runners.docker]
    tls_verify = false
    image = "192.168.56.104:5000/ali-maven-docker:3.5.4-jdk-8-alpine"
    privileged = false
    disable_entrypoint_overwrite = false
    oom_kill_disable = false
    disable_cache = false
    volumes = ["/var/run/docker.sock:/var/run/docker.sock","/cache","/root/.m2:/root/.m2"]
    shm_size = 0
    pull_policy="if-not-present"
  [runners.cache]
    [runners.cache.s3]
    [runners.cache.gcs]
~~~  
要修改有volumes字段和pull_policy字段

### 编写项目springboot项目  
1. 创建一个maven项目，父项目pom文件见[springcloud-k8s-ci-demo/pom.xml](https://github.com/sorcerer310/springcloud-k8s-ci/blob/master/springcloud-k8s-ci-demo/pom.xml)  
2. 在父项目下创建若干maven子项目eureka、zuul、oauth、order、storage等子项目，每个子项目的pom文件，[子项目目录](https://github.com/sorcerer310/springcloud-k8s-ci/tree/master/springcloud-k8s-ci-demo)  
3. 我们以storage项目为例，先配置好一个可以运行的springboot程序  
在pom.xml中引(1)入必要的库。(2)启用maven插件。(3)增加maven打包docker的插件。(4)配置插件的相关信息。
```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <parent>
        <artifactId>demo</artifactId>
        <groupId>com.bsu.skc</groupId>
        <version>1.0</version>
    </parent>
    <modelVersion>4.0.0</modelVersion>

    <artifactId>storage</artifactId>
    <dependencies>
        <!--(1)eureka客户端，用于与eurekaka服务端通信-->
        <dependency>
            <groupId>org.springframework.cloud</groupId>
            <artifactId>spring-cloud-starter-netflix-eureka-client</artifactId>
        </dependency>
    </dependencies>

    <!--(1)自动管理cloud包对springboot依赖版本号-->
    <dependencyManagement>
        <dependencies>
            <dependency>
                <groupId>org.springframework.cloud</groupId>
                <artifactId>spring-cloud-dependencies</artifactId>
                <version>Greenwich.RELEASE</version>
                <type>pom</type>
                <scope>import</scope>
            </dependency>
        </dependencies>
    </dependencyManagement>

    <!--docker相关-->
    <properties>
        <docker.image.prefix>springboot</docker.image.prefix>
    </properties>
    <build>
        <plugins>
            <!--(2)启用maven插件-->
            <plugin>
                <groupId>org.springframework.boot</groupId>
                <artifactId>spring-boot-maven-plugin</artifactId>
            </plugin>
            <!--(3)maven打包docker的插件-->
            <plugin>
                <groupId>com.spotify</groupId>
                <artifactId>docker-maven-plugin</artifactId>
                <version>1.0.0</version>
                <configuration>
                    <!--(4)打包好镜像的名称-->
                    <imageName>${docker.image.prefix}/${project.artifactId}</imageName>
                    <!--(4)docker使用的目录-->
                    <dockerDirectory>src/main/docker</dockerDirectory>
                    <resources>
                        <!--(4)要打包进镜像的资源-->
                        <resource>
                            <targetPath>/</targetPath>
                            <directory>${project.build.directory}</directory>
                            <include>${project.build.finalName}.jar</include>
                        </resource>
                    </resources>
                    <!--(4)registry私有库的地址及端口-->
                    <registryUrl>http://192.168.56.104:5000</registryUrl>
                    <pushImage>true</pushImage>
                    <!--(4)push到私有库镜像的名称-->
                    <imageName>${project.artifactId}:${project.version}</imageName>
                </configuration>
            </plugin>
        </plugins>
    </build>
</project>
```  
4. 为storage项目增加入口类StorageApplication，写好入口函数。再增加一个RestController类，定义好用来在浏览器中测试数据接口。[Storage.java](https://github.com/sorcerer310/springcloud-k8s-ci/blob/master/springcloud-k8s-ci-demo/storage/src/main/java/com/bsu/skc/api/Storage.java)
5. 接下来我们在IDEA中Maven Project窗口中依次执行install、docker:build、docker:push命令测试程序是否能被正常打包、生成镜像、推送到registry。测试正常后，以后我们在本地开发只需直接运行程序测试程序是否能正常运行，不需要执行docker:build、docker:push两条命令了，这两条命令会在GitLab-ci中执行对程序进行生成镜像、推送至registry。  
### 编写GitLab脚本

