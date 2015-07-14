package com.mathedia.tobi.feuerwehrhrtlingen;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.loopj.android.http.RequestParams;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URI;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class MainActivity extends AppCompatActivity {
    ProgressDialog prgDialog;
    RequestParams params = new RequestParams();
    GoogleCloudMessaging gcmObj;
    Context applicationContext;
    String regId = "";
    String userName = "";
    int userStatus = 0;

    private final static int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;

    AsyncTask<Void, Void, String> createRegIdTask;

    public static final String REG_ID = "regId";
    public static final String EMAIL_ID = "eMailId";
    EditText emailET;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        applicationContext = getApplicationContext();
        emailET = (EditText) findViewById(R.id.email);

        prgDialog = new ProgressDialog(this);
        // Set Progress Dialog Text
        prgDialog.setMessage("Please wait...");
        // Set Cancelable as False
        prgDialog.setCancelable(false);

        SharedPreferences prefs = getSharedPreferences("UserDetails",
                Context.MODE_PRIVATE);
        String registrationId = prefs.getString(REG_ID, "");

        if (!TextUtils.isEmpty(registrationId)) {
            Intent i = new Intent(applicationContext, HomeActivity.class);
            i.putExtra("regId", registrationId);
            startActivity(i);
            finish();
        }
    }

    public void login(View view) {
        String username = emailET.getText().toString();
        String userS = "0";
        try {
            userS = new SigninActivity(this).execute(username).get(3L, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
        } catch (ExecutionException e) {
        } catch (TimeoutException e) {
        }
        userStatus = Integer.parseInt(userS);
        switch (userStatus) {
            case 0:
                Toast.makeText(applicationContext, "Anmelden fehlgeschlagen,\n keine Internetverbindung?", Toast.LENGTH_LONG).show();
                break;
            case 3:
                Toast.makeText(applicationContext, "Anmelden fehlgeschlagen,\n falscher Benutzername?", Toast.LENGTH_LONG).show();
                break;
        }

        if (userStatus == 1 || userStatus == 2) {
            if (checkPlayServices()) {
                userName = username;
                registerInBackground(username);
            }
        }
    }


//    public void RegisterUser(View view) {
//        String emailID = emailET.getText().toString();
//
//        if (!TextUtils.isEmpty(emailID) && Utility.validate(emailID)) {
//                if (checkPlayServices()) {
//                    registerInBackground(emailID);
//                }
//        }
//            // When Email is invalid
//        else {
//                Toast.makeText(applicationContext, "Please enter valid email",
//                        Toast.LENGTH_LONG).show();
//        }
//    }

    private void registerInBackground(final String emailID) {
        new AsyncTask<Void, Void, String>() {
            @Override
            protected String doInBackground(Void... params) {
                String msg = "";
                try {
                    if (gcmObj == null) {
                        gcmObj = GoogleCloudMessaging
                                .getInstance(applicationContext);
                    }
                    regId = gcmObj
                            .register(ApplicationConstants.GOOGLE_PROJ_ID);
                    msg = "Registration ID :" + regId;
                    System.out.println(msg);

                } catch (IOException ex) {
                    msg = "Error :" + ex.getMessage();
                }
                return msg;
            }

            @Override
            protected void onPostExecute(String msg) {
                if (!TextUtils.isEmpty(regId)) {
                    storeRegIdinSharedPref(applicationContext, regId, emailID);
                    Toast.makeText(
                            applicationContext,
                            "Registered with GCM Server successfully.\n\n"
                                    + msg, Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(
                            applicationContext,
                            "Reg ID Creation Failed.\n\nEither you haven't enabled Internet or GCM server is busy right now. Make sure you enabled Internet and try registering again after some time."
                                    + msg, Toast.LENGTH_LONG).show();
                }
            }
        }.execute(null, null, null);
    }

    private void storeRegIdinSharedPref(Context context, String regId,
                                        String emailID) {
        SharedPreferences prefs = getSharedPreferences("UserDetails",
                Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(REG_ID, regId);
        editor.putString(EMAIL_ID, emailID);
        editor.commit();
        storeRegIdinServer();

    }

    private void storeRegIdinServer() {
        String username = userName;
        String regid = regId;
        String userS = "0";
        prgDialog.show();
        params.put("regId", regId);
        params.put("username", userName);
        try {
            userS = new RegisterPost(this).execute(username, regid).get(3L, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
        } catch (ExecutionException e) {
        } catch (TimeoutException e) {
        }
        System.out.println(userS);
//            String data  = URLEncoder.encode("username", "UTF-8") + "=" + URLEncoder.encode(userName, "UTF-8");
//            data += "&" + URLEncoder.encode("regid", "UTF-8") + "=" + URLEncoder.encode(regId, "UTF-8");
//
//            System.out.println(data);
//
//            URL url = new URL(link);
//            URLConnection conn = url.openConnection();
//
//            conn.setDoOutput(true);
//            OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());
//
//            wr.write( data );
//            wr.flush();
//
//            BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
//
//            StringBuilder sb = new StringBuilder();
//            String line = null;
//
//            // Read Server Response
//            while((line = reader.readLine()) != null)
//            {
//                sb.append(line);
//                break;
//            }
//            String rueckgabe = sb.toString();
//            System.out.println(rueckgabe);


        prgDialog.hide();
        if (prgDialog != null) {
            prgDialog.dismiss();
        }
        Toast.makeText(applicationContext,
                "Reg Id shared successfully with Web App ",
                Toast.LENGTH_LONG).show();
        Intent i = new Intent(applicationContext,
                HomeActivity.class);
        i.putExtra("regId", regId);
        startActivity(i);
        finish();
    }

    // Make RESTful webservice call using AsyncHttpClient object
//        AsyncHttpClient client = new AsyncHttpClient();
//        client.post(ApplicationConstants.APP_SERVER_URL, params,
//                new AsyncHttpResponseHandler() {
//                    // When the response returned by REST has Http
//                    // response code '200'
//                    @Override
//                    public void onSuccess(String response) {
//                        // Hide Progress Dialog
//                        prgDialog.hide();
//                        if (prgDialog != null) {
//                            prgDialog.dismiss();
//                        }
//                        Toast.makeText(applicationContext,
//                                "Reg Id shared successfully with Web App ",
//                                Toast.LENGTH_LONG).show();
//                        Intent i = new Intent(applicationContext,
//                                HomeActivity.class);
//                        i.putExtra("regId", regId);
//                        startActivity(i);
//                        finish();
//                    }
//
//                    // When the response returned by REST has Http
//                    // response code other than '200' such as '404',
//                    // '500' or '403' etc
//                    @Override
//                    public void onFailure(int statusCode, Throwable error,
//                                          String content) {
//                        // Hide Progress Dialog
//                        prgDialog.hide();
//                        if (prgDialog != null) {
//                            prgDialog.dismiss();
//                        }
//                        // When Http response code is '404'
//                        if (statusCode == 404) {
//                            Toast.makeText(applicationContext,
//                                    "Requested resource not found",
//                                    Toast.LENGTH_LONG).show();
//                        }
//                        // When Http response code is '500'
//                        else if (statusCode == 500) {
//                            Toast.makeText(applicationContext,
//                                    "Something went wrong at server end",
//                                    Toast.LENGTH_LONG).show();
//                        }
//                        // When Http response code other than 404, 500
//                        else {
//                            Toast.makeText(
//                                    applicationContext,
//                                    "Unexpected Error occcured! [Most common Error: Device might "
//                                            + "not be connected to Internet or remote server is not up and running], check for other errors as well",
//                                    Toast.LENGTH_LONG).show();
//                        }
//                    }
//                });

    private boolean checkPlayServices() {
        int resultCode = GooglePlayServicesUtil
                .isGooglePlayServicesAvailable(this);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
                GooglePlayServicesUtil.getErrorDialog(resultCode, this,
                        PLAY_SERVICES_RESOLUTION_REQUEST).show();
            } else {
                Toast.makeText(
                        applicationContext,
                        "Google Play service nicht installiert, App funktioniert nicht richtig",
                        Toast.LENGTH_LONG).show();
                finish();
            }
            return false;
        } else {
            return true;
        }
    }
}

//    @Override
//    protected void onResume() {
//        super.onResume();
//        checkPlayServices();
//    }
//
//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.menu_main, menu);
//        return true;
//    }
//
//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        // Handle action bar item clicks here. The action bar will
//        // automatically handle clicks on the Home/Up button, so long
//        // as you specify a parent activity in AndroidManifest.xml.
//        int id = item.getItemId();
//
//        //noinspection SimplifiableIfStatement
//        if (id == R.id.action_settings) {
//            return true;
//        }
//
//        return super.onOptionsItemSelected(item);
//    }
