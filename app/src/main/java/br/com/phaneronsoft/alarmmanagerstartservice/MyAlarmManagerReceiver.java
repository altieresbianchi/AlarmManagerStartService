package br.com.phaneronsoft.alarmmanagerstartservice;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.v4.content.ContextCompat;
import android.util.Log;

public class MyAlarmManagerReceiver extends BroadcastReceiver {
    private final String TAG = getClass().getSimpleName();

    // Código da requisição. Deve ser um número único no app
    public static final int REQUEST_CODE = 12345;

    @Override
    public void onReceive(Context pContext, Intent intent) {
        try {
            Log.d(TAG, "MyAlarmManagerReceiver - onReceive startService.");

            // Cria o Intent
            Intent iService = new Intent(pContext, MyIntentService.class);

            // Valida a versao do Android. A partir do 8, usar startForegroundService
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                ContextCompat.startForegroundService(pContext, iService);

            } else {
                pContext.startService(iService);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
