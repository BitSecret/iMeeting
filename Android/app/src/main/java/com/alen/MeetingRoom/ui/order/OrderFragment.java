package com.alen.MeetingRoom.ui.order;


import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.alen.MeetingRoom.Bean.BookBean;
import com.alen.MeetingRoom.R;
import com.alen.MeetingRoom.base.BaseActivity;
import com.alen.MeetingRoom.base.BaseFragment;
import com.alen.MeetingRoom.broadcast.BookingListChangedReceiver;
import com.alen.MeetingRoom.broadcast.LoadBookingDownReceiver;
import com.alen.MeetingRoom.http.WorkServices;
import com.alen.MeetingRoom.utils.BookingListAdapter;
import com.alen.MeetingRoom.utils.DecorationUtils;
import com.alen.MeetingRoom.utils.WorkDatabase;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;

import java.util.ArrayList;
import java.util.List;
import java.util.Observable;

import javax.sql.StatementEvent;

import butterknife.BindView;
import retrofit2.http.PartMap;

/**
 * A simple {@link Fragment} subclass.
 */
public class OrderFragment extends BaseFragment {

    private  static final String ORDERTAG = "ORDERTAG";
    private final List<BookBean> list = new ArrayList<>();
    private BookingListChangedReceiver bcreceiver;
    private LoadBookingDownReceiver lbdreceiver;
    @BindView(R.id.rv_order)
    RecyclerView rv_order;

    private BookingListAdapter adapter;

    public OrderFragment() {
        // Required empty public constructor
    }

    @Override
    protected int getLayout() {
        return R.layout.fragment_order;
    }

    @Override
    protected void init(Bundle savedInstanceState) {
//        final List<BookBean> list = WorkDatabase.getInstance(getContext()).getAllBookingList();
        List<BookBean> newList = WorkDatabase.getInstance(getContext()).getAllBookingList();
        for (BookBean bookBean : newList){
//            Log.d("asdadf",bookBean.getId() + ", " + bookBean.getRoom());
            list.add(bookBean);
        }
        LinearLayoutManager manager = new LinearLayoutManager(mContext);
        rv_order.setLayoutManager(manager);
        rv_order.addItemDecoration(new DecorationUtils.Line(R.color.transparent, 10));
        adapter = new BookingListAdapter(getContext(),R.layout.module_recyclerview_order_item, list);
        rv_order.setAdapter(adapter);
        adapter.setOnItemChildClickListener(new BaseQuickAdapter.OnItemChildClickListener() {
            @Override
            public void onItemChildClick(BaseQuickAdapter adapter, View view, int position) {
                try {
                    WorkServices.getInstance((BaseActivity) mContext).deleteBooking(list.get(position).getId());
                    adapter.remove(position);
                }catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        initBroadcast();

        /*adapter.setUpFetchListener(new BaseQuickAdapter.UpFetchListener() {
            @Override
            public void onUpFetch() {
                sendToast("refresh®");
            }
        });*/
//        rv_order.
//        adapter.setUpFetchEnable(false);

        adapter.setUpFetchEnable(true);
        adapter.setStartUpFetchPosition(2);
        adapter.setUpFetchListener(new BaseQuickAdapter.UpFetchListener() {
            @Override
            public void onUpFetch() {
                Log.d("refreshTag","UpFetch");
            }
        });
    }

    private void initBroadcast() {
        IntentFilter intentFilter = new IntentFilter(BookingListChangedReceiver.BOOKING_LIST_CHANGED);
        bcreceiver = new BookingListChangedReceiver(){
            @Override
            public void onReceive(Context context, Intent intent) {
//                Log.d("werwewqr","on receive");
                List<BookBean> bookBeans = WorkDatabase.getInstance(getContext()).getAllBookingList();
                list.clear();
                for (BookBean bookBean: bookBeans){
                    list.add(bookBean);
                }
                adapter.notifyDataSetChanged();
            }
        };
        IntentFilter intentFilter1 = new IntentFilter(LoadBookingDownReceiver.ACTION_LOAD_BOOKING_DOWN);
        lbdreceiver = new LoadBookingDownReceiver(){
            @Override
            public void onReceive(Context context, Intent intent) {
                List<BookBean> bookBeans = WorkDatabase.getInstance(getContext()).getAllBookingList();
                list.clear();
                for (BookBean bookBean: bookBeans){
                    list.add(bookBean);
                }
                adapter.notifyDataSetChanged();
            }
        };
        getContext().registerReceiver(bcreceiver,intentFilter);
        getContext().registerReceiver(lbdreceiver,intentFilter1);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (null != bcreceiver) {
            getContext().unregisterReceiver(bcreceiver);
        }
        if (null!=lbdreceiver) {
            getContext().unregisterReceiver(lbdreceiver);
        }
    }
}
