# flutter_tele

A Flutter plugin for telephony operations based on Android's InCallService and telecom APIs.

## Features

- Make outgoing calls
- Answer incoming calls
- Hangup/decline calls
- Hold/unhold calls
- Mute/unmute calls
- Switch between speaker and earpiece
- Real-time call state monitoring
- Event-driven architecture for call events
- Support for multiple SIM cards

## Getting Started

### Installation

Add this to your package's `pubspec.yaml` file:

```yaml
dependencies:
  flutter_tele: ^0.0.1
```

### Android Permissions

Add the following permissions to your `android/app/src/main/AndroidManifest.xml`:

```xml
<uses-permission android:name="android.permission.READ_PHONE_STATE" />
<uses-permission android:name="android.permission.CALL_PHONE" />
<uses-permission android:name="android.permission.READ_CALL_LOG" />
<uses-permission android:name="android.permission.MODIFY_PHONE_STATE" />
<uses-permission android:name="android.permission.READ_PRECISE_PHONE_STATE" />
<uses-permission android:name="android.permission.ANSWER_PHONE_CALLS" />
<uses-permission android:name="android.permission.MANAGE_OWN_CALLS" />
<uses-permission android:name="android.permission.USE_FULL_SCREEN_INTENT" />
<uses-permission android:name="android.permission.WAKE_LOCK" />
<uses-permission android:name="android.permission.FOREGROUND_SERVICE" />
<uses-permission android:name="android.permission.FOREGROUND_SERVICE_PHONE_CALL" />
```

### Usage

#### Initialize the Telephony Service

```dart
import 'package:flutter_tele/flutter_tele.dart';

final TeleEndpoint endpoint = TeleEndpoint();

// Start the telephony service
final result = await endpoint.start({
  'ReplaceDialer': false,
  'Permissions': false,
});

print('Initial calls: ${result['calls']}');
```

#### Make a Call

```dart
// Make an outgoing call
final call = await endpoint.makeCall(
  1, // SIM slot (1 or 2)
  '+1234567890', // Phone number
  null, // Call settings (optional)
  null, // Message data (optional)
);

print('Call initiated: ${call.id}');
```

#### Handle Call Events

```dart
// Listen for incoming calls
endpoint.on('call_received').listen((event) {
  print('Incoming call: $event');
  final call = TeleCall.fromMap(event);
  // Handle incoming call
});

// Listen for call state changes
endpoint.on('call_changed').listen((event) {
  print('Call state changed: $event');
  final call = TeleCall.fromMap(event);
  // Handle call state change
});

// Listen for call termination
endpoint.on('call_terminated').listen((event) {
  print('Call terminated: $event');
  final call = TeleCall.fromMap(event);
  // Handle call termination
});
```

#### Call Control Operations

```dart
// Answer an incoming call
await endpoint.answerCall(call);

// Hangup a call
await endpoint.hangupCall(call);

// Decline an incoming call
await endpoint.declineCall(call);

// Hold a call
await endpoint.holdCall(call);

// Unhold a call
await endpoint.unholdCall(call);

// Mute a call
await endpoint.muteCall(call);

// Unmute a call
await endpoint.unMuteCall(call);

// Use speaker
await endpoint.useSpeaker(call);

// Use earpiece
await endpoint.useEarpiece(call);
```

#### Call Information

```dart
// Get call duration
final duration = call.getTotalDuration();
final formattedDuration = call.getFormattedTotalDuration();

// Get call state
final state = call.getState();
final isTerminated = call.isTerminated();

// Get remote party information
final remoteNumber = call.getRemoteNumber();
final remoteName = call.getRemoteName();
```

#### Cleanup

```dart
// Dispose the endpoint when done
endpoint.dispose();
```

## API Reference

### TeleEndpoint

The main class for telephony operations.

#### Methods

- `start(Map<String, dynamic> configuration)` - Initialize the telephony service
- `makeCall(int sim, String destination, Map<String, dynamic>? callSettings, Map<String, dynamic>? msgData)` - Make an outgoing call
- `answerCall(TeleCall call)` - Answer an incoming call
- `hangupCall(TeleCall call)` - Hangup a call
- `declineCall(TeleCall call)` - Decline an incoming call
- `holdCall(TeleCall call)` - Hold a call
- `unholdCall(TeleCall call)` - Unhold a call
- `muteCall(TeleCall call)` - Mute a call
- `unMuteCall(TeleCall call)` - Unmute a call
- `useSpeaker(TeleCall call)` - Use speaker
- `useEarpiece(TeleCall call)` - Use earpiece
- `sendEnvelope(TeleCall call)` - Send envelope command
- `dispose()` - Clean up resources

#### Events

- `call_received` - Fired when a new call is received
- `call_changed` - Fired when call state changes
- `call_terminated` - Fired when a call is terminated
- `connectivity_changed` - Fired when connectivity changes

### TeleCall

Represents a telephony call.

#### Properties

- `id` - Call identifier
- `state` - Current call state
- `remoteNumber` - Remote party number
- `remoteName` - Remote party name
- `direction` - Call direction (incoming/outgoing)
- `held` - Whether call is on hold
- `muted` - Whether call is muted
- `speaker` - Whether speaker is enabled

#### Methods

- `getTotalDuration()` - Get total call duration in seconds
- `getFormattedTotalDuration()` - Get formatted duration (MM:SS)
- `getConnectDuration()` - Get connected duration in seconds
- `getFormattedConnectDuration()` - Get formatted connected duration
- `isTerminated()` - Check if call is terminated
- `toMap()` - Convert to map for serialization
- `fromMap(Map<String, dynamic> map)` - Create from map

## Example

See the `example` directory for a complete example application demonstrating all features.

## Requirements

- Android API level 21 or higher
- Flutter 3.3.0 or higher
- Dart 3.8.1 or higher

## License

This project is licensed under the ISC License.

