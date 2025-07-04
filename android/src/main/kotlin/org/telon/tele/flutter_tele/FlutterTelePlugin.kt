package org.telon.tele.flutter_tele

import io.flutter.embedding.engine.plugins.FlutterPlugin
import io.flutter.plugin.common.MethodCall
import io.flutter.plugin.common.MethodChannel
import io.flutter.plugin.common.MethodChannel.MethodCallHandler
import io.flutter.plugin.common.MethodChannel.Result
import io.flutter.plugin.common.EventChannel
import android.content.Context
import android.content.Intent
import android.util.Log

/** FlutterTelePlugin */
class FlutterTelePlugin: FlutterPlugin, MethodCallHandler {
  private lateinit var channel: MethodChannel
  private lateinit var eventChannel: EventChannel
  private lateinit var context: Context
  private var eventSink: EventChannel.EventSink? = null

  companion object {
    private const val TAG = "FlutterTelePlugin"
    private var instance: FlutterTelePlugin? = null
    
    fun getInstance(): FlutterTelePlugin? {
      return instance
    }
  }

  override fun onAttachedToEngine(flutterPluginBinding: FlutterPlugin.FlutterPluginBinding) {
    instance = this
    context = flutterPluginBinding.applicationContext
    channel = MethodChannel(flutterPluginBinding.binaryMessenger, "flutter_tele")
    channel.setMethodCallHandler(this)
    
    eventChannel = EventChannel(flutterPluginBinding.binaryMessenger, "flutter_tele_events")
    eventChannel.setStreamHandler(object : EventChannel.StreamHandler {
      override fun onListen(arguments: Any?, events: EventChannel.EventSink?) {
        eventSink = events
      }

      override fun onCancel(arguments: Any?) {
        eventSink = null
      }
    })
  }

  override fun onMethodCall(call: MethodCall, result: Result) {
    when (call.method) {
      "start" -> {
        val configuration = call.arguments as? Map<String, Any>
        startTelephonyService(configuration, result)
      }
      "makeCall" -> {
        val args = call.arguments as? Map<String, Any>
        if (args != null) {
          val sim = args["sim"] as? Int ?: 1
          val destination = args["destination"] as? String ?: ""
          val callSettings = args["callSettings"] as? Map<String, Any>
          val msgData = args["msgData"] as? Map<String, Any>
          makeCall(sim, destination, callSettings, msgData, result)
        } else {
          result.error("INVALID_ARGUMENTS", "Invalid arguments for makeCall", null)
        }
      }
      "answerCall" -> {
        val callId = call.arguments as? Int
        if (callId != null) {
          answerCall(callId, result)
        } else {
          result.error("INVALID_ARGUMENTS", "Invalid call ID", null)
        }
      }
      "hangupCall" -> {
        val callId = call.arguments as? Int
        if (callId != null) {
          hangupCall(callId, result)
        } else {
          result.error("INVALID_ARGUMENTS", "Invalid call ID", null)
        }
      }
      "declineCall" -> {
        val callId = call.arguments as? Int
        if (callId != null) {
          declineCall(callId, result)
        } else {
          result.error("INVALID_ARGUMENTS", "Invalid call ID", null)
        }
      }
      "holdCall" -> {
        val callId = call.arguments as? Int
        if (callId != null) {
          holdCall(callId, result)
        } else {
          result.error("INVALID_ARGUMENTS", "Invalid call ID", null)
        }
      }
      "unholdCall" -> {
        val callId = call.arguments as? Int
        if (callId != null) {
          unholdCall(callId, result)
        } else {
          result.error("INVALID_ARGUMENTS", "Invalid call ID", null)
        }
      }
      "muteCall" -> {
        val callId = call.arguments as? Int
        if (callId != null) {
          muteCall(callId, result)
        } else {
          result.error("INVALID_ARGUMENTS", "Invalid call ID", null)
        }
      }
      "unMuteCall" -> {
        val callId = call.arguments as? Int
        if (callId != null) {
          unMuteCall(callId, result)
        } else {
          result.error("INVALID_ARGUMENTS", "Invalid call ID", null)
        }
      }
      "useSpeaker" -> {
        val callId = call.arguments as? Int
        if (callId != null) {
          useSpeaker(callId, result)
        } else {
          result.error("INVALID_ARGUMENTS", "Invalid call ID", null)
        }
      }
      "useEarpiece" -> {
        val callId = call.arguments as? Int
        if (callId != null) {
          useEarpiece(callId, result)
        } else {
          result.error("INVALID_ARGUMENTS", "Invalid call ID", null)
        }
      }
      "sendEnvelope" -> {
        val callId = call.arguments as? Int
        if (callId != null) {
          sendEnvelope(callId, result)
        } else {
          result.error("INVALID_ARGUMENTS", "Invalid call ID", null)
        }
      }
      else -> {
        result.notImplemented()
      }
    }
  }

