package com.alen.MeetingRoom;

import android.util.Log;

import com.alen.MeetingRoom.http.WorkServices;

import org.junit.Test;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.junit.Assert.*;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {
    @Test
    public void addition_isCorrect() {
        assertEquals(4, 2 + 2);
    }

    @Test
    public void test(){
        String line = "{'results': [{'room': '107', 'remark': 'remark', 'booking_date': '2019-03-09', 'end_time': '09:30:00', 'id': 95, 'start_time': '08:00:00', 'found_time': '2019-03-09 14:43:44.228138'}], 'status': 'success'}";
//        String line = "123def";
        String regex = "(.*id.*: )(\\d+)(.*)";
        boolean isMatch = Pattern.matches(regex, line);
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(line);

        System.out.println(isMatch);
        if (matcher.find()){
            System.out.println(matcher.group(0));
            System.out.println(matcher.group(1));
            System.out.println(matcher.group(2));
            System.out.println(matcher.group(3));
        }
    }

    @Test
    public void testDate() throws ParseException {
            SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd");
            //跨年的情况会出现问题哦
            //如果时间为：2016-03-18 11:59:59 和 2016-03-19 00:00:01的话差值为 1
            Date fDate=sdf.parse("2019-03-08");
            Date oDate=sdf.parse("2016-03-08");
            Calendar aCalendar = Calendar.getInstance();
            aCalendar.setTime(fDate);
            int day1 = aCalendar.get(Calendar.DAY_OF_YEAR);
            aCalendar.setTime(oDate);
            int day2 = aCalendar.get(Calendar.DAY_OF_YEAR);
            int days=day2-day1;
            System.out.print(days);
    }

    @Test
    public void testXX2XXX(){
        String newNumber = null;
        int number = 64;
        // 103
        if (number < 8){
            newNumber = "10" + number%8;
        }else {
            if (number%8 != 0) {
                newNumber = number / 8 + 1 + "0" + number % 8;
            }else {
                newNumber = number / 8  + "08";
            }
        }
//        String newNumber = number % 8+"";
        System.out.println("newNumber is : " + newNumber);
    }
}

