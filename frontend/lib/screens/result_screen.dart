import 'package:flutter/material.dart';

import '../models/simulation_response.dart';
import '../theme/app_theme.dart';
import '../widgets/route_summary_card.dart';
import 'impact_screen.dart';

/// Exibe a menor e a maior rota encontradas entre origem e destino.
class ResultScreen extends StatelessWidget {
  final SimulationResponseModel response;

  const ResultScreen({super.key, required this.response});

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(title: const Text('Resultado da simulação')),
      body: SingleChildScrollView(
        padding: const EdgeInsets.all(20),
        child: Column(
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            _routeHeader(),
            const SizedBox(height: 20),
            RouteSummaryCard(
              title: 'MENOR ROTA (Dijkstra)',
              route: response.shortestRoute,
              accentColor: AppColors.success,
              icon: Icons.trending_down,
            ),
            const SizedBox(height: 16),
            RouteSummaryCard(
              title: 'MAIOR ROTA ENCONTRADA',
              route: response.longestRoute,
              accentColor: AppColors.warning,
              icon: Icons.trending_up,
            ),
            const SizedBox(height: 28),
            SizedBox(
              width: double.infinity,
              child: ElevatedButton.icon(
                onPressed: () {
                  Navigator.of(context).push(
                    MaterialPageRoute(builder: (_) => ImpactScreen(response: response)),
                  );
                },
                icon: const Icon(Icons.insights),
                label: const Text('Ver impacto (diário / semanal / mensal / anual)'),
              ),
            ),
          ],
        ),
      ),
    );
  }

  Widget _routeHeader() {
    return Container(
      padding: const EdgeInsets.all(18),
      decoration: BoxDecoration(
        color: AppColors.navy,
        borderRadius: BorderRadius.circular(16),
      ),
      child: Row(
        children: [
          const Icon(Icons.route, color: Colors.white),
          const SizedBox(width: 12),
          Expanded(
            child: Text(
              '${response.origin.name}  →  ${response.destination.name}',
              style: const TextStyle(color: Colors.white, fontWeight: FontWeight.w600, fontSize: 15),
            ),
          ),
        ],
      ),
    );
  }
}
