package com.joaobravo.isttickets;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
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

public class MainActivity extends AppCompatActivity {

    private static Boolean DEBUG = true;
    private Handler mainHandler;
    @SuppressWarnings("FieldCanBeLocal")
    private static int taskDelay = 3000; // in milliseconds
    private Boolean[] serviceIsIssuing; // service flags

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
        // Remove notification bar
        // this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_main);

        // Update flags even before onResume()
        serviceIsIssuing = new Boolean[]{true, true, true};
        new GetJSONFromURLTask().execute(getString(R.string.url_base));

        mainHandler = new Handler();

        if (DEBUG) LogcatDebug("SUCCESS", 1);
    }

    @Override
    public void onResume() {
        super.onResume();
        // Start updating flags when this Activity is in foreground (started/resumed)
        startRepeatingTask();

        if (DEBUG) LogcatDebug("SUCCESS", 1);
    }

    @Override
    public void onPause() {
        super.onPause();
        // Stop updating flags when this Activity is in background
        stopRepeatingTask();

        if (DEBUG) LogcatDebug("SUCCESS", 1);
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
        @Override
        protected void onPostExecute(String response) {
            super.onPostExecute(response);

            // Parse the JSON response from URL
            if (response == null) {
                if (DEBUG) LogcatDebug("ERROR", 2);
            } else {
                try {
                    JSONArray jArray = new JSONArray((response));

                    for (int i = 0; i < jArray.length(); i++) {
                        JSONObject jObject = jArray.getJSONObject(i);
                        serviceIsIssuing[i] = jObject.getBoolean("is_issuing_tickets");

//                        Log.i("caralho"+i, String.valueOf(serviceIsIssuing[i]));
                        if (DEBUG) LogcatDebug("SUCCESS"+i, 2);
                    }

                } catch (JSONException e) {
                    LogcatDebug("ERROR", 2);
                }
            }
        }
    }

    /**
     * Repeat task periodically
     */
    Runnable mainStatusChecker = new Runnable() {
        @Override
        public void run() {
            new GetJSONFromURLTask().execute(getString(R.string.url_base));
            mainHandler.postDelayed(mainStatusChecker, taskDelay);
        }
    };

    public void startRepeatingTask() {
        mainStatusChecker.run();
    }

    public void stopRepeatingTask() {
        mainHandler.removeCallbacks(mainStatusChecker);
    }

    /**
     * Called when taps button
     */
    public void onAOClick(View view) {
        Intent intent;
        if (serviceIsIssuing[0])
            intent = new Intent(this, AcademicOfficeActivity.class);
        else
            intent = new Intent(this, AcademicOfficeActivity.class);

        startActivity(intent);
    }

    /**
     * Called when taps button
     */
    public void onMIOClick(View view) {
        Intent intent;
        if (serviceIsIssuing[1])
            intent = new Intent(this, MobilityInternationalActivity.class);
        else
            intent = new Intent(this, MobilityInternationalActivity.class);

        startActivity(intent);
    }

    /**
     * Called when taps button
     */
    public void onTOClick(View view) {
        Intent intent;
        if (serviceIsIssuing[1])
            intent = new Intent(this, TagusparkActivity.class);
        else
            intent = new Intent(this, TagusparkActivity.class);

        startActivity(intent);
    }

    /**
     * Called when taps button
     */
    public void showSchedulesDialogOnClick(View view) {
        // Setup the alert builder
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(getString(R.string.schedules));
        // Add a button
        // builder.setPositiveButton("OK", null);

        // Set custom layout
        @SuppressLint("InflateParams") final View scheduleLayout = getLayoutInflater().inflate(R.layout.dialog_mainschedules, null);
        builder.setView(scheduleLayout);

        // Create and show the alert dialog
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    /**
     * Called when taps button
     */
    public void showAboutDialogOnClick(View view) {
        // Setup the alert builder
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        // Add a button
        // builder.setPositiveButton("OK", null);

        // Set custom layout
        @SuppressLint("InflateParams") final View aboutLayout = getLayoutInflater().inflate(R.layout.dialog_about, null);
        builder.setView(aboutLayout);

        // Create and show the alert dialog
        AlertDialog dialog = builder.create();
        dialog.show();
    }
}
