package com.example.osku.fuksipassi;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import com.github.barteksc.pdfviewer.PDFView;

import org.apache.commons.io.IOUtils;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;

import static android.widget.Toast.makeText;

/**
 * Created by Osku on 21.7.2018.
 */

public class CalendarView extends AppCompatActivity {
    PDFView mPdfView;
    String urlAddress;
    ProgressDialog progDialog;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calendar);
        mPdfView = (PDFView) findViewById(R.id.pdfView);
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        try {
            if (bundle.containsKey("virallinen")) {
                urlAddress = "https://people.uta.fi/~on428246/Fuksipassi/officialCalendar.pdf";
                // mPdfView.fromAsset("officialCalendar.pdf").load();
            } else {
                urlAddress = "https://people.uta.fi/~on428246/Fuksipassi/partyCalendar.pdf";
                //mPdfView.fromAsset("partyCalendar.pdf").load();
            }
        } catch (NullPointerException ex){
            Log.d("Error:","Null pointer exception when fetching url from bundle." );
            urlAddress = "https://people.uta.fi/~on428246/Fuksipassi/officialCalendar.pdf";
        }
        new RetrievePDFBytes().execute(urlAddress);
    }
    // Class for loading calendars from server.
    class RetrievePDFBytes extends AsyncTask<String,Void,byte []>
    {
        /*@Override
        protected void onPreExecute() {
            super.onPreExecute();
            progDialog = new ProgressDialog(getApplicationContext());
            progDialog.setMessage("Loading...");
            progDialog.setIndeterminate(false);
            progDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            progDialog.setCancelable(true);
            progDialog.show();
        }*/
        @Override
        protected byte [] doInBackground(String... strings) {
            InputStream inputStream = null;
            try {
                URL url = new URL(strings[0]);
                HttpURLConnection urlConnection = (HttpURLConnection)url.openConnection();
                if(urlConnection.getResponseCode() == 200){
                    inputStream = new BufferedInputStream(urlConnection.getInputStream());
                }
            } catch (IOException e){
                e.printStackTrace();
                return null;
            }
            try {
                return IOUtils.toByteArray(inputStream);
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
        }
        @Override
        protected void onPostExecute (byte[] bytes){
            if(bytes != null){
                mPdfView.fromBytes(bytes).load();
                //progDialog.dismiss();
            } else if(!isNetworkAvailable()){
                Toast t1 = makeText(getApplicationContext(),"Tarkista internetyhteys!",Toast.LENGTH_LONG);
                t1.show();
            }

        }
    }

    // Method checks if there is network available and returns the result.
    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(getApplicationContext().CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    // Inflates menu and sets onClick methods on the menu items.
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return true;
    }
    Intent intent;
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // action with ID action_settings was selected
            case R.id.action_calendar1:
                intent = new Intent(getApplicationContext(),CalendarView.class);
                intent.putExtra("virallinen",1);
                finish();
                startActivity(intent);
                break;
            case R.id.action_calendar2:
                intent = new Intent(getApplicationContext(),CalendarView.class);
                intent.putExtra("pedago",1);
                finish();
                startActivity(intent);
                break;
            case R.id.action_summary:
                intent = new Intent(getApplicationContext(),SummaryActivity.class);
                finish();
                startActivity(intent);
                break;
            case R.id.action_info:
                intent = new Intent(getApplicationContext(),InstructionsActivity.class);
                finish();
                startActivity(intent);
                break;
            default:
                break;
        }
        return true;
    }
}
