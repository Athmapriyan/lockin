import 'package:flutter/material.dart';
import 'package:provider/provider.dart';
import '../../viewmodels/task_store.dart';
import '../../viewmodels/settings_store.dart';
import '../../theme/app_colors.dart';
import '../widgets/lockin_card.dart';
import 'widgets/focus_score_card.dart';
import 'create_task_view.dart';
import 'package:intl/intl.dart';

class HomeView extends StatelessWidget {
  const HomeView({super.key});

  @override
  Widget build(BuildContext context) {
    final taskStore = context.watch<TaskStore>();
    final settingsStore = context.watch<SettingsStore>();
    final isDark = Theme.of(context).brightness == Brightness.dark;

    return Scaffold(
      appBar: AppBar(
        title: const Text('Lock In',
            style: TextStyle(fontWeight: FontWeight.bold, fontSize: 24)),
        centerTitle: false,
        actions: [
          IconButton(
            icon: Icon(
              settingsStore.isPrivacyModeActive
                  ? Icons.visibility_off_rounded
                  : Icons.visibility_rounded,
              color: settingsStore.isPrivacyModeActive
                  ? AppColors.primary
                  : null,
            ),
            onPressed: () => settingsStore.togglePrivacyMode(),
          ),
          const SizedBox(width: 8),
        ],
      ),
      body: CustomScrollView(
        slivers: [
          SliverToBoxAdapter(
            child: Padding(
              padding: const EdgeInsets.symmetric(horizontal: 20, vertical: 8),
              child: Text(
                DateFormat('EEEE, MMM d').format(DateTime.now()).toUpperCase(),
                style: TextStyle(
                  color: isDark
                      ? Colors.white54
                      : AppColors.textSecondaryLight,
                  fontSize: 12,
                  fontWeight: FontWeight.w600,
                  letterSpacing: 1.2,
                ),
              ),
            ),
          ),
          const SliverToBoxAdapter(child: FocusScoreCard()),
          SliverToBoxAdapter(
            child: Padding(
              padding:
                  const EdgeInsets.symmetric(horizontal: 20, vertical: 16),
              child: Container(
                padding: const EdgeInsets.symmetric(
                    horizontal: 16, vertical: 12),
                decoration: BoxDecoration(
                  color: isDark
                      ? Colors.white.withValues(alpha: 0.08)
                      : Colors.black.withValues(alpha: 0.05),
                  borderRadius: BorderRadius.circular(12),
                ),
                child: Row(
                  children: [
                    Icon(Icons.search,
                        size: 20,
                        color: isDark
                            ? Colors.white54
                            : AppColors.textSecondaryLight),
                    const SizedBox(width: 8),
                    Text('Search tasks...',
                        style: TextStyle(
                            color: isDark
                                ? Colors.white54
                                : AppColors.textSecondaryLight)),
                  ],
                ),
              ),
            ),
          ),
          if (taskStore.isLoading)
            const SliverFillRemaining(
                child: Center(child: CircularProgressIndicator())),
          if (!taskStore.isLoading && taskStore.todayTasks.isEmpty)
            SliverFillRemaining(
              child: Center(
                child: Text('No tasks today! Time to relax.',
                    style: TextStyle(color: AppColors.textSecondaryLight)),
              ),
            ),
          if (!taskStore.isLoading && taskStore.todayTasks.isNotEmpty)
            SliverList(
              delegate: SliverChildBuilderDelegate(
                (context, index) {
                  final task = taskStore.todayTasks[index];
                  return LockInCard(
                    task: task,
                    isPrivacyMode: settingsStore.isPrivacyModeActive,
                    onToggle: () => taskStore.toggleTaskCompletion(task.id),
                  );
                },
                childCount: taskStore.todayTasks.length,
              ),
            ),
          const SliverToBoxAdapter(child: SizedBox(height: 100)),
        ],
      ),
      floatingActionButton: Padding(
        padding: const EdgeInsets.only(bottom: 24.0),
        child: FloatingActionButton(
          onPressed: () {
            showModalBottomSheet(
              context: context,
              isScrollControlled: true,
              backgroundColor: Colors.transparent,
              builder: (context) => const CreateTaskView(),
            );
          },
          backgroundColor: AppColors.primary,
          child: const Icon(Icons.add, color: Colors.white),
        ),
      ),
    );
  }
}
