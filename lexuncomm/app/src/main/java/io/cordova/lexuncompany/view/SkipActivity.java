package io.cordova.lexuncompany.view;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;

import io.cordova.lexuncompany.R;

/**
 * Created by JasonYao on 2018/4/8.
 */

public class SkipActivity extends BaseActivity {
    private static final String TAG = "SkipActivity---";

    private boolean hasJump = false;  //标记是否已经跳转

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_skip);

        startIntent(getIntent());
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (hasJump) {
            Intent intent = new Intent(this, CardContentActivity.class);
            startActivity(intent);

            finish();
        }

        hasJump = !hasJump;
    }

    //根据拦截的url跳转页面
    private void startIntent(Intent intentUrl) {
        ActivityManager activityManager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        activityManager.moveTaskToFront(getTaskId(), ActivityManager.MOVE_TASK_WITH_HOME);
        //在浏览器获取到需要制定打开app的某个页面
        if (intentUrl.getScheme() != null) {
            String action = intentUrl.getAction();
            if (Intent.ACTION_VIEW.equals(action)) {
                Uri uri = intentUrl.getData();
                if (uri != null) {
                    /*String host = uri.getHost();
                    String dataString = intentUrl.getDataString();
                    String bookId = uri.getQueryParameter("bookId");
                    String path = uri.getPath();
                    String queryString = uri.getQueryParameter("code");*/
                    startActivity(new Intent(this, CardContentActivity.class));
                }
            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        hasJump = false;
    }
}
