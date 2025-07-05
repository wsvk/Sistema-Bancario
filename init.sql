-- Criação da tabela de usuários
CREATE TABLE usuarios (
    id SERIAL PRIMARY KEY,
    nome VARCHAR(100) NOT NULL,
    email VARCHAR(100) NOT NULL UNIQUE,
    senha VARCHAR(255) NOT NULL
);

-- Criação da tabela de contas
CREATE TABLE contas (
    id SERIAL PRIMARY KEY,
    id_usuario INT NOT NULL,
    saldo NUMERIC(12, 2) DEFAULT 0.00,
    tipo VARCHAR(20) NOT NULL CHECK (tipo IN ('CORRENTE', 'POUPANCA')),
    FOREIGN KEY (id_usuario) REFERENCES usuarios(id) ON DELETE CASCADE
);

-- Criação da tabela de transações
CREATE TABLE transacoes (
    id SERIAL PRIMARY KEY,
    conta_origem_id INT,
    conta_destino_id INT,
    tipo VARCHAR(20) NOT NULL CHECK (tipo IN ('DEPOSITO', 'SAQUE', 'TRANSFERENCIA')),
    valor NUMERIC(12, 2) NOT NULL CHECK (valor > 0),
    data TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (conta_origem_id) REFERENCES contas(id),
    FOREIGN KEY (conta_destino_id) REFERENCES contas(id)
);

-- Criação da tabela de investimentos
CREATE TABLE investimentos (
    id SERIAL PRIMARY KEY,
    id_conta INT NOT NULL,
    tipo VARCHAR(20) NOT NULL CHECK (tipo IN ('CDB', 'POUPANCA', 'TESOURO')),
    valor NUMERIC(12, 2) NOT NULL CHECK (valor > 0),
    data TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (id_conta) REFERENCES contas(id)
);

-- Inserção de usuários
INSERT INTO usuarios (nome, email, senha)
VALUES 
  ('João Silva', 'joao@email.com', 'senha123'),
  ('Maria Souza', 'maria@email.com', 'senha456');

-- Inserção de contas
INSERT INTO contas (id_usuario, saldo, tipo)
VALUES 
  (1, 1000.00, 'CORRENTE'),
  (2, 500.00, 'POUPANCA');

-- Inserção de transações
INSERT INTO transacoes (conta_origem_id, conta_destino_id, tipo, valor)
VALUES
  (NULL, 1, 'DEPOSITO', 1000.00),
  (NULL, 2, 'DEPOSITO', 500.00),
  (1, 2, 'TRANSFERENCIA', 200.00);

  -- Criação da tabela de investimentos
CREATE TABLE investimentos (
    id SERIAL PRIMARY KEY,
    id_conta INT NOT NULL,
    tipo VARCHAR(20) NOT NULL,
    valor DECIMAL(10,2) NOT NULL,
    data TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (id_conta) REFERENCES contas(id)
);

-- Inserção de investimentos
INSERT INTO investimentos (id_conta, tipo, valor)
VALUES
  (1, 'CDB', 300.00),
  (2, 'TESOURO', 150.00);

