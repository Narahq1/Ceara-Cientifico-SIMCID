# SIMCID v2.5 — Simulador de Rotas Urbanas
### Backend Spring Boot + Frontend Flutter

Este pacote contém a reimplementação do SIMCID (antes um protótipo em HTML/CSS/JS
puro) como uma aplicação full stack: **API REST em Java/Spring Boot** (grafo com
JGraphT) e **app em Flutter** consumindo essa API.

```
.
├── backend/     # API REST Spring Boot (Java 17, Maven, JGraphT)
├── frontend/    # App Flutter (Dart, http)
└── README.md    # este arquivo
```

A malha viária e os pontos de interesse foram portados 1:1 do protótipo original
(`app.js`): grid ortogonal de **9 colunas x 7 linhas = 63 cruzamentos**, **110
trechos de rua** de **0,25 km** cada, e os mesmos **21 pontos selecionáveis**
(10 escolas + 11 pontos gerais: prefeitura, hospital, terminal, etc.).

---

## 1. Pré-requisitos

Instale antes de começar:

| Ferramenta | Versão mínima | Verificar com |
|---|---|---|
| JDK        | 17            | `java -version` |
| Maven      | 3.9+          | `mvn -version` |
| Flutter SDK| 3.3+ (com Dart) | `flutter --version` |

Se não tiver Maven instalado:
- **macOS (Homebrew):** `brew install maven`
- **Ubuntu/Debian:** `sudo apt install maven`
- **Windows (Chocolatey):** `choco install maven`

Se não tiver Flutter instalado, siga `https://docs.flutter.dev/get-started/install`
e depois rode `flutter doctor` para confirmar que está tudo certo.

---

## 2. Rodando o backend (Spring Boot)

```bash
cd backend

# 1) Compila o projeto e roda os testes JUnit (unitários + integração)
mvn clean install

# 2) Sobe a API na porta 8080
mvn spring-boot:run
```

Se preferir rodar só os testes, sem subir o servidor:

```bash
mvn test
```

Quando o log mostrar `Started SimcidBackendApplication`, a API está no ar.
Confirme com:

```bash
curl http://localhost:8080/api/v1/health
# {"status":"UP","service":"simcid-backend"}

curl http://localhost:8080/api/v1/points
# lista os 21 pontos (10 escolas + 11 pontos gerais)

curl http://localhost:8080/api/v1/graph
# a malha viária completa (63 nós / 110 arestas)

curl -X POST http://localhost:8080/api/v1/routes/simulate \
  -H "Content-Type: application/json" \
  -d '{"originId":"A","destinationId":"H","requiredPointIds":[]}'
# JSON completo: menor rota, maior rota e impacto (diário/semanal/mensal/anual)
```

### Alternativa: gerar e rodar o .jar diretamente

```bash
cd backend
mvn clean package
java -jar target/simcid-backend.jar
```

---

## 3. Rodando o frontend (Flutter)

Em **outro terminal**, com o backend já rodando:

```bash
cd frontend

# 1) Baixa as dependências
flutter pub get

# 2) Roda os testes
flutter test

# 3) Lista os dispositivos/emuladores disponíveis
flutter devices

# 4) Roda o app (escolha um dos comandos abaixo conforme o dispositivo)
flutter run -d chrome        # Flutter Web no navegador
flutter run                  # deixa o Flutter perguntar/usar o dispositivo conectado
```

### Importante: apontando o app para o backend certo

Por padrão, o app chama `http://localhost:8080/api/v1` (arquivo
`lib/services/api_service.dart`). Isso funciona direto para **Flutter Web** e
para apps **desktop** rodando na mesma máquina que o backend. Ajuste conforme
seu cenário:

| Onde o app roda | URL a usar |
|---|---|
| Flutter Web / Desktop (mesma máquina do backend) | `http://localhost:8080/api/v1` (padrão, não precisa mudar) |
| Emulador Android | `http://10.0.2.2:8080/api/v1` |
| Dispositivo físico (celular na mesma rede Wi-Fi) | `http://SEU_IP_LOCAL:8080/api/v1` (ex.: `http://192.168.0.10:8080/api/v1`) |

Para alterar, edite a primeira linha do construtor em
`frontend/lib/services/api_service.dart`:

