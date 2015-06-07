ALTER TABLE CategoriaTransacao ADD system bit NOT NULL  DEFAULT 0;
INSERT INTO CategoriaTransacao (nome, id_TipoTransacao, system) VALUES ('Transferência',1,1);
INSERT INTO CategoriaTransacao (nome, id_TipoTransacao, system) VALUES ('Transferência',2,1);


