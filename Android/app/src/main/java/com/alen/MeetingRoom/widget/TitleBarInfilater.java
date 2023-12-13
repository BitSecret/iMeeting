package com.alen.MeetingRoom.widget;

import android.app.Activity;
import android.graphics.Color;
import android.os.Build;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alen.MeetingRoom.R;
import com.alen.MeetingRoom.utils.ConvertUtils;
import com.alen.MeetingRoom.utils.Utils;
import com.blankj.utilcode.util.BarUtils;


public class TitleBarInfilater {

    private static final String TAG = TitleBarInfilater.class.getSimpleName() + "------";

    public static final int NULL = 0, TEXT = 1, IMAGE = 2, EDIT = 3;

    private static int PX = ConvertUtils.dp2px(5);

    private static int IMAGE_PX = ConvertUtils.dp2px(40);

    //默认id
    private static final int TITLE_BAR_ID = R.id.tool_bar;

    //默认字体颜色
    private static final int TEXT_COLOR = R.color.MainTextColor;

    //默认字体大小
    private static final int TEXT_SIZE = 18;

    //默认背景颜色
    private static final int BG_COLOR = R.color.MainColor;

    public static Builder form(Activity activity){
        return form(activity, TEXT);
    }

    private static Builder form(Activity activity, int type_center){
        return form(activity, type_center, IMAGE, NULL);
    }

    public static Builder form(Activity activity, int type_left, int type_right){
        return form(activity, TEXT, type_left, type_right);
    }

    public static Builder form(Activity activity, int type_center, int type_left, int type_right){
        return form(activity, TITLE_BAR_ID, type_center, type_left, type_right);
    }

    private static Builder form(Activity activity, int toolbarId, int type_center, int type_left, int type_right){
        Toolbar toolbar = getToolbar(activity, toolbarId);
        RelativeLayout layout = getLayout(activity);
        View view_centre = getTitle(activity, type_center);
        layout.addView(view_centre);
        View view_left = getLeftView(activity, type_left);
        View view_right = getRightView(activity, type_right);
        if (view_left != null){
            layout.addView(view_left);
        }
        if (view_right != null){
            layout.addView(view_right);
        }
        toolbar.addView(layout);
        return new Builder(activity,toolbar,layout,view_centre,view_left,view_right);
    }

    private static Toolbar getToolbar(Activity activity, int toolbarId){
        Toolbar toolbar = activity.findViewById(toolbarId);
        if (toolbar == null){
            Log.e(TAG, "init error : can't find toolbarID");
        }

        toolbar.setBackgroundResource(BG_COLOR);
        return toolbar;
    }

