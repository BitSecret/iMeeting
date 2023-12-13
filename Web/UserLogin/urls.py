from django.urls import path
from . import views
from .api import logout, all_booking, update, userinfo, get_orders_by, register, login, booking, delete_order

urlpatterns = [
	path('Login', views.Login, name='Login'),
	path('Logout', views.Logout, name='Logout'),
	path('Register', views.Register, name='Register'),
	path('api/userinfo/<username>', userinfo),
	path('api/orders/<username>', get_orders_by),
	path('api/register', register),
	path('api/login', login),
	path('api/booking', booking),
	path('api/delete', delete_order),
	path('api/update', update),
	path('api/logout', logout),
	path('api/allbooking', all_booking),
]