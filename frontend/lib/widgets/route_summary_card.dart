import 'package:flutter/material.dart';
import '../models/route_summary.dart';
import '../theme/app_theme.dart';

/// Cartão que resume uma rota calculada (menor ou maior), com distância,
/// tempo estimado e a sequência de pontos visitados.
class RouteSummaryCard extends StatelessWidget {
  final String title;
  final RouteSummaryModel route;
  final Color accentColor;
  final IconData icon;

  const RouteSummaryCard({
    super.key,
    required this.title,
    required this.route,
    required this.accentColor,
    required this.icon,
  });

  @override
  Widget build(BuildContext context) {
    return Card(
      color: AppColors.surface,
      elevation: 0,
      margin: EdgeInsets.zero,
      shape: RoundedRectangleBorder(
        borderRadius: BorderRadius.circular(16),
        side: const BorderSide(color: AppColors.border),
      ),
      child: Padding(
        padding: const EdgeInsets.all(18),
        child: Column(
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            Row(
              children: [
                CircleAvatar(
                  backgroundColor: accentColor.withOpacity(0.12),
                  child: Icon(icon, color: accentColor, size: 20),
                ),
                const SizedBox(width: 12),
                Expanded(
                  child: Text(
                    title,
                    style: const TextStyle(
                      fontSize: 15,
                      fontWeight: FontWeight.w700,
                      color: AppColors.textPrimary,
                    ),
                  ),
                ),
              ],
            ),
            const SizedBox(height: 16),
            Row(
              children: [
                Expanded(child: _stat('Distância', '${route.distanceKm.toStringAsFixed(2)} km')),
                Expanded(child: _stat('Tempo estimado', '${route.estimatedTimeMinutes.toStringAsFixed(1)} min')),
                Expanded(child: _stat('Trechos', '${route.edgeCount}')),
              ],
            ),
            if (route.searchLimitReached == true) ...[
              const SizedBox(height: 12),
              Text(
                'Busca limitada por segurança (${route.pathsExplored} caminhos avaliados). '
                'Este é o maior caminho encontrado até o limite configurado.',
                style: const TextStyle(fontSize: 11, color: AppColors.warning),
              ),
            ],
            const SizedBox(height: 14),
            const Divider(height: 1),
            const SizedBox(height: 14),
            const Text(
              'Ordem dos pontos visitados',
              style: TextStyle(fontSize: 12, color: AppColors.textSecondary, fontWeight: FontWeight.w600),
            ),
            const SizedBox(height: 8),
            Text(
              route.orderLabel,
              style: const TextStyle(fontSize: 14, color: AppColors.textPrimary, height: 1.4),
            ),
          ],
        ),
      ),
    );
  }

  Widget _stat(String label, String value) {
    return Column(
      crossAxisAlignment: CrossAxisAlignment.start,
      children: [
        Text(value, style: const TextStyle(fontSize: 16, fontWeight: FontWeight.w700, color: AppColors.textPrimary)),
        const SizedBox(height: 2),
        Text(label, style: const TextStyle(fontSize: 11, color: AppColors.textSecondary)),
      ],
    );
  }
}
