import 'package:flutter/material.dart';
import 'package:provider/provider.dart';
import '../../viewmodels/auth_store.dart';
import '../../theme/app_colors.dart';

class WelcomeView extends StatefulWidget {
  const WelcomeView({super.key});

  @override
  State<WelcomeView> createState() => _WelcomeViewState();
}

class _WelcomeViewState extends State<WelcomeView> {
  String _pin = '';

  @override
  void initState() {
    super.initState();
    WidgetsBinding.instance.addPostFrameCallback((_) {
      _attemptBiometric();
    });
  }

  Future<void> _attemptBiometric() async {
    final authStore = context.read<AuthStore>();
    await authStore.authenticateWithBiometrics();
  }

  void _onKeyPress(String val) {
    if (_pin.length < 4) {
      setState(() => _pin += val);
      if (_pin.length == 4) {
        _attemptLogin();
      }
    }
  }

  void _onBackspace() {
    if (_pin.isNotEmpty) {
      setState(() => _pin = _pin.substring(0, _pin.length - 1));
    }
  }

  Future<void> _attemptLogin() async {
    final authStore = context.read<AuthStore>();
    final success = await authStore.login(_pin);
    if (mounted && !success) {
      ScaffoldMessenger.of(context)
          .showSnackBar(const SnackBar(content: Text('Invalid PIN. Try again.')));
      setState(() => _pin = '');
    }
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      backgroundColor: Colors.black,
      body: SafeArea(
        child: Column(
          children: [
            const Spacer(),
            const Icon(Icons.lock, size: 64, color: AppColors.primary),
            const SizedBox(height: 24),
            const Text(
              'Welcome Back',
              style: TextStyle(
                  color: Colors.white,
                  fontSize: 32,
                  fontWeight: FontWeight.bold),
            ),
            const SizedBox(height: 8),
            const Text(
              'Securely access your tasks and reminders.',
              style: TextStyle(color: Colors.white54, fontSize: 16),
            ),
            const SizedBox(height: 48),
            Row(
              mainAxisAlignment: MainAxisAlignment.center,
              children: List.generate(4, (index) {
                return Container(
                  margin: const EdgeInsets.symmetric(horizontal: 12),
                  width: 20,
                  height: 20,
                  decoration: BoxDecoration(
                    shape: BoxShape.circle,
                    color: index < _pin.length
                        ? AppColors.primary
                        : Colors.white24,
                  ),
                );
              }),
            ),
            const SizedBox(height: 24),
            IconButton(
              onPressed: _attemptBiometric,
              icon: const Icon(Icons.fingerprint, color: AppColors.primary, size: 48),
            ),
            const Spacer(),
            _buildKeypad(),
            const SizedBox(height: 40),
          ],
        ),
      ),
    );
  }

  Widget _buildKeypad() {
    return Padding(
      padding: const EdgeInsets.symmetric(horizontal: 40),
      child: Column(
        children: [
          _buildKeyRow(['1', '2', '3']),
          const SizedBox(height: 16),
          _buildKeyRow(['4', '5', '6']),
          const SizedBox(height: 16),
          _buildKeyRow(['7', '8', '9']),
          const SizedBox(height: 16),
          Row(
            mainAxisAlignment: MainAxisAlignment.spaceEvenly,
            children: [
              const SizedBox(width: 80),
              _KeypadButton('0', onPressed: () => _onKeyPress('0')),
              SizedBox(
                width: 80,
                child: IconButton(
                  onPressed: _onBackspace,
                  icon: const Icon(Icons.backspace,
                      color: Colors.white, size: 28),
                ),
              ),
            ],
          ),
        ],
      ),
    );
  }

  Widget _buildKeyRow(List<String> keys) {
    return Row(
      mainAxisAlignment: MainAxisAlignment.spaceEvenly,
      children: keys
          .map((k) => _KeypadButton(k, onPressed: () => _onKeyPress(k)))
          .toList(),
    );
  }
}

class _KeypadButton extends StatelessWidget {
  final String text;
  final VoidCallback onPressed;

  const _KeypadButton(this.text, {required this.onPressed});

  @override
  Widget build(BuildContext context) {
    return GestureDetector(
      onTap: onPressed,
      child: Container(
        width: 80,
        height: 80,
        decoration: const BoxDecoration(
          color: Colors.white10,
          shape: BoxShape.circle,
        ),
        alignment: Alignment.center,
        child: Text(
          text,
          style: const TextStyle(
              color: Colors.white,
              fontSize: 32,
              fontWeight: FontWeight.normal),
        ),
      ),
    );
  }
}
