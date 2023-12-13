# Create your tests here.
from .models import MeetingRoom
rooms = []

for x in range(1, 7):
    for y in range(1, 6):
        rooms.append(str(x) + '0' + str(y))

for r in rooms:
    room = MeetingRoom(room_number=r, room_type="normal")
    room.save()