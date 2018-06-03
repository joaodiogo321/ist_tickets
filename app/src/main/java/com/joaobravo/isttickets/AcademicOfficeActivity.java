package com.joaobravo.isttickets;

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

public class AcademicOfficeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_academic_office);

        // Set toolbar as action bar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Get a support ActionBar corresponding to this toolbar
        ActionBar actionBar = getSupportActionBar();

        // Enable the Up button
        if (actionBar != null)
            actionBar.setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_academic_office, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item selection. Home/Up button is automatically
        // handled, if a parent activity is specified in AndroidManifest.xml.
        switch (item.getItemId()) {
            case R.id.item_general_service:

                return true;

            case R.id.item_priority_attendance:

                return true;

            case R.id.item_documents:

                return true;

            default:
                // Invoke superclass to handle action.
                return super.onOptionsItemSelected(item);
        }
    }

    /** Called when taps button */
    public void showScheduleDialogOnClick(View view) {
        // Setup the alert builder
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setTitle(getString(R.string.schedule));
        // Add a button
        // builder.setPositiveButton("OK", null);

        // Set custom layout
        final View scheduleLayout = getLayoutInflater().inflate(R.layout.dialog_schedule, null);
        builder.setView(scheduleLayout);

        // Create and show the alert dialog
        AlertDialog dialog = builder.create();
        dialog.show();
    }
}
