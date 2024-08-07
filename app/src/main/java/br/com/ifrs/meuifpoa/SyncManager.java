package br.com.ifrs.meuifpoa;


import android.content.Context;
import android.content.SharedPreferences;

public class SyncManager {

    // Nome do arquivo SharedPreferences
    private static final String PREFS_NAME = "SyncPrefs";
    // Chave para armazenar a data da última sincronização
    private static final String LAST_SYNC_DATE_KEY = "lastSyncDate";

    // Salva a data da última sincronização
    public static void saveLastSyncDate(Context context, long timestamp) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putLong(LAST_SYNC_DATE_KEY, timestamp);
        editor.apply();
    }

    // Recupera a data da última sincronização
    public static long getLastSyncDate(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        return prefs.getLong(LAST_SYNC_DATE_KEY, 0);
    }


}

