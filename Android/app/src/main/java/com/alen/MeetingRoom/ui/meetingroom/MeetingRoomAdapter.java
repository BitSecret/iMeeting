package com.alen.MeetingRoom.ui.meetingroom;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.alen.MeetingRoom.R;
import com.alen.MeetingRoom.base.BaseActivity;
import com.alen.MeetingRoom.broadcast.ProfileChangedReceiver;
import com.alen.MeetingRoom.http.HttpHolder;
import com.alen.MeetingRoom.http.WorkServices;
import com.alen.MeetingRoom.interfaces.AddBookingListener;
import com.alen.MeetingRoom.utils.WorkDatabase;

import java.nio.file.Path;
import java.util.List;
import java.util.Map;

import io.reactivex.annotations.Nullable;
import sysu.zyb.panellistlibrary.AbstractPanelListAdapter;
import sysu.zyb.panellistlibrary.PanelListLayout;

public class MeetingRoomAdapter extends AbstractPanelListAdapter {

    private Context context;
    private List<Room> roomList;
    public static final String meetinAdaTag = "meetinAdaTag";
    private int resourceId;
    private WorkServices services;
    private String booking_date;
//    Map<Integer,Integer> roomDetail;

    public void setBooking_date(String booking_date){
        this.booking_date = booking_date;
    }

//    public MeetingRoomAdapter(Context context, PanelListLayout pl_root, List<Room> roomList, ListView lv_content) {
    public MeetingRoomAdapter(Context context, PanelListLayout pl_root, ListView lv_content, List<Room> roomList,int resourceId) {
        super(context, pl_root, lv_content);
        this.context = context;
        this.roomList = roomList;
        this.resourceId = resourceId;
    }

    @Override
    protected BaseAdapter getContentAdapter() {
        return new ContentAdapter(context, resourceId, roomList);
    }

    private class ContentAdapter extends ArrayAdapter {

        private int itemResourceId;
        private List<Room> roomList;

        public ContentAdapter(@NonNull Context context, @LayoutRes int resource, List<Room> roomList) {
            super(context, resource);
            this.itemResourceId = resource;
            this.roomList = roomList;
            services = WorkServices.getInstance((BaseActivity) context);
        }

