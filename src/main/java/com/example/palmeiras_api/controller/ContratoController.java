package com.example.palmeiras_api.controller;

import com.example.palmeiras_api.dto.ContratoRequest;
import com.example.palmeiras_api.model.Contrato;
import com.example.palmeiras_api.model.Jogador;
import com.example.palmeiras_api.repository.ContratoRepository;
import com.example.palmeiras_api.repository.JogadorRepository;
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

import java.util.List;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/contratos")
@Tag(name = "Contratos", description = "CRUD, consulta personalizada, paginação e HATEOAS de contratos.")
public class ContratoController {
    private final ContratoRepository repository;
    private final JogadorRepository jogadorRepository;
    public ContratoController(ContratoRepository repository, JogadorRepository jogadorRepository) {
        this.repository = repository; this.jogadorRepository = jogadorRepository;
    }

    @PostMapping
    @Operation(summary = "Cadastrar contrato", description = "Cria um novo recurso após validar o corpo da requisição.")
    @ApiResponses({@ApiResponse(responseCode="201", description="Criado com sucesso"), @ApiResponse(responseCode="400", description="Dados inválidos")})
    public ResponseEntity<EntityModel<Contrato>> cadastrar(@Valid @RequestBody ContratoRequest request) {
        Contrato salvo = repository.save(fromRequest(new Contrato(), request));
        return ResponseEntity.status(HttpStatus.CREATED).body(toModel(salvo));
    }

    @GetMapping
    @Operation(summary = "Listar com paginação", description = "Lista recursos usando Pageable. page começa em 0; size define itens por página; sort define ordenação. Ex.: ?page=0&size=5&sort=id,asc")
    @ApiResponse(responseCode="200", description="Página retornada com links HATEOAS")
    public ResponseEntity<PagedModel<EntityModel<Contrato>>> listar(@ParameterObject @PageableDefault(size=10, sort="id", direction=Sort.Direction.ASC) Pageable pageable) {
        return ResponseEntity.ok(toPagedModel(repository.findAll(pageable), "/contratos"));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar por ID", description = "Busca um único recurso pelo identificador.")
    @ApiResponses({@ApiResponse(responseCode="200", description="Encontrado"), @ApiResponse(responseCode="404", description="ID não encontrado")})
    public ResponseEntity<EntityModel<Contrato>> buscarPorId(@Parameter(description="ID do recurso", example="1") @PathVariable Long id) {
        return repository.findById(id).map(x -> ResponseEntity.ok(toModel(x))).orElseGet(() -> missing());
    }

    @PutMapping("/{id}")
    @Operation(summary = "Atualizar por ID", description = "Substitui os dados do recurso existente mantendo o mesmo ID.")
    @ApiResponses({@ApiResponse(responseCode="200", description="Atualizado"), @ApiResponse(responseCode="400", description="Dados inválidos"), @ApiResponse(responseCode="404", description="ID não encontrado")})
    public ResponseEntity<EntityModel<Contrato>> atualizar(@PathVariable Long id, @Valid @RequestBody ContratoRequest request) {
        Contrato existente = repository.findById(id).orElseThrow(() -> notFound("Contrato", id));
        return ResponseEntity.ok(toModel(repository.save(fromRequest(existente, request))));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Excluir por ID", description = "Exclui o recurso. Em sucesso retorna 204 sem corpo.")
    @ApiResponses({@ApiResponse(responseCode="204", description="Excluído"), @ApiResponse(responseCode="404", description="ID não encontrado")})
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        if (repository.excluirPorId(id) == 0) return missing();
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/buscar")
    @Operation(summary = "Consulta personalizada", description = "Consulta personalizada paginada exigida pelo projeto. O filtro é aplicado pelo Spring Data JPA.")
    @ApiResponse(responseCode="200", description="Resultado paginado da consulta")
    public ResponseEntity<PagedModel<EntityModel<Contrato>>> buscar(
            @Parameter(description="Duração em meses", example="24") @RequestParam Integer duracaoMeses,
            @ParameterObject @PageableDefault(size=10, sort="id", direction=Sort.Direction.ASC) Pageable pageable) {
        Page<Contrato> page = repository.findByDuracaoMeses(duracaoMeses, pageable);
        return ResponseEntity.ok(toPagedModel(page, "/contratos/buscar"));
    }

    private Contrato fromRequest(Contrato contrato, ContratoRequest request) {
        Jogador jogador = jogadorRepository.findById(request.jogadorId())
                .orElseThrow(() -> notFound("Jogador", request.jogadorId()));
        repository.findByJogadorId(jogador.getId()).filter(c -> !c.getId().equals(contrato.getId())).ifPresent(c -> {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "O jogador já possui um contrato");
        });
        contrato.setSalario(request.salario()); contrato.setDuracaoMeses(request.duracaoMeses()); contrato.setJogador(jogador);
        return contrato;
    }

    private ResponseStatusException notFound(String recurso, Long id) {
        return new ResponseStatusException(HttpStatus.NOT_FOUND, recurso + " de ID " + id + " não encontrado(a)");
    }

    private <T> ResponseEntity<T> missing(){throw new ResponseStatusException(HttpStatus.NOT_FOUND,"Recurso não encontrado");}

    private EntityModel<Contrato> toModel(Contrato item) {
        Long id = item.getId();
        return EntityModel.of(item,
                item.getJogador()==null?Link.of("/jogadores").withRel("jogador"):Link.of("/jogadores/"+item.getJogador().getId()).withRel("jogador"),
                Link.of("/contratos/" + id).withSelfRel(),
                Link.of("/contratos/" + id).withRel("update"),
                Link.of("/contratos/" + id).withRel("delete"),
                Link.of("/contratos").withRel("collection"));
    }

    private PagedModel<EntityModel<Contrato>> toPagedModel(Page<Contrato> page, String basePath) {
        List<EntityModel<Contrato>> content = page.getContent().stream().map(this::toModel).toList();
        PagedModel.PageMetadata metadata = new PagedModel.PageMetadata(page.getSize(), page.getNumber(), page.getTotalElements(), page.getTotalPages());
        PagedModel<EntityModel<Contrato>> model = PagedModel.of(content, metadata);
        model.add(Link.of(org.springframework.web.servlet.support.ServletUriComponentsBuilder.fromCurrentRequest().replaceQueryParam("page", page.getNumber()).replaceQueryParam("size",page.getSize()).build().toUriString()).withSelfRel());
        if (page.hasPrevious()) model.add(Link.of(org.springframework.web.servlet.support.ServletUriComponentsBuilder.fromCurrentRequest().replaceQueryParam("page", (page.getNumber()-1)).replaceQueryParam("size",page.getSize()).build().toUriString()).withRel("prev"));
        if (page.hasNext()) model.add(Link.of(org.springframework.web.servlet.support.ServletUriComponentsBuilder.fromCurrentRequest().replaceQueryParam("page", (page.getNumber()+1)).replaceQueryParam("size",page.getSize()).build().toUriString()).withRel("next"));
        return model;
    }
}
