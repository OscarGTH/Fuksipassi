package com.example.osku.fuksipassi;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.github.barteksc.pdfviewer.PDFView;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.journeyapps.barcodescanner.BarcodeEncoder;

import java.text.DecimalFormat;
import java.util.List;

/**
 * Created by Osku on 2.8.2018.
 * Class contains the summary of completed challenges and the QR-code for tutors to read.
 */

public class SummaryActivity extends AppCompatActivity{
    // Initialize text views
    TextView totalCount;
    TextView verdict;
    ImageView qrImage;
    String badgeType;
    int challengeAmount;
    int completedAmount;
    double percentage;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_summary);
        totalCount = findViewById(R.id.challengesDone_txt);
        verdict = findViewById(R.id.verdict_txt);
        qrImage = findViewById(R.id.qr_image);
        // Getting the lists of challenges.
        initializeTextViews();
        createQRcode();
    }

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
            // Launches the activity_calendar containing official info.
            case R.id.action_calendar1:
                intent = new Intent(getApplicationContext(),CalendarView.class);
                intent.putExtra("virallinen",0);
                finish();
                startActivity(intent);
                break;
            // Launches the activity_calendar activity and displays unofficial activity_calendar.
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

    // Reads the number of total and completed challenges, and displays them on their own text views.
    public void initializeTextViews(){
        TempDataReader reader = new TempDataReader(getApplicationContext());
        List<List<String>> challengeList = reader.readFile("challenges", true);
        List<List<String>> totalList = reader.readFile("completed", false);
        challengeAmount = challengeList.get(0).size();
        completedAmount = totalList.get(0).size();
        double c1 = challengeAmount;
        double c2 = completedAmount;
        percentage = c2 / c1;
        totalCount.setText("Suoritetut: " +  completedAmount + '/' + challengeAmount);
        // Creating a number format to limit the decimals on double to zero.
        DecimalFormat numberFormat = new DecimalFormat("#");
        // Set the final verdict by calculating if the user has completed more than 50% of total challenges.
        if(percentage >= 0.5){
            verdict.setTextColor(Color.GREEN);
            verdict.setText("Tehtäviä tehty " + numberFormat.format(percentage * 100) + "%");
        } else {
            verdict.setTextColor(Color.RED);
            verdict.setText("Tehtäviä tehty " + numberFormat.format(percentage * 100) + "%");
        }
    }

    // Creates a QR code containing data about badge type and number of completed challenges.
    public void createQRcode(){
        if(percentage < 0.5){
            badgeType = "Ei merkkiä.";
        } else if(percentage >= 0.5 && percentage < 0.9){
            badgeType = "Fuksi";
        } else if(percentage >= 0.9){
            badgeType = "Fuksi + Superfuksi";
        }
        MultiFormatWriter multiFormatWriter = new MultiFormatWriter();
        try{
            BitMatrix bitMatrix = multiFormatWriter.encode("Suoritettu " + completedAmount + '/' + challengeAmount + ". Haalarimerkkityyppi: " + badgeType, BarcodeFormat.QR_CODE,800,800);
            BarcodeEncoder barcodeEncoder = new BarcodeEncoder();
            Bitmap bitmap = barcodeEncoder.createBitmap(bitMatrix);
            qrImage.setImageBitmap(bitmap);
        } catch (WriterException ex){
            ex.printStackTrace();
        }

    }



}
