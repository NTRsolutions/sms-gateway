package com.theah64.sg.api_server.servlets;

import com.theah64.sg.api_server.database.tables.BaseTable;
import com.theah64.sg.api_server.database.tables.Recipients;
import com.theah64.sg.api_server.database.tables.SMSRequests;
import com.theah64.sg.api_server.database.tables.Servers;
import com.theah64.sg.api_server.models.SMSRequest;
import com.theah64.sg.api_server.models.Server;
import com.theah64.sg.api_server.utils.APIResponse;
import com.theah64.sg.api_server.utils.FCMUtils;
import com.theah64.sg.api_server.utils.Request;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import javax.servlet.annotation.WebServlet;

/**
 * Created by theapache64 on 29/11/16,9:54 AM.
 */
@WebServlet(urlPatterns = {AdvancedBaseServlet.VERSION_CODE + "/send_sms"})
public class SendSMSServlet extends AdvancedBaseServlet {

    @Override
    protected boolean isSecureServlet() {
        return true;
    }

    @Override
    protected String[] getRequiredParameters() {
        return new String[]{Recipients.TABLE_NAME, SMSRequests.COLUMN_MESSAGE};
    }

    @Override
    protected void doAdvancedPost() throws Request.RequestException, BaseTable.InsertFailedException, JSONException, BaseTable.UpdateFailedException {

        final JSONArray jaRecipients = new JSONArray(getStringParameter(Recipients.TABLE_NAME));
        if (jaRecipients.length() > 0) {
            final String message = getStringParameter(SMSRequests.COLUMN_MESSAGE);
            final Server smsServer = Servers.getInstance().getLeastUsedServer();

            if (smsServer != null) {

                //Adding request
                final String smsReqId = SMSRequests.getInstance().addv3(new SMSRequest(message, smsServer.getId(), getHeaderSecurity().getUserId()));

                //Adding recipients
                final JSONArray jaAdvancedRecipients = Recipients.getInstance().add(smsReqId, jaRecipients);

                System.out.println(jaAdvancedRecipients);

                //Sending sms
                final JSONObject joFcmResp = FCMUtils.sendSMS(jaAdvancedRecipients, message, smsServer.getFcmId());

                if (joFcmResp != null) {

                    System.out.println("FCM Says: " + joFcmResp);

                    final boolean isSuccess = joFcmResp.getInt("success") == 1;

                    if (isSuccess) {
                        final JSONObject joData = new JSONObject();
                        joData.put(Recipients.COLUMN_SMS_REQUEST_ID, smsReqId);

                        getWriter().write(new APIResponse("SMS Sent", joData).getResponse());
                    } else {
                        final String failureReason = joFcmResp.getJSONArray("results").getJSONObject(0).getString("error");
                        throw new Request.RequestException("SMS Server error: " + failureReason);
                    }

                } else {
                    throw new Request.RequestException("SMS Failed to send");
                }

            } else {
                throw new Request.RequestException("No active server found");
            }

        } else {
            throw new Request.RequestException("There must be at least one recipient");
        }

    }
}
