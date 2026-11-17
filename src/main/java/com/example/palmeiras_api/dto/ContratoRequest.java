package com.example.palmeiras_api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.math.BigDecimal;

@Schema(description = "Dados para criar ou atualizar um contrato. O jogador é informado pelo ID.")
public record ContratoRequest(
        @NotNull @Positive @Schema(example = "850000.00") BigDecimal salario,
        @NotNull @Positive @Schema(example = "24") Integer duracaoMeses,
        @NotNull @Schema(example = "1", description = "ID de um jogador já cadastrado") Long jogadorId
) {}
