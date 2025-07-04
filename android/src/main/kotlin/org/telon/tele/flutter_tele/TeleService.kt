package org.telon.tele.flutter_tele

import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.telecom.Call
import android.telecom.InCallService
import android.telecom.TelecomManager
import android.telephony.TelephonyManager
import android.util.Log
import android.content.Context
import android.media.AudioManager
import android.os.PowerManager
import android.os.Handler
import android.os.Looper
import android.os.Bundle
import java.util.*

class TeleService : InCallService() {
    companion object {
        private const val TAG = "TeleService"
        private var instance: TeleService? = null
        
        fun getInstance(): TeleService? {
            return instance
        }
    }

    private var mInitialized = false
    private var mHandler: Handler? = null
    private var mAudioManager: AudioManager? = null
    private var mPowerManager: PowerManager? = null
    private var mTelephonyManager: TelephonyManager? = null
    private val mCalls = mutableListOf<TeleCall>()
    private var currentCall: Call? = null
    private var teleCallIds = 0

    override fun onCreate() {
        super.onCreate()
        instance = this
        mHandler = Handler(Looper.getMainLooper())
        mAudioManager = getSystemService(Context.AUDIO_SERVICE) as AudioManager
        mPowerManager = getSystemService(Context.POWER_SERVICE) as PowerManager
        mTelephonyManager = getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        intent?.let { handleIntent(it) }
        return START_STICKY
    }

    private fun handleIntent(intent: Intent) {
        when (intent.action) {
            "START_TELEPHONY_SERVICE" -> {
                val configuration = intent.getStringExtra("configuration")
                Log.d(TAG, "Starting telephony service with config: $configuration")
                initializeService()
            }
            "MAKE_CALL" -> {
                val sim = intent.getIntExtra("sim", 1)
                val destination = intent.getStringExtra("destination") ?: ""
                val callSettings = intent.getStringExtra("callSettings")
                val msgData = intent.getStringExtra("msgData")
                makeCall(sim, destination, callSettings, msgData)
            }
            "ANSWER_CALL" -> {
                val callId = intent.getIntExtra("callId", -1)
                answerCall(callId)
            }
            "HANGUP_CALL" -> {
                val callId = intent.getIntExtra("callId", -1)
                hangupCall(callId)
            }
            "DECLINE_CALL" -> {
                val callId = intent.getIntExtra("callId", -1)
                declineCall(callId)
            }
            "HOLD_CALL" -> {
                val callId = intent.getIntExtra("callId", -1)
                holdCall(callId)
            }
            "UNHOLD_CALL" -> {
                val callId = intent.getIntExtra("callId", -1)
                unholdCall(callId)
            }
            "MUTE_CALL" -> {
                val callId = intent.getIntExtra("callId", -1)
                muteCall(callId)
            }
            "UNMUTE_CALL" -> {
                val callId = intent.getIntExtra("callId", -1)
                unMuteCall(callId)
            }
            "USE_SPEAKER" -> {
                val callId = intent.getIntExtra("callId", -1)
                useSpeaker(callId)
            }
            "USE_EARPIECE" -> {
                val callId = intent.getIntExtra("callId", -1)
                useEarpiece(callId)
            }
        }
    }

    private fun initializeService() {
        if (!mInitialized) {
            mInitialized = true
            Log.d(TAG, "Telephony service initialized")
            
            // Send initialization event to Flutter
            FlutterTelePlugin.getInstance()?.sendEvent("service_started", mapOf(
                "status" to "initialized"
            ))
        }
    }

    private fun makeCall(sim: Int, destination: String, callSettings: String?, msgData: String?) {
        try {
            Log.d(TAG, "Making call to $destination on SIM $sim")
            
            // Create a call object for tracking
            teleCallIds++
            val teleCall = TeleCall(
                id = teleCallIds,
                destination = destination,
                sim = sim,
                state = "INITIATING"
            )
            mCalls.add(teleCall)
            
            // Send call initiated event
            FlutterTelePlugin.getInstance()?.sendEvent("call_received", teleCall.toMap())
            
            // In a real implementation, you would use TelecomManager to make the call
            // For now, we'll just simulate the call creation
            Log.d(TAG, "Call initiated: ${teleCall.id}")
            
        } catch (e: Exception) {
            Log.e(TAG, "Error making call", e)
        }
    }

