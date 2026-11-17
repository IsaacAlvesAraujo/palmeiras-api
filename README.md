# Palmeiras API — Parte 2

Projeto acadêmico individual: Spring Boot 4.1.1 (versão mantida do projeto original e do professor), Java 17+ (Docker Java 21), Maven, H2, JPA, Bean Validation, HATEOAS e OpenAPI.

## Executar no Windows (PowerShell)

```powershell
$env:ADMIN_API_KEY = "substitua-por-uma-chave-longa-e-privada"
.\mvnw.cmd clean test
.\mvnw.cmd spring-boot:run
```
No Linux/macOS: `export ADMIN_API_KEY=...` e `./mvnw spring-boot:run` (use `chmod +x mvnw` se necessário). Não envie a chave administrativa ao GitHub. O padrão sem variável deixa operações protegidas indisponíveis.

- Aplicação web: http://localhost:8080/
- Swagger: http://localhost:8080/swagger-ui.html
- OpenAPI: http://localhost:8080/v3/api-docs
- Saúde: http://localhost:8080/health

## Entidades e rotas

Para cada recurso `/estadios`, `/campeonatos`, `/equipes`, `/jogadores`, `/contratos`, `/salarios`: POST, GET paginado, GET /{id}, PUT /{id}, DELETE /{id}, GET /buscar paginado. Filtros: nome (quatro primeiros), duracaoMeses (contratos), cargo (salarios). Crie nesta ordem e exclua na ordem inversa. Equipe exige estadioId e pode receber campeonatoIds; jogador exige equipeId; contrato exige jogadorId. Salário é registro independente (cargo/valor), como na primeira entrega.

## Parte 2

- `X-API-Key`: administrador cria, lista e revoga chaves em /api-keys; envie chave administrativa nesse recurso apenas. Chaves normais autorizam POST/PUT/DELETE dos recursos e leitura de contratos/salários. Outros GET são públicos. Armazena hash SHA-256 das chaves normais em memória; resposta de geração mostra chave bruta uma vez (ou replay idempotente do administrador).
- `X-Idempotency-Key`: opcional no POST; use valor único por operação. Mesmo cliente, rota, versão, chave e corpo repetem resposta original (201, Idempotent-Replayed: true); corpo diferente 422; concorrência 409. Cache 24h em memória (adaptado do filtro do professor).
- Rate limiting: 60 requisições autorizadas por minuto por IP (GET público) ou identidade da chave, configurable RATE_LIMIT. Retorna X-Rate-Limit-Limit, X-Rate-Limit-Remaining e 429 com Retry-After. Requisições recusadas na autenticação não entram nesse contador.
- CORS: origens padrão http://localhost:3000 e http://localhost:5173; configure CORS_ORIGINS como lista separada por vírgulas. OPTIONS não exige chave.
- Versionamento: GET /jogadores/{id}/resumo; header X-API-Version: 1 retorna id/nome; 2 acrescenta posição e equipeId; outra versão retorna 400. Sem header usa 1.
- Erros: @RestControllerAdvice e respostas JSON nos filtros. HATEOAS com self, update, delete, collection, links de relacionamentos e paginação preservando filtros.

## Testes

Importe Palmeiras.postman_collection.json no Postman. Ajuste baseUrl e adminKey, execute a coleção em ordem com banco recém-iniciado. A coleção cria dados e os apaga ao final, portanto use somente ambiente de teste. Para rate limiting, inicie separadamente com RATE_LIMIT=3 e envie quatro GET /jogadores seguidos: quarto retorna 429. Para CORS, OPTIONS /jogadores com Origin: http://localhost:3000 e Access-Control-Request-Method: POST deve permitir a origem; repita com uma origem não autorizada para verificar rejeição. Reinicie com limite padrão antes de rodar a coleção.

## Publicar por Render (mesma estratégia do professor)

1. Crie repositório GitHub e envie os arquivos da pasta (não envie target, .env, chaves).
2. No Render, crie Web Service conectado ao repositório, selecione Docker; use a raiz que contém Dockerfile. Alternativamente use Blueprint com render.yaml.
3. Configure ADMIN_API_KEY como variável privada e, se necessário, RATE_LIMIT e CORS_ORIGINS. Health Check Path: /health.
4. Após o deploy bem-sucedido, abra a URL HTTPS fornecida pelo Render e acrescente /swagger-ui.html. O Swagger usa servidor relativo, não localhost. A página inicial já existe no projeto e aceita chave para visualizar dados protegidos.
5. Teste o Postman com baseUrl apontando para a URL online.

## Limitações e validação

H2 em memória, chaves, idempotência e contador são reiniciados junto com a aplicação. Solução de instância única para demonstração acadêmica; não é arquitetura de produção distribuída. Salários são dados fictícios de demonstração. Não foi possível compilar neste ambiente: Maven não está instalado e o DNS do repositório não está acessível. Execute os testes locais antes de entregar. Não há URL de deploy criada neste pacote.
