from .models import User #需要建立api的文章模型数据
from rest_framework import serializers #系列化器
from rest_framework.response import Response #构建视图，返回JSON
from rest_framework.decorators import api_view
from mainsystem.models import Order
from .models import User_order
from mainsystem.models import MeetingRoom
from django.core.exceptions import ObjectDoesNotExist
import datetime
import os
import hashlib
from django.core.files import File

# 错误码:
# 成功
SUCCESS = 0
# 请求格式错误
FORMAT_ERROR = 99999 # 数据格式有误
# 登录注册方面错误码
PASSWORD_ERROR = 10000    # 用户名或密码错误
USER_EXIST_ERROR = 10001 # 用户已存在错误
USER_OTHER_ERROR = 10002 # 其他错误
# 用户预定方面错误码
ORDER_DATA_ERROR = 20000 # 数据不合法
ORDER_TIME_CONFLICT = 20001 # 时间冲突
ORDER_OTHER_ERROR = 20002 # 其他错误
ORDER_NOT_EXIST = 20003 # 订单不存在


class UserSerializer(serializers.ModelSerializer):
	class Meta:
		model = User
		fields = '__all__'


class OrderSerializer(serializers.ModelSerializer):
	class Meta:
		model = Order
		fields = '__all__'


class RoomSerializer(serializers.ModelSerializer):
	class Meta:
		model = MeetingRoom
		fields = '__all__'


class TableSerializer(serializers.ModelSerializer):
	class Meta:
		model = User_order
		fields = '__all__'


# 用md5加密用户密码
def sha256hash(psw):
	h = hashlib.sha256()
	h.update(psw.encode('utf-8'))
	password = h.hexdigest()

	return password


# 判断用户登陆状态
def auth(request):
	user = request.session.get('user_info', None)
	if user:
		return user
	else:
		return False


@api_view(['GET'])  #装饰器
def userinfo(request, username):
	if auth(request):
		user = User.objects.get(name=username)
		serializer = UserSerializer(user, many=False)
		return Response(serializer.data)


@api_view(['GET'])  #装饰器
def get_orders_by(request, username):
	if auth(request):
		user_id = User.objects.get(name=username).id
		orders_table = User_order.objects.filter(user_id=user_id)
		orders_len = len(orders_table)
		if  orders_len == 1:
			order_id = orders_table[0].order_id
			order = Order.objects.get(id=order_id)
			order.room_id = MeetingRoom.objects.get(id=order.room_id).room_number
			serializer = OrderSerializer(order, many=False)

			return Response(serializer.data)
		elif orders_len > 1:
			orders = [Order.objects.get(id=o.order_id) for o in orders_table]
			for order in orders:
				order.room_id = MeetingRoom.objects.get(id=order.room_id).room_number
			serializer = OrderSerializer(orders, many=True)
			print(serializer.data)
			return Response(serializer.data)
		else:
			return Response(ORDER_NOT_EXIST)


@api_view(['POST'])  #装饰器
def register(request):
	if request.method == 'POST':
		# pdb.set_trace()
		name = request.POST.get("name", None)
		password = request.POST.get("password", None)
		if name and password:
			# 用md5 hash生成密码摘要并储存
			password = sha256hash(password)
			user = User.objects.filter(name=name)
			# 无重复用户, 成功注册 check用来弹出js
			if not user:
				user = User(name=name, password=password, nick_name=name)
				user.save()
				return Response(SUCCESS)
			else:
				return Response(USER_EXIST_ERROR)
		else:
			return Response(FORMAT_ERROR)