  private fun startTelephonyService(configuration: Map<String, Any>?, result: Result) {
    try {
      val intent = Intent(context, TeleService::class.java).apply {
        action = "START_TELEPHONY_SERVICE"
        putExtra("configuration", configuration?.toString())
      }
      context.startService(intent)
      
      // Return initial state
      val initialState = mapOf(
        "accounts" to emptyList<Map<String, Any>>(),
        "calls" to emptyList<Map<String, Any>>(),
        "status" to "started"
      )
      result.success(initialState)
    } catch (e: Exception) {
      Log.e(TAG, "Error starting telephony service", e)
      result.error("START_ERROR", "Failed to start telephony service", e.message)
    }
  }

  private fun makeCall(sim: Int, destination: String, callSettings: Map<String, Any>?, msgData: Map<String, Any>?, result: Result) {
    try {
      Log.d(TAG, "makeCall called with sim=$sim, destination=$destination")
      
      val intent = Intent(context, TeleService::class.java).apply {
        action = "MAKE_CALL"
        putExtra("sim", sim)
        putExtra("destination", destination)
        putExtra("callSettings", callSettings?.toString())
        putExtra("msgData", msgData?.toString())
      }
      context.startService(intent)
      
      // Return a more complete call object that matches Flutter expectations
      val callData = mapOf(
        "id" to 1,
        "callId" to "call_1",
        "accountId" to 1,
        "localContact" to "",
        "localUri" to "",
        "remoteContact" to destination,
        "remoteUri" to "tel:$destination",
        "state" to "INITIATING",
        "stateText" to "Initiating call",
        "held" to false,
        "muted" to false,
        "speaker" to false,
        "connectDuration" to 0,
        "totalDuration" to 0,
        "remoteOfferer" to false,
        "remoteAudioCount" to 0,
        "remoteVideoCount" to 0,
        "audioCount" to 0,
        "videoCount" to 0,
        "lastStatusCode" to 0,
        "lastReason" to "",
        "media" to emptyMap<String, Any>(),
        "provisionalMedia" to emptyMap<String, Any>(),
        "creationTime" to "",
        "connectTime" to "",
        "details" to emptyMap<String, Any>(),
        "hashCode" to "call_1_hash",
        "extras" to emptyMap<String, Any>(),
        "connectTimeMillis" to 0,
        "creationTimeMillis" to 0,
        "disconnectCause" to "",
        "direction" to "DIRECTION_OUTGOING",
        "simSlot" to sim,
        "simSlot1" to sim,
        "simSlot2" to sim,
        "remoteNumber" to destination,
        "remoteName" to destination
      )
      
      Log.d(TAG, "makeCall returning call data: $callData")
      result.success(callData)
    } catch (e: Exception) {
      Log.e(TAG, "Error making call", e)
      result.error("MAKE_CALL_ERROR", "Failed to make call", e.message)
    }
  }

  private fun answerCall(callId: Int, result: Result) {
    try {
      val intent = Intent(context, TeleService::class.java).apply {
        action = "ANSWER_CALL"
        putExtra("callId", callId)
      }
      context.startService(intent)
      result.success(true)
    } catch (e: Exception) {
      Log.e(TAG, "Error answering call", e)
      result.error("ANSWER_CALL_ERROR", "Failed to answer call", e.message)
    }
  }

