import 'package:flutter_local_notifications/flutter_local_notifications.dart';
import '../models/task_item.dart';

class NotificationManager {
  static final NotificationManager _instance = NotificationManager._internal();
  factory NotificationManager() => _instance;
  NotificationManager._internal();

  final FlutterLocalNotificationsPlugin _plugin = FlutterLocalNotificationsPlugin();
  bool _isInitialized = false;

  Future<void> init() async {
    if (_isInitialized) return;

    await _plugin.initialize(
      settings: const InitializationSettings(
        android: AndroidInitializationSettings('@mipmap/ic_launcher'),
        iOS: DarwinInitializationSettings(),
      ),
    );
    _isInitialized = true;
  }

  Future<void> showTaskNotification(TaskItem task, bool isPrivacyMode) async {
    final String title =
        isPrivacyMode && task.isPrivate ? 'Private Task' : task.title;
    final String body = isPrivacyMode && task.isPrivate
        ? 'You have a scheduled task.'
        : "It's time to work on: ${task.title}";

    await _plugin.show(
      id: task.id.hashCode,
      title: title,
      body: body,
      notificationDetails: const NotificationDetails(
        android: AndroidNotificationDetails(
          'lockin_tasks',
          'Tasks',
          importance: Importance.max,
          priority: Priority.high,
        ),
        iOS: DarwinNotificationDetails(),
      ),
    );
  }
}
