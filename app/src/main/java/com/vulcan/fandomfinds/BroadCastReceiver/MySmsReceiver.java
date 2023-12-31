package com.vulcan.fandomfinds.BroadCastReceiver;

import android.annotation.TargetApi;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.telephony.SmsMessage;
import android.util.Log;
import android.widget.Toast;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;

public class MySmsReceiver extends BroadcastReceiver {
    public static final String pdu_type = "pdus";
    private static SmsReceivedCallback callback;

    public static void setCallback(SmsReceivedCallback smsReceivedCallback) {
        callback = smsReceivedCallback;
    }
    @Override
    @TargetApi(Build.VERSION_CODES.M)
    public void onReceive(Context context, Intent intent) {
        Bundle bundle = intent.getExtras();
        SmsMessage[] msgs;
        String format = bundle.getString("format");

        Object[] pdus = (Object[]) bundle.get(pdu_type);

        if (pdus != null) {
            boolean isVersionM = (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M);
            msgs = new SmsMessage[pdus.length];
            for (int i = 0; i < msgs.length; i++) {
                if (isVersionM) {
                    msgs[i] = SmsMessage.createFromPdu((byte[]) pdus[i], format);
                } else {
                    msgs[i] = SmsMessage.createFromPdu((byte[]) pdus[i]);
                }
                String verificationCode = msgs[i].getMessageBody().substring(0,6);

                if (callback != null) {
                    callback.onSmsReceived(verificationCode);
                }
            }
        }
    }
}