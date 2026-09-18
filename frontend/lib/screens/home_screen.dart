import 'package:flutter/material.dart';

import '../models/api_error.dart';
import '../models/point.dart';
import '../services/api_service.dart';
import '../theme/app_theme.dart';
import '../widgets/point_dropdown.dart';
import '../widgets/state_views.dart';
import 'result_screen.dart';

/// Tela inicial do SIMCID: seleção de origem, destino e pontos obrigatórios.
class HomeScreen extends StatefulWidget {
  const HomeScreen({super.key});

  @override
  State<HomeScreen> createState() => _HomeScreenState();
}

class _HomeScreenState extends State<HomeScreen> {
  final ApiService _apiService = ApiService();

  late Future<List<PointModel>> _pointsFuture;
  PointModel? _origin;
  PointModel? _destination;
  final Set<PointModel> _requiredPoints = {};
  bool _submitting = false;

  static const int _maxRequiredPoints = 6;

  @override
  void initState() {
    super.initState();
    _pointsFuture = _apiService.fetchPoints();
  }

  @override
  void dispose() {
    _apiService.dispose();
    super.dispose();
  }

  void _ensureDefaults(List<PointModel> points) {
    _origin ??= points.isNotEmpty ? points.first : null;
    _destination ??= points.length > 1 ? points[1] : _origin;
  }

  Future<void> _simulate() async {
    final origin = _origin;
    final destination = _destination;
    if (origin == null || destination == null) return;

    if (origin.id == destination.id) {
      _showError('Origem e destino devem ser pontos diferentes.');
      return;
    }

    setState(() => _submitting = true);
    try {
      final result = await _apiService.simulateRoute(
        originId: origin.id,
        destinationId: destination.id,
        requiredPointIds: _requiredPoints.map((p) => p.id).toList(),
      );
      if (!mounted) return;
      await Navigator.of(context).push(
        MaterialPageRoute(builder: (_) => ResultScreen(response: result)),
      );
    } on ApiException catch (e) {
      _showError(e.error.message);
    } catch (_) {
      _showError('Não foi possível conectar ao servidor. Verifique se o backend está rodando em ${_apiService.baseUrl}.');
    } finally {
      if (mounted) setState(() => _submitting = false);
    }
  }

  void _showError(String message) {
    ScaffoldMessenger.of(context).showSnackBar(
      SnackBar(content: Text(message), backgroundColor: AppColors.warning),
    );
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(title: const Text('SIMCID · Simulador de Rotas')),
      body: FutureBuilder<List<PointModel>>(
        future: _pointsFuture,
        builder: (context, snapshot) {
          if (snapshot.connectionState != ConnectionState.done) {
            return const LoadingView(label: 'Carregando pontos da cidade...');
          }
          if (snapshot.hasError) {
            return ErrorView(
              message: 'Não foi possível carregar os pontos.\n'
                  'Verifique se o backend está rodando em ${_apiService.baseUrl}.',
              onRetry: () => setState(() => _pointsFuture = _apiService.fetchPoints()),
            );
          }

          final points = snapshot.data ?? const <PointModel>[];
          if (points.isEmpty) {
            return const Center(child: Text('Nenhum ponto cadastrado.'));
          }

          _ensureDefaults(points);
          return _buildForm(points);
        },
      ),
    );
  }

  Widget _buildForm(List<PointModel> points) {
    final availableForRequired =
        points.where((p) => p.id != _origin?.id && p.id != _destination?.id).toList();

    return SingleChildScrollView(
      padding: const EdgeInsets.all(20),
      child: Column(
        crossAxisAlignment: CrossAxisAlignment.start,
        children: [
          const Text(
            'Planeje uma rota',
            style: TextStyle(fontSize: 22, fontWeight: FontWeight.w700, color: AppColors.textPrimary),
          ),
          const SizedBox(height: 4),
          const Text(
            'Escolha origem, destino e, se quiser, pontos obrigatórios de passagem.',
            style: TextStyle(color: AppColors.textSecondary),
          ),
          const SizedBox(height: 24),
          PointDropdown(
            label: 'Origem',
            points: points,
            value: _origin,
            onChanged: (p) => setState(() {
              _origin = p;
              if (p != null) _requiredPoints.remove(p);
            }),
          ),
          const SizedBox(height: 16),
          PointDropdown(
            label: 'Destino',
            points: points,
            value: _destination,
            onChanged: (p) => setState(() {
              _destination = p;
              if (p != null) _requiredPoints.remove(p);
            }),
          ),
          const SizedBox(height: 24),
          const Text(
            'Pontos obrigatórios (opcional)',
            style: TextStyle(fontWeight: FontWeight.w600, color: AppColors.textPrimary),
          ),
          const SizedBox(height: 4),
          Text(
            'Máximo de $_maxRequiredPoints pontos. A rota é obrigada a passar por todos os selecionados.',
            style: const TextStyle(fontSize: 12, color: AppColors.textSecondary),
          ),
          const SizedBox(height: 10),
          Wrap(
            spacing: 8,
            runSpacing: 8,
            children: availableForRequired.map((point) {
              final selected = _requiredPoints.contains(point);
              return FilterChip(
                label: Text(point.name),
                selected: selected,
                onSelected: (value) {
                  setState(() {
                    if (value) {
                      if (_requiredPoints.length < _maxRequiredPoints) {
                        _requiredPoints.add(point);
                      } else {
                        _showError('Máximo de $_maxRequiredPoints pontos obrigatórios.');
                      }
                    } else {
                      _requiredPoints.remove(point);
                    }
                  });
                },
              );
            }).toList(),
          ),
          const SizedBox(height: 32),
          SizedBox(
            width: double.infinity,
            child: ElevatedButton.icon(
              onPressed: _submitting ? null : _simulate,
              icon: _submitting
                  ? const SizedBox(
                      width: 18,
                      height: 18,
                      child: CircularProgressIndicator(strokeWidth: 2, color: Colors.white),
                    )
                  : const Icon(Icons.alt_route),
              label: Text(_submitting ? 'Calculando...' : 'Simular rota'),
            ),
          ),
        ],
      ),
    );
  }
}
