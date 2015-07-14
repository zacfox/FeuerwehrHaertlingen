package com.mathedia.tobi.feuerwehrhrtlingen;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URI;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import android.content.Context;
import android.os.AsyncTask;
import android.widget.TextView;

/**
 * Created by Tobi on 31.05.2015.
 * Schreibt die RegId in die Datenbank
 */
public class RegisterPost extends AsyncTask<String, Void, String> {
    private Context context;

    public RegisterPost(Context context) {
        this.context = context;
    }

    protected void onPreExecute() {

    }

    @Override
    protected String doInBackground(String... arg0) {
        try {
            String username = (String) arg0[0];
            String regid = (String) arg0[1];
            String link = "http://mathedia.com/fw/appregister.php";
            String data = URLEncoder.encode("username", "UTF-8") + "=" + URLEncoder.encode(username, "UTF-8");
            data += "&" + URLEncoder.encode("regid", "UTF-8") + "=" + URLEncoder.encode(regid, "UTF-8");

            System.out.println(data);

            URL url = new URL(link);
            URLConnection conn = url.openConnection();

            conn.setDoOutput(true);
            OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());

            wr.write(data);
            wr.flush();

            BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));

            StringBuilder sb = new StringBuilder();
            String line = null;

            // Read Server Response
            while ((line = reader.readLine()) != null) {
                sb.append(line);
                break;
            }
            String rueckgabe = sb.toString();
            System.out.println(rueckgabe);
            return rueckgabe;
        } catch (Exception e) {
            return "0";
        }
    }
}
