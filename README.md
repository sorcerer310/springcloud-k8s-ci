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
四. 在另外三台主机上配置docker、kubernetes环境及安装其他一些必要软件，模拟集群3台主机  
五. 编写项目springboot项目

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

4.安装GitLab-Runner。GitLab-Runner是配合Git-Lab-CI一起使用，用来执行CI的自动化脚本。

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
