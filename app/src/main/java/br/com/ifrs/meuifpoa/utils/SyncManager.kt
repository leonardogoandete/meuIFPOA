package br.com.ifrs.meuifpoa.utils

import android.content.Context
import android.content.SharedPreferences

/**
 * Gerencia a lógica de tempo para a sincronização de dados.
 */
class SyncManager(context: Context) {

    private val sharedPreferences: SharedPreferences =
        context.getSharedPreferences("SincPrefs", Context.MODE_PRIVATE)

    companion object {
        private const val LAST_SYNC_KEY = "last_sync_timestamp"
        // Define o intervalo de sincronização em milissegundos (ex: 1 hora)
        private const val SYNC_INTERVAL_MS = 60 * 60 * 1000
    }

    /**
     * Verifica se uma nova sincronização é necessária com base no tempo.
     * Retorna `true` se a última sincronização foi há mais tempo que o intervalo definido.
     */
    fun deveSincronizar(): Boolean {
        val ultimoSync = sharedPreferences.getLong(LAST_SYNC_KEY, 0)
        val agora = System.currentTimeMillis()
        // Se nunca sincronizou (ultimoSync == 0) ou se o intervalo passou, deve sincronizar.
        return agora - ultimoSync > SYNC_INTERVAL_MS
    }

    /**
     * Registra o timestamp da última sincronização bem-sucedida.
     */
    fun registrarSincronizacaoConcluida() {
        with(sharedPreferences.edit()) {
            putLong(LAST_SYNC_KEY, System.currentTimeMillis())
            apply()
        }
    }

    /**
     * Limpa todas as preferências de sincronização e outras informações de sessão.
     */
    fun limparPreferenciasDeSincronizacao(context: Context) {
        sharedPreferences.edit().clear().apply()
        // Limpa também as credenciais do SIGAA, se existirem
        context.getSharedPreferences("loginSigaa", Context.MODE_PRIVATE).edit().clear().apply()
    }
}
