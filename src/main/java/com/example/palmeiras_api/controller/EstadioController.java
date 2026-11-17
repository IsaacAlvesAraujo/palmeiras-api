package com.example.palmeiras_api.controller;

import com.example.palmeiras_api.model.Estadio;
import com.example.palmeiras_api.repository.EstadioRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.PagedModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/estadios")
@Tag(name = "Estádios", description = "CRUD, consulta personalizada, paginação e HATEOAS de estádios.")
public class EstadioController {
    private final EstadioRepository repository;
    public EstadioController(EstadioRepository repository) { this.repository = repository; }

    @PostMapping
    @Operation(summary = "Cadastrar estadio", description = "Cria um novo recurso após validar o corpo da requisição.")
    @ApiResponses({@ApiResponse(responseCode="201", description="Criado com sucesso"), @ApiResponse(responseCode="400", description="Dados inválidos")})
    public ResponseEntity<EntityModel<Estadio>> cadastrar(@Valid @RequestBody Estadio estadio) {
        estadio.setId(null);
        Estadio salvo = repository.save(estadio);
        return ResponseEntity.status(HttpStatus.CREATED).body(toModel(salvo));
    }

    @GetMapping
    @Operation(summary = "Listar com paginação", description = "Lista recursos usando Pageable. page começa em 0; size define itens por página; sort define ordenação. Ex.: ?page=0&size=5&sort=id,asc")
    @ApiResponse(responseCode="200", description="Página retornada com links HATEOAS")
    public ResponseEntity<PagedModel<EntityModel<Estadio>>> listar(@ParameterObject @PageableDefault(size=10, sort="id", direction=Sort.Direction.ASC) Pageable pageable) {
        return ResponseEntity.ok(toPagedModel(repository.findAll(pageable), "/estadios"));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar por ID", description = "Busca um único recurso pelo identificador.")
    @ApiResponses({@ApiResponse(responseCode="200", description="Encontrado"), @ApiResponse(responseCode="404", description="ID não encontrado")})
    public ResponseEntity<EntityModel<Estadio>> buscarPorId(@Parameter(description="ID do recurso", example="1") @PathVariable Long id) {
        return repository.findById(id).map(x -> ResponseEntity.ok(toModel(x))).orElseGet(() -> missing());
    }

    @PutMapping("/{id}")
    @Operation(summary = "Atualizar por ID", description = "Substitui os dados do recurso existente mantendo o mesmo ID.")
    @ApiResponses({@ApiResponse(responseCode="200", description="Atualizado"), @ApiResponse(responseCode="400", description="Dados inválidos"), @ApiResponse(responseCode="404", description="ID não encontrado")})
    public ResponseEntity<EntityModel<Estadio>> atualizar(@PathVariable Long id, @Valid @RequestBody Estadio dados) {
        if (!repository.existsById(id)) return missing();
        dados.setId(id);
        return ResponseEntity.ok(toModel(repository.save(dados)));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Excluir por ID", description = "Exclui o recurso. Em sucesso retorna 204 sem corpo.")
    @ApiResponses({@ApiResponse(responseCode="204", description="Excluído"), @ApiResponse(responseCode="404", description="ID não encontrado")})
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        if (!repository.existsById(id)) return missing();
        repository.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/buscar")
    @Operation(summary = "Consulta personalizada", description = "Consulta personalizada paginada exigida pelo projeto. O filtro é aplicado pelo Spring Data JPA.")
    @ApiResponse(responseCode="200", description="Resultado paginado da consulta")
    public ResponseEntity<PagedModel<EntityModel<Estadio>>> buscar(
            @Parameter(description="Nome do estádio", example="Allianz") @RequestParam String nome,
            @ParameterObject @PageableDefault(size=10, sort="id", direction=Sort.Direction.ASC) Pageable pageable) {
        Page<Estadio> page = repository.findByNomeContainingIgnoreCase(nome, pageable);
        return ResponseEntity.ok(toPagedModel(page, "/estadios/buscar"));
    }

    private <T> ResponseEntity<T> missing(){throw new ResponseStatusException(HttpStatus.NOT_FOUND,"Recurso não encontrado");}

    private EntityModel<Estadio> toModel(Estadio item) {
        Long id = item.getId();
        return EntityModel.of(item,
                Link.of("/estadios/" + id).withSelfRel(),
                Link.of("/estadios/" + id).withRel("update"),
                Link.of("/estadios/" + id).withRel("delete"),
                Link.of("/estadios").withRel("collection"));
    }

    private PagedModel<EntityModel<Estadio>> toPagedModel(Page<Estadio> page, String basePath) {
        List<EntityModel<Estadio>> content = page.getContent().stream().map(this::toModel).toList();
        PagedModel.PageMetadata metadata = new PagedModel.PageMetadata(page.getSize(), page.getNumber(), page.getTotalElements(), page.getTotalPages());
        PagedModel<EntityModel<Estadio>> model = PagedModel.of(content, metadata);
        model.add(Link.of(org.springframework.web.servlet.support.ServletUriComponentsBuilder.fromCurrentRequest().replaceQueryParam("page", page.getNumber()).replaceQueryParam("size",page.getSize()).build().toUriString()).withSelfRel());
        if (page.hasPrevious()) model.add(Link.of(org.springframework.web.servlet.support.ServletUriComponentsBuilder.fromCurrentRequest().replaceQueryParam("page", (page.getNumber()-1)).replaceQueryParam("size",page.getSize()).build().toUriString()).withRel("prev"));
        if (page.hasNext()) model.add(Link.of(org.springframework.web.servlet.support.ServletUriComponentsBuilder.fromCurrentRequest().replaceQueryParam("page", (page.getNumber()+1)).replaceQueryParam("size",page.getSize()).build().toUriString()).withRel("next"));
        return model;
    }
}
