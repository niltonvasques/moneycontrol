INSERT INTO TipoConta (id, nome) VALUES(1,'Conta Corrente'); 
INSERT INTO TipoConta (id, nome) VALUES(2,'Poupança');
INSERT INTO TipoConta (id, nome) VALUES(3,'Corretora');
INSERT INTO TipoConta (id, nome) VALUES(4,'Cartão de Crédito');

INSERT INTO TipoBem (id, nome) VALUES(1,'Ativo');
INSERT INTO TipoBem (id, nome) VALUES(2,'Passivo');

INSERT INTO TipoTransacao (id, nome) VALUES(1, 'Crédito');
INSERT INTO TipoTransacao (id, nome) VALUES(2, 'Débito');

INSERT INTO CategoriaTransacao (id, nome, id_TipoTransacao) VALUES(1, 'Alimentação',2);
INSERT INTO CategoriaTransacao (id, nome, id_TipoTransacao) VALUES(2, 'Salário',1);