        @Override
        public int getCount() {
//            Log.d("ybz", "getCount: ");
//            Log.d(meetinAdaTag,"roomList.size(): " + roomList.size());
            return roomList.size();
        }

        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            View view;
            ViewHolder viewHolder;
            Map<Integer,Integer> roomDetail = roomList.get(position).getRoomDetail();
            RoomClickListener listener = new RoomClickListener(position);
            if (convertView == null){
                view = LayoutInflater.from(parent.getContext()).inflate(itemResourceId,parent,false);
                viewHolder = new ViewHolder(view);
                view.setTag(viewHolder);
            } else {
                view = convertView;
                viewHolder = (ViewHolder) view.getTag();
            }
            viewHolder.tv_1.setOnClickListener(listener);
            viewHolder.tv_2.setOnClickListener(listener);
            viewHolder.tv_3.setOnClickListener(listener);
            viewHolder.tv_4.setOnClickListener(listener);
            viewHolder.tv_5.setOnClickListener(listener);
            viewHolder.tv_6.setOnClickListener(listener);
            viewHolder.tv_7.setOnClickListener(listener);
            viewHolder.tv_8.setOnClickListener(listener);
            viewHolder.tv_9.setOnClickListener(listener);
            viewHolder.tv_10.setOnClickListener(listener);
            viewHolder.tv_11.setOnClickListener(listener);
            viewHolder.tv_12.setOnClickListener(listener);
            viewHolder.tv_13.setOnClickListener(listener);
            viewHolder.tv_14.setOnClickListener(listener);
            viewHolder.tv_15.setOnClickListener(listener);
//            viewHolder.tv_16.setOnClickListener(listener);
            try {
                viewHolder.tv_1.setBackgroundResource(getBackgroundResource(roomDetail.get(1)));
                viewHolder.tv_1.setText(getText(roomDetail.get(1)));
                if (roomDetail.get(1) == 1|| roomDetail.get(1) == 2){
                    viewHolder.tv_1.setClickable(false);
                }
                viewHolder.tv_2.setBackgroundResource(getBackgroundResource(roomDetail.get(2)));
                viewHolder.tv_2.setText(getText(roomDetail.get(2)));
                if (roomDetail.get(2) == 1|| roomDetail.get(2) == 2){
                    viewHolder.tv_2.setClickable(false);
                }
                viewHolder.tv_3.setBackgroundResource(getBackgroundResource(roomDetail.get(3)));
                viewHolder.tv_3.setText(getText(roomDetail.get(3)));
                if (roomDetail.get(3) == 1|| roomDetail.get(3) == 2){
                    viewHolder.tv_3.setClickable(false);
                }
                viewHolder.tv_4.setBackgroundResource(getBackgroundResource(roomDetail.get(4)));
                viewHolder.tv_4.setText(getText(roomDetail.get(4)));
                if (roomDetail.get(4) == 1|| roomDetail.get(3) == 2){
                    viewHolder.tv_4.setClickable(false);
                }
                viewHolder.tv_5.setBackgroundResource(getBackgroundResource(roomDetail.get(5)));
                viewHolder.tv_5.setText(getText(roomDetail.get(5)));
                if (roomDetail.get(5) == 1|| roomDetail.get(5) == 2){
                    viewHolder.tv_5.setClickable(false);
                }
                viewHolder.tv_6.setBackgroundResource(getBackgroundResource(roomDetail.get(6)));
                viewHolder.tv_6.setText(getText(roomDetail.get(6)));
                if (roomDetail.get(6) == 1|| roomDetail.get(6) == 2){
                    viewHolder.tv_6.setClickable(false);
                }
                viewHolder.tv_7.setBackgroundResource(getBackgroundResource(roomDetail.get(7)));
                viewHolder.tv_7.setText(getText(roomDetail.get(7)));
                if (roomDetail.get(7) == 1|| roomDetail.get(7) == 2){
                    viewHolder.tv_7.setClickable(false);
                }
                viewHolder.tv_8.setBackgroundResource(getBackgroundResource(roomDetail.get(8)));
                viewHolder.tv_8.setText(getText(roomDetail.get(8)));
                if (roomDetail.get(8) == 1|| roomDetail.get(8) == 2){
                    viewHolder.tv_8.setClickable(false);
                }
                viewHolder.tv_9.setBackgroundResource(getBackgroundResource(roomDetail.get(9)));
                viewHolder.tv_9.setText(getText(roomDetail.get(9)));
                if (roomDetail.get(9) == 1|| roomDetail.get(9) == 2){
                    viewHolder.tv_9.setClickable(false);
                }
                viewHolder.tv_10.setBackgroundResource(getBackgroundResource(roomDetail.get(10)));
                viewHolder.tv_10.setText(getText(roomDetail.get(10)));
                if (roomDetail.get(10) == 1|| roomDetail.get(10) == 2){
                    viewHolder.tv_10.setClickable(false);
                }
                viewHolder.tv_11.setBackgroundResource(getBackgroundResource(roomDetail.get(11)));
                viewHolder.tv_11.setText(getText(roomDetail.get(11)));
                if (roomDetail.get(11) == 1|| roomDetail.get(11) == 2){
                    viewHolder.tv_11.setClickable(false);
                }
                viewHolder.tv_12.setBackgroundResource(getBackgroundResource(roomDetail.get(12)));
                viewHolder.tv_12.setText(getText(roomDetail.get(12)));
                if (roomDetail.get(12) == 1|| roomDetail.get(12) == 2){
                    viewHolder.tv_12.setClickable(false);
                }
                viewHolder.tv_13.setBackgroundResource(getBackgroundResource(roomDetail.get(13)));
                viewHolder.tv_13.setText(getText(roomDetail.get(13)));
                if (roomDetail.get(13) == 1|| roomDetail.get(13) == 2){
                    viewHolder.tv_13.setClickable(false);
                }
                viewHolder.tv_14.setBackgroundResource(getBackgroundResource(roomDetail.get(14)));
                viewHolder.tv_14.setText(getText(roomDetail.get(14)));
                if (roomDetail.get(14) == 1|| roomDetail.get(14) == 2){
                    viewHolder.tv_14.setClickable(false);
                }
                viewHolder.tv_15.setBackgroundResource(getBackgroundResource(roomDetail.get(15)));
                viewHolder.tv_15.setText(getText(roomDetail.get(15)));
                if (roomDetail.get(15) == 1|| roomDetail.get(15) == 2){
                    viewHolder.tv_15.setClickable(false);
                }
            }catch (Exception e){
                e.printStackTrace();
            }

            return view;
        }

