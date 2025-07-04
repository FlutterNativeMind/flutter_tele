package org.telon.tele.flutter_tele

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.util.Log

class TeleBroadcastReceiver(private val context: Context) : BroadcastReceiver() {
    
    companion object {
        private const val TAG = "TeleBroadcastReceiver"
        
        // Action constants
        const val ACTION_TELE_CALL_RECEIVED = "org.telon.tele.TELE_CALL_RECEIVED"
        const val ACTION_TELE_CALL_CHANGED = "org.telon.tele.TELE_CALL_CHANGED"
        const val ACTION_TELE_CALL_TERMINATED = "org.telon.tele.TELE_CALL_TERMINATED"
        const val ACTION_TELE_CONNECTIVITY_CHANGED = "org.telon.tele.TELE_CONNECTIVITY_CHANGED"
    }

    override fun onReceive(context: Context?, intent: Intent?) {
        intent?.let { handleIntent(it) }
    }

    private fun handleIntent(intent: Intent) {
        when (intent.action) {
            ACTION_TELE_CALL_RECEIVED -> {
                Log.d(TAG, "Call received event")
                // Handle call received event
                val callData = intent.getSerializableExtra("call_data")
                FlutterTelePlugin.getInstance()?.sendEvent("call_received", mapOf(
                    "call" to (callData?.toString() ?: "")
                ))
            }
            ACTION_TELE_CALL_CHANGED -> {
                Log.d(TAG, "Call changed event")
                // Handle call changed event
                val callData = intent.getSerializableExtra("call_data")
                FlutterTelePlugin.getInstance()?.sendEvent("call_changed", mapOf(
                    "call" to (callData?.toString() ?: "")
                ))
            }
            ACTION_TELE_CALL_TERMINATED -> {
                Log.d(TAG, "Call terminated event")
                // Handle call terminated event
                val callData = intent.getSerializableExtra("call_data")
                FlutterTelePlugin.getInstance()?.sendEvent("call_terminated", mapOf(
                    "call" to (callData?.toString() ?: "")
                ))
            }
            ACTION_TELE_CONNECTIVITY_CHANGED -> {
                Log.d(TAG, "Connectivity changed event")
                // Handle connectivity changed event
                val available = intent.getBooleanExtra("available", false)
                FlutterTelePlugin.getInstance()?.sendEvent("connectivity_changed", mapOf(
                    "available" to available
                ))
            }
        }
    }

    fun getFilter(): IntentFilter {
        return IntentFilter().apply {
            addAction(ACTION_TELE_CALL_RECEIVED)
            addAction(ACTION_TELE_CALL_CHANGED)
            addAction(ACTION_TELE_CALL_TERMINATED)
            addAction(ACTION_TELE_CONNECTIVITY_CHANGED)
        }
    }
} 