运行指南：
1.下载并安装Android Studio

2.新建项目
①Company domain 填写 example.com
②API水平选择 API21
③样式选择 Empty Activity
④Activity name 填写 MainActivity

3.打开项目地址，替换文件
用本文件夹libs文件夹     替换     项目目录->app->libs文件夹
用本文件夹src->main文件夹    替换    项目目录->app->src->main文件夹
下载虹软Android Arc_Face SDK 2.0，将下载文件夹中的   libarcsoft_face.so和libarcsoft_face_engine.so两个文件  复制到   项目目录->app->src->main->jniLibs->armeabi-v7a文件夹内

4.打开项目，重新 Sync Project

5.填写启动信息
①打开项目java->common-Constants
②填写申请到的虹软SDK激活码
③填写web端已搭载的服务器信息

6.连接终端设备或开启虚拟机

7.编译程序并执行

8.若提示缺少程序包，请百度相应的包，并下载到本地，添加入本地环境或项目中