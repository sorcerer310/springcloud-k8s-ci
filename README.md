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
### vagrant搭建集群主机
使用vagrant搭建4个CentOS7的虚拟机模拟集群主机，4台主机名称分别为:gl1,node1,node2,node3

1.创建本机工作目录:  
>$ mkdir ~/works/vagrant
  
2.安装VirtualBox:  
>到http://www.virtualbox.org/wiki/Downloads 下载虚拟机程序并安装
