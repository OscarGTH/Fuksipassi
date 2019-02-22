package com.example.osku.fuksipassi;


import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MenuInflater;
import android.view.WindowManager;
import android.widget.Toast;


/**
 * Created by Osku on 10.4.2018.
 */

public class MainActivity extends AppCompatActivity {
    public final String TAG = "MainActivity";
    private SectionsPagerAdapter adapter;
    Intent intent;
    private ViewPager mViewPager;



    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //adjustFontScale(getResources().getConfiguration());
        Log.d("ACT",TAG);

        // Set up the ViewPager with the sections adapter.
        mViewPager = findViewById(R.id.pagercontainer);
        setupViewPager(mViewPager);

        TabLayout tabLayout = findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);
    }



    private void setupViewPager(ViewPager viewPager) {
        adapter = new SectionsPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new ChallengeFragment(), "Tehtävät");
        adapter.addFragment(new CompletedFragment(), "Suoritetut");
        viewPager.setAdapter(adapter);
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                if(position == 1){
                    adapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_refresh, menu);
        return true;
    }

    // Menu containing calendars
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // action with ID action_settings was selected
            // Launches the activity_calendar activity and displays official activity_calendar.
            case R.id.action_calendar1:
                intent = new Intent(getApplicationContext(),CalendarView.class);
                intent.putExtra("virallinen",0);
                startActivity(intent);
                break;
            // Launches the activity_calendar activity and displays unofficial activity_calendar.
            case R.id.action_calendar2:
                intent = new Intent(getApplicationContext(),CalendarView.class);
                intent.putExtra("pedago",1);
                startActivity(intent);
                break;
            // Launches an activity containing summary of the completed challenges.
            case R.id.action_summary:
                intent = new Intent(getApplicationContext(),SummaryActivity.class);
                startActivity(intent);
                break;
            case R.id.action_info:
                intent = new Intent(getApplicationContext(),InstructionsActivity.class);
                startActivity(intent);
                break;
            case R.id.action_refresh:
                adapter.notifyDataSetChanged();
                Toast toast = Toast.makeText(getApplicationContext(),"Päivitys onnistui!",Toast.LENGTH_SHORT);
                toast.show();
                break;
            default:
                break;
        }
        return true;
    }

    public void adjustFontScale(Configuration configuration) {
        if (configuration.fontScale > 1.30) {
        
            configuration.fontScale = (float) 1.30;
            DisplayMetrics metrics = getResources().getDisplayMetrics();
            WindowManager wm = (WindowManager) getSystemService(WINDOW_SERVICE);
            wm.getDefaultDisplay().getMetrics(metrics);
            metrics.scaledDensity = configuration.fontScale * metrics.density;
            getBaseContext().getResources().updateConfiguration(configuration, metrics);
        }
    }
}

