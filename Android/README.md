搭建指南：
1. 安装相关工具及下载文件
Android studio

 https://developer.android.google.cn/studio/



2. 启动Android studio，本文使用的是3.2.0版本

3. 准备需要在Android Studio中打开的项目。请将zip文件下载下来并解压缩

4. 修改项目中所需的Gradle软件包需求。 
 找到 EdgeEffectOverride-master\gradle\wrapper下面的gradle-wrapper.properties文件，使用Notepad++之类的文本文件工具打开它,请将红色部分改为你的机器上已安装的Gradle版本，比如我这里是gradle-1.12-all.zip。

5. 点击Import Project，定位到项目所在的目录，找到其中的build.gradle文件，然后点击OK按钮。

6. 导入成功之后即可编译运行。

7. 将编译好的android studio项目打包成apk
（1）首先在Android studio上方的工具栏找到build，点击查看bulid工具；
（2）在bulid工具栏的下拉栏，找到倒数第四行的Build Bundle(s)/APK(s)，点击进入该设置-Build APK(s)即可导出成功；
（3）这样，在刚才设置的路径，我们就可以看到已打包成功的apk了，可以在手机上直接安装。