    private fun answerCall(callId: Int) {
        try {
            val teleCall = findCall(callId)
            if (teleCall != null && currentCall != null) {
                currentCall?.answer(0)
                teleCall.state = "CONNECTED"
                FlutterTelePlugin.getInstance()?.sendEvent("call_changed", teleCall.toMap())
                Log.d(TAG, "Call answered: $callId")
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error answering call", e)
        }
    }

    private fun hangupCall(callId: Int) {
        try {
            val teleCall = findCall(callId)
            if (teleCall != null && currentCall != null) {
                currentCall?.disconnect()
                teleCall.state = "DISCONNECTED"
                FlutterTelePlugin.getInstance()?.sendEvent("call_terminated", teleCall.toMap())
                mCalls.remove(teleCall)
                Log.d(TAG, "Call hung up: $callId")
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error hanging up call", e)
        }
    }

    private fun declineCall(callId: Int) {
        try {
            val teleCall = findCall(callId)
            if (teleCall != null && currentCall != null) {
                currentCall?.reject(false, null)
                teleCall.state = "DECLINED"
                FlutterTelePlugin.getInstance()?.sendEvent("call_terminated", teleCall.toMap())
                mCalls.remove(teleCall)
                Log.d(TAG, "Call declined: $callId")
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error declining call", e)
        }
    }

    private fun holdCall(callId: Int) {
        try {
            val teleCall = findCall(callId)
            if (teleCall != null && currentCall != null) {
                currentCall?.hold()
                teleCall.held = true
                FlutterTelePlugin.getInstance()?.sendEvent("call_changed", teleCall.toMap())
                Log.d(TAG, "Call held: $callId")
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error holding call", e)
        }
    }

    private fun unholdCall(callId: Int) {
        try {
            val teleCall = findCall(callId)
            if (teleCall != null && currentCall != null) {
                currentCall?.unhold()
                teleCall.held = false
                FlutterTelePlugin.getInstance()?.sendEvent("call_changed", teleCall.toMap())
                Log.d(TAG, "Call unheld: $callId")
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error unholding call", e)
        }
    }

    private fun muteCall(callId: Int) {
        try {
            val teleCall = findCall(callId)
            if (teleCall != null) {
                mAudioManager?.isMicrophoneMute = true
                teleCall.muted = true
                FlutterTelePlugin.getInstance()?.sendEvent("call_changed", teleCall.toMap())
                Log.d(TAG, "Call muted: $callId")
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error muting call", e)
        }
    }

    private fun unMuteCall(callId: Int) {
        try {
            val teleCall = findCall(callId)
            if (teleCall != null) {
                mAudioManager?.isMicrophoneMute = false
                teleCall.muted = false
                FlutterTelePlugin.getInstance()?.sendEvent("call_changed", teleCall.toMap())
                Log.d(TAG, "Call unmuted: $callId")
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error unmuting call", e)
        }
    }

    private fun useSpeaker(callId: Int) {
        try {
            val teleCall = findCall(callId)
            if (teleCall != null) {
                mAudioManager?.mode = AudioManager.MODE_NORMAL
                mAudioManager?.isSpeakerphoneOn = true
                teleCall.speaker = true
                FlutterTelePlugin.getInstance()?.sendEvent("call_changed", teleCall.toMap())
                Log.d(TAG, "Speaker enabled: $callId")
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error using speaker", e)
        }
    }

    private fun useEarpiece(callId: Int) {
        try {
            val teleCall = findCall(callId)
            if (teleCall != null) {
                mAudioManager?.mode = AudioManager.MODE_IN_COMMUNICATION
                mAudioManager?.isSpeakerphoneOn = false
                teleCall.speaker = false
                FlutterTelePlugin.getInstance()?.sendEvent("call_changed", teleCall.toMap())
                Log.d(TAG, "Earpiece enabled: $callId")
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error using earpiece", e)
        }
    }

    private fun findCall(callId: Int): TeleCall? {
        return mCalls.find { it.id == callId }
    }

    // InCallService callbacks
    override fun onCallAdded(call: Call) {
        super.onCallAdded(call)
        Log.d(TAG, "Call added")
        
        currentCall = call
        teleCallIds++
        
        val teleCall = TeleCall(
            id = teleCallIds,
            state = "INCOMING",
            direction = "DIRECTION_INCOMING"
        )
        mCalls.add(teleCall)
        
        // Send call received event
        FlutterTelePlugin.getInstance()?.sendEvent("call_received", teleCall.toMap())
        
        // Register call callbacks
        call.registerCallback(object : Call.Callback() {
            override fun onStateChanged(call: Call, state: Int) {
                super.onStateChanged(call, state)
                Log.d(TAG, "Call state changed: $state")
                
                teleCall.state = when (state) {
                    Call.STATE_RINGING -> "RINGING"
                    Call.STATE_DISCONNECTED -> "DISCONNECTED"
                    Call.STATE_ACTIVE -> "ACTIVE"
                    Call.STATE_HOLDING -> "HOLDING"
                    else -> "UNKNOWN"
                }
                
                FlutterTelePlugin.getInstance()?.sendEvent("call_changed", teleCall.toMap())
            }

            override fun onCallDestroyed(call: Call) {
                super.onCallDestroyed(call)
                Log.d(TAG, "Call destroyed")
                
                teleCall.state = "DISCONNECTED"
                FlutterTelePlugin.getInstance()?.sendEvent("call_terminated", teleCall.toMap())
                mCalls.remove(teleCall)
            }
        })
    }

    override fun onCallRemoved(call: Call) {
        super.onCallRemoved(call)
        Log.d(TAG, "Call removed")
        
        val teleCall = findCallByCall(call)
        if (teleCall != null) {
            mCalls.remove(teleCall)
        }
        
        if (currentCall == call) {
            currentCall = null
        }
    }

    private fun findCallByCall(call: Call): TeleCall? {
        // This is a simplified implementation
        // In a real app, you'd maintain a mapping between Call objects and TeleCall objects
        return mCalls.lastOrNull()
    }

    override fun onBind(intent: Intent): IBinder? {
        return super.onBind(intent)
    }

    override fun onDestroy() {
        super.onDestroy()
        instance = null
        Log.d(TAG, "TeleService destroyed")
    }
}

// Data class for representing a call
data class TeleCall(
    val id: Int,
    val destination: String? = null,
    val sim: Int? = null,
    var state: String? = null,
    var held: Boolean? = null,
    var muted: Boolean? = null,
    var speaker: Boolean? = null,
    val direction: String? = null,
    val remoteNumber: String? = null,
    val remoteName: String? = null
) {
    fun toMap(): Map<String, Any> {
        return mapOf(
            "id" to id,
            "destination" to (destination ?: ""),
            "sim" to (sim ?: 1),
            "state" to (state ?: "UNKNOWN"),
            "held" to (held ?: false),
            "muted" to (muted ?: false),
            "speaker" to (speaker ?: false),
            "direction" to (direction ?: "UNKNOWN"),
            "remoteNumber" to (remoteNumber ?: ""),
            "remoteName" to (remoteName ?: "")
        )
    }
} 