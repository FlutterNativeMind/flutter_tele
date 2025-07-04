import 'package:flutter_dialer/flutter_dialer.dart';

/// TeleDialer provides dialer replacement functionality for flutter_tele
class TeleDialer {
  /// Check if this app is the default dialer
  static Future<bool> isDefaultDialer() async {
    return await FlutterDialer.isDefaultDialer();
  }

  /// Set this app as the default dialer
  static Future<bool> setDefaultDialer() async {
    return await FlutterDialer.setDefaultDialer();
  }

  /// Check if this app can be set as default dialer
  static Future<bool> canSetDefaultDialer() async {
    return await FlutterDialer.canSetDefaultDialer();
  }

  /// Request to become the default dialer (opens system dialog)
  static Future<bool> requestDefaultDialer() async {
    return await FlutterDialer.setDefaultDialer();
  }
} 