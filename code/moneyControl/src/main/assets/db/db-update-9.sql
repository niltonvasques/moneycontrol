DROP TABLE EventoATivo;
DROP TABLE TipoEventoAtivo;
DROP TABLE Ativo;

CREATE TABLE Ativo (
id INTEGER DEFAULT NULL PRIMARY KEY AUTOINCREMENT,
data DATE NOT NULL  DEFAULT 'NULL',
valor DECIMAL NOT NULL  DEFAULT NULL,
id_Conta INTEGER DEFAULT NULL REFERENCES Conta (id),
quantidade DECIMAL NOT NULL  DEFAULT 1,
id_TipoAtivo INTEGER DEFAULT NULL REFERENCES TipoAtivo (id),
vencimento DATE NOT NULL  DEFAULT 'NULL',
nome VARCHAR DEFAULT NULL,
sigla VARCHAR DEFAULT NULL,
id_Transacao INTEGER DEFAULT NULL REFERENCES Transacao (id)
);

INSERT INTO CategoriaTransacao (nome, id_TipoTransacao, system) VALUES ('Investimento',1,1);