import 'package:flutter/material.dart';
import 'package:provider/provider.dart';
import 'package:intl/intl.dart';
import '../../models/task_item.dart';
import '../../viewmodels/task_store.dart';
import '../../viewmodels/settings_store.dart';
import '../../theme/app_colors.dart';

class TaskDetailView extends StatefulWidget {
  final TaskItem task;

  const TaskDetailView({super.key, required this.task});

  @override
  State<TaskDetailView> createState() => _TaskDetailViewState();
}

class _TaskDetailViewState extends State<TaskDetailView> {
  final TextEditingController _notesController = TextEditingController();

  @override
  void initState() {
    super.initState();
    _notesController.text = widget.task.notes;
  }

  void _saveNotes() {
    final updatedTask = widget.task.copyWith(notes: _notesController.text);
    context.read<TaskStore>().updateTask(updatedTask);
    ScaffoldMessenger.of(context).showSnackBar(
      const SnackBar(content: Text('Notes saved successfully')),
    );
  }

  void _deleteTask() {
    context.read<TaskStore>().deleteTask(widget.task.id);
    Navigator.of(context).pop();
  }

  @override
  Widget build(BuildContext context) {
    final theme = Theme.of(context);
    final isDark = theme.brightness == Brightness.dark;
    final settingsStore = context.watch<SettingsStore>();

    final displayTitle = (settingsStore.isPrivacyModeActive && widget.task.isPrivate)
        ? 'Locked Task'
        : widget.task.title;

    return Scaffold(
      appBar: AppBar(
        title: const Text('Task Details'),
        actions: [
          IconButton(
            icon: const Icon(Icons.delete_outline, color: AppColors.danger),
            onPressed: () {
              showDialog(
                context: context,
                builder: (ctx) => AlertDialog(
                  title: const Text('Delete Task?'),
                  content: const Text('This action cannot be undone.'),
                  actions: [
                    TextButton(onPressed: () => Navigator.pop(ctx), child: const Text('Cancel')),
                    TextButton(onPressed: () {
                      Navigator.pop(ctx);
                      _deleteTask();
                    }, child: const Text('Delete', style: TextStyle(color: AppColors.danger))),
                  ],
                ),
              );
            },
          ),
        ],
      ),
      body: SingleChildScrollView(
        padding: const EdgeInsets.all(20),
        child: Column(
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            Row(
              children: [
                GestureDetector(
                  onTap: () {
                    context.read<TaskStore>().toggleTaskCompletion(widget.task.id);
                    Navigator.pop(context);
                  },
                  child: Container(
                    height: 32,
                    width: 32,
                    decoration: BoxDecoration(
                      shape: BoxShape.circle,
                      border: Border.all(
                        color: widget.task.isCompleted ? AppColors.success : (isDark ? Colors.white38 : Colors.black26),
                        width: 2,
                      ),
                      color: widget.task.isCompleted ? AppColors.success : Colors.transparent,
                    ),
                    child: widget.task.isCompleted ? const Icon(Icons.check, size: 20, color: Colors.white) : null,
                  ),
                ),
                const SizedBox(width: 16),
                Expanded(
                  child: Text(
                    displayTitle,
                    style: theme.textTheme.headlineMedium?.copyWith(
                      fontSize: 24,
                      decoration: widget.task.isCompleted ? TextDecoration.lineThrough : null,
                      color: widget.task.isCompleted ? (isDark ? Colors.white54 : AppColors.textSecondaryLight) : null,
                    ),
                  ),
                ),
              ],
            ),
            const SizedBox(height: 24),
            Row(
              children: [
                const Icon(Icons.calendar_today, size: 20, color: AppColors.primary),
                const SizedBox(width: 12),
                Text(
                  DateFormat('EEEE, MMMM d, y').format(widget.task.date),
                  style: theme.textTheme.titleMedium,
                ),
              ],
            ),
            const SizedBox(height: 12),
            Row(
              children: [
                const Icon(Icons.access_time, size: 20, color: AppColors.primary),
                const SizedBox(width: 12),
                Text(
                  DateFormat('h:mm a').format(widget.task.date),
                  style: theme.textTheme.titleMedium,
                ),
              ],
            ),
            const SizedBox(height: 32),
            const Text('Notes', style: TextStyle(fontWeight: FontWeight.bold, fontSize: 18)),
            const SizedBox(height: 12),
            TextField(
              controller: _notesController,
              maxLines: 8,
              decoration: InputDecoration(
                hintText: 'Add some details...',
                filled: true,
                fillColor: theme.cardColor,
                border: OutlineInputBorder(borderRadius: BorderRadius.circular(16), borderSide: BorderSide.none),
                contentPadding: const EdgeInsets.all(16),
              ),
            ),
            const SizedBox(height: 20),
            SizedBox(
              width: double.infinity,
              child: ElevatedButton(
                onPressed: _saveNotes,
                child: const Text('Save Notes', style: TextStyle(fontSize: 16, fontWeight: FontWeight.bold)),
              ),
            ),
          ],
        ),
      ),
    );
  }
}
