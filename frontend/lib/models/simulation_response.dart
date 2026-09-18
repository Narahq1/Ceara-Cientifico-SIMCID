import 'impact.dart';
import 'point.dart';
import 'route_summary.dart';

/// JSON completo devolvido por `POST /api/v1/routes/simulate`,
/// espelhando `RouteSimulationResponseDTO`.
class SimulationResponseModel {
  final PointModel origin;
  final PointModel destination;
  final List<PointModel> requiredPoints;
  final RouteSummaryModel shortestRoute;
  final RouteSummaryModel longestRoute;
  final RouteImpactModel impact;

  const SimulationResponseModel({
    required this.origin,
    required this.destination,
    required this.requiredPoints,
    required this.shortestRoute,
    required this.longestRoute,
    required this.impact,
  });

  factory SimulationResponseModel.fromJson(Map<String, dynamic> json) {
    return SimulationResponseModel(
      origin: PointModel.fromJson(json['origin'] as Map<String, dynamic>),
      destination: PointModel.fromJson(json['destination'] as Map<String, dynamic>),
      requiredPoints: (json['requiredPoints'] as List<dynamic>)
          .map((e) => PointModel.fromJson(e as Map<String, dynamic>))
          .toList(),
      shortestRoute: RouteSummaryModel.fromJson(json['shortestRoute'] as Map<String, dynamic>),
      longestRoute: RouteSummaryModel.fromJson(json['longestRoute'] as Map<String, dynamic>),
      impact: RouteImpactModel.fromJson(json['impact'] as Map<String, dynamic>),
    );
  }
}
