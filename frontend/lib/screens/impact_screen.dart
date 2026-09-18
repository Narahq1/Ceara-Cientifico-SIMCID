import 'package:flutter/material.dart';

import '../models/impact.dart';
import '../models/simulation_response.dart';
import '../theme/app_theme.dart';
import '../widgets/metric_card.dart';

/// Exibe o impacto (combustível, custo, CO2 e tempo) da menor rota, da maior
/// rota e da economia entre elas, nas quatro escalas de tempo.
class ImpactScreen extends StatelessWidget {
  final SimulationResponseModel response;

  const ImpactScreen({super.key, required this.response});

  @override
  Widget build(BuildContext context) {
    return DefaultTabController(
      length: 3,
      child: Scaffold(
        appBar: AppBar(
          title: const Text('Impacto da simulação'),
          bottom: const TabBar(
            indicatorColor: Colors.white,
            labelColor: Colors.white,
            unselectedLabelColor: Colors.white70,
            tabs: [
              Tab(text: 'Menor rota'),
              Tab(text: 'Maior rota'),
              Tab(text: 'Economia'),
            ],
          ),
        ),
        body: TabBarView(
          children: [
            _ScalesView(scales: response.impact.shortestRoute),
            _ScalesView(scales: response.impact.longestRoute),
            _ScalesView(scales: response.impact.savings, isSavings: true),
          ],
        ),
      ),
    );
  }
}

class _ScalesView extends StatelessWidget {
  final ImpactScalesModel scales;
  final bool isSavings;

  const _ScalesView({required this.scales, this.isSavings = false});

  @override
  Widget build(BuildContext context) {
    return ListView(
      padding: const EdgeInsets.all(20),
      children: [
        if (isSavings)
          Padding(
            padding: const EdgeInsets.only(bottom: 16),
            child: Text(
              'Diferença estimada ao optar pela menor rota em vez da maior rota encontrada, '
              'repetindo o trajeto todos os dias no período indicado.',
              style: const TextStyle(color: AppColors.textSecondary, fontSize: 13, height: 1.4),
            ),
          ),
        _scaleBlock('Diária', scales.daily),
        const SizedBox(height: 20),
        _scaleBlock('Semanal (7x)', scales.weekly),
        const SizedBox(height: 20),
        _scaleBlock('Mensal (30x)', scales.monthly),
        const SizedBox(height: 20),
        _scaleBlock('Anual (365x)', scales.annual),
      ],
    );
  }

  Widget _scaleBlock(String title, ImpactBreakdownModel breakdown) {
    return Column(
      crossAxisAlignment: CrossAxisAlignment.start,
      children: [
        Text(
          title,
          style: const TextStyle(fontSize: 15, fontWeight: FontWeight.w700, color: AppColors.textPrimary),
        ),
        const SizedBox(height: 4),
        Text(
          '${breakdown.distanceKm.toStringAsFixed(2)} km percorridos no período',
          style: const TextStyle(fontSize: 12, color: AppColors.textSecondary),
        ),
        const SizedBox(height: 10),
        GridView.count(
          crossAxisCount: 2,
          shrinkWrap: true,
          physics: const NeverScrollableScrollPhysics(),
          mainAxisSpacing: 10,
          crossAxisSpacing: 10,
          childAspectRatio: 1.6,
          children: [
            MetricCard(
              icon: Icons.local_gas_station_outlined,
              label: 'Combustível',
              value: '${breakdown.fuelLiters.toStringAsFixed(2)} L',
              accentColor: AppColors.accent,
            ),
            MetricCard(
              icon: Icons.attach_money,
              label: 'Custo estimado',
              value: 'R\$ ${breakdown.costReais.toStringAsFixed(2)}',
              accentColor: AppColors.navy,
            ),
            MetricCard(
              icon: Icons.eco_outlined,
              label: 'Emissão de CO₂',
              value: '${breakdown.co2Kg.toStringAsFixed(2)} kg',
              accentColor: AppColors.success,
            ),
            MetricCard(
              icon: Icons.schedule,
              label: 'Tempo estimado',
              value: '${breakdown.timeMinutes.toStringAsFixed(1)} min',
              accentColor: AppColors.warning,
            ),
          ],
        ),
      ],
    );
  }
}
