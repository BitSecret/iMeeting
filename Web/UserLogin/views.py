from django.shortcuts import render, redirect, HttpResponse
from .forms import LoginForm, RegisterForm
from .models import User
import hashlib

# Create your views here.
def Login(request):
	if request.method == 'GET':
		return render(request, 'UserLogin/LoginPage.html', {"form": LoginForm})
	elif request.method == 'POST':
		form = LoginForm(request.POST)
		if form.is_valid():
			name = form.cleaned_data['name']
			password = form.cleaned_data['password']
			password = sha256hash(password)
			user = User.objects.filter(name=name, password=password)
			# 成功登陆:
			if user:
				# 添加session
				request.session['user_info'] = {'name': name}
				return redirect('/')
			else:
			# 登陆失败:
				form.add_error('password', '用户名/密码错误')
				return render(request, 'UserLogin/LoginPage.html', {"form": form})


def Logout(request):
	del request.session['user_info']
	return redirect('/')


def Register(request):
	# 注册页面
	if request.method == 'GET':
		return render(request, 'UserLogin/RegisterPage.html', {"form": RegisterForm})
	# 注册逻辑处理
	elif request.method == 'POST':
		form = RegisterForm(request.POST)
		if form.is_valid():
			name = form.cleaned_data['name']
			password = form.cleaned_data['password']
			# 用md5 hash生成密码摘要并储存
			password = sha256hash(password)
			user = User.objects.filter(name=name)
			# 无重复用户, 成功注册 check用来弹出js
			if not user:
				user = User(name=name, password=password, nick_name=name)
				user.save()
				return render(request, 'UserLogin/RegisterPage.html', {"form": form, "check": 1})
			else:
				form.add_error('name', '用户已经存在')
				return render(request, 'UserLogin/RegisterPage.html', {"form": form})


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
