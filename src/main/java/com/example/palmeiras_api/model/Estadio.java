package com.example.palmeiras_api.model;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.NotBlank;
import java.util.List;

@Entity
@Schema(description = "Estádio relacionado às equipes cadastradas.")
public class Estadio {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "Identificador único do estádio", example = "1", accessMode = Schema.AccessMode.READ_ONLY)
    private Long id;

    @NotBlank(message = "O nome do estádio é obrigatório")
    @Size(min=2, max=120)
    @Schema(description = "Nome do estádio", example = "Allianz Parque", requiredMode = Schema.RequiredMode.REQUIRED)
    private String nome;

    @NotBlank(message = "O endereço é obrigatório")
    @Schema(description = "Endereço do estádio", example = "Av. Francisco Matarazzo, 1705 - São Paulo/SP", requiredMode = Schema.RequiredMode.REQUIRED)
    private String endereco;

    @OneToMany(mappedBy = "estadio")
    @com.fasterxml.jackson.annotation.JsonIgnore
    @Schema(description = "Equipes que utilizam o estádio")
    private List<Equipe> equipes;

    public Estadio() {}
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }
    public String getEndereco() { return endereco; }
    public void setEndereco(String endereco) { this.endereco = endereco; }
    public List<Equipe> getEquipes() { return equipes; }
    public void setEquipes(List<Equipe> equipes) { this.equipes = equipes; }
}
