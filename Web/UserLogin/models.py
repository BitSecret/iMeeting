from django.db import models
from mainsystem.models import Order

# 用户表
class User(models.Model):
    name = models.CharField(max_length=12, default='None')
    password = models.CharField(max_length=100, default='None')
    nick_name = models.CharField(max_length=100, default='None')
    face_data_file = models.FileField(upload_to='userfeature', default='None')
    picture = models.ImageField(upload_to='userimg', default='None')
    email = models.EmailField(default='None')
    phone_number = models.CharField(max_length=11, default='None')

    def __str__(self):
        return str(self.name)


# 用户订单表
class User_order(models.Model):
    user = models.ForeignKey(verbose_name='订单用户', to=User, on_delete=models.CASCADE)
    order = models.ForeignKey(verbose_name='订单', to=Order, on_delete=models.CASCADE)


# 会议
class Meeting(models.Model):
    host = models.ForeignKey(verbose_name='预定人', to=User, on_delete=models.CASCADE) # 预定人
    guest = models.ForeignKey(verbose_name='其他与会人员', to=User, related_name="fuck", on_delete=models.CASCADE) # 与会人员
    order = models.ForeignKey(verbose_name='订单', to=Order, on_delete=models.CASCADE) # 会议订单
    auth = models.BooleanField(verbose_name='授权状态', default=0) # 授权状态 1 为host授权guest开启 0 为待认证
    join_remark = models.CharField(verbose_name='加入备注', max_length=32, default='None')

