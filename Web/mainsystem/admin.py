from django.contrib import admin

# Register your models here.
from django.contrib import admin
from . import models

# Register your models here.

class MeetingRoomAdmin(admin.ModelAdmin):
	list_display = ('room_number', 'room_type', 'content', 'img')

class OrderAdmin(admin.ModelAdmin):
	list_display = ('room', 'found_time', 'booking_date', 'remark', 'start_time', 'end_time')

admin.site.register(models.MeetingRoom, MeetingRoomAdmin)
admin.site.register(models.Order, OrderAdmin)