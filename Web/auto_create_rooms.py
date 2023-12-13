from mainsystem.models import MeetingRoom
from django.core.files import File
import os
from random import choice

room_type = ["中小型会议室", "大型会议室", "董事会形", "课堂式", "剧院式", "U形", "鸡尾酒式", "宴会式"]

os.chdir("room_img")
for i in range(1, 9):
	for j in range(1, 9):
		with open (str(i) + ".jpg", "rb") as f:
			room_number = str(i) + '0' + str(j)
			r = MeetingRoom(room_number=room_number)
			r.room_type = choice(room_type)
			r.img.save(str(i) + ".jpg", File(f))
			r.save()