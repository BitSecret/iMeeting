from django.shortcuts import render, redirect
from django.http import JsonResponse
from UserLogin.models import User
from UserLogin.models import User_order
from UserLogin.models import Meeting
from .models import MeetingRoom, Order
from django.views.decorators.csrf import csrf_exempt
from django.core.exceptions import ObjectDoesNotExist
from django.core.files import File
import os, datetime
import subprocess
import re

time_choices = (
        (1, '8:00'),
        (2, '9:00'),
        (3, '10:00'),
        (4, '11:00'),
        (5, '12:00'),
        (6, '13:00'),
        (7, '14:00'),
        (8, '15:00'),
        (9, '16:00'),
        (10, '17:00'),
        (11, '18:00'),
        (12, '19:00'),
        (13, '20:00'),
        (14, '21:00'),
        (15, '22:00'),
    )

time_dict = {
	"08:00:00": 1,
	"09:00:00": 2,
	"10:00:00": 3,
	"11:00:00": 4,
	"12:00:00": 5,
	"13:00:00": 6,
	"14:00:00": 7,
	"15:00:00": 8,
	"16:00:00": 9,
	"17:00:00": 10,
	"18:00:00": 11,
	"19:00:00": 12,
	"20:00:00": 13,
	"21:00:00": 14,
	"22:00:00": 15,
}
times = [ time[1] for time in time_choices]

# 用户取消预定
@csrf_exempt
def disorder(request):
	user = auth(request)

	if user:
		order_id = request.POST.get("order_id", "None")
		user_order_table = User_order.objects.filter(user_id=user.id, order_id=order_id)

		# 响应json
		json_dict = {
			"disorder_status": 0
		}
		# 确认用户有此预定
		if len(user_order_table) != 0:
			order = Order.objects.get(id=order_id)
			order.delete()

			return JsonResponse(json_dict)
		else:
			json_dict["disorder_status"] = 1
			return JsonResponse(json_dict)

# 主页
def mainpage(request):
	user = auth(request)
	date = str(datetime.datetime.now()).split(" ")[0]
	if user:
		return render(request, 'mainsystem/mainpage.html', {
			'username': user.name,
			'date': date,
			'message_count': user.message_count,
		})
	else:
		return render(request, 'mainsystem/mainpage.html', {
			'username': None,
			'date': date,
		})

# 个人订单页面
def my_orders(request, date):
	user = auth(request)
	todaydate = str(datetime.datetime.now()).split(" ")[0]

	if user and date:
		order_table = User_order.objects.filter(user_id=user.id) # 订单表(用户id-订单id表)
		meeting = [] # 详细订单
		j_meeting = [] # 申请加入的会议

		# 查询用户有无申请加入
		join_meeting = Meeting.objects.filter(guest_id=user.id)
		if len(join_meeting):
			# 遍历订单表，获取详细订单
			for j in join_meeting:
				order = Order.objects.filter(id=j.order_id, booking_date=date)
				if len(order):
					order = order[0]
					order.room = MeetingRoom.objects.get(id=order.room_id)
					order.user = User.objects.get(id=j.host_id)
					order.my_remark = j.join_remark
					order.auth = j.auth
					j_meeting.append(order)

		# 查询该用户有无预定
		# 如果有预定
		if len(order_table):
			# 遍历订单表，获取详细订单
			for o in order_table:
				order = Order.objects.filter(id=o.order_id, booking_date=date)
				if len(order):
					order = order[0]
					order.room = MeetingRoom.objects.get(id=order.room_id)
					order.user = User.objects.get(id=user.id)
					meeting.append(order)
		# 无预定
		else:
			pass

		return render(request, 'mainsystem/myorders.html', {
			'username': user.name,
			'meeting': meeting,
			'date': date,
			'message_count': user.message_count,
			'join_meeting': j_meeting,
		})
	else:
		return render(request, 'mainsystem/myorders.html', {
			'username': None,
			'date': todaydate,
		})


