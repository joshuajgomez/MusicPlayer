package com.joshgomez.musicplayer.service.broadcastreceiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;

import com.joshgomez.musicplayer.generic.log.Loggy;
import com.joshgomez.musicplayer.service.musicmanager.AutoPauseManager;

import static android.content.Context.TELEPHONY_SERVICE;

public class CallBroadcastReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Loggy.entryLog();

        /*if (intent.getStringExtra("option").contains("pause")) {
            AutoPauseManager.getInstance().pauseSong();
        } else if (intent.getStringExtra("option").contains("resume")) {
            AutoPauseManager.getInstance().resumeSong();
        }*/

        PhoneStateListener phoneStateListener = new PhoneStateListener() {
            @Override
            public void onCallStateChanged(int state, String incomingNumber) {
                if (state == TelephonyManager.CALL_STATE_RINGING) {
                    //Incoming call: Pause music
                    AutoPauseManager.getInstance().pauseSong();
                } else if (state == TelephonyManager.CALL_STATE_IDLE) {
                    //Not in call: Play music
                    AutoPauseManager.getInstance().resumeSong();
                } else if (state == TelephonyManager.CALL_STATE_OFFHOOK) {
                    //A call is dialing, active or on hold
                    AutoPauseManager.getInstance().pauseSong();
                } else {
                    AutoPauseManager.getInstance().resumeSong();
                }
                super.onCallStateChanged(state, incomingNumber);
            }
        };
        TelephonyManager mgr = (TelephonyManager) context.getSystemService(TELEPHONY_SERVICE);
        if (mgr != null) {
            mgr.listen(phoneStateListener, PhoneStateListener.LISTEN_CALL_STATE);
        }
        Loggy.exitLog();
    }
}
