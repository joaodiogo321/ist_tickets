package com.joaobravo.isttickets;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Remove notification bar
        // this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        // Remove title bar
        // Objects.requireNonNull(getSupportActionBar()).hide();

        setContentView(R.layout.activity_main);
    }

    /** Called when taps button */
    public void onAOClick(View view) {
        Intent intent = new Intent(this, AcademicOfficeActivity.class);
        startActivity(intent);
    }

    /** Called when taps button */
    public void onMIOClick(View view) {
//        Intent intent = new Intent(this, MobilityInternationalActivity.class);
//        startActivity(intent);
    }

    /** Called when taps button */
    public void onTOClick(View view) {
//        Intent intent = new Intent(this, TagusparkActivity.class);
//        startActivity(intent);
    }

    /** Called when taps button */
    public void showSchedulesDialogOnClick(View view) {
        // Setup the alert builder
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(getString(R.string.schedules));
        // Add a button
        // builder.setPositiveButton("OK", null);

        // Set custom layout
        final View scheduleLayout = getLayoutInflater().inflate(R.layout.dialog_mainschedules, null);
        builder.setView(scheduleLayout);

        // Create and show the alert dialog
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    /** Called when taps button */
    public void showAboutDialogOnClick(View view) {
        // Setup the alert builder
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        // Add a button
        // builder.setPositiveButton("OK", null);

        // Set custom layout
        final View aboutLayout = getLayoutInflater().inflate(R.layout.dialog_about, null);
        builder.setView(aboutLayout);

        // Create and show the alert dialog
        AlertDialog dialog = builder.create();
        dialog.show();
    }
}
