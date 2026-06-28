# Project API - Desafio Técnico

Uma API RESTful para gerenciamento de Projetos e Membros, desenvolvida como parte de um desafio técnico. O sistema permite criar e acompanhar o ciclo de vida de projetos, gerenciar orçamentos, classificações de risco e associar membros (funcionários) aos projetos.

## 🚀 Tecnologias Utilizadas

- **Java 21**
- **Spring Boot** (Web, Data JPA, Security)
- **PostgreSQL** (Banco de dados relacional)
- **Docker** (Containerização do banco de dados)
- **Springdoc OpenAPI / Swagger** (Documentação da API)
- **Lombok** (Produtividade e redução de boilerplate)

## ⚙️ Como executar o projeto

### 1. Subindo o Banco de Dados (PostgreSQL)

O projeto requer um banco de dados PostgreSQL rodando na porta `5432`. A forma mais fácil de subir o banco é utilizando o Docker. 

No seu terminal, execute o comando abaixo para criar e iniciar o container:

```bash
docker run --name postgres-project -e POSTGRES_USER=postgres -e POSTGRES_PASSWORD=postgres -e POSTGRES_DB=projectdb -p 5432:5432 -d postgres
```

### 2. Rodando a Aplicação Spring Boot

Com o banco de dados rodando, você pode iniciar a aplicação utilizando o Maven Wrapper que já vem incluso no projeto.

- Se estiver usando o **Git Bash** ou **Linux/macOS**:
  ```bash
  ./mvnw spring-boot:run
  ```

- Se estiver usando o **PowerShell** no Windows:
  ```powershell
  .\mvnw.cmd spring-boot:run
  ```

> **Nota para usuários de Windows:** Caso encontre um erro de permissão no PowerShell (`Rename-Item`), utilize o terminal do **Git Bash** para evitar conflitos de bloqueio de pasta do próprio sistema operacional.

## 📚 Documentação da API (Swagger)

A API está totalmente documentada utilizando o Swagger. Quando a aplicação estiver rodando, você pode acessar a interface web para visualizar e testar todos os endpoints.

- **URL:** [http://localhost:8080/swagger-ui.html](http://localhost:8080/swagger-ui.html)
- **Usuário:** `admin`
- **Senha:** `admin123`

### Principais Endpoints

* **Projetos:**
  * `POST /api/projetos` - Cria um novo projeto.
  * `GET /api/projetos` - Lista os projetos (com paginação e filtro opcional por nome).
  * `GET /api/projetos/{id}` - Busca um projeto pelo ID.
  * `PUT /api/projetos/{id}` - Atualiza um projeto existente.
  * `DELETE /api/projetos/{id}` - Exclui um projeto (se o status permitir).
  * `POST /api/projetos/{projectId}/members/{memberId}` - Associa um membro a um projeto.
  * `DELETE /api/projetos/{projectId}/members/{memberId}` - Remove um membro de um projeto.

* **Membros:**
  * `POST /api/membros` - Cadastra um novo membro.

## 💼 Regras de Negócio Implementadas

- **Exclusão de Projetos:** Um projeto não pode ser excluído se o seu status for `INICIADO`, `EM_ANDAMENTO` ou `ENCERRADO`.
- **Associação de Membros:** Somente pessoas com atribuição de funcionário podem ser associadas aos projetos.
- **Limites de Alocação:** Uma pessoa pode fazer parte de 1 a 10 projetos no máximo. Se os projetos estiverem com status `ENCERRADO` ou `CANCELADO`, eles não contam para esse limite (o limite máximo de projetos ativos simultâneos é 3).
- **Classificação de Risco (Regra bônus dinâmica):** 
  - Projetos até 3 meses = Risco BAIXO
  - Projetos entre 3 a 6 meses = Risco MÉDIO
  - Projetos com mais de 6 meses = Risco ALTO
