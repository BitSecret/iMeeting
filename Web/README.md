运行指南：
方法一：浏览器输入 47.106.81.165:8000 直接登录已经部署的站点

方法二：自行搭建环境：
# MeetingRoom
A meetingRoom reservation system

# 创建项目目录
mkdir Meeting
cd Meeting

# 创建一个virtualenv来隔离我们本地的包依赖关系
virtualenv env
source env/bin/activate  # 在Windows下使用 `env\Scripts\activate`

# 在创建的虚拟环境中安装 Django , Django REST framework与Mysql ORM

# 安装django
	$python3 -m pip install django
# 安装rest framework
	$python3 -m pip install djangorestframework
# 安装pymysql
	$python3 -m pip install pymysql
# 自动初始化会议室信息存入数据库
	$python3 auto_create_rooms
# 运行
	$python3 manage.py runserver
# 管理数据库
	$python3 manage.py dbshell
# 直接登录已经部署的站点
	浏览器输入 47.106.81.165:8000