    private static RelativeLayout getLayout(Activity activity){
        RelativeLayout layout = new RelativeLayout(activity);
        RelativeLayout.LayoutParams layout_rp = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ConvertUtils.dp2px(40));
        layout_rp.setMargins(PX, PX, PX, PX);
        layout.setLayoutParams(layout_rp);
        BarUtils.addMarginTopEqualStatusBarHeight(layout);
        layout.setPadding(PX, 0, PX, 0);
        return layout;
    }

    private static TextView getTitle(Activity activity, int type_center){
        RelativeLayout.LayoutParams title_rp = new RelativeLayout.LayoutParams(ConvertUtils.dp2px(240), ViewGroup.LayoutParams.MATCH_PARENT);
        title_rp.addRule(RelativeLayout.CENTER_IN_PARENT);
        switch (type_center){
            case TEXT:
                TextView titleText = new TextView(activity);
                titleText.setLayoutParams(title_rp);
                titleText.setGravity(Gravity.CENTER);
                titleText.setText("default");
                titleText.setTextSize(TypedValue.COMPLEX_UNIT_SP, TEXT_SIZE);
                titleText.setTextColor(Utils.getContext().getResources().getColor(TEXT_COLOR));
                return titleText;
            case EDIT:
                EditText editText = new EditText(activity);
                editText.setLayoutParams(title_rp);
                editText.setBackgroundResource(R.color.transparent);
                editText.setGravity(Gravity.CENTER|Gravity.BOTTOM);
                editText.setSingleLine(true);
                editText.setImeOptions(EditorInfo.IME_ACTION_SEARCH);
                editText.setTextSize(TypedValue.COMPLEX_UNIT_SP, TEXT_SIZE);
                editText.setTextColor(Utils.getContext().getResources().getColor(TEXT_COLOR));
                return editText;
        }
        return null;
    }

    private static View getLeftView(final Activity activity, int type){
        RelativeLayout.LayoutParams rp = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        rp.addRule(RelativeLayout.CENTER_VERTICAL);
        rp.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
        switch (type){
            case TEXT:
                TextView textView = new TextView(activity);
                textView.setLayoutParams(rp);
                textView.setGravity(Gravity.CENTER);
                textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, TEXT_SIZE);
                textView.setTextColor(Utils.getContext().getResources().getColor(TEXT_COLOR));
                textView.setText("返回");
                textView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        activity.onBackPressed();
                    }
                });
                return textView;
            case IMAGE:
                rp.height = IMAGE_PX;
                rp.width = IMAGE_PX;
                ImageView imageView = new ImageView(activity);
                imageView.setLayoutParams(rp);
                imageView.setImageResource(R.drawable.icon_back);
                imageView.setScaleType(ImageView.ScaleType.CENTER);
                imageView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        activity.onBackPressed();
                    }
                });
                return imageView;
            default:
                return null;
        }
    }

    private static View getRightView(Activity activity, int type){
        RelativeLayout.LayoutParams rp = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        rp.addRule(RelativeLayout.CENTER_VERTICAL);
        rp.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
        switch (type){
            case TEXT:
                TextView textView = new TextView(activity);
                textView.setLayoutParams(rp);
                textView.setGravity(Gravity.CENTER);
                textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, TEXT_SIZE);
                textView.setTextColor(Utils.getContext().getResources().getColor(TEXT_COLOR));
                return textView;
            case IMAGE:
                rp.height = IMAGE_PX;
                rp.width = IMAGE_PX;
                ImageView imageView = new ImageView(activity);
                imageView.setLayoutParams(rp);
                imageView.setScaleType(ImageView.ScaleType.FIT_XY);
                return imageView;
            default:
                return null;
        }
    }

    public static class Builder {
        public Builder builder;
        public Activity activity;
        public Toolbar toolbar;
        public RelativeLayout layout;
        public View view_center, view_left, view_right;

        public Builder(Activity activity, Toolbar toolbar, RelativeLayout layout, View view_center, View view_left, View view_right) {
            this.activity = activity;
            this.toolbar = toolbar;
            this.layout = layout;
            this.view_center = view_center;
            this.view_left = view_left;
            this.view_right = view_right;
            builder = this;
        }

        public Builder setElevation(int size){
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                toolbar.setElevation(ConvertUtils.dp2px(size));
            }
            return builder;
        }

        public Builder setToolbarBackground(int resId){
            toolbar.setBackgroundResource(resId);
            return builder;
        }

        public Builder setLayoutBackground(int resId){
            layout.setBackgroundResource(resId);
            return builder;
        }


        public Builder setTitleText(String text) {
            ((TextView)view_center).setText(text);
            return builder;
        }

        public Builder setTitleTextSize(int sp) {
            ((TextView)view_center).setTextSize(TypedValue.COMPLEX_UNIT_SP, sp);
            return builder;
        }

        public Builder setTitleTextColor(int resId) {
            ((TextView)view_center).setTextColor(Color.parseColor(activity.getResources().getString(resId)));
            return builder;
        }

        public Builder setLeftImageResId(int leftImageResId) {
            ((ImageView)view_left).setImageResource(leftImageResId);
            return builder;
        }

        public Builder setLeftViewClick(View.OnClickListener leftImageClick) {
            view_left.setOnClickListener(leftImageClick);
            return builder;
        }

        public Builder setRightImageResId(int rightImageResId) {
            ((ImageView)view_right).setImageResource(rightImageResId);
            return builder;
        }

        public Builder setRightImageWH(int w, int h) {
            ViewGroup.LayoutParams params = view_right.getLayoutParams();
            params.width = w;
            params.height = h;
            view_right.setLayoutParams(params);
            return builder;
        }

        public Builder setRightViewClick(View.OnClickListener rightImageClick) {
            view_right.setOnClickListener(rightImageClick);
            return builder;
        }

        public Builder setLeftViewText(String text) {
            ((TextView)view_left).setText(text);
            return builder;
        }

        public Builder setLeftViewTextSize(int sp) {
            ((TextView)view_left).setTextSize(TypedValue.COMPLEX_UNIT_SP, sp);
            return builder;
        }

        public Builder setLeftViewTextColor(int resId) {
            ((TextView)view_left).setTextColor(Color.parseColor(activity.getResources().getString(resId)));
            return builder;
        }

        public Builder setRightViewText(String text) {
            ((TextView)view_right).setText(text);
            return builder;
        }

        public Builder setRightViewTextSize(int sp) {
            ((TextView)view_right).setTextSize(TypedValue.COMPLEX_UNIT_SP, sp);
            return builder;
        }

        public Builder setRightViewTextColor(int resId) {
            ((TextView)view_right).setTextColor(Color.parseColor(activity.getResources().getString(resId)));
            return builder;
        }
    }
}
