from django import forms
from django.forms import Form
from django.forms import fields
from django.forms import widgets

class LoginForm(Form):
    name = fields.CharField(
        required=True,
        error_messages={'required': '用户名不能为空'},
        widget=widgets.TextInput(attrs={'placeholder': '用户名', 'id': 'name'})
    )
    password = fields.CharField(
        required=True,
        error_messages={'required': '密码不能为空'},
        widget=widgets.PasswordInput(attrs={'placeholder': '密码', 'id': 'password'})
    )

class RegisterForm(forms.Form):
    name = fields.CharField(
        required=True,
        error_messages={'required': '用户名不能为空'},
        widget=widgets.TextInput(attrs={'placeholder': '用户名', 'id': 'name'})
    )
    password = fields.CharField(
        required=True,
        error_messages={'required': '密码不能为空'},
        widget=widgets.PasswordInput(attrs={'placeholder': '密码', 'id': 'password'})
    )
