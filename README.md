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
使用Vagrant搭建4个CentOS7的虚拟机模拟集群主机，4台主机名称分别为:gl1,node1,node2,node3

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
$ vim ~/works/vagrant/Vagrantfile
```shell
Vagrant.configure("2") do |config|
	(1..3).each do |i|
		config.vm.define "node#{i}" do |node|
		# 设置虚拟机的Box
		node.vm.box = "./virtualbox.box"
		# 设置虚拟机的主机名
		node.vm.hostname="node#{i}"
		# 设置虚拟机的IP
		node.vm.network "private_network", ip: "192.168.59.#{i}"
		# 设置主机与虚拟机的共享目录
		node.vm.synced_folder "~/works/vagrant/share", "/home/vagrant/share"
		# VirtaulBox相关配置
		node.vm.provider "virtualbox" do |v|
			# 设置虚拟机的名称
			v.name = "node#{i}"
			# 设置虚拟机的内存大小  
			v.memory = 2048
			# 设置虚拟机的CPU个数
			v.cpus = 1
		end
		# 使用shell脚本进行软件安装和配置
		node.vm.provision "shell", inline: <<-SHELL
			# 安装docker 1.11.0
			wget -qO- https://get.docker.com/ | sed 's/docker-engine/docker-engine=1.11.0-0~trusty/' | sh
			usermod -aG docker vagrant
		SHELL
		end
	end
end
```

6.创建虚拟机:
>$ vagrant up

7.虚拟机常用的一些命令:
>vagrant up:			启动所有虚拟机  
>vagrant up node1 node3:	启动名称为node1、node3多个虚拟机  
>vagrant halt:			关闭虚拟机
>vagrant destroy:		删除虚拟机  
>vagrant ssh-config:		查看虚拟机的ssh配置
