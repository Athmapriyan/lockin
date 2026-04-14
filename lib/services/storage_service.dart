import 'dart:convert';
import 'dart:io';
import 'package:flutter/foundation.dart';
import 'package:path_provider/path_provider.dart';
import '../models/task_item.dart';

class StorageService {
  static const String _fileName = 'lockin_tasks.json';

  Future<File> get _localFile async {
    final directory = await getApplicationDocumentsDirectory();
    return File('${directory.path}/$_fileName');
  }

  Future<List<TaskItem>> loadTasks() async {
    try {
      final file = await _localFile;
      if (!await file.exists()) {
        return [];
      }
      
      final contents = await file.readAsString();
      if (contents.isEmpty) return [];

      final List<dynamic> jsonList = jsonDecode(contents);
      return jsonList.map((e) => TaskItem.fromJson(e)).toList();
    } catch (e) {
      debugPrint('Error loading tasks: $e');
      return [];
    }
  }

  Future<void> saveTasks(List<TaskItem> tasks) async {
    try {
      final file = await _localFile;
      final String jsonString = jsonEncode(tasks.map((e) => e.toJson()).toList());
      
      await file.writeAsString(jsonString);
    } catch (e) {
      debugPrint('Error saving tasks: $e');
    }
  }
}
