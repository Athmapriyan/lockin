import 'package:flutter/foundation.dart';
import '../models/task_item.dart';
import '../services/storage_service.dart';

class TaskStore extends ChangeNotifier {
  final StorageService _storageService = StorageService();
  List<TaskItem> _tasks = [];
  bool _isLoading = true;

  List<TaskItem> get tasks => _tasks;
  bool get isLoading => _isLoading;

  List<TaskItem> get todayTasks {
    final now = DateTime.now();
    return _tasks.where((t) => 
      t.date.year == now.year && 
      t.date.month == now.month && 
      t.date.day == now.day
    ).toList();
  }

  double get todayProgress {
    final today = todayTasks;
    if (today.isEmpty) return 0;
    final completed = today.where((t) => t.isCompleted).length;
    return completed / today.length;
  }

  TaskStore() {
    _loadTasks();
  }

  Future<void> _loadTasks() async {
    _isLoading = true;
    notifyListeners();
    
    _tasks = await _storageService.loadTasks();
    
    _isLoading = false;
    notifyListeners();
  }

  Future<void> addTask(TaskItem task) async {
    _tasks.add(task);
    notifyListeners();
    await _storageService.saveTasks(_tasks);
  }

  Future<void> toggleTaskCompletion(String id) async {
    final index = _tasks.indexWhere((t) => t.id == id);
    if (index != -1) {
      _tasks[index] = _tasks[index].copyWith(isCompleted: !_tasks[index].isCompleted);
      notifyListeners();
      await _storageService.saveTasks(_tasks);
    }
  }

  Future<void> deleteTask(String id) async {
    _tasks.removeWhere((t) => t.id == id);
    notifyListeners();
    await _storageService.saveTasks(_tasks);
  }
}
