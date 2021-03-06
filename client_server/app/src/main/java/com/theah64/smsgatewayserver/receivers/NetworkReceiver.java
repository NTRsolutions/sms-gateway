package com.theah64.smsgatewayserver.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.theah64.smsgatewayserver.async.FCMSynchronizer;
import com.theah64.smsgatewayserver.databases.SMSStatuses;
import com.theah64.smsgatewayserver.models.SMSStatus;
import com.theah64.smsgatewayserver.models.Server;
import com.theah64.smsgatewayserver.utils.APIRequestGateway;
import com.theah64.smsgatewayserver.utils.NetworkUtils;
import com.theah64.smsgatewayserver.utils.PermissionUtils;
import com.theah64.smsgatewayserver.utils.PrefUtils;

import java.util.List;


public class NetworkReceiver extends BroadcastReceiver implements PermissionUtils.Callback {

    private static final String X = NetworkReceiver.class.getSimpleName();
    private Context context;

    public NetworkReceiver() {
    }

    @Override
    public void onReceive(final Context context, Intent intent) {
        this.context = context;
        new PermissionUtils(context, this, null).begin();
    }

    private static void doNormalWork(final Context context) {

        if (NetworkUtils.hasNetwork(context)) {

            if (!PrefUtils.getInstance(context).getBoolean(Server.KEY_IS_FCM_SYNCED)) {
                new APIRequestGateway(context, new APIRequestGateway.APIRequestGatewayCallback() {

                    @Override
                    public void onReadyToRequest(String apiKey) {
                        new FCMSynchronizer(context, apiKey).execute();

                        //Syncing sms statuses
                        final List<SMSStatus> statusList = SMSStatuses.getInstance(context).getAll();
                        SMSStatus.sync(context,statusList);
                    }

                    @Override
                    public void onFailed(String reason) {
                        Log.e(X, "Reason: " + reason);
                    }
                }, false);

            }
        }
    }

    @Override
    public void onAllPermissionGranted() {
        doNormalWork(context);
    }

    @Override
    public void onPermissionDenial() {
        Log.e(X, "Permission denied");
    }
}