# 主页
def mainsystem(request):
	user = auth(request)
	date = str(datetime.datetime.now()).split(" ")[0]
	# 房间
	rooms = [r.room_number for r in MeetingRoom.objects.all()]

	if user:
		return render(request, 'mainsystem/mainsystem.html', {
			'username': user.name,
			'times': times,
			'rooms': rooms,
			'date': date,
			'message_count': user.message_count,
		})
	else:
		return render(request, 'mainsystem/mainsystem.html', {
			'username': None,
			'times': times,
			'rooms': rooms,
			'date': date,
		})

# 判断用户登陆状态
def auth(request):
	try:
		user = User.objects.get(name=request.session.get('user_info', None)["name"])

		now_datetime = datetime.datetime.now()
		today_date = str(now_datetime).split(' ')[0]
		today_time = str(now_datetime).split(' ')[1]

		meetings = Meeting.objects.filter(host_id=user.id, auth=0)

		count = 0
		for m in meetings:
			count += len(Order.objects.filter(id=m.order_id, booking_date__gte=today_date))

		user.message_count = count
		return user
	except Exception as e:
		print(e)
		return False

# 处理用户上传图片
def deal_upload_img(request):
	user = auth(request)
	if request.method == "POST" and user:
		#删除旧图片文件
		if user.picture != 'None':
			try:
				os.remove(user.picture.path)
			except:
				pass
			finally:
				user.picture = 'None'
		picture = request.FILES.get('userimg')
		user.picture = picture
		user.save()

		# 提取特征值
		feature = extract_feature(user.picture.path)
		# 删除旧特征值文件
		if user.face_data_file != 'None':
			try:
				os.remove(user.face_data_file.path)
			except Exception as e:
				print(e)
				pass
		# 把特征值存入文件
		feature_path = user.name + "_feature_file.txt"
		f = open("temp_feature.txt", "w+")
		f.write(feature)
		user.face_data_file.save(feature_path, File(f))

		return redirect('/profile')

def extract_feature(path):
	try:
		temp_dir = os.getcwd()
		os.chdir("/home/sawyer/MeetingRoom/ASFTestDemo/build")
		completed = subprocess.run(
			['./go', path],
			stdout=subprocess.PIPE,
		)

		print('returncode:', completed.returncode)
		os.chdir(temp_dir)

		if completed.returncode == 0:
			pattern = completed.stdout.decode('utf-8')
			g = re.search(r"---.*---", pattern)
			if g:
				feature = g.group().strip('---')

				return feature
		else:
			return "None"
	except Exception as e:
		print(e)
		return "None"


# 加入会议界面
def meeting(request, date):
	user = auth(request)
	if user and date:
		orders = Order.objects.filter(booking_date=date)
		for o in orders:
			o.room = MeetingRoom.objects.get(id=o.room_id)
			user_id = User_order.objects.get(id=o.id).user_id
			o.user = User.objects.get(id=user_id)

		meeting = orders
		return render(request, 'mainsystem/meeting.html', {
			'username': user.name,
			'meeting': meeting,
			'date': date,
			'request_number': 2,
			'message_count': user.message_count,
		})
	else:
		return render(request, 'mainsystem/meeting.html')


# 处理用户个人信息界面
def profile(request):
	date = str(datetime.datetime.now()).split(" ")[0]
	user = auth(request)
	if user:
		email = user.email
		phone_number = user.phone_number
		nick_name = user.nick_name
		check_feature = 0
		if user.face_data_file != "None" and len(user.face_data_file.read()) > 1000:
			check_feature = 1
		return render(request, 'mainsystem/profile.html', {
			'username': user.name,
			'email': email,
			'img': user.picture,
			'nick_name': nick_name,
			'phone_number': phone_number,
			'check_feature': check_feature,
			'request_number': 2,
			'date': date,
			'message_count': user.message_count,
		})
	else:
		print("Not landed...")
		return render(request, 'mainsystem/profile.html', {
			'username': None,
			'date': date,
		})



