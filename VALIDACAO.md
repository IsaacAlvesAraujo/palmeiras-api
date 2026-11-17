# Validação da revisão
- ApiKeyFilter: condição de autenticação dividida em variáveis readOnly e sensitive, corrigindo o parêntese ausente.
- 36 arquivos Java (produção e testes) analisados pelo parser oficial do JDK 21 com --release 17: zero erros de sintaxe.
- Maven clean test: bloqueado por indisponibilidade de DNS/acesso ao Maven Central neste ambiente. Compilação com dependências, inicialização e testes HTTP ainda precisam ser executados localmente.
- Nenhuma alteração nas versões das dependências ou no contrato da API.
