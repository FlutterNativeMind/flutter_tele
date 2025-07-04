import 'package:flutter/foundation.dart';
import 'package:flutter/services.dart';

import 'flutter_tele_platform_interface.dart';

/// An implementation of [FlutterTelePlatform] that uses method channels.
class MethodChannelFlutterTele extends FlutterTelePlatform {
  /// The method channel used to interact with the native platform.
  @visibleForTesting
  final methodChannel = const MethodChannel('flutter_tele');

  @override
  Future<String?> getPlatformVersion() async {
    final version = await methodChannel.invokeMethod<String>('getPlatformVersion');
    return version;
  }
}
