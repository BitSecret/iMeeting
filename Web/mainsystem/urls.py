from django.urls import path
from . import views

urlpatterns = [
	path('', views.mainpage, name='mainpage'),
	path('mainsystem/', views.mainsystem, name='mainsystem'),
	path('profile/', views.profile, name='profile' ),
	path('uploadimg/', views.deal_upload_img, name='deal_upload_img'),
	path('booking/', views.booking, name='booking'),
	path('disorder/', views.disorder, name='disorder'),
	path('my_orders/', views.my_orders, name='my_orders'),
	path('my_orders/<date>', views.my_orders, name='my_orders'),
	path('meeting/', views.meeting, name='meeting'),
	path('meeting/<date>', views.meeting, name='meeting'),
	path('room_status_by_date/', views.room_status_by_date, name='room_status_by_date'),
	path('edit_info/', views.edit_info, name='edit_info'),
	path('join_meeting/', views.join_meeting, name='join_meeting'),
	path('manage_meeting/', views.manage_meeting, name='manage_meeting'),
	path('change_auth/', views.change_auth, name='change_auth'),
]