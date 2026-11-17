package com.example.palmeiras_api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.Instant;

@Schema(description = "Resposta padronizada de erro")
public record ApiError(Instant timestamp, int status, String error, String message, String path) {}
