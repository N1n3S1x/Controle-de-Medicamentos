package com.example.controle_de_medicamentos;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.util.Log;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;

public class AlarmReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d("AlarmReceiver", "Alarme recebido com sucesso!");

        String nomeMedicamento = intent.getStringExtra("nome");
        int notificationId = (int) System.currentTimeMillis();

        String canalId = "canal_medicamento";
        String canalNome = "Lembretes de Medicamentos";

        // Criar canal de notificação (Android 8+)
        NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel canal = new NotificationChannel(canalId, canalNome,
                    NotificationManager.IMPORTANCE_HIGH);
            canal.enableLights(true);
            canal.enableVibration(true);
            canal.setLightColor(Color.BLUE);
            canal.setLockscreenVisibility(NotificationCompat.VISIBILITY_PUBLIC);
            manager.createNotificationChannel(canal);
        }


        // Intent ao clicar na notificação
        Intent notificacaoIntent = new Intent(context, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(
                context, 0, notificacaoIntent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        // Som padrão
        Uri som = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        // Montar notificação
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, canalId)
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setContentTitle("Hora do Medicamento")
                .setContentText("Tome o medicamento: " + nomeMedicamento)
                .setAutoCancel(true)
                .setSound(som)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setVibrate(new long[]{1000, 1000})
                .setContentIntent(pendingIntent);

        manager.notify(notificationId, builder.build());

        Log.d("AlarmReceiver", "onReceive chamado! Nome do medicamento: " + nomeMedicamento);
        Toast.makeText(context, "Notificação recebida!", Toast.LENGTH_SHORT).show();

    }
}
