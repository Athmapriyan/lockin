import 'package:flutter/material.dart';
import 'package:provider/provider.dart';

import 'theme/app_theme.dart';
import 'viewmodels/task_store.dart';
import 'viewmodels/settings_store.dart';
import 'viewmodels/auth_store.dart';
import 'services/notification_manager.dart';
import 'views/main_navigation.dart';
import 'views/auth/welcome_view.dart';

void main() async {
  WidgetsFlutterBinding.ensureInitialized();
  await NotificationManager().init();
  
  runApp(
    MultiProvider(
      providers: [
        ChangeNotifierProvider(create: (_) => TaskStore()),
        ChangeNotifierProvider(create: (_) => SettingsStore()),
        ChangeNotifierProvider(create: (_) => AuthStore()),
      ],
      child: const LockInApp(),
    ),
  );
}

class LockInApp extends StatelessWidget {
  const LockInApp({super.key});

  @override
  Widget build(BuildContext context) {
    final settingsStore = context.watch<SettingsStore>();
    final authStore = context.watch<AuthStore>();

    return MaterialApp(
      title: 'LockIn',
      debugShowCheckedModeBanner: false,
      theme: AppTheme.lightTheme,
      darkTheme: AppTheme.darkTheme,
      themeMode: settingsStore.isDarkMode ? ThemeMode.dark : ThemeMode.light,
      home: authStore.isAuthenticated ? const MainNavigation() : const WelcomeView(),
    );
  }
}
