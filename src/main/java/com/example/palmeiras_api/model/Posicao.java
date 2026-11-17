package com.example.palmeiras_api.model;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Posições disponíveis para um jogador.")
public enum Posicao {
    GOLEIRO,
    ZAGUEIRO,
    LATERAL,
    VOLANTE,
    MEIA,
    ATACANTE
}
