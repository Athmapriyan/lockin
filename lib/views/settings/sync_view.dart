import 'dart:convert';
import 'dart:math';
import 'package:flutter/material.dart';
import 'package:flutter/services.dart';
import 'package:provider/provider.dart';
import 'package:qr_flutter/qr_flutter.dart';
import 'package:mobile_scanner/mobile_scanner.dart';
import 'package:supabase_flutter/supabase_flutter.dart';
import '../../viewmodels/task_store.dart';
import '../../models/task_item.dart';
import '../../theme/app_colors.dart';

class SyncView extends StatefulWidget {
  const SyncView({super.key});

  @override
  State<SyncView> createState() => _SyncViewState();
}

class _SyncViewState extends State<SyncView> {
  bool _isGenerating = false;
  String? _generatedCode;
  bool _isScanning = false;
  final TextEditingController _codeController = TextEditingController();

  String _generateRandomCode() {
    const chars = 'ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789';
    final rnd = Random();
    return String.fromCharCodes(Iterable.generate(
        6, (_) => chars.codeUnitAt(rnd.nextInt(chars.length))));
  }

  Future<void> _generateAndPushCode() async {
    setState(() => _isGenerating = true);
    
    try {
      final code = _generateRandomCode();
      final taskStore = context.read<TaskStore>();
      final payload = jsonEncode(taskStore.tasks.map((e) => e.toJson()).toList());
      
      await Supabase.instance.client.from('sync_sessions').upsert({
        'sync_code': code,
        'payload': payload,
      });

      setState(() {
        _generatedCode = code;
        _isGenerating = false;
      });
    } catch (e) {
      setState(() => _isGenerating = false);
      if (mounted) {
        ScaffoldMessenger.of(context).showSnackBar(
          SnackBar(content: Text('Failed to generate code: $e')),
        );
      }
    }
  }

  Future<void> _pullTasks(String code) async {
    if (code.isEmpty) return;
    
    final taskStore = context.read<TaskStore>();
    setState(() => _isScanning = true);
    try {
      final response = await Supabase.instance.client
          .from('sync_sessions')
          .select('payload')
          .eq('sync_code', code.toUpperCase())
          .maybeSingle();

      if (response == null) {
        throw Exception('Sync code not found or expired.');
      }

      final String rawPayload = response['payload'];
      final List<dynamic> jsonList = jsonDecode(rawPayload);
      final tasks = jsonList.map((e) => TaskItem.fromJson(e)).toList();

      for (final t in tasks) {
        if (!taskStore.tasks.any((existing) => existing.id == t.id)) {
          await taskStore.addTask(t);
        } else {
          await taskStore.updateTask(t); // Update existing
        }
      }

      if (mounted) {
        setState(() => _isScanning = false);
        ScaffoldMessenger.of(context).showSnackBar(
          const SnackBar(content: Text('Tasks synced successfully!')),
        );
        Navigator.pop(context);
      }
    } catch (e) {
      if (mounted) {
        setState(() => _isScanning = false);
        ScaffoldMessenger.of(context).showSnackBar(
          SnackBar(content: Text('Failed to sync tasks: $e')),
        );
      }
    }
  }

  @override
  void dispose() {
    _codeController.dispose();
    super.dispose();
  }

