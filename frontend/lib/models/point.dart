/// Representa um ponto selecionável (escola ou ponto geral da cidade),
/// espelhando o `PointDTO` devolvido pelo backend em `/api/v1/points`.
class PointModel {
  final String id;
  final String name;
  final String type;
  final String nodeId;
  final int column;
  final int row;

  const PointModel({
    required this.id,
    required this.name,
    required this.type,
    required this.nodeId,
    required this.column,
    required this.row,
  });

  factory PointModel.fromJson(Map<String, dynamic> json) {
    return PointModel(
      id: json['id'] as String,
      name: json['name'] as String,
      type: json['type'] as String,
      nodeId: json['nodeId'] as String,
      column: json['column'] as int,
      row: json['row'] as int,
    );
  }

  Map<String, dynamic> toJson() => {
        'id': id,
        'name': name,
        'type': type,
        'nodeId': nodeId,
        'column': column,
        'row': row,
      };

  @override
  String toString() => name;

  @override
  bool operator ==(Object other) =>
      identical(this, other) || (other is PointModel && other.id == id);

  @override
  int get hashCode => id.hashCode;
}
