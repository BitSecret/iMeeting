package com.alen.MeetingRoom.ui.splash;

import android.content.Intent;
import android.os.Handler;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.alen.MeetingRoom.R;
import com.alen.MeetingRoom.base.BaseActivity;
import com.alen.MeetingRoom.ui.MainActivity;

import butterknife.BindView;

public class SplashActivity extends BaseActivity {

    @BindView(R.id.tv_skip)
    TextView tv_skip;

    private Handler handler;
    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
            jump();
        }
    };

    @Override
    public int getLayout() {
        return R.layout.activity_splash;
    }

    @Override
    public void init(Bundle savedInstanceState) {
        if (!this.isTaskRoot()) {
            Intent intent = getIntent();
            if (intent != null) {
                String action = intent.getAction();
                if (intent.hasCategory(Intent.CATEGORY_LAUNCHER) && Intent.ACTION_MAIN.equals(action)) {
                    finish();
                    return;
                }
            }
        }

        handler = new Handler();
        handler.postDelayed(runnable, 3000);
        tv_skip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                jump();
            }
        });
    }

    private void jump(){
        tv_skip.setClickable(false);
        handler.removeCallbacks(runnable);
        startActivity(new Intent(mContext, MainActivity.class));
        finish();
    }
}
