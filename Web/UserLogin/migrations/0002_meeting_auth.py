# Generated by Django 2.1 on 2019-03-08 22:55

from django.db import migrations, models


class Migration(migrations.Migration):

    dependencies = [
        ('UserLogin', '0001_initial'),
    ]

    operations = [
        migrations.AddField(
            model_name='meeting',
            name='auth',
            field=models.BooleanField(default=0, verbose_name='授权状态'),
        ),
    ]
