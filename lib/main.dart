import 'package:flutter/material.dart';
import 'package:provider/provider.dart';

import 'theme/app_theme.dart';
import 'viewmodels/task_store.dart';
import 'viewmodels/settings_store.dart';
import 'viewmodels/auth_store.dart';
import 'services/notification_manager.dart';
import 'views/main_navigation.dart';
import 'views/auth/welcome_view.dart';
import 'package:supabase_flutter/supabase_flutter.dart';

void main() async {
  WidgetsFlutterBinding.ensureInitialized();
  await NotificationManager().init();
  await Supabase.initialize(
    url: 'https://qhdqehchosusllwazgyo.supabase.co',
    anonKey: 'sb_publishable_RmynHhXc0vLJgcEQOlFTLw_I-4bFJa3',
  );
  runApp(MyApp());
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
      home: authStore.isAuthenticated
          ? const MainNavigation()
          : const WelcomeView(),
    );
  }
}

class MyApp extends StatelessWidget {
  const MyApp({super.key});

  @override
  Widget build(BuildContext context) {
    return const MaterialApp(title: 'Todos', home: HomePage());
  }
}

class HomePage extends StatefulWidget {
  const HomePage({super.key});

  @override
  State<HomePage> createState() => _HomePageState();
}

class _HomePageState extends State<HomePage> {
  final _future = Supabase.instance.client.from('todos').select();

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      body: FutureBuilder(
        future: _future,
        builder: (context, snapshot) {
          if (!snapshot.hasData) {
            return const Center(child: CircularProgressIndicator());
          }
          final todos = snapshot.data!;
          return ListView.builder(
            itemCount: todos.length,
            itemBuilder: ((context, index) {
              final todo = todos[index];
              return ListTile(title: Text(todo['name']));
            }),
          );
        },
      ),
    );
  }
}
