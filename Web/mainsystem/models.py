from django.db import models

# Create your models here.

class MeetingRoom(models.Model):
    # 会议室表
    room_number = models.CharField(verbose_name='会议室房间号', max_length=32, default='None')
    room_type = models.CharField(verbose_name='会议室类型', max_length=32, default='None')
    content = models.IntegerField(verbose_name='会议室容量', default=40)
    img = models.FileField(upload_to="room_img", default="None")

    def __str__(self):
        return str(self.room_number)


class Order(models.Model):
    # 订单表
    room = models.ForeignKey(verbose_name='会议室', to=MeetingRoom, on_delete=models.CASCADE)
    found_time = models.DateTimeField(verbose_name='订单创建时间', auto_now=True)
    booking_date = models.DateField(verbose_name='订单日期')
    remark = models.CharField(verbose_name='备注', max_length=32, default='None')
    start_time = models.TimeField(verbose_name='开始时间')
    end_time = models.TimeField(verbose_name='结束时间')

    def __str__(self):
        return str(self.id)