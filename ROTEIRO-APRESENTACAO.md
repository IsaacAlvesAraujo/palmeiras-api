# Roteiro para apresentação (15 minutos)

## Introdução (2 min)
“Minha API representa o gerenciamento de informações do Palmeiras. Temos seis entidades e relacionamentos, operações CRUD, consultas paginadas e documentação Swagger. Na segunda etapa implementei recursos de segurança e confiabilidade.”

## Demonstração (8 min)
1. Abra a página inicial e Swagger; mostre as seis entidades.
2. Use Authorize com chave administrativa para POST /api-keys e crie um usuário de demonstração. Troque Authorize para chave gerada.
3. Crie estádio, campeonato, equipe (IDs anteriores), jogador (equipeId), contrato (jogadorId), salário. Use Postman ou Swagger. Demonstre GET /jogadores?page=0&size=2 e /buscar?nome=Jogador.
4. Repita POST /estadios com mesmo X-Idempotency-Key e corpo. Mostre mesmo ID e Idempotent-Replayed. Troque corpo com mesma chave: 422.
5. Retire a chave e acesse /salarios: 401. Use chave normal novamente.
6. GET /jogadores/{id}/resumo com X-API-Version 1 e 2. Compare campos.
7. Mostre 400 com nome vazio; 404 com ID inexistente; 429 em execução separada com RATE_LIMIT=3. Mostre preflight CORS no Postman.

## Arquitetura (3 min)
Navegador/Postman -> filtro CORS -> autenticação -> rate limiting -> idempotência -> Controller -> Repository -> JPA/H2. DTO valida o corpo e recebe IDs; Controller resolve os relacionamentos e cria EntityModel/PagedModel. @RestControllerAdvice trata erros de entrada e integridade. ApiKeys é um Service. Docker empacota o jar; Render executa a aplicação. Swagger documenta headers e respostas.

## Perguntas (2 min)
- Idempotência: evita duplicação por repetição do mesmo POST, sem criar outro ID.
- API Key: identifica e autoriza cliente, não é uma senha de usuário. Chaves armazenadas por hash.
- Rate limiting: janela de 60s reduz excesso; Retry-After indica tempo para tentar de novo.
- CORS: política de acesso entre origens aplicada pelo navegador; não substitui autenticação.
- Versionamento: permite mudar a resposta sem quebrar clientes da versão anterior.
- HATEOAS: links que ajudam navegar na API; Pageable limita os resultados.
- OneToOne jogador/contrato; OneToMany equipe/jogadores e estádio/equipes; ManyToMany equipe/campeonatos.
- H2 em memória: demonstração simples; dados somem ao reiniciar. Para produção seria necessário persistência e armazenamento compartilhado de chaves, limite e idempotência.

Antes da apresentação, faça um ensaio completo. Valores e jogadores utilizados nos testes são exemplos fictícios, não dados oficiais do clube.
