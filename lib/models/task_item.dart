class TaskItem {
  final String id;
  final String title;
  final String notes;
  final DateTime date;
  final bool isCompleted;
  final bool isPrivate;

  TaskItem({
    required this.id,
    required this.title,
    this.notes = '',
    required this.date,
    this.isCompleted = false,
    this.isPrivate = false,
  });

  TaskItem copyWith({
    String? id,
    String? title,
    String? notes,
    DateTime? date,
    bool? isCompleted,
    bool? isPrivate,
  }) {
    return TaskItem(
      id: id ?? this.id,
      title: title ?? this.title,
      notes: notes ?? this.notes,
      date: date ?? this.date,
      isCompleted: isCompleted ?? this.isCompleted,
      isPrivate: isPrivate ?? this.isPrivate,
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
    );
  }
}
