package com.example.myspecialstalker;

import android.Manifest;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;
import android.widget.TextView;

import java.lang.ref.WeakReference;

public class MainActivity extends AppCompatActivity {
    private static final int PERMISSIONS_CODE = 1;
    private EditText stalkerNumberEditText;
    private EditText stalkerMessageEditText;
    private TextView missingInformation;
    private Bundle savedInstanceState;
    private static String stalkerNumber = "";
    private static String stalkerMessage = "";
    private static boolean readyToSend = false;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        this.savedInstanceState = savedInstanceState;
        checkStalkerPermissions();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState)
    {
        super.onSaveInstanceState(outState);
        outState.putString("stalkerNumber", stalkerNumberEditText.getText().toString());
        outState.putString("stalkerMessage", stalkerMessageEditText.getText().toString());
        SharedPreferences sharedPreferences =
                PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("stalkerNumber", stalkerNumberEditText.getText().toString());
        editor.putString("stalkerMessage", stalkerMessageEditText.getText().toString());
        editor.putBoolean("ready", readyToSend);
        editor.apply();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults)
    {
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSIONS_CODE)
        {
            if (grantResults.length == 3
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED
                    && grantResults[1] == PackageManager.PERMISSION_GRANTED
                    && grantResults[2] == PackageManager.PERMISSION_GRANTED)
            {
                afterGotPermissions();
            }
            else
            {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.READ_PHONE_STATE,
                                Manifest.permission.PROCESS_OUTGOING_CALLS,
                                Manifest.permission.SEND_SMS},
                        PERMISSIONS_CODE);
            }
        }
    }

    private void checkStalkerPermissions()
    {
        if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.READ_PHONE_STATE)
                != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.PROCESS_OUTGOING_CALLS)
                        != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.SEND_SMS)
                        != PackageManager.PERMISSION_GRANTED)
        {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.READ_PHONE_STATE,
                            Manifest.permission.PROCESS_OUTGOING_CALLS,
                            Manifest.permission.SEND_SMS},
                    PERMISSIONS_CODE);
        }
        else
        {
            afterGotPermissions();
        }
    }

    private void getViews()
    {
        stalkerNumberEditText = findViewById(R.id.stalkerNumberEditText);
        stalkerMessageEditText = findViewById(R.id.stalkerMessageEditText);
        missingInformation = findViewById(R.id.missingInformation);
    }

    private void afterGotPermissions()
    {
        setContentView(R.layout.activity_main);
        getViews();
        getData();
        setListeners();
    }

    private void getData()
    {
        if (savedInstanceState != null)
        {
            getSavedInstanceState();
        }
        else
        {
            new AsyncDataLoad(this).execute();
        }
    }

    private static class AsyncDataLoad extends AsyncTask<Void, Void, Void>
    {
        private WeakReference<MainActivity> mainActivityWeakReference;

        AsyncDataLoad(MainActivity activity)
        {
            mainActivityWeakReference = new WeakReference<>(activity);
        }

        @Override
        protected Void doInBackground(Void... voids)
        {
            MainActivity activity = mainActivityWeakReference.get();
            if (activity == null || activity.isFinishing())
            {
                return null;
            }
            SharedPreferences sharedPreferences =
                    PreferenceManager.getDefaultSharedPreferences(activity.getApplicationContext());
            activity.stalkerNumberEditText.setText(sharedPreferences.
                    getString("stalkerNumber", ""));
            activity.stalkerMessageEditText.setText(sharedPreferences.
                    getString("stalkerMessage", ""));
            activity.stalkerNumber = sharedPreferences
                    .getString("stalkerNumber", "");
            activity.stalkerMessage = sharedPreferences.
                    getString("stalkerMessage", "");
            activity.readyToSend = sharedPreferences.
                    getBoolean("ready", false);
            return null;
        }
    }

    private void getSavedInstanceState()
    {
        stalkerNumberEditText.setText(savedInstanceState.getString("stalkerNumber"));
        stalkerMessageEditText.setText(savedInstanceState.getString("stalkerMessage"));
        stalkerNumber = savedInstanceState.getString("stalkerNumber");
        stalkerMessage = savedInstanceState.getString("stalkerMessage");
        readyToSend = savedInstanceState.getBoolean("ready");
    }

    private void setListeners()
    {
        stalkerNumberEditText.addTextChangedListener(settingsTextWatcher);
        stalkerMessageEditText.addTextChangedListener(settingsTextWatcher);
    }

    private TextWatcher settingsTextWatcher = new TextWatcher()
    {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after)
        {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count)
        {
            stalkerNumber = stalkerNumberEditText.getText().toString();
            stalkerMessage = stalkerMessageEditText.getText().toString();
            if (stalkerNumber.isEmpty() || stalkerMessage.isEmpty())
            {
                missingInformation.setText("Please enter information into all fields!");
                readyToSend = false;
            }
            else
            {
                missingInformation.setText("The application is ready to stalk!");
                readyToSend = true;
            }
        }

        @Override
        public void afterTextChanged(Editable s)
        {

        }
    };

    public static String getStalkerNumber()
    {
        return stalkerNumber;
    }

    public static String getStalkerMessage()
    {
        return stalkerMessage;
    }

    public static boolean isReadyToSend()
    {
        return readyToSend;
    }
}
