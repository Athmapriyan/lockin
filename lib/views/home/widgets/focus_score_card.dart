import 'package:flutter/material.dart';
import 'package:provider/provider.dart';
import '../../../viewmodels/task_store.dart';
import '../../../theme/app_colors.dart';
import 'package:flutter_animate/flutter_animate.dart';

class FocusScoreCard extends StatelessWidget {
  const FocusScoreCard({super.key});

  @override
  Widget build(BuildContext context) {
    final taskStore = context.watch<TaskStore>();
    final isDark = Theme.of(context).brightness == Brightness.dark;

    final completed = taskStore.todayTasks.where((t) => t.isCompleted).length;
    final total = taskStore.todayTasks.length;
    final progress = taskStore.todayProgress;

    return Container(
      margin: const EdgeInsets.symmetric(horizontal: 20, vertical: 8),
      padding: const EdgeInsets.all(20),
      decoration: BoxDecoration(
        color: isDark ? AppColors.surfaceDark : Colors.white,
        borderRadius: BorderRadius.circular(24),
        boxShadow: isDark
            ? []
            : [
                BoxShadow(
                  color: Colors.black.withValues(alpha: 0.05),
                  blurRadius: 15,
                  offset: const Offset(0, 5),
                )
              ],
      ),
      child: Row(
        mainAxisAlignment: MainAxisAlignment.spaceBetween,
        children: [
          Row(
            children: [
              SizedBox(
                width: 60,
                height: 60,
                child: Stack(
                  fit: StackFit.expand,
                  children: [
                    CircularProgressIndicator(
                      value: 1.0,
                      strokeWidth: 6,
                      color: isDark ? Colors.white10 : Colors.black12,
                    ),
                    CircularProgressIndicator(
                      value: progress,
                      strokeWidth: 6,
                      color: AppColors.primary,
                      strokeCap: StrokeCap.round,
                    ).animate().scale(
                        delay: const Duration(milliseconds: 200)),
                    Center(
                      child: Text(
                        '${(progress * 100).toInt()}%',
                        style: const TextStyle(
                            fontWeight: FontWeight.bold, fontSize: 14),
                      ),
                    ),
                  ],
                ),
              ),
              const SizedBox(width: 20),
              Column(
                crossAxisAlignment: CrossAxisAlignment.start,
                children: [
                  Text(
                    '$completed / $total',
                    style: const TextStyle(
                        fontWeight: FontWeight.bold, fontSize: 20),
                  ),
                  const SizedBox(height: 4),
                  Text(
                    "Today's Focus Score",
                    style: TextStyle(
                      color: isDark
                          ? Colors.white54
                          : AppColors.textSecondaryLight,
                      fontSize: 14,
                    ),
                  ),
                ],
              ),
            ],
          ),
          const Icon(Icons.arrow_forward_ios, size: 16, color: Colors.grey),
        ],
      ),
    ).animate().fade().slideY(begin: 0.1);
  }
}
