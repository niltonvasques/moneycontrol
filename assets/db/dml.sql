INSERT INTO TipoConta (id, nome) VALUES(1,'Conta Corrente'); 
INSERT INTO TipoConta (id, nome) VALUES(2,'Poupança');
INSERT INTO TipoConta (id, nome) VALUES(3,'Corretora');
INSERT INTO TipoConta (id, nome) VALUES(4,'Cartão de Crédito');
INSERT INTO TipoConta (id, nome) VALUES(5,'Dinheiro');

INSERT INTO TipoBem (id, nome) VALUES(1,'Ativo');
INSERT INTO TipoBem (id, nome) VALUES(2,'Passivo');

INSERT INTO TipoTransacao (id, nome) VALUES(1, 'Receita');
INSERT INTO TipoTransacao (id, nome) VALUES(2, 'Despesa');

INSERT INTO CategoriaTransacao (id, nome, id_TipoTransacao, system) VALUES (1, 'Transferência',1,1);
INSERT INTO CategoriaTransacao (id, nome, id_TipoTransacao, system) VALUES (2, 'Transferência',2,1);
INSERT INTO CategoriaTransacao (id, nome, id_TipoTransacao, system) VALUES (3, 'Investimento',2,1);
INSERT INTO CategoriaTransacao (id, nome, id_TipoTransacao, system) VALUES (4, 'Investimento',1,1);
INSERT INTO CategoriaTransacao (id, nome, id_TipoTransacao, system) VALUES (5, 'Alimentação',2,1);
INSERT INTO CategoriaTransacao (id, nome, id_TipoTransacao, system) VALUES (6, 'Salário',1,1);

INSERT INTO Ativo (id, nome) VALUES(1, 'Ações');
INSERT INTO Ativo (id, nome) VALUES(2, 'Tesouro Direto');
INSERT INTO Ativo (id, nome) VALUES(3, 'CDB');
INSERT INTO Ativo (id, nome) VALUES(4, 'Debênture');
INSERT INTO Ativo (id, nome) VALUES(5, 'LCI');
INSERT INTO Ativo (id, nome) VALUES(6, 'LCA');
INSERT INTO Ativo (id, nome) VALUES(7, 'Poupança');
INSERT INTO Ativo (id, nome) VALUES(8, 'Fundos Imobiliários');

INSERT INTO Conta (id, nome, saldo, icon, id_TipoConta) VALUES (1, 'Carteira', 0, 'carteira_icon.jpg', 5);


