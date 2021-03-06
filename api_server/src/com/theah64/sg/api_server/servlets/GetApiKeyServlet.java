package com.theah64.sg.api_server.servlets;


import com.theah64.sg.api_server.database.tables.BaseTable;
import com.theah64.sg.api_server.database.tables.Preference;
import com.theah64.sg.api_server.database.tables.Users;
import com.theah64.sg.api_server.models.User;
import com.theah64.sg.api_server.utils.*;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Created by theapache64 on 15/10/16.
 */
@WebServlet(urlPatterns = {AdvancedBaseServlet.VERSION_CODE + "/get_api_key"})
public class GetApiKeyServlet extends AdvancedBaseServlet {


    private static final int API_KEY_LENGTH = 10;

    @Override
    protected boolean isSecureServlet() {
        return false;
    }

    @Override
    protected String[] getRequiredParameters() {
        return new String[]{Users.COLUMN_EMAIL};
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException, ServletException {
        super.doPost(req, resp);
    }

    @Override
    protected void doAdvancedPost() throws RequestException, BaseTable.InsertFailedException {

        final String email = getStringParameter(Users.COLUMN_EMAIL);

        if (CommonUtils.isValidEmail(email)) {

            //Checking if the email already has an api_key.
            User user = Users.getInstance().get(Users.COLUMN_EMAIL, email);
            final boolean isAdded;

            if (user != null) {
                isAdded = true;
            } else {
                final String apiKey = RandomString.getNewApiKey(API_KEY_LENGTH);
                user = new User(null, email, apiKey);
                isAdded = Users.getInstance().add(user);
            }

            if (isAdded) {

                if (MailHelper.sendApiKey(user.getEmail(), user.getApiKey())) {

                    final String message = String.format("Hey, New user established\n\nUser: %s\n\nThat's it. :) ", user.toString());
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            //Sending mail to admin about the new member join
                            MailHelper.sendMail(Preference.getInstance().getString(Preference.KEY_ADMIN_EMAIL), "SG - New user joined", message);
                        }
                    }).start();

                    getWriter().write(new APIResponse("API key sent to " + email).getResponse());
                } else {
                    getWriter().write(new APIResponse("Failed to generate new api key for " + email).getResponse());
                }

            } else {
                throw new RequestException("Failed to add new user");
            }


        } else {
            throw new RequestException("Invalid email");
        }
    }
}
