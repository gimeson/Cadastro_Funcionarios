CREATE DATABASE aulajava;
GO

USE aulajava;
GO

-- Criação da tabela de cargos
CREATE TABLE tbcargos (
    cd_cargo smallint PRIMARY KEY,
    ds_cargo char(20)
);

-- Criação da tabela de funcionários
CREATE TABLE tbfuncs (
    cod_func decimal(9,0) PRIMARY KEY,
    nome_func char(30),
    sal_func money,
    cod_cargo smallint FOREIGN KEY REFERENCES tbcargos(cd_cargo)
);

-- Inserção de dados de exemplo
INSERT INTO tbcargos (cd_cargo, ds_cargo) VALUES (1, 'Administrativo');
INSERT INTO tbcargos (cd_cargo, ds_cargo) VALUES (2, 'Analista');

INSERT INTO tbfuncs (cod_func, nome_func, sal_func, cod_cargo) VALUES (1, 'Marcelo', 2000.00, 1);
INSERT INTO tbfuncs (cod_func, nome_func, sal_func, cod_cargo) VALUES (2, 'Ana Souza', 3500.00, 2);