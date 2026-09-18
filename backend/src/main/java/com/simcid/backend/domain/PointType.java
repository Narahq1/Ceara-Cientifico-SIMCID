package com.simcid.backend.domain;

/**
 * Categoria de um ponto de interesse no mapa da cidade.
 * Espelha os "kind" usados no prototipo original (escolas + pontos gerais da aba "Mapa Geral").
 */
public enum PointType {
    SCHOOL,
    GOVERNMENT,
    LEISURE,
    CHURCH,
    TRANSPORT,
    COMMERCE,
    SECURITY,
    FUEL,
    HEALTH,
    SERVICE
}
