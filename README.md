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
二. 在node4上安装本地Docker镜像库和GitLab
三. 在另外三台主机上配置docker、kubernetes环境及安装其他一些必要软件，模拟集群3台主机  
四. 编写项目springboot项目

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

另外Registry要求使用https通信，我们在内部使用可以配置为使用http即可。

>$ vim /etc/docker/daemon.json
>{
>   "insecure-registries" : ["192.168.56.104:5000"]
>}