# ajax处理用户下单
@csrf_exempt
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
				# 交叉算法检查时间冲突 / 加上=临界时间不可预定
				if start_time < order.end_time and end_time > order.start_time:
					return 1
		# 一个订单
		elif len(orders_by_the_date) == 1:
			if start_time < orders_by_the_date[0].end_time and end_time > orders_by_the_date[0].start_time:
				return 1

		return 0

	def return_json(status):
		json_dict = {
			"booking_status": status
		}
		return JsonResponse(json_dict)



	if request.method == "POST":
		remark = request.POST.get("remark", "None")
		start_time = request.POST.get("startTime", "None")
		end_time = request.POST.get("endTime", "None")
		date = request.POST.get("date", "None")
		room_number = request.POST.get("roomNumber", "None")
		username = request.POST.get("username", "None")

		# 检验数据存在
		if start_time != "None" and end_time != "None" and date != "None":
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
				return return_json("数据不合法")

			else:
				if check_time_conflict(room_id, datetime_date, datetime_start_time, datetime_end_time) == 0:
					# 存入数据库
					user = User.objects.get(name=username)
					order = Order(room=room, booking_date=datetime_date, remark=remark, start_time=datetime_start_time, end_time=datetime_end_time)
					order.save()
					orders = User_order(user=user, order=order)
					orders.save()

					return return_json(0)

				else:
					return return_json("时间冲突")

		else:
			return return_json("数据不存在")


# ajax返回房间状态(黑他人预定，蓝自己预定)
@csrf_exempt
def room_status_by_date(request):
	user = auth(request)

	json = {
		"others_room": {},
		"my_room": {},
	}
	room = MeetingRoom.objects.all()
	for r in room:
		json["my_room"][r.room_number] = []
		json["others_room"][r.room_number] = []

	if request.method == "POST" and request.POST.get("date", None):
		if user:
			# 已登陆
			orders = Order.objects.filter(booking_date=request.POST.get("date"))
			# 没有订单
			if len(orders) == 0:
				print("no booking for the date")

				return JsonResponse(json)
			# 有订单
			else:
				for order in orders:
					room_number = MeetingRoom.objects.get(id=order.room_id).room_number
					# 自己的预定
					if User_order.objects.get(id=order.id).user_id == user.id:
						json["my_room"][room_number].append(time_dict[str(order.start_time)])

						a_hour = datetime.timedelta(hours=1)
						delta1 = datetime.timedelta(hours=int(str(order.start_time).split(':')[0]),
													minutes=int(str(order.start_time).split(':')[1]))
						delta2 = datetime.timedelta(hours=int(str(order.end_time).split(':')[0]),
													minutes=int(str(order.end_time).split(':')[1]))
						if delta2 - delta1 > a_hour:
							json["my_room"][room_number].append(time_dict[str(order.start_time)] + 1)
					# 他人的预定
					else:
						json["others_room"][room_number].append(time_dict[str(order.start_time)])

						a_hour = datetime.timedelta(hours=1)
						delta1 = datetime.timedelta(hours=int(str(order.start_time).split(':')[0]),
													minutes=int(str(order.start_time).split(':')[1]))
						delta2 = datetime.timedelta(hours=int(str(order.end_time).split(':')[0]),
													minutes=int(str(order.end_time).split(':')[1]))
						if delta2 - delta1 > a_hour:
							json["others_room"][room_number].append(time_dict[str(order.start_time)] + 1)

				return JsonResponse(json)
		# 未登陆
		else:
			print("not sign in yet")

			orders = Order.objects.filter(booking_date=request.POST.get("date"))
			for order in orders:
				room_number = MeetingRoom.objects.get(id=order.room_id).room_number
				# 他人的预定
				json["others_room"][room_number].append(time_dict[str(order.start_time)])
				# 添加一个格子
				a_hour = datetime.timedelta(hours=1)
				delta1 = datetime.timedelta(hours=int(str(order.start_time).split(':')[0]), minutes=int(str(order.start_time).split(':')[1]))
				delta2 = datetime.timedelta(hours=int(str(order.end_time).split(':')[0]),
											minutes=int(str(order.end_time).split(':')[1]))
				if delta2 - delta1 > a_hour:
					json["others_room"][room_number].append(time_dict[str(order.start_time)] + 1)

			return JsonResponse(json)



