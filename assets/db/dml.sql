INSERT INTO TipoConta (id, nome) VALUES(1,'Conta Corrente'); 
INSERT INTO TipoConta (id, nome) VALUES(2,'Poupança');
INSERT INTO TipoConta (id, nome) VALUES(3,'Corretora');
INSERT INTO TipoConta (id, nome) VALUES(4,'Cartão de Crédito');
INSERT INTO TipoConta (id, nome) VALUES(5,'Dinheiro');

INSERT INTO TipoBem (id, nome) VALUES(1,'Ativo');
INSERT INTO TipoBem (id, nome) VALUES(2,'Passivo');

INSERT INTO TipoTransacao (id, nome) VALUES(1, 'Crédito');
INSERT INTO TipoTransacao (id, nome) VALUES(2, 'Débito');

INSERT INTO CategoriaTransacao (id, nome, id_TipoTransacao, system) VALUES(1, 'Alimentação',2,1);
INSERT INTO CategoriaTransacao (id, nome, id_TipoTransacao, system) VALUES(2, 'Salário',1,1);
INSERT INTO CategoriaTransacao (nome, id_TipoTransacao, system) VALUES ('Transferência',1,1);
INSERT INTO CategoriaTransacao (nome, id_TipoTransacao, system) VALUES ('Transferência',2,1);
INSERT INTO CategoriaTransacao (nome, id_TipoTransacao, system) VALUES ('Investimento',2,1);

INSERT INTO TipoAtivo (id, nome) VALUES(1, 'Ações');
INSERT INTO TipoAtivo (id, nome) VALUES(2, 'Tesouro Direto');
INSERT INTO TipoAtivo (id, nome) VALUES(3, 'CDB');
INSERT INTO TipoAtivo (id, nome) VALUES(4, 'Debênture');
INSERT INTO TipoAtivo (id, nome) VALUES(5, 'LCI');
INSERT INTO TipoAtivo (id, nome) VALUES(6, 'LCA');
INSERT INTO TipoAtivo (id, nome) VALUES(7, 'Poupança');
INSERT INTO TipoAtivo (id, nome) VALUES(8, 'Fundos Imobiliários');

INSERT INTO TipoEventoAtivo (id, nome) VALUES(1,'Compra');
INSERT INTO TipoEventoAtivo (id, nome) VALUES(2,'Venda');


