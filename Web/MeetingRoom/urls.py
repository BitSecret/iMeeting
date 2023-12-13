"""MeetingRoom URL Configuration

The `urlpatterns` list routes URLs to views. For more information please see:
    https://docs.djangoproject.com/en/2.1/topics/http/urls/
Examples:
Function views
    1. Add an import:  from my_app import views
    2. Add a URL to urlpatterns:  path('', views.home, name='home')
Class-based views
    1. Add an import:  from other_app.views import Home
    2. Add a URL to urlpatterns:  path('', Home.as_view(), name='home')
Including another URLconf
    1. Import the include() function: from django.urls import include, path
    2. Add a URL to urlpatterns:  path('blog/', include('blog.urls'))
"""
from django.contrib import admin
from django.urls import path, include

urlpatterns = [
    path('admin/', admin.site.urls),
    path('', include(('UserLogin.urls', 'Login'), namespace='UserLogin')),
    path('', include(('mainsystem.urls', 'MTRsystem'), namespace='MTRsystem')),
]


# Debug 模式用的处理media视图函数, 生产模式要自己配置
# 配置处理media里的文件的路由
# serve视图函数:https://docs.djangoproject.com/en/2.1/ref/views/
from django.conf import settings
from django.urls import re_path
from django.views.static import serve


if settings.DEBUG:
    urlpatterns += [
        re_path(r'^usermedia/(?P<path>.*)$', serve, {
            'document_root': settings.MEDIA_ROOT,
        }),
    ]