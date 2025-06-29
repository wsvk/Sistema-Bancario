# Sistema Bancario

Trabalho da disciplina de introducao ao desenvolvimento web.

## 🗄️ Banco de Dados (PostgreSQL + Docker)

Esta seção descreve como subir e utilizar o banco de dados PostgreSQL do sistema bancário localmente com Docker, além de explicar os arquivos envolvidos.

---

### 🚀 Como subir o banco

**Pré-requisitos:**
- Docker instalado
- Docker Compose instalado (ou Docker Desktop)

**Passos:**
1. No terminal, navegue até a pasta do projeto e execute:
   ```bash
   docker-compose up -d
   ```

2. O banco será acessível com os seguintes dados:
   - Host: `localhost`
   - Porta: `5432`
   - Banco: `sistema_bancario`
   - Usuário: `devweb`
   - Senha: `123`

---

### 📁 Arquivos envolvidos

- **`docker-compose.yml`**  
  Define e sobe o serviço PostgreSQL, cria o banco `bancodb`, define usuário/senha e executa o script `init.sql` na primeira inicialização.

- **`init.sql`**  
  Script SQL com o seguinte:
  - Criação das tabelas `usuarios`, `contas`, `transacoes` e `investimentos`
  - Definição de chaves primárias, estrangeiras e regras de consistência
  - Inserção de dados iniciais para testes

---

### 🧪 Dados de exemplo incluídos

- **Usuários:**
  - João Silva (`joao@email.com`)
  - Maria Souza (`maria@email.com`)

- **Contas:**
  - Conta corrente para João com R$ 1000,00
  - Conta poupança para Maria com R$ 500,00

- **Transações:**
  - Dois depósitos
  - Uma transferência de João para Maria

- **Investimentos:**
  - João aplicou R$ 300,00 em CDB
  - Maria aplicou R$ 150,00 em Tesouro

---

### 🛑 Como parar e limpar o banco

- Parar o container:
  ```bash
  docker-compose down
  ```

- Parar e **remover os dados**:
  ```bash
  docker-compose down -v
  ```

---

> ⚠️ O script `init.sql` roda **somente na primeira criação do container**. Para reaplicar, é necessário remover o volume com `docker-compose down -v`.

