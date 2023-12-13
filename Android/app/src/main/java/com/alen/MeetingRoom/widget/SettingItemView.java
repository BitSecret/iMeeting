package com.alen.MeetingRoom.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;

import com.alen.MeetingRoom.R;
import com.alen.MeetingRoom.utils.ToastUtils;
import com.alen.MeetingRoom.utils.Utils;


/**
 * Created by Alen on 2018/2/11.
 */

public class SettingItemView extends ViewGroup {
    private Context context;
    private Paint paint;

    private int viewHeight;
    private int viewWidth;

    private int main_icon;//主图标
    private int main_icon_height;//主图标高
    private int main_icon_width;//主图标宽
    private int main_icon_to_title;//主图标到标题文字的间隔
    private int main_icon_round_size;//圆角大小
    private boolean main_icon_is_circular;//是否为圆形
    private int vice_icon;//副图标
    private int vice_icon_height;//副图标高
    private int vice_icon_width;//副图标宽
    private int vice_icon_to_tip;//副图标到提示文字的间隔
    private String title_text;//标题文字
    private int title_text_size;//标题文字大小
    private int title_text_color;//标题文字颜色
    private int title_to_content_size;//标题到内容的距离大小
    private String content_text;//内容文字
    private int content_text_size;//内容文字大小
    private int content_text_color;//内容文字颜色
    private String tip_text;//提示文字
    private int tip_text_size;//提示文字大小
    private int tip_text_color;//提示文字颜色
    private boolean have_slide_button, have_top_line, have_bottom_line;//滑动开关
    public SlideButton slideButton;

    private Bitmap mainBitmap, viceBitmap,myBitmap;

    public SettingItemView(Context context) {
        super(context);
        this.context = context;
        init(null);
    }

