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
    private int queue_pk;

    /**
     * Debugger method
     * int trace_levels_up: number of steps up the stack trace to reach class
     */
    private void LogcatDebug(String tag, int trace_levels_up) {
        StackTraceElement trace = new Throwable().fillInStackTrace().getStackTrace()[trace_levels_up];
        String filename = trace.getFileName();
        Log.d(tag, "[ " + filename.substring(0, filename.length() - 5) + " : " + trace.getMethodName() + " : line " + trace.getLineNumber() + " ]");
    }

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

        // Update UI even before onResume()
        queue_pk = 1;
        new GetJSONFromURLTask().execute(getString(R.string.url_base) + getString(R.string.url_end_ao));

        mHandler = new Handler();
    }

    @Override
    public void onResume() {
        super.onResume();
        // Start updating UI when this Activity is in foreground (started/resumed)
        startRepeatingTask();

        if (DEBUG) LogcatDebug("SUCCESS",1);
    }

    @Override
    public void onPause() {
        super.onPause();
        // Stop updating UI when this Activity is in background
        stopRepeatingTask();

        if (DEBUG) LogcatDebug("SUCCESS",1);
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
                queue_pk = 1;
                return true;

            case R.id.item_priority_attendance:
                queue_pk = 3;
                return true;

            case R.id.item_documents:
                queue_pk = 4;
                return true;

            default:
                // Invoke superclass to handle action.
                if (DEBUG) LogcatDebug("Switch case default",1);
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

                    // if (DEBUG) Log.v("Response: ", "> " + line);
                }

                return buffer.toString();

            } catch (IOException e) {
                if (DEBUG) LogcatDebug("ERROR",2);
                e.printStackTrace();
            } finally {
                if (connection != null) {
                    connection.disconnect();
                }
                try {
                    if (reader != null) {
                        reader.close();
                    }
                } catch (IOException e) {
                    if (DEBUG) LogcatDebug("ERROR",2);
                    e.printStackTrace();
                }
            }

            if (DEBUG) LogcatDebug("Did nothing",2);
            return null;
        }

        // Executed on main thread. Has access to UI
        // Can be modified to support other web APIs !!
        @Override
        protected void onPostExecute(String response) {
            super.onPostExecute(response);

            // Parse the JSON response from URL
            if (response == null) {
                if (DEBUG) LogcatDebug("ERROR", 2);
            } else {
                JSONArray jArray = null;

                try {
                    jArray = new JSONArray(response);
                } catch (JSONException e) {
                    if (DEBUG) LogcatDebug("ERROR",2);
                    e.printStackTrace();
                }

                assert jArray != null;
                for (int i = 0; i < jArray.length(); i++) {
                    try {
                        JSONObject jObject = jArray.getJSONObject(i);

                        // Pulling items from the array
//                        String jName = "pk";
//                        String jValue = jObject.getString(jName);
//                        text.append("\n"+jValue);
//                        text.append("\n" + jObject.toString(8));

                        if (DEBUG) LogcatDebug("SUCCESS"+i,2);

                    } catch (JSONException e) {
                        if (DEBUG) LogcatDebug("ERROR"+i,2);
                        e.printStackTrace();
                    }
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
            new GetJSONFromURLTask().execute(getString(R.string.url_base) + getString(R.string.url_end_ao));
            mHandler.postDelayed(mStatusChecker, 4000); // milliseconds
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
        final View scheduleLayout = getLayoutInflater().inflate(R.layout.dialog_schedule, null);
        builder.setView(scheduleLayout);

        // Create and show the alert dialog
        AlertDialog dialog = builder.create();
        dialog.show();
    }
}
