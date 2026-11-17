package com.example.palmeiras_api.dto;

import com.example.palmeiras_api.model.Posicao;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

@Schema(description = "Dados para criar ou atualizar um jogador. A equipe é informada pelo ID.")
public record JogadorRequest(
        @NotBlank @Size(min = 2, max = 100)
        @Schema(example = "Raphael Veiga") String nome,
        @NotNull
        @Schema(example = "MEIA") Posicao posicao,
        @NotNull
        @Schema(example = "3", description = "ID de uma equipe já cadastrada") Long equipeId
) {}
