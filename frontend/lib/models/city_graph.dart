/// Um cruzamento da malha viária, espelhando `GraphNodeDTO`.
class GraphNodeModel {
  final String id;
  final int column;
  final int row;

  const GraphNodeModel({
    required this.id,
    required this.column,
    required this.row,
  });

  factory GraphNodeModel.fromJson(Map<String, dynamic> json) {
    return GraphNodeModel(
      id: json['id'] as String,
      column: json['column'] as int,
      row: json['row'] as int,
    );
  }
}

/// Um trecho de rua entre dois cruzamentos, espelhando `GraphEdgeDTO`.
class GraphEdgeModel {
  final String from;
  final String to;
  final double weightKm;

  const GraphEdgeModel({
    required this.from,
    required this.to,
    required this.weightKm,
  });

  factory GraphEdgeModel.fromJson(Map<String, dynamic> json) {
    return GraphEdgeModel(
      from: json['from'] as String,
      to: json['to'] as String,
      weightKm: (json['weightKm'] as num).toDouble(),
    );
  }
}

/// A malha viária completa (63 nós / 110 arestas), espelhando `GraphDTO`.
class CityGraphModel {
  final List<GraphNodeModel> nodes;
  final List<GraphEdgeModel> edges;

  const CityGraphModel({required this.nodes, required this.edges});

  factory CityGraphModel.fromJson(Map<String, dynamic> json) {
    return CityGraphModel(
      nodes: (json['nodes'] as List<dynamic>)
          .map((e) => GraphNodeModel.fromJson(e as Map<String, dynamic>))
          .toList(),
      edges: (json['edges'] as List<dynamic>)
          .map((e) => GraphEdgeModel.fromJson(e as Map<String, dynamic>))
          .toList(),
    );
  }
}
