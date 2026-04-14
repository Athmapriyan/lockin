import 'package:flutter/foundation.dart';
import 'package:flutter/services.dart';
import 'package:shared_preferences/shared_preferences.dart';
import 'package:local_auth/local_auth.dart';

class AuthStore extends ChangeNotifier {
  bool _isAuthenticated = false;
  final LocalAuthentication _auth = LocalAuthentication();

  bool get isAuthenticated => _isAuthenticated;

  AuthStore() {
    _checkAuth();
  }

  Future<void> _checkAuth() async {
    final prefs = await SharedPreferences.getInstance();
    _isAuthenticated = prefs.getBool('isLoggedIn') ?? false;
    notifyListeners();
  }

  Future<bool> authenticateWithBiometrics() async {
    try {
      final isAvailable = await _auth.canCheckBiometrics || await _auth.isDeviceSupported();
      if (!isAvailable) return false;

      final prefs = await SharedPreferences.getInstance();
      final biometricEnabled = prefs.getBool('faceIdEnabled') ?? false;
      if (!biometricEnabled) return false;

      final success = await _auth.authenticate(
        localizedReason: 'Please authenticate to access your tasks',
        biometricOnly: false,
        persistAcrossBackgrounding: true,
      );

      if (success) {
        _isAuthenticated = true;
        await prefs.setBool('isLoggedIn', true);
        notifyListeners();
      }
      return success;
    } on PlatformException catch (_) {
      return false;
    }
  }

  Future<bool> login(String pin) async {
    final prefs = await SharedPreferences.getInstance();
    final savedPin = prefs.getString('userPin');
    if (savedPin == pin || savedPin == null) {
      _isAuthenticated = true;
      await prefs.setBool('isLoggedIn', true);
      if (savedPin == null) {
        await prefs.setString('userPin', pin);
      }
      notifyListeners();
      return true;
    }
    return false;
  }

  Future<void> logout() async {
    _isAuthenticated = false;
    final prefs = await SharedPreferences.getInstance();
    await prefs.setBool('isLoggedIn', false);
    notifyListeners();
  }
}
