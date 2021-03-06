package com.theah64.smsgatewayserver.activities.base;

import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.theah64.smsgatewayserver.utils.PermissionUtils;

/**
 * Created by theapache64 on 6/1/17.
 */

public abstract class PermissionActivity extends AppCompatActivity implements PermissionUtils.Callback {

    private static final String X = PermissionActivity.class.getSimpleName();

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == PermissionUtils.RQ_CODE_ASK_PERMISSION) {

            Log.d(X, "Grant result length: " + grantResults.length);

            boolean isAllPermissionGranted = true;
            for (final int grantResult : grantResults) {
                if (grantResult != PackageManager.PERMISSION_GRANTED) {
                    isAllPermissionGranted = false;
                    break;
                }
            }

            if (isAllPermissionGranted) {
                onAllPermissionGranted();
            } else {
                onPermissionDenial();
            }
        }
    }
}
