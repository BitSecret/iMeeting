package com.alen.MeetingRoom.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.alen.MeetingRoom.R;
import com.alen.MeetingRoom.utils.ConvertUtils;


public class ItemView extends LinearLayout {

    private Context context;

    public ImageView imageView, imageView2;

    public TextView textView01, textView02, textView03;

    public EditText editText;

    //
    int image_src, //图标资源id
            image_src2, //图标2资源id
            color1, //textview1的颜色
            color2, //textview2的颜色
            color3, //textview3的颜色
            edit_height, //editview的高度,默认warp_content
            default_height, //textview和edit的默认高度
            image_magrinTop;//图标的magrin_top

    String text1, text2, text3, edit_hint;

    public boolean have_top_line, have_bottom_line;

    Paint paint;

    public ItemView(Context context) {
        this(context, null);
    }

    public ItemView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ItemView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
        paint = new Paint();
        paint.setColor(Color.parseColor("#d9d9d9"));
        paint.setStrokeWidth((float)1);
        init(attrs);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (have_bottom_line){
            canvas.drawLine(getPaddingLeft(),getHeight()-1,getWidth()-getPaddingRight(),getHeight()-1,paint);
        }
        if (have_top_line){
            canvas.drawLine(getPaddingLeft(), 1, getWidth()-getPaddingRight(), 1, paint);
        }
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        invalidate();
    }

    private void init(AttributeSet attrs){
        TypedArray types = context.obtainStyledAttributes(attrs, R.styleable.ItemView);
        image_src = types.getResourceId(R.styleable.ItemView_set_icon, 0x0);
        image_src2 = types.getResourceId(R.styleable.ItemView_set_icon2, 0x0);
        image_magrinTop = types.getDimensionPixelSize(R.styleable.ItemView_set_icon_margin_top, 0);
        text1 = types.getString(R.styleable.ItemView_set_text01);
        text2 = types.getString(R.styleable.ItemView_set_text02);
        text3 = types.getString(R.styleable.ItemView_set_text03);
        edit_hint = types.getString(R.styleable.ItemView_set_edit_hint);
        color1 = types.getColor(R.styleable.ItemView_set_text_color01, Color.BLACK);
        color2 = types.getColor(R.styleable.ItemView_set_text_color02, Color.BLACK);
        color3 = types.getColor(R.styleable.ItemView_set_text_color03, Color.BLACK);
        edit_height = types.getDimensionPixelSize(R.styleable.ItemView_set_edit_height, 0);
        default_height = types.getDimensionPixelSize(R.styleable.ItemView_set_default_height,
                ConvertUtils.dp2px(30));
        have_top_line = types.getBoolean(R.styleable.ItemView_have_top_line, false);
        have_bottom_line = types.getBoolean(R.styleable.ItemView_have_bottom_line, true);
        types.recycle();

        setOrientation(HORIZONTAL);

        creatImage();

        creatText1();
        creatText2();
        creatText3();

        creatEdit();

        creatImage2();

    }

    private int getDefault_height(){
        return default_height == 0 ? LayoutParams.WRAP_CONTENT : default_height;
    }

    private void creatImage(){
        if (image_src != 0x0){
            imageView = new ImageView(getContext());
            LayoutParams params = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
            params.setMargins(0,image_magrinTop, ConvertUtils.dp2px(20), 0);
            imageView.setLayoutParams(params);
            imageView.setImageResource(image_src);
            addView(imageView);
        }
    }

    private void creatImage2(){
        if (image_src2 != 0x0){
            imageView2 = new ImageView(getContext());
            LayoutParams params = new LayoutParams(ConvertUtils.dp2px(60), ConvertUtils.dp2px(60));
            params.setMargins(0,image_magrinTop, 0, 0);
            imageView2.setLayoutParams(params);
            imageView2.setImageResource(image_src2);
            addView(imageView2);
        }
    }


    private void creatText1(){
        if (text1 != null){
            textView01 = new TextView(getContext());
            LayoutParams params;
            if (image_src == 0x0){
                if (image_src2 != 0x0){
                    params = new LayoutParams(LayoutParams.WRAP_CONTENT, getDefault_height(), 1);
                }else params = new LayoutParams(LayoutParams.WRAP_CONTENT, getDefault_height());
                params.setMargins(0,0, ConvertUtils.dp2px(20), 0);
            }else {
                params = new LayoutParams(LayoutParams.WRAP_CONTENT, getDefault_height(), 1);
            }

            textView01.setLayoutParams(params);
            textView01.setGravity(Gravity.CENTER_VERTICAL);
            textView01.setTextColor(color1);
            textView01.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
            textView01.setText(text1);
            addView(textView01);
        }
    }
    private void creatText2(){
        if (text2 != null){
            textView02 = new TextView(getContext());
            LayoutParams params;
            if (image_src != 0x0){
                params = new LayoutParams(LayoutParams.WRAP_CONTENT, getDefault_height());
                params.setMargins(0,0, ConvertUtils.dp2px(20), 0);
                textView02.setGravity(Gravity.CENTER_VERTICAL);
            }else {
                params = new LayoutParams(LayoutParams.WRAP_CONTENT, getDefault_height(), 1);
                textView02.setGravity(Gravity.CENTER_VERTICAL|Gravity.RIGHT);
            }
            textView02.setLayoutParams(params);
            textView02.setTextColor(color2);
            textView02.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
            textView02.setText(text2);
            addView(textView02);
        }
    }
    private void creatText3(){
        if (text3 != null){
            textView03 = new TextView(getContext());
            LayoutParams params;
            if (image_src == 0x0){
                params = new LayoutParams(LayoutParams.WRAP_CONTENT, getDefault_height());
                params.setMargins(0,0, ConvertUtils.dp2px(20), 0);
            }else {
                params = new LayoutParams(LayoutParams.WRAP_CONTENT, getDefault_height(), 1);
            }
            textView03.setLayoutParams(params);
            textView03.setGravity(Gravity.CENTER_VERTICAL);
            textView03.setTextColor(color3);
            textView03.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
            textView03.setText(text3);
            addView(textView03);
        }
    }

    private void creatEdit(){
        if (edit_hint != null){
            editText = new EditText(getContext());
            if (edit_height != 0){
                editText.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, edit_height, 1));
            }else {
                editText.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, getDefault_height(), 1));
            }
            editText.setSingleLine(true);
            editText.setTextColor(Color.BLACK);
            editText.setHintTextColor(Color.GRAY);
            editText.setGravity(Gravity.TOP);
            editText.setPadding(12,12,12,12);
            editText.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
            editText.setHint(edit_hint);
            editText.setBackgroundResource(R.color.transparent);
            addView(editText);
        }
    }
}