@csrf_exempt
def edit_info(request):
	user = auth(request)
	if request.method == "POST" and user:
		json = {
			"status": 0,
		}

		edit_info = request.POST.get("eidt_info", None)
		edit_type = request.POST.get("edit_type", None)
		print("info", edit_info)
		print("type", edit_type)

		def edit_nickname(nickname):
			user.nick_name = nickname
			user.save()

		def edit_phone_number(phone_number):
			user.phone_number = phone_number
			user.save()

		def edit_email(email):
			user.email = email
			user.save()

		switch = {
			"editNickName": edit_nickname,  # 修改昵称
			"editPhoneNumber": edit_phone_number,  # 修改邮箱
			"editEmail": edit_email,  # 修改电话
		}

		switch[edit_type](edit_info)

		return JsonResponse(json)
	else:
		print("用户认证错误...")


@csrf_exempt
def join_meeting(request):
	user = auth(request)
	if request.method == "POST" and user:
		json = {
			"status": 0,
		}

		guest = user

		order_id = request.POST.get("order_id", None)
		host_name = request.POST.get("host_name", None)
		join_remark = request.POST.get("join_remark", None)
		print("order_id", order_id)
		print("host_name", host_name)
		print("join_remark", join_remark)
		host = User.objects.get(name=host_name)
		order = Order.objects.get(id=order_id)
		print("order_id", order_id)
		print("host_name", host_name)

		meeting = Meeting.objects.filter(order=order_id, guest=guest.id, host=host.id)

		# 未请求过该会议
		if len(meeting) == 0:
			print("第一次请求此会议")
			meeting = Meeting(guest=guest, host=host, order=order, auth=0, join_remark=join_remark)
			meeting.save()
		# 请求过该会议
		else:
			print("已经请求过了")
			pass

		return JsonResponse(json)
	else:
		print("用户认证错误...")


@csrf_exempt
def manage_meeting(request):
	user = auth(request)
	if request.method == "POST" and user:
		json = {
			"status": 0,
			"names": [],
			"remarks": [],
			"checked": [],
		}

		host = user
		order_id = request.POST.get("order_id", None)
		print("order_id", order_id)
		order = Order.objects.get(id=order_id)

		meeting = Meeting.objects.filter(host=host, order=order)

		# 没有人请求参加自己的会议
		if len(meeting) == 0:
			return JsonResponse(json)
		# 请求过该会议
		else:
			for m in meeting:
				guest = User.objects.get(id=m.guest_id).name
				json["names"].append(guest)
				json["remarks"].append(m.join_remark)
				json["checked"].append(m.auth)

			print(json)
			return JsonResponse(json)
	else:
		print("用户认证错误...")


@csrf_exempt
def change_auth(request):
	user = auth(request)
	if request.method == "POST" and user:
		json = {
			"status": 0,
		}

		host = user
		order_id = request.POST.get("order_id", None)
		print(request.POST)
		common_dict = dict(request.POST)
		print(common_dict)
		order = Order.objects.get(id=order_id)
		checked_name_list = common_dict.get("checked_name[]", None)
		checked_id_list = []
		if checked_name_list:
			checked_id_list = [User.objects.get(name=name).id for name in checked_name_list]
		meeting = Meeting.objects.filter(host=host, order=order)
		for m in meeting:
			m.auth = 0
			if m.guest_id in checked_id_list:
				m.auth = 1
			m.save()

		return JsonResponse(json)
	else:
		print("用户认证错误...")