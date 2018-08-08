package br.com.phaneronsoft.alarmmanagerstartservice;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 */
public class MyIntentService extends IntentService {
    // Notificacoes de sincronizacao geral
    public static final String ACTION_NOTIFY_SYNC_STATUS = "br.com.phaneronsoft.alarmmanagerstartservice.ACTION_NOTIFY_SYNC_STATUS";
    public static final String EXTRA_STATUS_PROGRESS = "extraStatusProgress";
    public static final String EXTRA_IS_FINISHED = "extraIsFinished";

    private final String TAG = getClass().getSimpleName();

    private Context mContext = this;

    // Progressbar
    private NotificationCompat.Builder mBuilder;
    private NotificationManager mNotifyManager;
    private int notificationId = 3; // Nao pode ser 0
    private String mChannelId = "channel-sync";
    private String mChannelName = "Sinchronization";

    public MyIntentService() {
        super("SynchronizeIntentService");
    }

    @Override
    public void onCreate() {
        Log.d(TAG, "onCreate");

        // Mensagem progresso sincronizacao
        String message = getString(R.string.msg_start_service_on_create);

        // Necessário somente a partir da versão 8 do Android
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // Cria notificação em foreground. Se não chamar o startForeground em até 5 segundos, será gerado um erro em tempo de execução
            this.showProgressNotification(message, false, true);
        }
        super.onCreate();
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        // Mensagem progresso sincronizacao
        String message = getString(R.string.msg_start_service_on_handle_intent);

        Log.d(TAG, message);

        try {
            // Exibe a mensagem de inicio
            this.showProgressNotification(message, false, true);

            // Loop para mostrar o progresso na notificacao
            for (int progress = 1; progress <= 100; progress++) {
                // Atualiza a mensagem
                message = getString(R.string.label_progress);

                // Atualiza o progresso
                this.updateProgressNotification(message, progress, 100, false, true);

                // Aguarda tempo para atualizar o progresso
                Thread.sleep(100);
            }

            Log.d(TAG, "Finalizou processo");

        } catch (Exception e) {
            e.printStackTrace();

            Log.e(TAG, "Exception!!! " + e.getMessage());

            // Pega o texto da excecao
            message = e.getMessage();

        } finally {
            // Atualiza a notificacao
            if (mBuilder != null) {
                this.updateProgressNotification(message, 100, 100, true, true);
            } else {
                this.showProgressNotification(message, true, true);
            }

            // Finaliza o servico
            stopSelf();
        }
    }

    @Override
    public void onDestroy() {
        Log.d(TAG, "MyIntentService - onDestroy");

        super.onDestroy();
    }

    // Funcao para atualizar a tela do chat ao receber um push
    private void sendSynchronizationProgressToActivity(String progress, boolean isFinished) {
        // Seta o intent
        Intent intent = new Intent(ACTION_NOTIFY_SYNC_STATUS);

        // Passa o ID do ticket para recarregar
        intent.putExtra(EXTRA_STATUS_PROGRESS, progress);
        intent.putExtra(EXTRA_IS_FINISHED, isFinished);

        // Notifica o broadcast
        sendBroadcast(intent);

        // Log.d(App.TAG_SERVICE, "Enviou - " + progress);
    }

    private void showProgressNotification(String message, boolean isFinishedSynch, boolean isNotifyActivity) {
        try {
            // Inicializa as variaveis
            if (mNotifyManager == null) {
                mNotifyManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            }
            if (mBuilder == null) {
                mBuilder = new NotificationCompat.Builder(mContext, mChannelId);
            }

            // Cancela notificacoes anteriores
            if (mNotifyManager != null) {
                mNotifyManager.cancelAll();
            }

            // Seta o channel na versao 8
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                NotificationChannel notificationChannel = new NotificationChannel(mChannelId, mChannelName, NotificationManager.IMPORTANCE_NONE);
                notificationChannel.enableVibration(false);
                notificationChannel.enableLights(false);
                mNotifyManager.createNotificationChannel(notificationChannel);
            }

            // Seta o titulo
            mBuilder.setContentTitle(getString(R.string.app_name));
            mBuilder.setContentText(message);
            mBuilder.setSmallIcon(R.mipmap.ic_launcher);
            mBuilder.setAutoCancel(true);

            // Desabilita o som e vibracao
            mBuilder.setOnlyAlertOnce(true);

            // Inicializa o progresso
            if (isFinishedSynch) {
                mBuilder.setProgress(100, 100, false);

            } else {
                mBuilder.setProgress(100, 0, false);
            }

            // Pega a notification do builder
            Notification notification = mBuilder.build();

            // Displays the progress bar for the first time
            //mNotifyManager.notify(mNotificationProgressId, mBuilder.build());
            startForeground(3, notification);

            // Valida se eh pra notificar o receiver
            if (isNotifyActivity) {
                sendSynchronizationProgressToActivity(message, isFinishedSynch);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void updateProgressNotification(String message, int progress, int progressTotal, boolean isFinishedProgress, boolean isNotifyActivity) {
        try {
            // Seta a porcentagem atual
            mBuilder.setProgress(progressTotal, progress, false);
            if (progress < progressTotal) {
                message = (message + " " + progress + " de " + progressTotal);
            }

            // Seta a mensagem
            mBuilder.setContentText(message);

            //mNotifyManager.notify(mNotificationProgressId, mBuilder.build());
            startForeground(3, mBuilder.build());

            // Valida se eh pra notificar o receiver
            if (isNotifyActivity) {
                // Notifica o receiver
                sendSynchronizationProgressToActivity(message, isFinishedProgress);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
