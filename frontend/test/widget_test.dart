import 'package:flutter/material.dart';
import 'package:flutter_test/flutter_test.dart';

import 'package:simcid_frontend/main.dart';

void main() {
  testWidgets('SimcidApp renders the home screen without throwing', (WidgetTester tester) async {
    await tester.pumpWidget(const SimcidApp());

    // A tela inicial deve mostrar o titulo do app na AppBar.
    expect(find.text('SIMCID · Simulador de Rotas'), findsOneWidget);

    // Como nao ha backend rodando durante o teste, a tela deve entrar em
    // estado de carregamento (nao deve lancar excecao nao tratada).
    await tester.pump(const Duration(milliseconds: 100));
    expect(find.byType(CircularProgressIndicator), findsWidgets);
  });
}
