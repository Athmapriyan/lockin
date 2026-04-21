import 'package:flutter/foundation.dart';
import 'package:shared_preferences/shared_preferences.dart';
import 'package:screen_brightness/screen_brightness.dart';

class SettingsStore extends ChangeNotifier {
  bool _isDarkMode = false;
  bool _isPrivacyModeActive = false;
  bool _faceIdEnabled = false;

  bool get isDarkMode => _isDarkMode;
  bool get isPrivacyModeActive => _isPrivacyModeActive;
  bool get faceIdEnabled => _faceIdEnabled;

  SettingsStore() {
    _loadSettings();
  }

  Future<void> _loadSettings() async {
    final prefs = await SharedPreferences.getInstance();
    _isDarkMode = prefs.getBool('isDarkMode') ?? false;
    _isPrivacyModeActive = prefs.getBool('isPrivacyModeActive') ?? false;
    _faceIdEnabled = prefs.getBool('faceIdEnabled') ?? false;
    
    if (_isPrivacyModeActive) {
      try {
        await ScreenBrightness().setScreenBrightness(0.1);
      } catch (e) {
        debugPrint('Failed to set brightness: $e');
      }
    }
    
    notifyListeners();
  }

  Future<void> toggleDarkMode() async {
    _isDarkMode = !_isDarkMode;
    notifyListeners();
    final prefs = await SharedPreferences.getInstance();
    await prefs.setBool('isDarkMode', _isDarkMode);
  }

  Future<void> togglePrivacyMode() async {
    _isPrivacyModeActive = !_isPrivacyModeActive;
    
    try {
      if (_isPrivacyModeActive) {
        await ScreenBrightness().setScreenBrightness(0.1);
      } else {
        await ScreenBrightness().resetScreenBrightness();
      }
    } catch (e) {
      debugPrint('Failed to set brightness: $e');
    }

    notifyListeners();
    final prefs = await SharedPreferences.getInstance();
    await prefs.setBool('isPrivacyModeActive', _isPrivacyModeActive);
  }

  Future<void> toggleFaceId() async {
    _faceIdEnabled = !_faceIdEnabled;
    notifyListeners();
    final prefs = await SharedPreferences.getInstance();
    await prefs.setBool('faceIdEnabled', _faceIdEnabled);
  }
}
