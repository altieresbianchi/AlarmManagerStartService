package br.com.phaneronsoft.alarmmanagerstartservice;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private final String TAG = getClass().getSimpleName();

    private Context mContext = this;

    private Button btnStartService, btnStopService;
    private TextView textViewMessage;


    private ReceiverServiceProgress myReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Instancia os elementos
        btnStartService = findViewById(R.id.btnStartService);
        btnStopService = findViewById(R.id.btnStopService);
        textViewMessage = findViewById(R.id.textViewMessage);

        // Seta o clique nos botoes
        btnStartService.setOnClickListener(this);
        btnStopService.setOnClickListener(this);
    }

    @Override
    protected void onStart() {
        // Configura o receiver
        this.configReceiver();

        Log.i(TAG, "onStart");

        super.onStart();
    }

    @Override
    public void onStop() {
        // Remove o receiver
        unregisterReceiver(myReceiver);

        Log.i(TAG, "onStop");

        super.onStop();
    }

    @Override
    public void onClick(View view) {
        if (view.equals(btnStartService)) {
            // Inicializa o AlarmManager
            this.scheduleAlarmSynch();

        } else if (view.equals(btnStopService)) {
            // Cancela o AlarmManager
            this.cancelAlarm();
        }
    }

    // Setup a recurring alarm every half hour
    public void scheduleAlarmSynch() {
        try {
            // Instancia o broadcast
            Intent intent = new Intent(mContext, MyAlarmManagerReceiver.class);

            // Cria um PendingIntent para ser disparado quando o alarme iniciar
            PendingIntent pendingIntent = PendingIntent.getBroadcast(mContext, MyAlarmManagerReceiver.REQUEST_CODE, intent, PendingIntent.FLAG_UPDATE_CURRENT);

            // Intancia o AlarmManager
            AlarmManager alarmManager = (AlarmManager) mContext.getSystemService(Context.ALARM_SERVICE);

            // Pega o tempo atual para iniciar o servico
            long firstStartMillis = System.currentTimeMillis();

            // Intervalo em milissegundos
            // Caso queira, pode usar um dos tempos disponibilizados pela lib.
            // Ex: AlarmManager.INTERVAL_FIFTEEN_MINUTES, AlarmManager.INTERVAL_HALF_HOUR etc
            long intervalMillis = (1 * 60 * 1000); // 1 minuto

            // Seta a data de inicio do alarm
            // First parameter is the type: ELAPSED_REALTIME, ELAPSED_REALTIME_WAKEUP, RTC_WAKEUP
            // Interval can be INTERVAL_FIFTEEN_MINUTES, INTERVAL_HALF_HOUR, INTERVAL_HOUR, INTERVAL_DAY
            alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, firstStartMillis, intervalMillis, pendingIntent);
            //alarmManager.setInexactRepeating(AlarmManager.RTC_WAKEUP, firstStartMillis, intervalMillis, pendingIntent);

            Log.i(TAG, "Iniciou scheduleAlarmSynch");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void cancelAlarm() {
        try {
            // Instancia o broadcast
            Intent intent = new Intent(mContext, MyAlarmManagerReceiver.class);

            // Cria um PendingIntent para ser disparado quando o alarme iniciar
            PendingIntent pendingIntent = PendingIntent.getBroadcast(mContext, MyAlarmManagerReceiver.REQUEST_CODE, intent, PendingIntent.FLAG_UPDATE_CURRENT);

            // Intancia o AlarmManager
            AlarmManager alarmManager = (AlarmManager) mContext.getSystemService(Context.ALARM_SERVICE);

            // Cancela o schedule
            alarmManager.cancel(pendingIntent);

            Log.i(TAG, "Cancelando AlarmManager");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void configReceiver() {
        try {
            // Instancia o receiver
            myReceiver = new ReceiverServiceProgress();

            // Cria o filter
            IntentFilter filter = new IntentFilter();
            filter.addAction(MyIntentService.ACTION_NOTIFY_SYNC_STATUS);

            // Registra o receiver
            registerReceiver(myReceiver, filter);

            Log.i(TAG, "configReceiver");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Recebe as notificacoes do service de sincronizacao
    class ReceiverServiceProgress extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            // Pega o texto do progresso
            String progress = intent.getStringExtra(MyIntentService.EXTRA_STATUS_PROGRESS);

            // Valida se finalizou o progresso
            boolean isFinished = intent.getBooleanExtra(MyIntentService.EXTRA_IS_FINISHED, false);

            Log.i(TAG, "ReceiverServiceProgress progress: " + progress);

            // Valida se finalizou a sincronizacao
            if (isFinished) {
                // Seta os status na tela
                textViewMessage.setText(getString(R.string.label_finished));

            } else {
                // Seta os status na tela
                textViewMessage.setText(progress);
            }
        }
    }
}
