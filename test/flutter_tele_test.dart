import 'package:flutter_test/flutter_test.dart';
import 'package:flutter_tele/flutter_tele.dart';

void main() {
  TestWidgetsFlutterBinding.ensureInitialized();

  group('flutter_tele', () {
    test('TeleCall.fromMap creates call with correct properties', () {
      final callData = {
        'id': 1,
        'state': 'ACTIVE',
        'remoteUri': '"Test User" <sip:+1234567890@domain.com>',
        'direction': 'DIRECTION_INCOMING',
        'held': false,
        'muted': false,
        'speaker': false,
      };

      final call = TeleCall.fromMap(callData);

      expect(call.id, equals(1));
      expect(call.state, equals('ACTIVE'));
      expect(call.remoteNumber, equals('+1234567890'));
      expect(call.remoteName, equals('Test User'));
      expect(call.direction, equals('DIRECTION_INCOMING'));
      expect(call.held, equals(false));
      expect(call.muted, equals(false));
      expect(call.speaker, equals(false));
    });

    test('TeleCall.toMap returns correct map', () {
      final call = TeleCall(
        id: 1,
        state: 'ACTIVE',
        remoteNumber: '+1234567890',
        remoteName: 'Test User',
        direction: 'DIRECTION_INCOMING',
        held: false,
        muted: false,
        speaker: false,
      );

      final map = call.toMap();

      expect(map['id'], equals(1));
      expect(map['state'], equals('ACTIVE'));
      expect(map['remoteNumber'], equals('+1234567890'));
      expect(map['remoteName'], equals('Test User'));
      expect(map['direction'], equals('DIRECTION_INCOMING'));
      expect(map['held'], equals(false));
      expect(map['muted'], equals(false));
      expect(map['speaker'], equals(false));
    });

    test('TeleCall duration formatting works correctly', () {
      final call = TeleCall(
        id: 1,
        totalDuration: 125, // 2 minutes 5 seconds
        creationTimeMillis: DateTime.now().millisecondsSinceEpoch ~/ 1000,
      );

      // The duration calculation depends on current time, so we'll test the formatting function directly
      expect(call.formatTime(125), equals('02:05'));
      expect(call.formatTime(0), equals('00:00'));
      expect(call.formatTime(3661), equals('61:01')); // 1 hour 1 second
    });

    test('TeleCall isTerminated returns correct value', () {
      final activeCall = TeleCall(id: 1, state: 'ACTIVE');
      final terminatedCall = TeleCall(id: 2, state: 'PJSIP_INV_STATE_DISCONNECTED');

      expect(activeCall.isTerminated(), equals(false));
      expect(terminatedCall.isTerminated(), equals(true));
    });

    test('TeleCall can be created with minimal parameters', () {
      final call = TeleCall(id: 1);
      expect(call.id, equals(1));
      expect(call.state, isNull);
      expect(call.remoteNumber, isNull);
    });

    test('TeleCall fromMap handles null values', () {
      final callData = {
        'id': 1,
      };

      final call = TeleCall.fromMap(callData);

      expect(call.id, equals(1));
      expect(call.state, isNull);
      expect(call.remoteNumber, isNull);
      expect(call.remoteName, isNull);
    });

    test('TeleCall toMap handles null values', () {
      final call = TeleCall(id: 1);

      final map = call.toMap();

      expect(map['id'], equals(1));
      expect(map['state'], isNull);
      expect(map['remoteNumber'], isNull);
      expect(map['remoteName'], isNull);
    });
  });
}
