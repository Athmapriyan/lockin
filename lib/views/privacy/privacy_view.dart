import 'package:flutter/material.dart';
import 'package:provider/provider.dart';
import '../../viewmodels/settings_store.dart';
import '../../theme/app_colors.dart';

class PrivacyView extends StatelessWidget {
  const PrivacyView({super.key});

  @override
  Widget build(BuildContext context) {
    final settingsStore = context.watch<SettingsStore>();
    final theme = Theme.of(context);
    final isDark = theme.brightness == Brightness.dark;

    return Scaffold(
      appBar: AppBar(title: const Text('Privacy')),
      body: ListView(
        padding: const EdgeInsets.all(20),
        children: [
          Container(
            padding:
                const EdgeInsets.symmetric(vertical: 32, horizontal: 20),
            decoration: BoxDecoration(
              color: theme.cardColor,
              borderRadius: BorderRadius.circular(24),
              boxShadow: isDark
                  ? []
                  : [
                      BoxShadow(
                          color: Colors.black.withValues(alpha: 0.05),
                          blurRadius: 15,
                          offset: const Offset(0, 5))
                    ],
            ),
            child: Column(
              children: [
                Icon(
                  settingsStore.isPrivacyModeActive
                      ? Icons.visibility_off
                      : Icons.visibility,
                  size: 48,
                  color: settingsStore.isPrivacyModeActive
                      ? AppColors.primary
                      : AppColors.textSecondaryLight,
                ),
                const SizedBox(height: 16),
                Text('Privacy Mode',
                    style: theme.textTheme.titleLarge
                        ?.copyWith(fontSize: 20)),
                const SizedBox(height: 8),
                const Text('Hides your data on the Lock Screen',
                    style:
                        TextStyle(color: AppColors.textSecondaryLight)),
                const SizedBox(height: 24),
                Switch(
                  value: settingsStore.isPrivacyModeActive,
                  onChanged: (v) => settingsStore.togglePrivacyMode(),
                  activeThumbColor: Colors.white,
                  activeTrackColor: AppColors.primary,
                ),
              ],
            ),
          ),
          const SizedBox(height: 32),
          const Text('APP LOCK',
              style: TextStyle(
                  color: AppColors.textSecondaryLight,
                  fontSize: 12,
                  fontWeight: FontWeight.bold,
                  letterSpacing: 1.2)),
          const SizedBox(height: 12),
          Container(
            decoration: BoxDecoration(
              color: theme.cardColor,
              borderRadius: BorderRadius.circular(16),
            ),
            child: ListTile(
              title: const Text('Enable Face ID / Passcode',
                  style: TextStyle(fontWeight: FontWeight.w600)),
              subtitle: const Text('Immediately',
                  style: TextStyle(fontSize: 12)),
              trailing: Switch(
                value: settingsStore.faceIdEnabled,
                onChanged: (v) => settingsStore.toggleFaceId(),
                activeThumbColor: Colors.white,
                activeTrackColor: AppColors.primary,
              ),
            ),
          ),
        ],
      ),
    );
  }
}
