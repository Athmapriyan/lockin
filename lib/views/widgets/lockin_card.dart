import 'package:flutter/material.dart';
import '../../models/task_item.dart';
import '../../theme/app_colors.dart';
import 'package:flutter_animate/flutter_animate.dart';
import 'package:intl/intl.dart';
import '../home/task_detail_view.dart';

class LockInCard extends StatelessWidget {
  final TaskItem task;
  final VoidCallback onToggle;
  final bool isPrivacyMode;

  const LockInCard({
    super.key,
    required this.task,
    required this.onToggle,
    this.isPrivacyMode = false,
  });

  @override
  Widget build(BuildContext context) {
    final theme = Theme.of(context);
    final bool isDark = theme.brightness == Brightness.dark;

    final String displayTitle =
        (isPrivacyMode && task.isPrivate) ? 'Locked Task' : task.title;

    return Container(
      margin: const EdgeInsets.symmetric(horizontal: 20, vertical: 8),
      decoration: BoxDecoration(
        color: theme.cardColor,
        borderRadius: BorderRadius.circular(20),
        boxShadow: isDark
            ? []
            : [
                BoxShadow(
                  color: Colors.black.withValues(alpha: 0.04),
                  blurRadius: 10,
                  offset: const Offset(0, 4),
                )
              ],
      ),
      child: Material(
        color: Colors.transparent,
        child: InkWell(
          borderRadius: BorderRadius.circular(20),
          onTap: () {
            Navigator.of(context).push(MaterialPageRoute(
              builder: (context) => TaskDetailView(task: task),
            ));
          },
          child: Padding(
            padding: const EdgeInsets.all(16),
            child: Row(
              children: [
                GestureDetector(
                  onTap: onToggle,
                  behavior: HitTestBehavior.opaque,
                  child: AnimatedContainer(
                    duration: const Duration(milliseconds: 300),
                    curve: Curves.easeOutBack,
                    height: 24,
                    width: 24,
                    decoration: BoxDecoration(
                      shape: BoxShape.circle,
                      border: Border.all(
                        color: task.isCompleted
                            ? AppColors.success
                            : (isDark ? Colors.white38 : Colors.black26),
                        width: 2,
                      ),
                      color: task.isCompleted
                          ? AppColors.success
                          : Colors.transparent,
                    ),
                    child: task.isCompleted
                        ? const Icon(Icons.check, size: 16, color: Colors.white)
                        : null,
                  ),
                ),
                const SizedBox(width: 16),
                Expanded(
                  child: Column(
                    crossAxisAlignment: CrossAxisAlignment.start,
                    children: [
                      Text(
                        displayTitle,
                        style: theme.textTheme.titleLarge?.copyWith(
                          fontSize: 16,
                          decoration: task.isCompleted
                              ? TextDecoration.lineThrough
                              : null,
                          color: task.isCompleted
                              ? (isDark
                                  ? Colors.white54
                                  : AppColors.textSecondaryLight)
                              : null,
                        ),
                      ),
                      const SizedBox(height: 4),
                      Text(
                        DateFormat('MMM d, y • h:mm a').format(task.date),
                        style: theme.textTheme.bodyMedium?.copyWith(
                          color: isDark
                              ? Colors.white54
                              : AppColors.textSecondaryLight,
                          fontSize: 13,
                        ),
                      ),
                    ],
                  ),
                ),
                if (task.isPrivate)
                  Icon(
                    Icons.lock_outline,
                    size: 16,
                    color: isDark ? Colors.white54 : AppColors.textSecondaryLight,
                  ),
              ],
            ),
          ),
        ),
      ),
    ).animate().slideY(
        begin: 0.1,
        duration: const Duration(milliseconds: 400),
        curve: Curves.easeOutBack).fade();
  }
}

