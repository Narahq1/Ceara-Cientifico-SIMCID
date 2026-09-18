import 'point.dart';

/// Resultado de uma rota calculada, espelhando `RouteSummaryDTO`.
///
/// Também é usado para a maior rota (`LongestRouteSummaryDTO`), que estende
/// os mesmos campos com [pathsExplored] e [searchLimitReached] — por isso
/// esses dois campos são opcionais aqui (nulos para a menor rota).
class RouteSummaryModel {
  final double distanceKm;
  final double estimatedTimeMinutes;
  final List<String> nodePath;
  final List<PointModel> pointOrder;
  final int edgeCount;
  final int? pathsExplored;
  final bool? searchLimitReached;

  const RouteSummaryModel({
    required this.distanceKm,
    required this.estimatedTimeMinutes,
    required this.nodePath,
    required this.pointOrder,
    required this.edgeCount,
    this.pathsExplored,
    this.searchLimitReached,
  });

  factory RouteSummaryModel.fromJson(Map<String, dynamic> json) {
    return RouteSummaryModel(
      distanceKm: (json['distanceKm'] as num).toDouble(),
      estimatedTimeMinutes: (json['estimatedTimeMinutes'] as num).toDouble(),
      nodePath: (json['nodePath'] as List<dynamic>).cast<String>(),
      pointOrder: (json['pointOrder'] as List<dynamic>)
          .map((e) => PointModel.fromJson(e as Map<String, dynamic>))
          .toList(),
      edgeCount: json['edgeCount'] as int,
      pathsExplored: json['pathsExplored'] as int?,
      searchLimitReached: json['searchLimitReached'] as bool?,
    );
  }

  /// Nomes dos pontos visitados, prontos para exibição (ex.: "A → D → H").
  String get orderLabel =>
      pointOrder.isEmpty ? 'Sem ponto intermediário' : pointOrder.map((p) => p.name).join(' → ');
}
