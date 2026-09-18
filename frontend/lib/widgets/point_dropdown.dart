import 'package:flutter/material.dart';
import '../models/point.dart';
import '../theme/app_theme.dart';

/// Dropdown reutilizável para escolher um [PointModel] dentre a lista carregada da API.
class PointDropdown extends StatelessWidget {
  final String label;
  final List<PointModel> points;
  final PointModel? value;
  final ValueChanged<PointModel?> onChanged;

  const PointDropdown({
    super.key,
    required this.label,
    required this.points,
    required this.value,
    required this.onChanged,
  });

  @override
  Widget build(BuildContext context) {
    return DropdownButtonFormField<PointModel>(
      value: value,
      isExpanded: true,
      decoration: InputDecoration(
        labelText: label,
        prefixIcon: const Icon(Icons.place_outlined, color: AppColors.navy),
      ),
      items: points
          .map((point) => DropdownMenuItem<PointModel>(
                value: point,
                child: Text(point.name, overflow: TextOverflow.ellipsis),
              ))
          .toList(),
      onChanged: onChanged,
    );
  }
}
