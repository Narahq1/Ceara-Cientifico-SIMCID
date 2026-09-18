import 'dart:convert';
import 'package:http/http.dart' as http;

import '../models/api_error.dart';
import '../models/city_graph.dart';
import '../models/point.dart';
import '../models/simulation_response.dart';

/// Cliente HTTP para a API REST do SIMCID (backend Spring Boot).
///
/// O [baseUrl] padrão funciona para Flutter Web e para apps desktop/iOS
/// rodando no mesmo computador que o backend. Se estiver testando em um
/// **emulador Android**, troque "localhost" por "10.0.2.2" (endereço que o
/// emulador usa para enxergar o host). Em um **dispositivo físico**, use o IP
/// da máquina que roda o backend na sua rede local (ex.: "http://192.168.0.10:8080/api/v1").
class ApiService {
  final String baseUrl;
  final http.Client _client;

  ApiService({this.baseUrl = 'http://localhost:8080/api/v1', http.Client? client})
      : _client = client ?? http.Client();

  Future<List<PointModel>> fetchPoints() async {
    final response = await _client.get(Uri.parse('$baseUrl/points'));
    final decoded = _decode(response);
    return (decoded as List<dynamic>)
        .map((e) => PointModel.fromJson(e as Map<String, dynamic>))
        .toList();
  }

  Future<CityGraphModel> fetchGraph() async {
    final response = await _client.get(Uri.parse('$baseUrl/graph'));
    final decoded = _decode(response);
    return CityGraphModel.fromJson(decoded as Map<String, dynamic>);
  }

  Future<SimulationResponseModel> simulateRoute({
    required String originId,
    required String destinationId,
    List<String> requiredPointIds = const [],
  }) async {
    final response = await _client.post(
      Uri.parse('$baseUrl/routes/simulate'),
      headers: const {'Content-Type': 'application/json'},
      body: jsonEncode({
        'originId': originId,
        'destinationId': destinationId,
        'requiredPointIds': requiredPointIds,
      }),
    );
    final decoded = _decode(response);
    return SimulationResponseModel.fromJson(decoded as Map<String, dynamic>);
  }

  /// Decodifica o corpo da resposta e lança [ApiException] em caso de erro HTTP.
  dynamic _decode(http.Response response) {
    final bodyText = response.body.isEmpty ? '{}' : response.body;
    final decoded = jsonDecode(bodyText);

    if (response.statusCode >= 200 && response.statusCode < 300) {
      return decoded;
    }

    if (decoded is Map<String, dynamic>) {
      throw ApiException(ApiErrorModel.fromJson(decoded));
    }
    throw ApiException(ApiErrorModel(
      status: response.statusCode,
      error: 'Erro',
      message: 'Falha ao comunicar com o servidor (HTTP ${response.statusCode}).',
      path: response.request?.url.path ?? '',
    ));
  }

  void dispose() => _client.close();
}