```dart
ApiService({this.baseUrl = 'http://localhost:8080/api/v1', http.Client? client})
```

---

## 4. Fluxo do app

1. **Tela inicial** — escolha origem, destino e (opcional) até 6 pontos
   obrigatórios de passagem, então toque em "Simular rota".
2. **Resultado** — mostra a menor rota (Dijkstra) e a maior rota encontrada,
   com distância, tempo estimado e a ordem dos pontos visitados.
3. **Impacto** — três abas (Menor rota / Maior rota / Economia), cada uma com
   o consumo de combustível, custo, emissão de CO₂ e tempo, projetados nas
   escalas diária, semanal (7x), mensal (30x) e anual (365x).

---

## 5. Constantes usadas no cálculo de impacto

Definidas em `backend/src/main/resources/application.yml` (seção `simcid.impact`),
lidas pelo `ImpactCalculatorService`:

- Consumo: **2 litros por km**
- Custo do combustível: **R$ 6,30 por litro**
- Velocidade média: **25 km/h**
- Emissão: **1,8 kg de CO₂ por km**

Você pode recalibrar esses valores editando o `application.yml` sem precisar
mexer em nenhuma classe Java.

---

## 6. Nota de engenharia sobre a "maior rota"

O `RouteService` calcula a menor rota com o algoritmo de **Dijkstra** (via
JGraphT) e a maior rota com uma **busca em profundidade (DFS) exaustiva**, que
percorre o espaço de caminhos simples entre origem e destino
(`LongestPathFinder`, pacote `graph`).

Em um grid de 63 cruzamentos, o número de caminhos simples entre dois pontos
distantes cresce de forma combinatória (pode passar de centenas de milhares).
Para a API nunca travar, a busca é interrompida por um limite de segurança
configurável em `application.yml` (`simcid.route.longest-route-max-paths-explored`
e `simcid.route.longest-route-max-elapsed-millis`, padrão: 150.000 caminhos
completos ou 4 segundos, o que vier primeiro). Quando o limite é atingido, o
JSON de resposta traz `"searchLimitReached": true` e o resultado é o **maior
caminho encontrado até aquele ponto** da busca — não uma garantia matemática
de que nenhum caminho maior existe. Isso é uma decisão de engenharia
deliberada (o próprio protótipo original também limitava essa busca com
`beamWidth`/`maxSteps`), e está documentada nos comentários Javadoc da classe.

---

## 7. Testes

- **Backend (JUnit 5):**
  - `CityGraphFactoryTest` — topologia do grafo (63 nós, 110 arestas, pesos)
  - `ImpactCalculatorServiceTest` — matemática do impacto (valores exatos)
  - `RouteServiceTest` — Dijkstra, busca da maior rota, pontos obrigatórios, validações
  - `RouteControllerIntegrationTest` — testes de integração ponta-a-ponta via MockMvc,
    cobrindo os 4 endpoints e os códigos de erro (400/404)

  Rodar: `cd backend && mvn test`

- **Frontend:** `cd frontend && flutter test` (smoke test garantindo que o app sobe sem exceções)

---

## 8. Estrutura de pastas

```
backend/
  pom.xml
  src/main/java/com/simcid/backend/
    SimcidBackendApplication.java
    config/            (SimcidProperties, CorsConfig)
    domain/             (PointType, PointOfInterest)
    graph/              (CityMapData, CityGraphFactory, LongestPathFinder)
    dto/                (records: PointDTO, RouteRequestDTO, RouteSimulationResponseDTO, ...)
    service/            (PointService, GraphQueryService, ImpactCalculatorService, RouteService)
    controller/          (RouteController)
    exception/          (GlobalExceptionHandler + exceções de domínio)
  src/main/resources/application.yml
  src/test/java/...     (JUnit 5: unitários + integração)

frontend/
  pubspec.yaml
  lib/
    main.dart
    theme/app_theme.dart        (paleta azul-marinho + branco)
    models/                     (mapeiam 1:1 os DTOs da API)
    services/api_service.dart   (cliente HTTP)
    screens/                    (home, result, impact)
    widgets/                    (componentes reutilizáveis)
  test/widget_test.dart
```
