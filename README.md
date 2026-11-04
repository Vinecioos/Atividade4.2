# Vinícius Amâncio Bueno Pinto - 234960

# Como executar o projeto PetCare API

Guia passo a passo, do zero até a API respondendo e os testes rodando.

## 1. Executar a API

Dentro da pasta do projeto:

```bash
mvn spring-boot:run
```

Na primeira vez, o Maven baixa as dependências (pode levar alguns minutos). A API está pronta quando aparecer algo como:

```
Started PetCareApplication in 4.2 seconds
```

**Deixe esse terminal aberto**: a API só responde enquanto o comando estiver rodando. Para parar, use `Ctrl + C`.

### Conferir se funcionou

Abra no navegador: <http://localhost:8081/tutores>

Deve aparecer `[]` (lista vazia), pois ainda não há tutores cadastrados.

## 2. Executar os testes automatizados

```bash
mvn test
```

Ao final, deve aparecer `BUILD SUCCESS`. Os testes não precisam da API no ar nem de banco instalado (usam o H2 em memória):

| Classe | Tipo | O que verifica |
|---|---|---|
| `PetCareApiTests` | Integração | Fluxo completo dos 3 módulos, com sucessos e erros |
| `ConsultaServiceImplTest` | Unitário | Regras de agendamento e cancelamento |
| `TutorServiceImplTest` | Unitário | Regras de cadastro de tutor |



## 3. Banco de dados

### H2 em memória (padrão)

Não precisa instalar nada. **Os dados são apagados toda vez que a API é reiniciada.**

Console web do banco: <http://localhost:8081/h2-console>

| Campo | Valor |
|---|---|
| JDBC URL | `jdbc:h2:mem:petcare` |
| User Name | `sa` |
| Password | _(vazio)_ |

### PostgreSQL (opcional)

1. Crie o banco: `CREATE DATABASE petcare;`
2. Abra `src/main/resources/application.properties`.
3. Comente as linhas do H2 e descomente o bloco do PostgreSQL (ajuste usuário e senha, se necessário):

```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/petcare
spring.datasource.driver-class-name=org.postgresql.Driver
spring.datasource.username=postgres
spring.datasource.password=postgres
```

4. Rode `mvn spring-boot:run`. As tabelas são criadas automaticamente.

## 4. Testar os endpoints no Postman

1. Com a API rodando, abra o **Postman Desktop**.
2. **Import** → selecione `docs/postman/PetCare_API.postman_collection.json`.
3. Clique nos `...` da coleção **PetCare API** → **Run collection** → **Run PetCare API**.
4. As 33 requisições devem aparecer como **PASS**.

> **Antes de repetir a bateria, reinicie a API** (o banco em memória é zerado). Detalhes e a lista de casos em [`docs/testes-postman.md`](docs/testes-postman.md).
.

## 5. Problemas comuns

| Sintoma | Causa provável | Solução |
|---|---|---|
| `mvn` não é reconhecido | Maven não instalado ou fora do PATH | Instale o Maven e abra um novo terminal |
| `invalid target release: 17` ou `release version 17 not supported` | JDK anterior ao 17 | Instale o JDK 17+ e confira com `java -version` |
| `Port 8080 was already in use` | Outro programa usa a porta 8080 | Feche-o ou use outra porta (seção 9). No Windows: `netstat -ano \| findstr :8080` |
| Erro ao baixar dependências | Sem internet, proxy ou firewall | Confira a conexão; em rede corporativa, configure o proxy no `~/.m2/settings.xml` |
| `Connection refused` ao usar PostgreSQL | Banco desligado ou dados errados | Confira se o PostgreSQL está rodando e usuário, senha e nome do banco |
| Postman: `Could not get response` | API desligada ou porta diferente | Suba a API e confira se está em `localhost:8080` |
| Muitos `FAIL` no Postman logo no início | Dados da rodada anterior continuam no banco | Reinicie a API e rode a coleção de novo |
| Agendamento retorna `400` por data | A data usada já passou (a coleção usa `2030-03-10`) | Use uma data futura nas requisições de agendamento |

# Print dos testes

<img width="1217" height="355" alt="Captura de tela 2026-11-04 203344" src="https://github.com/user-attachments/assets/12b8851f-6810-4158-a238-68619f47d9d0" />
<img width="1216" height="376" alt="Captura de tela 2026-11-04 203238" src="https://github.com/user-attachments/assets/a6281fca-a9e0-4e01-b17d-f76f5a926f4b" />
<img width="1221" height="667" alt="Captura de tela 2026-11-04 204116" src="https://github.com/user-attachments/assets/d94694e2-344b-4f0d-b43f-d12458d372c5" />
<img width="1194" height="578" alt="Captura de tela 2026-11-04 203731" src="https://github.com/user-attachments/assets/155c28ca-cf40-419f-be9c-240f1e42d4da" />
<img width="1193" height="640" alt="Captura de tela 2026-11-04 203542" src="https://github.com/user-attachments/assets/2b33647e-605c-4729-b9eb-ac6cd2aadba1" />
<img width="1196" height="733" alt="Captura de tela 2026-11-04 203412" src="https://github.com/user-attachments/assets/cf7ea2d8-9e4c-49e7-9de3-7283d7ba6183" />

# Diagrama UML
<img width="1974" height="1752" alt="mermaid-diagram" src="https://github.com/user-attachments/assets/9da25542-c5da-4df8-af83-3aae12d1b93a" />
