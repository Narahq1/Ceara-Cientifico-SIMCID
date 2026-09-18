/// Impacto (combustível, custo, CO2, tempo) de uma distância, espelhando `ImpactBreakdownDTO`.
class ImpactBreakdownModel {
  final double distanceKm;
  final double fuelLiters;
  final double costReais;
  final double co2Kg;
  final double timeMinutes;

  const ImpactBreakdownModel({
    required this.distanceKm,
    required this.fuelLiters,
    required this.costReais,
    required this.co2Kg,
    required this.timeMinutes,
  });

  factory ImpactBreakdownModel.fromJson(Map<String, dynamic> json) {
    return ImpactBreakdownModel(
      distanceKm: (json['distanceKm'] as num).toDouble(),
      fuelLiters: (json['fuelLiters'] as num).toDouble(),
      costReais: (json['costReais'] as num).toDouble(),
      co2Kg: (json['co2Kg'] as num).toDouble(),
      timeMinutes: (json['timeMinutes'] as num).toDouble(),
    );
  }
}

/// O mesmo impacto projetado em 4 escalas de tempo, espelhando `ImpactScalesDTO`.
class ImpactScalesModel {
  final ImpactBreakdownModel daily;
  final ImpactBreakdownModel weekly;
  final ImpactBreakdownModel monthly;
  final ImpactBreakdownModel annual;

  const ImpactScalesModel({
    required this.daily,
    required this.weekly,
    required this.monthly,
    required this.annual,
  });

  factory ImpactScalesModel.fromJson(Map<String, dynamic> json) {
    return ImpactScalesModel(
      daily: ImpactBreakdownModel.fromJson(json['daily'] as Map<String, dynamic>),
      weekly: ImpactBreakdownModel.fromJson(json['weekly'] as Map<String, dynamic>),
      monthly: ImpactBreakdownModel.fromJson(json['monthly'] as Map<String, dynamic>),
      annual: ImpactBreakdownModel.fromJson(json['annual'] as Map<String, dynamic>),
    );
  }

  /// Retorna o breakdown correspondente à escala nomeada ("daily", "weekly", "monthly", "annual").
  ImpactBreakdownModel byScale(String scale) {
    switch (scale) {
      case 'weekly':
        return weekly;
      case 'monthly':
        return monthly;
      case 'annual':
        return annual;
      case 'daily':
      default:
        return daily;
    }
  }
}

/// Impacto completo da simulação, espelhando `RouteImpactDTO`.
class RouteImpactModel {
  final ImpactScalesModel shortestRoute;
  final ImpactScalesModel longestRoute;
  final ImpactScalesModel savings;

  const RouteImpactModel({
    required this.shortestRoute,
    required this.longestRoute,
    required this.savings,
  });

  factory RouteImpactModel.fromJson(Map<String, dynamic> json) {
    return RouteImpactModel(
      shortestRoute: ImpactScalesModel.fromJson(json['shortestRoute'] as Map<String, dynamic>),
      longestRoute: ImpactScalesModel.fromJson(json['longestRoute'] as Map<String, dynamic>),
      savings: ImpactScalesModel.fromJson(json['savings'] as Map<String, dynamic>),
    );
  }
}
