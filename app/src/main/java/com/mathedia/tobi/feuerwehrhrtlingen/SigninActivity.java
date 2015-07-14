package com.mathedia.tobi.feuerwehrhrtlingen;


import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URI;
import java.net.URL;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import android.content.Context;
import android.os.AsyncTask;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Created by Tobi on 30.05.2015.
 */
public class SigninActivity extends AsyncTask<String, Void, String> {
    private TextView statusField, roleField;
    private Context context;

    public SigninActivity(Context context) {
        this.context = context;
    }

    protected void onPreExecute() {

    }

    @Override
    protected String doInBackground(String... arg0) {
        try {
            String username = (String) arg0[0];
            String link = "http://mathedia.com/fw/applogin.php?username=" + username;

            URL url = new URL(link);
            HttpClient client = new DefaultHttpClient();
            HttpGet request = new HttpGet();
            request.setURI(new URI(link));
            HttpResponse response = client.execute(request);
            BufferedReader in = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));

            StringBuffer sb = new StringBuffer("");
            String line = "";

            while ((line = in.readLine()) != null) {
                sb.append(line);
                break;
            }
            in.close();
            return sb.toString();
        } catch (Exception e) {
            return "0";
        }
    }
}
