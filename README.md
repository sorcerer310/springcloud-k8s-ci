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
二. 在gl1上安装GitLab,用于代码托管和CI/CD  
三. 在另外三台主机上配置docker、kubernetes环境及安装其他一些必要软件，模拟集群3台主机  
四. 编写项目springboot项目

## 详细内容
### Vagrant搭建集群主机
使用Vagrant搭建4个CentOS7的虚拟机模拟集群主机，4台主机名称分别为:node1,node2,node3,gl

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
将项目中的Vagrantfile文件copy到~/works/vagrant/目录下

6.创建虚拟机:
>$ vagrant up

7.虚拟机常用的一些命令:
>vagrant up:			启动所有虚拟机  
>vagrant up node1 node3:	启动名称为node1、node3多个虚拟机  
>vagrant halt:			关闭虚拟机
>vagrant destroy:		删除虚拟机  
>vagrant ssh-config:		查看虚拟机的ssh配置

### 在gl1上安装GitLab