  @override
  Widget build(BuildContext context) {
    final theme = Theme.of(context);

    return Scaffold(
      appBar: AppBar(title: const Text('Device Sync')),
      body: SingleChildScrollView(
        padding: const EdgeInsets.all(20),
        child: Column(
          children: [
            if (_generatedCode == null) ...[
              Card(
                color: theme.cardColor,
                child: Padding(
                  padding: const EdgeInsets.all(20),
                  child: Column(
                    children: [
                      const Icon(Icons.qr_code_2, size: 64, color: AppColors.primary),
                      const SizedBox(height: 16),
                      const Text(
                        'Push Data to New Device',
                        style: TextStyle(fontSize: 18, fontWeight: FontWeight.bold),
                      ),
                      const SizedBox(height: 8),
                      const Text(
                        'Generate a QR Code and 6-digit sync code for another device to scan.',
                        textAlign: TextAlign.center,
                      ),
                      const SizedBox(height: 24),
                      SizedBox(
                        width: double.infinity,
                        child: ElevatedButton(
                          onPressed: _isGenerating ? null : _generateAndPushCode,
                          child: _isGenerating
                              ? const CircularProgressIndicator(color: Colors.white)
                              : const Text('Generate Sync Code'),
                        ),
                      ),
                    ],
                  ),
                ),
              ),
              const SizedBox(height: 32),
              Card(
                color: theme.cardColor,
                child: Padding(
                  padding: const EdgeInsets.all(20),
                  child: Column(
                    children: [
                      const Icon(Icons.download_rounded, size: 64, color: Colors.purple),
                      const SizedBox(height: 16),
                      const Text(
                        'Pull Data to This Device',
                        style: TextStyle(fontSize: 18, fontWeight: FontWeight.bold),
                      ),
                      const SizedBox(height: 8),
                      const Text(
                        'Enter a 6-digit code or scan a QR code to import your tasks.',
                        textAlign: TextAlign.center,
                      ),
                      const SizedBox(height: 24),
                      Row(
                        children: [
                          Expanded(
                            child: TextField(
                              controller: _codeController,
                              textCapitalization: TextCapitalization.characters,
                              maxLength: 6,
                              decoration: const InputDecoration(
                                hintText: 'Enter 6-digit code',
                                counterText: '',
                                border: OutlineInputBorder(),
                                filled: false,
                              ),
                            ),
                          ),
                          const SizedBox(width: 8),
                          ElevatedButton(
                            style: ElevatedButton.styleFrom(
                              backgroundColor: Colors.purple,
                            ),
                            onPressed: _isScanning ? null : () => _pullTasks(_codeController.text),
                            child: _isScanning
                                ? const SizedBox(width: 20, height: 20, child: CircularProgressIndicator(strokeWidth: 2))
                                : const Text('Sync'),
                          ),
                        ],
                      ),
                      const SizedBox(height: 16),
                      ElevatedButton.icon(
                        icon: const Icon(Icons.camera_alt),
                        label: const Text('Scan QR Code'),
                        onPressed: () {
                          Navigator.push(
                            context,
                            MaterialPageRoute(
                              builder: (context) => ScanQRView(
                                onDetect: (code) {
                                  Navigator.pop(context); // close scanner
                                  _codeController.text = code;
                                  _pullTasks(code);
                                },
                              ),
                            ),
                          );
                        },
                      ),
                    ],
                  ),
                ),
              ),
            ] else ...[
              Card(
                color: theme.cardColor,
                child: Padding(
                  padding: const EdgeInsets.all(24),
                  child: Column(
                    mainAxisSize: MainAxisSize.min,
                    children: [
                      Text(
                        'Your Sync Code',
                        style: theme.textTheme.titleMedium,
                      ),
                      const SizedBox(height: 16),
                      Container(
                        padding: const EdgeInsets.symmetric(horizontal: 24, vertical: 12),
                        decoration: BoxDecoration(
                          color: AppColors.primary.withValues(alpha: 0.1),
                          borderRadius: BorderRadius.circular(12),
                        ),
                        child: Row(
                          mainAxisSize: MainAxisSize.min,
                          children: [
                            Text(
                              _generatedCode!,
                              style: const TextStyle(
                                fontSize: 32,
                                fontWeight: FontWeight.w800,
                                letterSpacing: 8,
                                color: AppColors.primary,
                              ),
                            ),
                            IconButton(
                              icon: const Icon(Icons.copy, color: AppColors.primary),
                              onPressed: () {
                                Clipboard.setData(ClipboardData(text: _generatedCode!));
                                ScaffoldMessenger.of(context).showSnackBar(
                                  const SnackBar(content: Text('Code copied to clipboard')),
                                );
                              },
                            )
                          ],
                        ),
                      ),
                      const SizedBox(height: 32),
                      Container(
                        padding: const EdgeInsets.all(16),
                        color: Colors.white,
                        child: QrImageView(
                          data: _generatedCode!,
                          version: QrVersions.auto,
                          size: 200.0,
                        ),
                      ),
                      const SizedBox(height: 32),
                      const Text(
                        'Scan this QR code from your other device to synchronize your tasks.',
                        textAlign: TextAlign.center,
                        style: TextStyle(color: AppColors.textSecondaryLight),
                      ),
                      const SizedBox(height: 16),
                      TextButton(
                        onPressed: () => setState(() => _generatedCode = null),
                        child: const Text('Done'),
                      )
                    ],
                  ),
                ),
              ),
            ]
          ],
        ),
      ),
    );
  }
}

class ScanQRView extends StatelessWidget {
  final Function(String) onDetect;

  const ScanQRView({super.key, required this.onDetect});

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(title: const Text('Scan Sync Code')),
      body: MobileScanner(
        onDetect: (capture) {
          final List<Barcode> barcodes = capture.barcodes;
          if (barcodes.isNotEmpty && barcodes.first.rawValue != null) {
            final code = barcodes.first.rawValue!;
            onDetect(code);
          }
        },
      ),
    );
  }
}
