package com.alen.MeetingRoom.ui.home;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.alen.MeetingRoom.Bean.BookBean;
import com.alen.MeetingRoom.R;
import com.alen.MeetingRoom.base.BaseActivity;
import com.alen.MeetingRoom.base.BaseFragment;
import com.alen.MeetingRoom.http.WorkServices;
import com.alen.MeetingRoom.ui.CallBackListener;
import com.alen.MeetingRoom.ui.meetingroom.MeetingRoomActivity;
import com.alen.MeetingRoom.utils.WorkDatabase;

import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * A simple {@link Fragment} subclass.
 */
public class HomeFragment extends BaseFragment {

    @BindView(R.id.layout_goto)
    LinearLayout layout_goto;
    @BindView(R.id.tv_goto_order)
    TextView tv_goto_order;

    private CallBackListener listener;

    public HomeFragment() {
        // Required empty public constructor
    }

    @Override
    protected int getLayout() {
        return R.layout.fragment_home;
    }

    @Override
    protected void init(final Bundle savedInstanceState) {
        listener = (CallBackListener) mContext;
        layout_goto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(mContext, MeetingRoomActivity.class));
            }
        });

        tv_goto_order.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.gotoOrder();
//                SharedPreferences sharedPreferences = getContext().getSharedPreferences("UserInfo", Context.MODE_PRIVATE);
//
//                WorkServices workServices = WorkServices.getInstance((BaseActivity) getContext());
//                WorkDatabase database = WorkDatabase.getInstance(getContext());
//                workServices.getAllBookList();
//                List<BookBean> list  = database.getAllBookingList();
//                List<BookBean> list  = database.getOneDayBookingData("2019-01-02");
//                for (BookBean bookBean: list){
//                    Log.d("book_bean_tag",bookBean.getBooking_date() + " , room is :" + bookBean.getRoom());
//                }
//                workServices.getUserOders("123");
//                workServices.getUserInfo("123");

//                workService123s.getAvatar(sharedPreferences.getString("picture",""));
            }
        });

    }

}
