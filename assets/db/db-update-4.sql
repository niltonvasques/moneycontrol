ALTER TABLE Conta RENAME TO tmp;

CREATE TABLE Conta (
id INTEGER DEFAULT NULL PRIMARY KEY AUTOINCREMENT,
nome VARCHAR(200) NOT NULL  DEFAULT 'NULL',
saldo DECIMAL DEFAULT NULL,
icon VARCHAR(200) NOT NULL  DEFAULT 'NULL',
id_TipoConta INTEGER DEFAULT NULL REFERENCES TipoConta (id)
);

INSERT INTO Conta(id, nome, saldo, icon, id_TipoConta)
   SELECT id, nome, saldo, icon, id_TipoConta
   FROM tmp;

DROP TABLE tmp;

UPDATE Conta
SET icon = 'bb_icon.png';
