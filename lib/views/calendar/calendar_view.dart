import 'package:flutter/material.dart';
import 'package:table_calendar/table_calendar.dart';
import 'package:provider/provider.dart';
import '../../viewmodels/task_store.dart';
import '../../viewmodels/settings_store.dart';
import '../../theme/app_colors.dart';
import '../widgets/lockin_card.dart';

class CalendarView extends StatefulWidget {
  const CalendarView({super.key});

  @override
  State<CalendarView> createState() => _CalendarViewState();
}

class _CalendarViewState extends State<CalendarView> {
  DateTime _focusedDay = DateTime.now();
  DateTime? _selectedDay;
  CalendarFormat _calendarFormat = CalendarFormat.month;

  @override
  void initState() {
    super.initState();
    _selectedDay = _focusedDay;
  }

  @override
  Widget build(BuildContext context) {
    final taskStore = context.watch<TaskStore>();
    final settingsStore = context.watch<SettingsStore>();
    final isDark = Theme.of(context).brightness == Brightness.dark;

    final selectedTasks = taskStore.tasks.where((t) {
      if (_selectedDay == null) return false;
      return t.date.year == _selectedDay!.year &&
          t.date.month == _selectedDay!.month &&
          t.date.day == _selectedDay!.day;
    }).toList();

    return Scaffold(
      appBar: AppBar(title: const Text('Calendar')),
      body: Column(
        children: [
          Container(
            margin: const EdgeInsets.all(20),
            padding: const EdgeInsets.all(8),
            decoration: BoxDecoration(
              color: Theme.of(context).cardColor,
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
            child: TableCalendar(
              firstDay: DateTime.utc(2020, 10, 16),
              lastDay: DateTime.utc(2030, 3, 14),
              focusedDay: _focusedDay,
              calendarFormat: _calendarFormat,
              selectedDayPredicate: (day) => isSameDay(_selectedDay, day),
              onDaySelected: (selectedDay, focusedDay) {
                setState(() {
                  _selectedDay = selectedDay;
                  _focusedDay = focusedDay;
                });
              },
              onFormatChanged: (format) {
                if (_calendarFormat != format) {
                  setState(() {
                    _calendarFormat = format;
                  });
                }
              },
              calendarStyle: const CalendarStyle(
                selectedDecoration: BoxDecoration(
                  color: AppColors.primary,
                  shape: BoxShape.circle,
                ),
                todayDecoration: BoxDecoration(
                  color: AppColors.primaryLight,
                  shape: BoxShape.circle,
                ),
              ),
              headerStyle: const HeaderStyle(
                formatButtonVisible: true,
                formatButtonShowsNext: false,
                titleCentered: true,
              ),
            ),
          ),
          Padding(
            padding:
                const EdgeInsets.symmetric(horizontal: 20, vertical: 8),
            child: Align(
              alignment: Alignment.centerLeft,
              child: Text(
                'Tasks',
                style: Theme.of(context)
                    .textTheme
                    .titleLarge
                    ?.copyWith(fontSize: 18),
              ),
            ),
          ),
          Expanded(
            child: selectedTasks.isEmpty
                ? Center(
                    child: Text('No tasks for this day',
                        style: TextStyle(
                            color: AppColors.textSecondaryLight)))
                : ListView.builder(
                    padding: const EdgeInsets.only(bottom: 100),
                    itemCount: selectedTasks.length,
                    itemBuilder: (context, index) {
                      final task = selectedTasks[index];
                      return LockInCard(
                        task: task,
                        isPrivacyMode: settingsStore.isPrivacyModeActive,
                        onToggle: () =>
                            taskStore.toggleTaskCompletion(task.id),
                      );
                    },
                  ),
          ),
        ],
      ),
    );
  }
}
