import 'package:flutter_local_notifications/flutter_local_notifications.dart';
import 'package:timezone/data/latest_all.dart' as tz;
import 'package:timezone/timezone.dart' as tz;
import '../models/task_item.dart';

class NotificationManager {
  static final NotificationManager _instance = NotificationManager._internal();
  factory NotificationManager() => _instance;
  NotificationManager._internal();

  final FlutterLocalNotificationsPlugin _plugin = FlutterLocalNotificationsPlugin();
  bool _isInitialized = false;

  Future<void> init() async {
    if (_isInitialized) return;

    tz.initializeTimeZones();

    await _plugin.initialize(
      settings: const InitializationSettings(
        android: AndroidInitializationSettings('@mipmap/ic_launcher'),
        iOS: DarwinInitializationSettings(),
      ),
    );

    // Request permissions for Android 13+
    await _plugin.resolvePlatformSpecificImplementation<AndroidFlutterLocalNotificationsPlugin>()?.requestNotificationsPermission();
    await _plugin.resolvePlatformSpecificImplementation<AndroidFlutterLocalNotificationsPlugin>()?.requestExactAlarmsPermission();

    _isInitialized = true;
  }

  Future<void> scheduleTaskNotification(TaskItem task) async {
    if (task.date.isBefore(DateTime.now())) return; // Cannot schedule in past

    final String title = task.isPrivate ? 'Private Scheduled Task' : task.title;
    final String body = task.isPrivate
        ? 'You have a scheduled task.'
        : "It's time to work on: ${task.title}";

    await _plugin.zonedSchedule(
      id: task.id.hashCode,
      title: title,
      body: body,
      scheduledDate: tz.TZDateTime.from(task.date, tz.local),
      notificationDetails: const NotificationDetails(
        android: AndroidNotificationDetails(
          'lockin_tasks',
          'Tasks',
          importance: Importance.max,
          priority: Priority.high,
        ),
        iOS: DarwinNotificationDetails(),
      ),
      androidScheduleMode: AndroidScheduleMode.exactAllowWhileIdle,
    );
  }

  Future<void> cancelTaskNotification(String taskId) async {
    await _plugin.cancel(id: taskId.hashCode);
  }
}