    public SettingItemView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        init(attrs);
    }

    public SettingItemView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
        init(attrs);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        if (getChildCount() > 0){
            View view = getChildAt(0);
            view.layout(viewWidth-view.getMeasuredWidth()-getPaddingRight(),(viewHeight-view.getMeasuredHeight())/2,
                    viewWidth-getPaddingRight(),(viewHeight+view.getMeasuredHeight())/2);
        }
    }

    protected void init(AttributeSet attrs){
        //初始化xml属性设置
        TypedArray types = context.obtainStyledAttributes(attrs, R.styleable.SettingItemView);

        main_icon = types.getResourceId(R.styleable.SettingItemView_main_icon, 0x0);
        main_icon_height = types.getDimensionPixelSize(R.styleable.SettingItemView_main_icon_height, 0);
        main_icon_width = types.getDimensionPixelSize(R.styleable.SettingItemView_main_icon_width, 0);
        main_icon_to_title = types.getDimensionPixelSize(R.styleable.SettingItemView_main_icon_to_title_size, 0);
        main_icon_round_size = types.getDimensionPixelSize(R.styleable.SettingItemView_main_icon_round_size, 0);
        main_icon_is_circular = types.getBoolean(R.styleable.SettingItemView_main_icon_is_circular, false);
        vice_icon = types.getResourceId(R.styleable.SettingItemView_vice_icon, 0x0);
        vice_icon_height = types.getDimensionPixelSize(R.styleable.SettingItemView_vice_icon_height, 0);
        vice_icon_width = types.getDimensionPixelSize(R.styleable.SettingItemView_vice_icon_width, 0);
        vice_icon_to_tip = types.getDimensionPixelSize(R.styleable.SettingItemView_vice_icon_to_tipSize, 0);
        title_text = types.getString(R.styleable.SettingItemView_title_text);
        title_text_size = types.getDimensionPixelSize(R.styleable.SettingItemView_title_text_size,
                (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 16, getResources().getDisplayMetrics()));
        title_text_color = types.getColor(R.styleable.SettingItemView_title_text_color, Color.BLACK);
        title_to_content_size = types.getDimensionPixelSize(R.styleable.SettingItemView_title_to_content_size,
                (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 4, getResources().getDisplayMetrics()));
        content_text = types.getString(R.styleable.SettingItemView_content_text);
        content_text_size = types.getDimensionPixelSize(R.styleable.SettingItemView_content_text_size,
                (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 12, getResources().getDisplayMetrics()));
        content_text_color = types.getColor(R.styleable.SettingItemView_content_text_color, Color.GRAY);
        tip_text = types.getString(R.styleable.SettingItemView_tip_text);
        tip_text_size = types.getDimensionPixelSize(R.styleable.SettingItemView_tip_text_size,
                (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 10, getResources().getDisplayMetrics()));
        tip_text_color = types.getColor(R.styleable.SettingItemView_tip_text_color, Color.DKGRAY);
        have_slide_button = types.getBoolean(R.styleable.SettingItemView_is_slide_button, false);
        have_top_line = types.getBoolean(R.styleable.SettingItemView_setting_have_top_line, false);
        have_bottom_line = types.getBoolean(R.styleable.SettingItemView_setting_have_bottom_line, false);

        types.recycle();

        initButton();
        paint = new Paint();
        paint.setAntiAlias(true);
        paint.setFilterBitmap(true);
        paint.setStyle(Paint.Style.FILL);
    }

    protected void initButton(){
        //removeAllViews();
        if (have_slide_button){
            if (slideButton == null){
                slideButton = new SlideButton(this.getContext());
                slideButton.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
                addView(slideButton);
                setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (slideButton != null){
                            slideButton.setCheckedWithAnimation(!slideButton.isCheck());
                        }else {
                            ToastUtils.send(Utils.getContext(), "空指针");
                        }
                    }
                });
            }
        }else {
            if (slideButton != null){
                slideButton = null;
            }
        }
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        viewHeight = h;
        viewWidth = w;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        measureChildren(widthMeasureSpec, heightMeasureSpec);
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    public void setMyBitmap(Bitmap bitmap){
        this.myBitmap = bitmap;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        int paddingLeft = getPaddingLeft();
        int paddingRight = getPaddingRight();

        if (main_icon != 0x0){
//            mainBitmap = getBitmap(main_icon, main_icon_width, main_icon_height);
            mainBitmap = setNewBitmap(myBitmap, main_icon_width, main_icon_height);
            if (null == mainBitmap){
                Bitmap bitmap = BitmapFactory.decodeFile(context.getCacheDir().getAbsolutePath()+"/"+context.getSharedPreferences("UserInfo",Context.MODE_PRIVATE)
                .getString("name",null)+".jpg");
                mainBitmap = setNewBitmap(bitmap, main_icon_width, main_icon_height);
            }
            if (null != mainBitmap){
                canvas.drawBitmap(mainBitmap,
                        new Rect(0, 0, mainBitmap.getWidth(), mainBitmap.getHeight()),
                        new RectF(paddingLeft, (viewHeight - mainBitmap.getHeight())/2,
                                mainBitmap.getWidth()+paddingLeft,
                                (viewHeight - mainBitmap.getHeight())/2 + mainBitmap.getHeight()), paint);
            }else {
                mainBitmap = getBitmap(main_icon,main_icon_width,main_icon_height);
                canvas.drawBitmap(mainBitmap,
                        new Rect(0, 0, mainBitmap.getWidth(), mainBitmap.getHeight()),
                        new RectF(paddingLeft, (viewHeight - mainBitmap.getHeight())/2,
                                mainBitmap.getWidth()+paddingLeft,
                                (viewHeight - mainBitmap.getHeight())/2 + mainBitmap.getHeight()), paint);
            }
        }

        if (title_text != null && !title_text.equals("")){
            paint.setTextSize(title_text_size);
            paint.setColor(title_text_color);
            canvas.drawText(title_text, getBitmapWidth(mainBitmap)+ main_icon_to_title+paddingLeft, getBaseLine(paint, 1), paint);
        }

        if (content_text != null && !content_text.equals("")){
            paint.setTextSize(content_text_size);
            paint.setColor(content_text_color);
            canvas.drawText(content_text, getBitmapWidth(mainBitmap)+ main_icon_to_title+paddingLeft, getBaseLine(paint, 2), paint);
        }

        if (have_slide_button != true){
            if (vice_icon != 0x0){
                viceBitmap = getBitmap(vice_icon, vice_icon_width, vice_icon_height);
                canvas.drawBitmap(viceBitmap,
                        new Rect(0, 0, viceBitmap.getWidth(), viceBitmap.getHeight()),
                        new RectF(viewWidth - viceBitmap.getWidth()-paddingRight, (viewHeight - viceBitmap.getHeight())/2,
                                viewWidth-paddingRight, (viewHeight - viceBitmap.getHeight())/2 + viceBitmap.getHeight()), paint);
            }

            if (tip_text != null && !tip_text.equals("")){
                paint.setTextSize(tip_text_size);
                paint.setColor(tip_text_color);

                canvas.drawText(tip_text, viewWidth-getBitmapWidth(viceBitmap)- vice_icon_to_tip - getTextWidth(paint, tip_text) - paddingRight,
                        getBaseLine(paint, 3), paint);
            }
        }
        if (have_bottom_line){
            paint.setColor(getResources().getColor(R.color.MinorTextColor));
            canvas.drawLine(getPaddingLeft(),getHeight()-1,getWidth()-getPaddingRight(),getHeight()-1,paint);
        }
        if (have_top_line){
            paint.setColor(getResources().getColor(R.color.MinorTextColor));
            canvas.drawLine(getPaddingLeft(), 1, getWidth()-getPaddingRight(), 1, paint);
        }
    }

    private Bitmap setNewBitmap(Bitmap bitmap,int newWidth, int newHeight){
        if (null == bitmap){
            return null;
        }
        Bitmap output = Bitmap.createBitmap(bitmap.getWidth(), bitmap
                .getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(output);

        final int color = 0xff424242;
        final Paint paint = new Paint();
        final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());

        final RectF rectF = new RectF(rect);

        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(color);
        if(main_icon_is_circular){
            canvas.drawOval(rectF, paint);
        }else {
            canvas.drawRoundRect(rectF, main_icon_round_size, main_icon_round_size, paint);
        }

        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(bitmap, rect, rect, paint);

        if (newWidth == 0 && newHeight == 0){
            return bitmap;
        }
        int height = bitmap.getHeight();
        int width = bitmap.getWidth();
        float scaleWidth = ((float) (newWidth == 0 ? width : newWidth)) / width;
        float scaleHeight = ((float) (newHeight == 0 ? height : newHeight)) / height;
        Matrix matrix = new Matrix();
        matrix.postScale(scaleWidth, scaleHeight);// 使用后乘
        Bitmap newBM = Bitmap.createBitmap(output, 0, 0, width, height, matrix, false);
        return newBM;
    }

    private Bitmap getBitmap(int icon, int newWidth, int newHeight) {
        //获取bitmap
        Bitmap bitmap = ((BitmapDrawable)getResources().getDrawable(icon)).getBitmap();
        if (bitmap == null) {
            return null;
        }

        Bitmap output = Bitmap.createBitmap(bitmap.getWidth(), bitmap
                .getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(output);

        final int color = 0xff424242;
        final Paint paint = new Paint();
        final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());

        final RectF rectF = new RectF(rect);

        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(color);
        if(main_icon_is_circular){
            canvas.drawOval(rectF, paint);
        }else {
            canvas.drawRoundRect(rectF, main_icon_round_size, main_icon_round_size, paint);
        }

        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(bitmap, rect, rect, paint);

        if (newWidth == 0 && newHeight == 0){
            return bitmap;
        }
        int height = bitmap.getHeight();
        int width = bitmap.getWidth();
        float scaleWidth = ((float) (newWidth == 0 ? width : newWidth)) / width;
        float scaleHeight = ((float) (newHeight == 0 ? height : newHeight)) / height;
        Matrix matrix = new Matrix();
        matrix.postScale(scaleWidth, scaleHeight);// 使用后乘
        Bitmap newBM = Bitmap.createBitmap(output, 0, 0, width, height, matrix, false);
        return newBM;
    }

    private int getBitmapWidth(Bitmap bitmap){
        if (bitmap != null){
            return bitmap.getWidth();
        }
        return 0;
    }

    private int getTextWidth(Paint paint, String str) {
        int iRet = 0;
        if (str != null && str.length() > 0) {
            int len = str.length();
            float[] widths = new float[len];
            paint.getTextWidths(str, widths);
            for (int j = 0; j < len; j++) {
                iRet += (int) Math.ceil(widths[j]);
            }
        }
        return iRet;
    }

    private int getTextHeight(String text, Paint paint) {
        Rect rect = new Rect();
        paint.getTextBounds(text,0,text.length(), rect);
        return rect.height();
    }

    private int getBaseLine(Paint paint, int type){
        int allSize = 0;
        int titleTextHeight = 0;
        int contentTextHeight = 0;
        int titleToContentSize = 0;
        if (title_text != null){
            titleTextHeight = getTextHeight(title_text, paint);
        }

        if (content_text != null){
            contentTextHeight = getTextHeight(content_text, paint);
        }

        if (title_text != null && content_text != null){
            titleToContentSize = title_to_content_size;
            Log.w("123-----------", "getBaseLine: "+title_to_content_size);
        }

        allSize = titleTextHeight + contentTextHeight + titleToContentSize;
        Rect targetRect = new Rect();
        switch (type){
            case 1:
                targetRect = new Rect(0, (viewHeight - allSize)/2, 100, (viewHeight - allSize)/2+titleTextHeight);
                break;
            case 2:
                targetRect = new Rect(0, viewHeight - (viewHeight - allSize)/2 - contentTextHeight, 100, viewHeight - (viewHeight - allSize)/2);
                break;
            case 3:
                targetRect = new Rect(0, 0, 100, viewHeight);
                break;

        }

        Paint.FontMetricsInt fontMetrics = paint.getFontMetricsInt();
        return (targetRect.bottom + targetRect.top - fontMetrics.bottom - fontMetrics.top) / 2;
    }

    private int dip2px(float dpValue) {
        float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    public void setMain_icon(int main_icon) {
        this.main_icon = main_icon;
    }

    public void removeMain_icon() {
        this.main_icon = 0x0;
    }

    public void setMain_icon_height(int main_icon_height) {
        this.main_icon_height = dip2px(main_icon_height);
    }

    public void setMain_icon_width(int main_icon_width) {
        this.main_icon_width = dip2px(main_icon_width);
    }

    public void setMain_icon_to_title(int main_icon_to_title) {
        this.main_icon_to_title = main_icon_to_title;
    }

    public void setMain_icon_round_size(int main_icon_round_size) {
        this.main_icon_round_size = main_icon_round_size;
    }

    public void setMain_icon_is_circular(boolean main_icon_is_circular) {
        this.main_icon_is_circular = main_icon_is_circular;
    }

    public void setVice_icon(int vice_icon) {
        this.vice_icon = vice_icon;
    }

    public void removeVice_icon() {
        this.vice_icon = 0x0;
    }

    public void setVice_icon_height(int vice_icon_height) {
        this.vice_icon_height = dip2px(vice_icon_height);
    }

    public void setVice_icon_width(int vice_icon_width) {
        this.vice_icon_width = dip2px(vice_icon_width);
    }

    public void setVice_icon_to_tip(int vice_icon_to_tip) {
        this.vice_icon_to_tip = vice_icon_to_tip;
    }

    public String getTitle_text() {
        return title_text;
    }

    public void setTitle_text(String title_text) {
        this.title_text = title_text;
    }

    public void removeTitle_text() {
        this.title_text = null;
    }

    public void setTitle_text_size(int title_text_size) {
        this.title_text_size = title_text_size;
    }

    public void setTitle_text_color(int title_text_color) {
        this.title_text_color = title_text_color;
    }

    public void setTitle_to_content_size(int title_to_content_size) {
        this.title_to_content_size = title_to_content_size;
    }

    public String getContent_text() {
        return content_text;
    }

    public void setContent_text(String content_text) {
        this.content_text = content_text;
    }

    public void removeContent_text() {
        this.content_text = null;
    }

    public void setContent_text_size(int content_text_size) {
        this.content_text_size = content_text_size;
    }

    public void setContent_text_color(int content_text_color) {
        this.content_text_color = content_text_color;
    }

    public String getTip_text() {
        return tip_text;
    }

    public void setTip_text(String tip_text) {
        this.tip_text = tip_text;
    }

    public void removeTip_text() {
        this.tip_text = null;
    }

    public void setTip_text_size(int tip_text_size) {
        this.tip_text_size = tip_text_size;
    }

    public void setTip_text_color(int tip_text_color) {
        this.tip_text_color = tip_text_color;
    }

    public void have_slide_button(boolean have_slide_button){
        this.have_slide_button = have_slide_button;
        initButton();
    }

    public void is_check(boolean is_slide){
        if (slideButton != null){
            slideButton.setCheckedWithAnimation(is_slide);
        }
    }

    public void updata(){
        invalidate();
    }


}
