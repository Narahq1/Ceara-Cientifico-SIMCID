/// Corpo de erro padrão devolvido pela API, espelhando `ErrorResponseDTO`.
class ApiErrorModel {
  final int status;
  final String error;
  final String message;
  final String path;

  const ApiErrorModel({
    required this.status,
    required this.error,
    required this.message,
    required this.path,
  });

  factory ApiErrorModel.fromJson(Map<String, dynamic> json) {
    return ApiErrorModel(
      status: json['status'] as int? ?? 0,
      error: json['error'] as String? ?? 'Erro',
      message: json['message'] as String? ?? 'Ocorreu um erro inesperado.',
      path: json['path'] as String? ?? '',
    );
  }
}

/// Exceção lançada pelo [ApiService] quando a API responde com um corpo de erro conhecido.
class ApiException implements Exception {
  final ApiErrorModel error;

  const ApiException(this.error);

  @override
  String toString() => error.message;
}