@api_view(['POST'])  #装饰器
def booking(request):
	# 检验订单时间冲突
	def check_time_conflict(room_id, date, start_time, end_time):
		orders_by_the_date = Order.objects.filter(booking_date=str(date), room_id=room_id)

		# 当前日期无订单
		if len(orders_by_the_date) == 0:
			return 0

		# 当日有多个订单
		elif len(orders_by_the_date) > 1:
			for order in orders_by_the_date:
				# 交叉算法检查时间冲突
				if start_time <= order.end_time and end_time >= order.start_time:
					return 1
		# 一个订单
		elif len(orders_by_the_date) == 1:
			if start_time <= orders_by_the_date[0].end_time and end_time >= orders_by_the_date[0].start_time:
				return 1

		return 0

	def return_json(status):
		# json_dict = {
		# 	"booking_status": status
		# }
		return Response(status)

	if request.method == "POST" and auth(request):
		remark = request.POST.get("remark", None)
		start_time = request.POST.get("startTime", None)
		end_time = request.POST.get("endTime", None)
		date = request.POST.get("date", None)
		room_number = request.POST.get("roomNumber", None)
		username = request.POST.get("username", None)

		# 检验数据存在
		if start_time and end_time and date and room_number and username:
			# 清理数据
			try:
				# 转换开始-结束时间
				start_hour = int(start_time.split(":")[0])
				datetime_start_time = datetime.time(start_hour, 0, 0)

				end_hour = int(end_time.split(":")[0])
				end_minute = int(end_time.split(":")[1])
				datetime_end_time = datetime.time(end_hour, end_minute, 0)

				# 转换日期
				year = int(date.split("-")[0])
				month = int(date.split("-")[1])
				day = int(date.split("-")[2])
				datetime_date = datetime.date(year, month, day)

				# 获取房间room_id
				room = MeetingRoom.objects.get(room_number=room_number)
				room_id = room.id

			# 数据处理出现不合法
			except ObjectDoesNotExist:
				return return_json(ORDER_DATA_ERROR)

			else:
				if check_time_conflict(room_id, datetime_date, datetime_start_time, datetime_end_time) == 0:
					# 存入数据库
					user = User.objects.get(name=username)
					order = Order(room=room, booking_date=datetime_date, remark=remark,
								  start_time=datetime_start_time, end_time=datetime_end_time)
					order.save()
					orders = User_order(user=user, order=order)
					orders.save()

					json_dict = {
						"status": "success",
						"results": [
							{
								"id": order.id,
								"found_time": str(order.found_time),
								"booking_date": str(order.booking_date),
								"remark": str(order.remark),
								"start_time": str(order.start_time),
								"end_time": str(order.end_time),
								"room": room_number,
							}
						]
					}

					return return_json(str(json_dict))

				else:
					return return_json(ORDER_TIME_CONFLICT)

		else:
			return return_json(ORDER_NOT_EXIST)


@api_view(['POST'])
def login(request):
	if request.method == 'POST':
		name = request.POST.get("name", None)
		password = request.POST.get("password", None)
		password = sha256hash(password)
		user = User.objects.filter(name=name, password=password)
		# 成功登陆:
		if len(user):
			request.session['user_info'] = {'name': name}
			return Response(SUCCESS)
		# 登陆失败:
		else:
			return Response(PASSWORD_ERROR)


@api_view(['POST'])
def delete_order(request):
	if auth(request):
		print("cool:" + request.POST.get("username", None))
		id = request.POST.get("id", None)
		print("id" + id)
		name = request.POST.get("username", None)
		if id and name == request.session.get('user_info', None)["name"]:
			try:
				print("fuck")
				order = Order.objects.get(id=id)
				order.delete()
				return Response("删除成功")
			except Exception as e:
				print(e)
				return Response("删除失败")


@api_view(['POST'])
def update(request):


	if auth(request):
		choice = request.POST.get('type', None)
		if choice == "3":
			value = request.FILES.get('value', None)
		else:
			value = request.POST.get('value', None)
		name = request.session.get('user_info', None)["name"]
		user = User.objects.get(name=name)

		def update_name(name):
			user.name = name
			user.save()


		def update_password(password):
			password = sha256hash(password)
			user.password = password
			user.save()


		def update_picture(picture):
			# 删除旧图片文件
			if user.picture != 'None':
				try:
					os.remove(user.picture.path)
				except:
					pass
				finally:
					user.picture = 'None'
			user.picture = picture
			user.save()


		def update_email(email):
			user.email = email
			user.save()


		def update_phone_number(phone_number):
			user.phone_number = phone_number
			user.save()


		def update_feature(feature):
			if user.face_data_file:
				os.remove(user.picture.path)
				f = open("temp_feature.txt", "w+")
				f.write(feature)
				# 把特征值存入文件
				feature_path = name + "_feature_file.txt"
				user.face_data_file.save(feature_path, File(f))


		switch = {
			"1": update_name, # 修改名字
			"2": update_password, # 修改密码
			"3": update_picture, # 修改头像
			"4": update_email, # 修改Email
			"5": update_phone_number, # 修改电话号码
			"6": update_feature,
		}

		try:
			switch[choice](value)
			return Response("修改成功")
		except Exception as e:
			print(e)
			return Response("修改失败")

@api_view(['POST'])
def logout(request):
	del request.session['user_info']
	return Response("成功注销")


@api_view(['GET'])
def all_booking(request):
	orders_table = User_order.objects.all()
	orders_len = len(orders_table)
	if  orders_len == 1:
		order_id = orders_table[0].order_id
		order = Order.objects.get(id=order_id)
		order.room_id = MeetingRoom.objects.get(id=order.room_id).room_number
		serializer = OrderSerializer(order, many=False)

		return Response(serializer.data)
	elif orders_len > 1:
		orders = [Order.objects.get(id=o.order_id) for o in orders_table]
		for order in orders:
			order.room_id = MeetingRoom.objects.get(id=order.room_id).room_number
		serializer = OrderSerializer(orders, many=True)

		print(serializer.data)
		return Response(serializer.data)
	else:
		return Response(ORDER_NOT_EXIST)