  private fun hangupCall(callId: Int, result: Result) {
    try {
      val intent = Intent(context, TeleService::class.java).apply {
        action = "HANGUP_CALL"
        putExtra("callId", callId)
      }
      context.startService(intent)
      result.success(true)
    } catch (e: Exception) {
      Log.e(TAG, "Error hanging up call", e)
      result.error("HANGUP_CALL_ERROR", "Failed to hangup call", e.message)
    }
  }

  private fun declineCall(callId: Int, result: Result) {
    try {
      val intent = Intent(context, TeleService::class.java).apply {
        action = "DECLINE_CALL"
        putExtra("callId", callId)
      }
      context.startService(intent)
      result.success(true)
    } catch (e: Exception) {
      Log.e(TAG, "Error declining call", e)
      result.error("DECLINE_CALL_ERROR", "Failed to decline call", e.message)
    }
  }

  private fun holdCall(callId: Int, result: Result) {
    try {
      val intent = Intent(context, TeleService::class.java).apply {
        action = "HOLD_CALL"
        putExtra("callId", callId)
      }
      context.startService(intent)
      result.success(true)
    } catch (e: Exception) {
      Log.e(TAG, "Error holding call", e)
      result.error("HOLD_CALL_ERROR", "Failed to hold call", e.message)
    }
  }

  private fun unholdCall(callId: Int, result: Result) {
    try {
      val intent = Intent(context, TeleService::class.java).apply {
        action = "UNHOLD_CALL"
        putExtra("callId", callId)
      }
      context.startService(intent)
      result.success(true)
    } catch (e: Exception) {
      Log.e(TAG, "Error unholding call", e)
      result.error("UNHOLD_CALL_ERROR", "Failed to unhold call", e.message)
    }
  }

  private fun muteCall(callId: Int, result: Result) {
    try {
      val intent = Intent(context, TeleService::class.java).apply {
        action = "MUTE_CALL"
        putExtra("callId", callId)
      }
      context.startService(intent)
      result.success(true)
    } catch (e: Exception) {
      Log.e(TAG, "Error muting call", e)
      result.error("MUTE_CALL_ERROR", "Failed to mute call", e.message)
    }
  }

  private fun unMuteCall(callId: Int, result: Result) {
    try {
      val intent = Intent(context, TeleService::class.java).apply {
        action = "UNMUTE_CALL"
        putExtra("callId", callId)
      }
      context.startService(intent)
      result.success(true)
    } catch (e: Exception) {
      Log.e(TAG, "Error unmuting call", e)
      result.error("UNMUTE_CALL_ERROR", "Failed to unmute call", e.message)
    }
  }

  private fun useSpeaker(callId: Int, result: Result) {
    try {
      val intent = Intent(context, TeleService::class.java).apply {
        action = "USE_SPEAKER"
        putExtra("callId", callId)
      }
      context.startService(intent)
      result.success(true)
    } catch (e: Exception) {
      Log.e(TAG, "Error using speaker", e)
      result.error("USE_SPEAKER_ERROR", "Failed to use speaker", e.message)
    }
  }

  private fun useEarpiece(callId: Int, result: Result) {
    try {
      val intent = Intent(context, TeleService::class.java).apply {
        action = "USE_EARPIECE"
        putExtra("callId", callId)
      }
      context.startService(intent)
      result.success(true)
    } catch (e: Exception) {
      Log.e(TAG, "Error using earpiece", e)
      result.error("USE_EARPIECE_ERROR", "Failed to use earpiece", e.message)
    }
  }

  private fun sendEnvelope(callId: Int, result: Result) {
    try {
      // This would typically send an envelope command to the SIM
      result.success("TESTTEST")
    } catch (e: Exception) {
      Log.e(TAG, "Error sending envelope", e)
      result.error("SEND_ENVELOPE_ERROR", "Failed to send envelope", e.message)
    }
  }

  fun sendEvent(eventType: String, eventData: Map<String, Any>) {
    val event = mapOf(
      "type" to eventType,
      "data" to eventData
    )
    Log.d(TAG, "Sending event to Flutter: $event")
    eventSink?.success(event)
  }

  override fun onDetachedFromEngine(binding: FlutterPlugin.FlutterPluginBinding) {
    channel.setMethodCallHandler(null)
    eventChannel.setStreamHandler(null)
    instance = null
  }
}