        int getBackgroundResource(int i){
            switch (i){
                case Room.AVAILABLE:
//                    return R.drawable.bg_room_green_available;
                    return R.color.WhiteColor;
                case Room.RESERVED:
//                    return R.drawable.bg_room_orange_occupied;
                    return R.color.colorGrey;
                case Room.OCCUPIED_BY_OTHERS:
                    return R.color.colorGrey;
//                    return R.drawable.bg_room_blue_reserved;
//                case Room.OUTOFSERVICE:
//                    return R.drawable.bg_room_gray_outofservice;
//                    return R.drawable.bg_room_gray_outofservice;
                default:
                    return -1;
            }
        }

        String getText(int i){
            switch (i){
                case Room.AVAILABLE:
//                    return "可预定";
                case Room.OCCUPIED_BY_OTHERS:
//                    return "已预定";
                case Room.RESERVED:
//                    return "已预订";
                case Room.OUTOFSERVICE:
//                    return "维修中";
                default:
                    return null;
            }
        }

        private class ViewHolder{
            private TextView tv_1,tv_2,tv_3,tv_4,tv_5,tv_6,tv_7,tv_8,
                    tv_9,tv_10,tv_11,tv_12,tv_13,tv_14,tv_15;

            ViewHolder(View itemView) {
                tv_1 = itemView.findViewById(R.id.id_tv_01);
                tv_2 = itemView.findViewById(R.id.id_tv_02);
                tv_3 = itemView.findViewById(R.id.id_tv_03);
                tv_4 = itemView.findViewById(R.id.id_tv_04);
                tv_5 = itemView.findViewById(R.id.id_tv_05);
                tv_6 = itemView.findViewById(R.id.id_tv_06);
                tv_7 = itemView.findViewById(R.id.id_tv_07);
                tv_8 = itemView.findViewById(R.id.id_tv_08);
                tv_9 = itemView.findViewById(R.id.id_tv_09);
                tv_10 = itemView.findViewById(R.id.id_tv_10);
                tv_11 = itemView.findViewById(R.id.id_tv_11);
                tv_12 = itemView.findViewById(R.id.id_tv_12);
                tv_13 = itemView.findViewById(R.id.id_tv_13);
                tv_14 = itemView.findViewById(R.id.id_tv_14);
                tv_15 = itemView.findViewById(R.id.id_tv_15);
//                tv_16 = itemView.findViewById(R.id.id_tv_16);
            }
        }
    }

    class RoomClickListener implements View.OnClickListener{

        int position;

        public RoomClickListener(int i) {
            super();
            position = i;
        }

        @Override
        public void onClick(View v) {
            int dada = 0;
            if (context instanceof MeetingRoomActivity){
                dada = ((MeetingRoomActivity) context).getCurrentDate();
            }
            String startTime;
            String roomNumber;
            switch (v.getId()){
                case R.id.id_tv_01:
                    startTime = "8:00";
                    roomNumber = roomList.get(position).getRoomNo()+"";
//                    Toast.makeText(context,roomNumber+" 个 "+ startTime + " : " + booking_date, Toast.LENGTH_SHORT).show();
                    if (dada >= 0) {
                        showDialog(roomNumber, booking_date, startTime);
                    }else {
                        Toast.makeText(context,"不能预定以前的房间",Toast.LENGTH_SHORT).show();
                    }

                    break;
                case R.id.id_tv_02:
                    startTime = "9:00";
                    roomNumber = roomList.get(position).getRoomNo()+"";
//                    Toast.makeText(context,roomNumber+" 个 "+ startTime + " : " + booking_date, Toast.LENGTH_SHORT).show();
                    if (dada >= 0) {
                        showDialog(roomNumber, booking_date, startTime);
                    }else {
                        Toast.makeText(context,"不能预定以前的房间",Toast.LENGTH_SHORT).show();
                    }

                    break;
                case R.id.id_tv_03:
                    startTime = "10:00";
                    roomNumber = roomList.get(position).getRoomNo()+"";
//                    Toast.makeText(context,roomNumber+" 个 "+ startTime + " : " + booking_date, Toast.LENGTH_SHORT).show();
                    if (dada >= 0) {
                        showDialog(roomNumber, booking_date, startTime);
                    }else {
                        Toast.makeText(context,"不能预定以前的房间",Toast.LENGTH_SHORT).show();
                    }
                    break;
                case R.id.id_tv_04:
                    startTime = "11:00";
                    roomNumber = roomList.get(position).getRoomNo()+"";
//                    Toast.makeText(context,roomNumber+" 个 "+ startTime + " : " + booking_date, Toast.LENGTH_SHORT).show();
                    if (dada >= 0) {
                        showDialog(roomNumber, booking_date, startTime);
                    }else {
                        Toast.makeText(context,"不能预定以前的房间",Toast.LENGTH_SHORT).show();
                    }

                    break;
                case R.id.id_tv_05:
                    startTime = "12:00";
                    roomNumber = roomList.get(position).getRoomNo()+"";
//                    Toast.makeText(context,roomNumber+" 个 "+ startTime + " : " + booking_date, Toast.LENGTH_SHORT).show();
                    if (dada >= 0) {
                        showDialog(roomNumber, booking_date, startTime);
                    }else {
                        Toast.makeText(context,"不能预定以前的房间",Toast.LENGTH_SHORT).show();
                    }

                    break;
                case R.id.id_tv_06:
                    startTime = "13:00";
                    roomNumber = roomList.get(position).getRoomNo()+"";
//                    Toast.makeText(context,roomNumber+" 个 "+ startTime + " : " + booking_date, Toast.LENGTH_SHORT).show();
                    if (dada >= 0) {
                        showDialog(roomNumber, booking_date, startTime);
                    }else {
                        Toast.makeText(context,"不能预定以前的房间",Toast.LENGTH_SHORT).show();
                    }

                    break;
                case R.id.id_tv_07:
                    startTime = "14:00";
                    roomNumber = roomList.get(position).getRoomNo()+"";
//                    Toast.makeText(context,roomNumber+" 个 "+ startTime + " : " + booking_date, Toast.LENGTH_SHORT).show();
                    if (dada >= 0) {
                        showDialog(roomNumber, booking_date, startTime);
                    }else {
                        Toast.makeText(context,"不能预定以前的房间",Toast.LENGTH_SHORT).show();
                    }
                    break;
                case R.id.id_tv_08:
                    startTime = "15:00";
                    roomNumber = roomList.get(position).getRoomNo()+"";
//                    Toast.makeText(context,roomNumber+" 个 "+ startTime + " : " + booking_date, Toast.LENGTH_SHORT).show();
                    if (dada >= 0) {
                        showDialog(roomNumber, booking_date, startTime);
                    }else {
                        Toast.makeText(context,"不能预定以前的房间",Toast.LENGTH_SHORT).show();
                    }
                    break;
                case R.id.id_tv_09:
                    startTime = "16:00";
                    roomNumber = roomList.get(position).getRoomNo()+"";
//                    Toast.makeText(context,roomNumber+" 个 "+ startTime + " : " + booking_date, Toast.LENGTH_SHORT).show();
                    if (dada >= 0) {
                        showDialog(roomNumber, booking_date, startTime);
                    }else {
                        Toast.makeText(context,"不能预定以前的房间",Toast.LENGTH_SHORT).show();
                    }
                    break;
                case R.id.id_tv_10:
                    startTime = "17:00";
                    roomNumber = roomList.get(position).getRoomNo()+"";
//                    Toast.makeText(context,roomNumber+" 个 "+ startTime + " : " + booking_date, Toast.LENGTH_SHORT).show();
                    if (dada >= 0) {
                        showDialog(roomNumber, booking_date, startTime);
                    }else {
                        Toast.makeText(context,"不能预定以前的房间",Toast.LENGTH_SHORT).show();
                    }
                    break;
                case R.id.id_tv_11:
                    startTime = "18:00";
                    roomNumber = roomList.get(position).getRoomNo()+"";
//                    Toast.makeText(context,roomNumber+" 个 "+ startTime + " : " + booking_date, Toast.LENGTH_SHORT).show();
                    if (dada >= 0) {
                        showDialog(roomNumber, booking_date, startTime);
                    }else {
                        Toast.makeText(context,"不能预定以前的房间",Toast.LENGTH_SHORT).show();
                    }
                    break;
                case R.id.id_tv_12:
                    startTime = "19:00";
                    roomNumber = roomList.get(position).getRoomNo()+"";
//                    Toast.makeText(context,roomNumber+" 个 "+ startTime + " : " + booking_date, Toast.LENGTH_SHORT).show();
                    if (dada >= 0) {
                        showDialog(roomNumber, booking_date, startTime);
                    }else {
                        Toast.makeText(context,"不能预定以前的房间",Toast.LENGTH_SHORT).show();
                    }
                    break;
                case R.id.id_tv_13:
                    startTime = "20:00";
                    roomNumber = roomList.get(position).getRoomNo()+"";
//                    Toast.makeText(context,roomNumber+" 个 "+ startTime + " : " + booking_date, Toast.LENGTH_SHORT).show();
                    if (dada >= 0) {
                        showDialog(roomNumber, booking_date, startTime);
                    }else {
                        Toast.makeText(context,"不能预定以前的房间",Toast.LENGTH_SHORT).show();
                    }
                    break;
                case R.id.id_tv_14:
                    startTime = "21:00";
                    roomNumber = roomList.get(position).getRoomNo()+"";
//                    Toast.makeText(context,roomNumber+" 个 "+ startTime + " : " + booking_date, Toast.LENGTH_SHORT).show();
                    if (dada >= 0) {
                        showDialog(roomNumber, booking_date, startTime);
                    }else {
                        Toast.makeText(context,"不能预定以前的房间",Toast.LENGTH_SHORT).show();
                    }
                    break;
                case R.id.id_tv_15:
                    startTime = "22:00";
                    roomNumber = roomList.get(position).getRoomNo()+"";
//                    Toast.makeText(context,roomNumber+" 个 "+ startTime + " : " + booking_date, Toast.LENGTH_SHORT).show();
                    if (dada >= 0) {
                        showDialog(roomNumber, booking_date, startTime);
                    }else {
                        Toast.makeText(context,"不能预定以前的房间",Toast.LENGTH_SHORT).show();
                    }
                    break;

                default:
                    break;
            }
        }

        private void showDialog(final String roomNumber, final String year, final String start_time){
            TextView booking_room_number;
            TextView booking_room_year;
            TextView booking_room_start_time;
            final Spinner booking_room_end_time_spinner;
            final EditText booking_room_remark;

            String remark;

            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            View dialogView = LayoutInflater.from(context).inflate(R.layout.booking_room_layout,null);
            booking_room_number = dialogView.findViewById(R.id.booking_room_number);
            booking_room_year = dialogView.findViewById(R.id.booking_room_year);
            booking_room_start_time = dialogView.findViewById(R.id.booking_room_start_time);
            booking_room_end_time_spinner = dialogView.findViewById(R.id.booking_room_end_time_spinner);
            booking_room_remark = dialogView.findViewById(R.id.booking_room_remark);

            String[] timeArray = new String[8];
            int start_hour = Integer.parseInt(start_time.split(":")[0]);
            if (start_hour == 22){
                timeArray = new String[4];
            }
            timeArray[0] = start_hour + ":15";
            timeArray[1] = start_hour + ":30";
            timeArray[2] = start_hour + ":45";
            timeArray[3] = (start_hour+1) + ":00";
            if (start_hour != 22){
                timeArray[4] = (start_hour+1) + ":15";
                timeArray[5] = (start_hour+1) + ":30";
                timeArray[6] = (start_hour+1) + ":45";
                timeArray[7] = (start_hour+2) + ":00";
            }

            ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(context,android.R.layout.simple_list_item_1,timeArray);
            booking_room_end_time_spinner.setAdapter(arrayAdapter);
//            Log.d("getRoomTAg","adapter dialog roomNumber is :" + roomNumber + " ");
            booking_room_number.setText(roomNumber);
            booking_room_year.setText(year);
            booking_room_start_time.setText(start_time);

            builder.setView(dialogView);


            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    String remark = booking_room_remark.getText().toString();
                    String endTime = booking_room_end_time_spinner.getSelectedItem().toString();
//                    HttpHolder.getInstance(context).service.booking(start_time,
//                            endTime,
//                            year,
//                            roomNumber,
//                            context.getSharedPreferences("UserInfo",Context.MODE_PRIVATE).getString("name","null"),
//                            remark);
//                    Log.d("qwdqd","start_time is : " + start_time +
//                            " endTime is : " + endTime +
//                            " year is : " + year +
//                            " roomNumber is : " + roomNumber +
//                            " remark is : " + remark +
//                            " name is : " + context.getSharedPreferences("UserInfo",Context.MODE_PRIVATE).getString("name","null"));
//                    Toast.makeText(context,"You clicked OK! " + remark + " , " + endTime,Toast.LENGTH_SHORT).show();
                    WorkServices services = WorkServices.getInstance((BaseActivity) context);
                    services.booking(
                            start_time,
                            endTime,
                            year,
                            roomNumber,
                            context.getSharedPreferences("UserInfo",Context.MODE_PRIVATE).getString("name","null"),
                            remark);
                    services.setAddBookingListener(new AddBookingListener() {
                        @Override
                        public void addABook(boolean isSucceed) {
                            if (isSucceed) {
                                if(context instanceof MeetingRoomActivity){
                                    ((MeetingRoomActivity) context).changeData(booking_date);
                                }
                            }
                        }
                    });


                }
            });

            builder.create().show();
        }
    }
}
