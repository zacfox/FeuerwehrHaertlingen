package com.mathedia.tobi.feuerwehrhrtlingen;

import android.content.Context;
import android.os.AsyncTask;

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

/**
 * Created by Tobi on 10.06.2015.
 */
public class GetDataActivity extends AsyncTask<String, Void, String> {

    public GetDataActivity(Context context) {
    }

    @Override
    protected String doInBackground(String... params) {
        String result = "";
        InputStream isr = null;

        try {
            HttpClient httpclient = new DefaultHttpClient();
            HttpPost httppost = new HttpPost("http://mathedia.com/fw/appstatus.php");
            HttpResponse response = httpclient.execute(httppost);
            HttpEntity entitiy = response.getEntity();
            isr = entitiy.getContent();
        } catch (Exception e) {
            System.out.println("Datenbank fehler");
        }
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(isr)); //evtl utf8 codieren
            StringBuilder sb = new StringBuilder();
            String line = null;
            while ((line = reader.readLine()) != null) {
                sb.append(line + "\n");
            }
            isr.close();
            result = sb.toString();
        } catch (Exception e) {
            System.out.println("response to string umwandlung fehler");
        }
        return result;
    }
}
