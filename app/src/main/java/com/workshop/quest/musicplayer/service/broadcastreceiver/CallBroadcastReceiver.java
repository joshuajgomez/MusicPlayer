package com.workshop.quest.musicplayer.service.broadcastreceiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;

import com.workshop.quest.musicplayer.service.MusicPlayerService;

import static android.content.Context.TELEPHONY_SERVICE;

public class CallBroadcastReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Log.println(Log.ASSERT, "onReceive", "called");

        PhoneStateListener phoneStateListener = new PhoneStateListener() {
            @Override
            public void onCallStateChanged(int state, String incomingNumber) {
                if (state == TelephonyManager.CALL_STATE_RINGING) {
                    Intent intent1 = new Intent(context, MusicPlayerService.class);
                    intent1.setAction("pause");
                    context.startService(intent1);
                    //Incoming call: Pause music
                } else if (state == TelephonyManager.CALL_STATE_IDLE) {
                    //Not in call: Play music
                    Intent intent1 = new Intent(context, MusicPlayerService.class);
                    intent1.setAction("play");
                    context.startService(intent1);
                } else if (state == TelephonyManager.CALL_STATE_OFFHOOK) {
                    //A call is dialing, active or on hold
                    Intent intent1 = new Intent(context, MusicPlayerService.class);
                    intent1.setAction("pause");
                    context.startService(intent1);
                } else {
                    Intent intent1 = new Intent(context, MusicPlayerService.class);
                    intent1.setAction("play ");
                    context.startService(intent1);
                }
                super.onCallStateChanged(state, incomingNumber);
            }
        };
        TelephonyManager mgr = (TelephonyManager) context.getSystemService(TELEPHONY_SERVICE);
        if (mgr != null) {
            mgr.listen(phoneStateListener, PhoneStateListener.LISTEN_CALL_STATE);
        }
    }
}
