package com.alen.MeetingRoom.utils;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.alen.MeetingRoom.R;


public class DecorationUtils {

    //默认分隔线
    public static class Line extends RecyclerView.ItemDecoration{
        private Paint paint;
        private int height;
        public Line() {
            this(R.color.LineColor, 0.5f);
        }

        public Line(int color, float size) {
            paint = new Paint();
            paint.setColor(Utils.getContext().getResources().getColor(color));
            height = ConvertUtils.dp2px(size);
        }

        @Override
        public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
            super.getItemOffsets(outRect, view, parent, state);
            outRect.bottom = height;
        }

        @Override
        public void onDraw(Canvas c, RecyclerView parent, RecyclerView.State state) {
            int childCount = parent.getChildCount();
            int left = parent.getPaddingLeft();
            int right = parent.getWidth() - parent.getPaddingRight();

            for (int i = 0; i < childCount - 1; i++) {
                View view = parent.getChildAt(i);
                float top = view.getBottom();
                float bottom = view.getBottom() + height;
                c.drawRect(left, top, right, bottom, paint);
            }
        }
    }
}
