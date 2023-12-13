from django.contrib import admin

# Register your models here.
from django.contrib import admin
from . import models

# Register your models here.

class UserAdmin(admin.ModelAdmin):
	list_display = ('name', 'password', 'nick_name', 'face_data_file', 'picture', 'email', 'phone_number')

class UserOrderAdmin(admin.ModelAdmin):
	list_display = ('user', 'order')

class MeetingAdmin(admin.ModelAdmin):
	list_display = ('host', 'guest', 'order', 'auth', 'join_remark')


admin.site.register(models.User, UserAdmin)
admin.site.register(models.User_order, UserOrderAdmin)
admin.site.register(models.Meeting, MeetingAdmin)