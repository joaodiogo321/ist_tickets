package com.joaobravo.isttickets;

import android.annotation.SuppressLint;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class AcademicOfficeActivity extends AppCompatActivity {

    private static Boolean DEBUG = false;
    private Handler mHandler;
    @SuppressWarnings("FieldCanBeLocal")
    private static int taskDelay = 10000; // in milliseconds
    private String url_end;
    private int queueItem; // Selects queue

    // Views to update
    public TextView text_QueueName;
    public TextView text_LineLetter;
    public TextView text_TicketNumber;
    public TextView text_DeskNumber;
    public TextView text_PeopleNumber;
    public TextView text_EstWaitingValue;

    /**
     * Debugger method
     * int trace_levels_up: number of steps up the stack trace to reach class
     */
    private void LogcatDebug(String tag, int trace_levels_up) {
        StackTraceElement trace = new Throwable().fillInStackTrace().getStackTrace()[trace_levels_up];
        String filename = trace.getFileName();
        Log.d(tag, "[ " + filename.substring(0, filename.length() - 5) + " : "
                + trace.getMethodName() + " : line " + trace.getLineNumber() + " ]");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_academic_office);

        // Set toolbar as ActionBar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Get a support ActionBar corresponding to this toolbar
        ActionBar actionBar = getSupportActionBar();

        // Enable the Up button
        if (actionBar != null)
            actionBar.setDisplayHomeAsUpEnabled(true);

        // Find Views to update
        text_QueueName = findViewById(R.id.text_QueueName);
        text_LineLetter = findViewById(R.id.text_LineLetter);
        text_DeskNumber = findViewById(R.id.text_DeskNumber);
        text_TicketNumber = findViewById(R.id.text_TicketNumber);
        text_PeopleNumber = findViewById(R.id.text_PeopleNumber);
        text_EstWaitingValue = findViewById(R.id.text_EstWaitingValue);

        // Update UI even before onResume()
        url_end = getString(R.string.url_end_ao);
        queueItem = 0;
        new GetJSONFromURLTask().execute(getString(R.string.url_base) + url_end);

        mHandler = new Handler();

        if (DEBUG) LogcatDebug("SUCCESS", 1);
    }

    @Override
    public void onResume() {
        super.onResume();
        // Start updating UI when this Activity is in foreground (started/resumed)
        startRepeatingTask();

        if (DEBUG) LogcatDebug("SUCCESS", 1);
    }

    @Override
    public void onPause() {
        super.onPause();
        // Stop updating UI when this Activity is in background
        stopRepeatingTask();

        if (DEBUG) LogcatDebug("SUCCESS", 1);
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
                text_QueueName.setText(getString(R.string.general_service));
                queueItem = 0;
                // Update UI even before repeating task
                new GetJSONFromURLTask().execute(getString(R.string.url_base) + url_end);
                return true;

            case R.id.item_priority_attendance:
                text_QueueName.setText(getString(R.string.priority_attendance));
                queueItem = 1;
                // Update UI even before repeating task
                new GetJSONFromURLTask().execute(getString(R.string.url_base) + url_end);
                return true;

            case R.id.item_documents:
                text_QueueName.setText(getString(R.string.documents));
                queueItem = 2;
                // Update UI even before repeating task
                new GetJSONFromURLTask().execute(getString(R.string.url_base) + url_end);
                return true;

            default:
                // Invoke superclass to handle action.
                if (DEBUG) LogcatDebug("Switch case default", 1);
                return super.onOptionsItemSelected(item);
        }
    }

    /**
     * Get JSON Data from URL once
     * (heavy work executed on another thread)
     * Params: < url, progressUpdate, response >
     */
    // Task is very short-lived, doesn't need to be static
    @SuppressLint("StaticFieldLeak")
    private class GetJSONFromURLTask extends AsyncTask<String, String, String> {

        // Executed on another thread to save resources. Does not have access to UI
        @Override
        protected String doInBackground(String... params) {

            HttpURLConnection connection = null;
            BufferedReader reader = null;

            try {
                URL url = new URL(params[0]);
                connection = (HttpURLConnection) url.openConnection();
                connection.connect();

                InputStream stream = connection.getInputStream();
                reader = new BufferedReader(new InputStreamReader(stream));
                StringBuilder buffer = new StringBuilder();
                String line;

                while ((line = reader.readLine()) != null) {
                    buffer.append(line).append("\n");

                    if (DEBUG) Log.v("Response: ", "> " + line);
                }

                return buffer.toString();

            } catch (IOException e) {
                LogcatDebug("ERROR", 2);
            } finally {
                if (connection != null) {
                    connection.disconnect();
                }
                try {
                    if (reader != null) {
                        reader.close();
                    }
                } catch (IOException e) {
                    LogcatDebug("ERROR", 2);
                }
            }

            if (DEBUG) LogcatDebug("Did nothing", 2);
            return null;
        }

        // Executed on main thread. Has access to UI
        // Can be modified to support other web APIs!!
        @SuppressLint("SetTextI18n")
        @Override
        protected void onPostExecute(String response) {
            super.onPostExecute(response);

            // Parse the JSON response from URL
            if (response == null) {
                if (DEBUG) LogcatDebug("ERROR", 2);
            } else {
                try {
                    JSONObject jObject = (new JSONArray(response)).getJSONObject(queueItem);

                    String queueLetter = jObject.getString("queue_short_name");
                    text_LineLetter.setText(queueLetter);

                    // Pulling items from the array
                    try {
                        JSONObject currentPerson = jObject.getJSONObject("current_called_ticket");
                        int currentTicket = currentPerson.getInt("number");
                        text_TicketNumber.setText(String.valueOf(currentTicket));

                        try {
                            JSONObject currentDesk = jObject.getJSONObject("desk");
                            int deskNumber = currentDesk.getInt("desk_number");
                            text_DeskNumber.setText(String.valueOf(deskNumber));

                        } catch (JSONException e) {
                            text_DeskNumber.setText("None");
                            LogcatDebug("ERROR", 2);
                        }

                    } catch (JSONException e) {
                        text_TicketNumber.setText("None");
                        text_DeskNumber.setText("None");
                        LogcatDebug("ERROR", 2);
                    }

                    int peopleInLine = jObject.getInt("number_of_tickets_to_call");
                    text_PeopleNumber.setText(String.valueOf(peopleInLine));

                    int avgWaitTime = jObject.getInt("average_wait_time");
                    text_EstWaitingValue.setText(String.valueOf(avgWaitTime / 60));

                    if (DEBUG) LogcatDebug("SUCCESS", 2);

                } catch (JSONException e) {
                    LogcatDebug("ERROR", 2);
                }
            }
        }
    }

    /**
     * Repeat task periodically
     */
    Runnable mStatusChecker = new Runnable() {
        @Override
        public void run() {
            new GetJSONFromURLTask().execute(getString(R.string.url_base) + url_end);
            mHandler.postDelayed(mStatusChecker, taskDelay);
        }
    };

    public void startRepeatingTask() {
        mStatusChecker.run();
    }

    public void stopRepeatingTask() {
        mHandler.removeCallbacks(mStatusChecker);
    }

    /**
     * Called when taps button
     */
    public void showScheduleDialogOnClick(View view) {
        // Setup the alert builder
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setTitle(getString(R.string.schedule));
        // Add a button
        // builder.setPositiveButton("OK", null);

        // Set custom layout
        @SuppressLint("InflateParams")
        final View scheduleLayout = getLayoutInflater().inflate(R.layout.dialog_schedule_academic_office, null);
        builder.setView(scheduleLayout);

        // Create and show the alert dialog
        AlertDialog dialog = builder.create();
        dialog.show();
    }
}
