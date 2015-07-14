package com.mathedia.tobi.feuerwehrhrtlingen;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * Created by Tobi on 28.05.2015.
 */
public class HomeActivity extends AppCompatActivity {
    TextView msgET, usertitleET;
    String[] names;
    int[] states;

    private final static int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        // Intent Message sent from Broadcast Receiver
        String str = getIntent().getStringExtra("msg");

        // Get Email ID from Shared preferences
        SharedPreferences prefs = getSharedPreferences("UserDetails",
                Context.MODE_PRIVATE);
        String eMailId = prefs.getString("eMailId", "");
        // Set Title
        usertitleET = (TextView) findViewById(R.id.usertitle);

        if (!checkPlayServices()) {
            Toast.makeText(
                    getApplicationContext(),
                    "This device doesn't support Play services, App will not work normally",
                    Toast.LENGTH_LONG).show();
        }

        usertitleET.setText("Angemeldet als " + eMailId.toUpperCase());
        // When Message sent from Broadcase Receiver is not empty
        if (str != null) {
            msgET = (TextView) findViewById(R.id.message);
            msgET.setText(str);
        }
    }

    private boolean checkPlayServices() {
        int resultCode = GooglePlayServicesUtil
                .isGooglePlayServicesAvailable(this);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
                GooglePlayServicesUtil.getErrorDialog(resultCode, this,
                        PLAY_SERVICES_RESOLUTION_REQUEST).show();
            } else {
                Toast.makeText(
                        getApplicationContext(),
                        "This device doesn't support Play services, App will not work normally",
                        Toast.LENGTH_LONG).show();
                finish();
            }
            return false;
        } else {
            return true;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        checkPlayServices();
    }

    public void showComradesState(View view) {
        Intent intent = new Intent(this, ComradesStateActivity.class);
        startActivity(intent);
    }

    public void getStatesFromDatabase(View view) {
        ArrayList<Comrade> comrades = new ArrayList<Comrade>();
        String result = "";

        try {
            result = new GetDataActivity(this).execute("A").get(3L, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
        } catch (ExecutionException e) {
        } catch (TimeoutException e) {
        }

        try {
            JSONArray jArray = new JSONArray(result);
            for (int i = 0; i < jArray.length(); i++) {
                JSONObject json = jArray.getJSONObject(i);
                comrades.add(new Comrade(json.getInt("ID"), json.getString("Vorname"), json.getString("Nachname"), json.getInt("Status"), json.getInt("Admin")));
            }
        } catch (Exception e) {
            System.out.println("Fehler beim JSON parsen");
        }

        String s = "";
        for (Comrade comrade : comrades) {
            System.out.println(comrade.getNachname());
            s = s + comrade.getNachname();
            msgET = (TextView) findViewById(R.id.message);
            msgET.setText(s);
        }
    }
}
