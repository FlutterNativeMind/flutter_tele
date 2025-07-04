import 'package:plugin_platform_interface/plugin_platform_interface.dart';

import 'flutter_tele_method_channel.dart';

abstract class FlutterTelePlatform extends PlatformInterface {
  /// Constructs a FlutterTelePlatform.
  FlutterTelePlatform() : super(token: _token);

  static final Object _token = Object();

  static FlutterTelePlatform _instance = MethodChannelFlutterTele();

  /// The default instance of [FlutterTelePlatform] to use.
  ///
  /// Defaults to [MethodChannelFlutterTele].
  static FlutterTelePlatform get instance => _instance;

  /// Platform-specific implementations should set this with their own
  /// platform-specific class that extends [FlutterTelePlatform] when
  /// they register themselves.
  static set instance(FlutterTelePlatform instance) {
    PlatformInterface.verifyToken(instance, _token);
    _instance = instance;
  }

  Future<String?> getPlatformVersion() {
    throw UnimplementedError('platformVersion() has not been implemented.');
  }
}
