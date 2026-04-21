import 'package:flutter/material.dart';
import 'package:provider/provider.dart';
import '../../viewmodels/settings_store.dart';
import '../../viewmodels/auth_store.dart';
import '../../theme/app_colors.dart';
import 'package:share_plus/share_plus.dart';
import '../../services/storage_service.dart';
import 'sync_view.dart';
class SettingsView extends StatelessWidget {
  const SettingsView({super.key});

  @override
  Widget build(BuildContext context) {
    final settingsStore = context.watch<SettingsStore>();
    final authStore = context.watch<AuthStore>();
    final theme = Theme.of(context);

    return Scaffold(
      appBar: AppBar(title: const Text('Settings')),
      body: ListView(
        padding: const EdgeInsets.all(20),
        children: [
          Container(
            padding: const EdgeInsets.all(16),
            decoration: BoxDecoration(
              color: theme.cardColor,
              borderRadius: BorderRadius.circular(20),
            ),
            child: Row(
              children: [
                const CircleAvatar(
                  radius: 24,
                  backgroundColor: AppColors.primaryLight,
                  child: Icon(Icons.person, color: Colors.white),
                ),
                const SizedBox(width: 16),
                const Column(
                  crossAxisAlignment: CrossAxisAlignment.start,
                  children: [
                    Text('LockIn User',
                        style: TextStyle(
                            fontWeight: FontWeight.bold, fontSize: 18)),
                    Text('Privacy Enthusiast',
                        style: TextStyle(
                            color: AppColors.textSecondaryLight,
                            fontSize: 14)),
                  ],
                )
              ],
            ),
          ),
          const SizedBox(height: 32),
          const Text('APPEARANCE',
              style: TextStyle(
                  color: AppColors.textSecondaryLight,
                  fontSize: 12,
                  fontWeight: FontWeight.bold,
                  letterSpacing: 1.2)),
          const SizedBox(height: 12),
          Container(
            decoration: BoxDecoration(
                color: theme.cardColor,
                borderRadius: BorderRadius.circular(16)),
            child: ListTile(
              leading: const Icon(Icons.dark_mode, color: AppColors.primary),
              title: const Text('Dark Mode',
                  style: TextStyle(fontWeight: FontWeight.w500)),
              trailing: Switch(
                value: settingsStore.isDarkMode,
                onChanged: (v) => settingsStore.toggleDarkMode(),
                activeThumbColor: Colors.white,
                activeTrackColor: AppColors.primary,
              ),
            ),
          ),
          const SizedBox(height: 32),
          const Text('BACKEND & SYSTEM',
              style: TextStyle(
                  color: AppColors.textSecondaryLight,
                  fontSize: 12,
                  fontWeight: FontWeight.bold,
                  letterSpacing: 1.2)),
          const SizedBox(height: 12),
          Container(
            decoration: BoxDecoration(
                color: theme.cardColor,
                borderRadius: BorderRadius.circular(16)),
            child: Column(
              children: [
                const ListTile(
                  leading: Icon(Icons.storage, color: Colors.blue),
                  title: Text('Storage Type',
                      style: TextStyle(fontWeight: FontWeight.w500)),
                  trailing: Text('AES-256 Encrypted',
                      style: TextStyle(
                          color: AppColors.textSecondaryLight,
                          fontSize: 12)),
                ),
                const Divider(height: 1, indent: 56),
                ListTile(
                  leading: const Icon(Icons.pie_chart, color: Colors.green),
                  title: const Text('Detailed Storage Info',
                      style: TextStyle(fontWeight: FontWeight.w500)),
                  trailing: const Icon(Icons.chevron_right,
                      color: AppColors.textSecondaryLight),
                  onTap: () {},
                ),
                const Divider(height: 1, indent: 56),
                ListTile(
                  leading:
                      const Icon(Icons.download, color: Colors.orange),
                  title: const Text('Export Data',
                      style: TextStyle(fontWeight: FontWeight.w500)),
                  trailing: const Icon(Icons.chevron_right,
                      color: AppColors.textSecondaryLight),
                  onTap: () async {
                    final path = await StorageService().taskFilePath;
                    await Share.shareXFiles([XFile(path)], text: 'My LockIn Tasks Backup');
                  },
                ),
                const Divider(height: 1, indent: 56),
                ListTile(
                  leading: const Icon(Icons.sync_rounded, color: Colors.purple),
                  title: const Text('Device Sync',
                      style: TextStyle(fontWeight: FontWeight.w500)),
                  trailing: const Icon(Icons.chevron_right, color: AppColors.textSecondaryLight),
                  onTap: () {
                    Navigator.push(context, MaterialPageRoute(builder: (_) => const SyncView()));
                  },
                ),
              ],
            ),
          ),
          const SizedBox(height: 32),
          ElevatedButton(
            onPressed: () => authStore.logout(),
            style: ElevatedButton.styleFrom(
              backgroundColor:
                  AppColors.danger.withValues(alpha: 0.1),
              foregroundColor: AppColors.danger,
            ),
            child: const Text('Log Out'),
          ),
        ],
      ),
    );
  }
}
