package com.example.palmeiras_api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.List;

@Schema(description = "Dados para criar ou atualizar uma equipe. Relacionamentos são informados por ID.")
public record EquipeRequest(
        @NotBlank @Size(min = 2, max = 100)
        @Schema(example = "Palmeiras", description = "Nome da equipe") String nome,
        @NotNull
        @Schema(example = "2", description = "ID de um estádio já cadastrado") Long estadioId,
        @Schema(example = "[1, 2]", description = "IDs de campeonatos já cadastrados") List<Long> campeonatoIds
) {}
