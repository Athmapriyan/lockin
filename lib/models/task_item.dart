class TaskItem {
  final String id;
  final String title;
  final String notes;
  final DateTime date;
  final bool isCompleted;
  final bool isPrivate;
  final int priority; // 0 = Low, 1 = Medium, 2 = High

  TaskItem({
    required this.id,
    required this.title,
    this.notes = '',
    required this.date,
    this.isCompleted = false,
    this.isPrivate = false,
    this.priority = 1,
  });

  TaskItem copyWith({
    String? id,
    String? title,
    String? notes,
    DateTime? date,
    bool? isCompleted,
    bool? isPrivate,
    int? priority,
  }) {
    return TaskItem(
      id: id ?? this.id,
      title: title ?? this.title,
      notes: notes ?? this.notes,
      date: date ?? this.date,
      isCompleted: isCompleted ?? this.isCompleted,
      isPrivate: isPrivate ?? this.isPrivate,
      priority: priority ?? this.priority,
    );
  }

  Map<String, dynamic> toJson() {
    return {
      'id': id,
      'title': title,
      'notes': notes,
      'date': date.toIso8601String(),
      'isCompleted': isCompleted,
      'isPrivate': isPrivate,
      'priority': priority,
    };
  }

  factory TaskItem.fromJson(Map<String, dynamic> map) {
    return TaskItem(
      id: map['id'] ?? '',
      title: map['title'] ?? '',
      notes: map['notes'] ?? '',
      date: DateTime.parse(map['date']),
      isCompleted: map['isCompleted'] ?? false,
      isPrivate: map['isPrivate'] ?? false,
      priority: map['priority'] ?? 1,
    );
  }
}
