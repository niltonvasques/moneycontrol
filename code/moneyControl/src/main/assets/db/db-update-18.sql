ALTER TABLE Transacao ADD id_ContaAPagar INTEGER DEFAULT NULL REFERENCES ContaAPagar (id);

INSERT INTO Repeticao (id, tipo) VALUES (1, "Uma vez");
INSERT INTO Repeticao (id, tipo) VALUES (2, "Semanalmente");
INSERT INTO Repeticao (id, tipo) VALUES (3, "Mensalmente");
INSERT INTO Repeticao (id, tipo) VALUES (4, "Anualmente");
