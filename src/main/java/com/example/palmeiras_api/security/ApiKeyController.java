package com.example.palmeiras_api.security;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.*;
import org.springframework.data.domain.*;
import org.springdoc.core.annotations.ParameterObject;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import io.swagger.v3.oas.annotations.Operation;
import java.util.*;
@RestController @RequestMapping("/api-keys") public class ApiKeyController {
 private final ApiKeys keys; public ApiKeyController(ApiKeys keys){this.keys=keys;}
 public record Request(@NotBlank @Size(max=100) String usuario){}
 @PostMapping @Operation(summary="Gerar chave (requer chave administrativa)") public ResponseEntity<Map<String,String>> create(@Valid @RequestBody Request req){String raw=keys.create(req.usuario());return ResponseEntity.status(201).body(Map.of("id",keys.find(raw).id(),"apiKey",raw,"usuario",req.usuario()));}
 @GetMapping @Operation(summary="Listar metadados das chaves (administrador)") public Page<ApiKeys.KeyInfo> list(@ParameterObject Pageable page){var all=keys.list();int start=(int)Math.min(page.getOffset(),all.size());return new PageImpl<>(all.subList(start,Math.min(start+page.getPageSize(),all.size())),page,all.size());}
 @DeleteMapping("/{id}") @Operation(summary="Revogar chave (administrador)") public ResponseEntity<Void> delete(@PathVariable String id){return keys.revoke(id)?ResponseEntity.noContent().build():ResponseEntity.notFound().build();}
}
