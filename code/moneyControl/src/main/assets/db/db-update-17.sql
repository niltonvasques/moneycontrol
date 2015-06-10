CREATE TABLE Repeticao (
id INTEGER DEFAULT NULL PRIMARY KEY AUTOINCREMENT,
tipo VARCHAR(50) NOT NULL  DEFAULT 'NULL'
);

CREATE TABLE ContaAPagar (
id INTEGER NOT NULL  DEFAULT NULL PRIMARY KEY AUTOINCREMENT,
valor DECIMAL DEFAULT NULL,
data DATE NOT NULL  DEFAULT 'NULL',
descricao VARCHAR(200) NOT NULL  DEFAULT 'NULL',
status bit NOT NULL  DEFAULT 1,
id_Repeticao INTEGER DEFAULT NULL REFERENCES Repeticao (id),
id_CategoriaTransacao INTEGER DEFAULT NULL REFERENCES CategoriaTransacao (id)
);

ALTER TABLE Transacao ADD executada bit NOT NULL DEFAULT 1;
ALTER TABLE Transacao ADD id_ContaAPagar INTEGER DEFAULT NULL REFERENCES ContaAPagar (id);

INSERT INTO Repeticao (id, tipo) VALUES (1, "Uma vez");
INSERT INTO Repeticao (id, tipo) VALUES (2, "Semanalmente");
INSERT INTO Repeticao (id, tipo) VALUES (3, "Mensalmente");
INSERT INTO Repeticao (id, tipo) VALUES (4, "Anualmente");

