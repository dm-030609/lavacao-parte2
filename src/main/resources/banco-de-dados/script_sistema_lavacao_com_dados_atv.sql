CREATE DATABASE  IF NOT EXISTS `db_lavacao_atv` /*!40100 DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci */ /*!80016 DEFAULT ENCRYPTION='N' */;
USE `db_lavacao_atv`;
-- MySQL dump 10.13  Distrib 8.0.41, for Win64 (x86_64)
--
-- Host: localhost    Database: db_lavacao_atv
-- ------------------------------------------------------
-- Server version	9.3.0

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!50503 SET NAMES utf8 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `cliente`
--

DROP TABLE IF EXISTS `cliente`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `cliente` (
  `id` int NOT NULL AUTO_INCREMENT,
  `nome` varchar(50) NOT NULL,
  `celular` varchar(50) NOT NULL,
  `email` varchar(50) NOT NULL,
  `data_cadastro` date NOT NULL,
  `id_pontuacao` int DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `fk_cliente_pontuacao` (`id_pontuacao`),
  CONSTRAINT `fk_cliente_pontuacao` FOREIGN KEY (`id_pontuacao`) REFERENCES `pontuacao` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=21 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `cliente`
--

LOCK TABLES `cliente` WRITE;
/*!40000 ALTER TABLE `cliente` DISABLE KEYS */;
INSERT INTO `cliente` VALUES (15,'HERVAL','48 99120-8014','herval@gmail.com','2025-07-25',2),(16,'MARCOS','48 99158-3041','marcos.gremio@email.com','2025-07-25',3),(17,'ANDRINO','48 99785-7026','andrino@gmail.com','2025-07-26',4),(19,'BRUNO','48 98523-9411','bruno@gmail.com','2025-07-26',6),(20,'DANIEL','48 99120-5854','daniel@gmail.com','2025-07-26',7);
/*!40000 ALTER TABLE `cliente` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `cor`
--

DROP TABLE IF EXISTS `cor`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `cor` (
  `id` int NOT NULL AUTO_INCREMENT,
  `nome` varchar(50) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=13 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `cor`
--

LOCK TABLES `cor` WRITE;
/*!40000 ALTER TABLE `cor` DISABLE KEYS */;
INSERT INTO `cor` VALUES (8,'ROXO'),(9,'AZUL'),(10,'PRETO'),(11,'BRANCO'),(12,'VERMELHO');
/*!40000 ALTER TABLE `cor` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `item_os`
--

DROP TABLE IF EXISTS `item_os`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `item_os` (
  `id` int NOT NULL AUTO_INCREMENT,
  `valor_servico` double NOT NULL,
  `observacoes` varchar(50) NOT NULL,
  `id_ordem_servico` int NOT NULL,
  `id_servico` int NOT NULL,
  PRIMARY KEY (`id`),
  KEY `fk_item_os_ordem_servico` (`id_ordem_servico`),
  KEY `fk_item_os_servico` (`id_servico`),
  CONSTRAINT `fk_item_os_ordem_servico` FOREIGN KEY (`id_ordem_servico`) REFERENCES `ordem_servico` (`id`),
  CONSTRAINT `fk_item_os_servico` FOREIGN KEY (`id_servico`) REFERENCES `servico` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=153 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `item_os`
--

LOCK TABLES `item_os` WRITE;
/*!40000 ALTER TABLE `item_os` DISABLE KEYS */;
INSERT INTO `item_os` VALUES (87,80,'',28,25),(89,100,'',29,26),(92,100,'',26,22),(93,100,'',26,26),(96,90,'',30,23),(97,90,'',30,27),(99,100,'',31,26),(106,100,'dededede',32,26),(107,100,'hyhyhyy',32,22),(109,90,'pontos resgatados',27,23),(130,100,'',36,22),(131,100,'',36,26),(136,100,'',37,22),(137,100,'',37,26),(146,100,'',38,22),(147,100,'',38,26),(148,200,'',38,29),(149,100,'',38,30),(150,100,'',34,22),(151,100,'',34,26),(152,100,'',33,22);
/*!40000 ALTER TABLE `item_os` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `marca`
--

DROP TABLE IF EXISTS `marca`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `marca` (
  `id` int NOT NULL AUTO_INCREMENT,
  `nome` varchar(50) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=13 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `marca`
--

LOCK TABLES `marca` WRITE;
/*!40000 ALTER TABLE `marca` DISABLE KEYS */;
INSERT INTO `marca` VALUES (8,'FERRARI'),(9,'BMW'),(10,'AUDI'),(11,'MERCEDEZ'),(12,'TESLA');
/*!40000 ALTER TABLE `marca` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `modelo`
--

DROP TABLE IF EXISTS `modelo`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `modelo` (
  `id` int NOT NULL AUTO_INCREMENT,
  `descricao` varchar(50) NOT NULL,
  `id_marca` int NOT NULL,
  `categoria` enum('PEQUENO','MEDIO','GRANDE','MOTO','PADRAO') NOT NULL DEFAULT 'PADRAO',
  PRIMARY KEY (`id`),
  KEY `fk_modelo_marca` (`id_marca`),
  CONSTRAINT `fk_modelo_marca` FOREIGN KEY (`id_marca`) REFERENCES `marca` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=61 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `modelo`
--

LOCK TABLES `modelo` WRITE;
/*!40000 ALTER TABLE `modelo` DISABLE KEYS */;
INSERT INTO `modelo` VALUES (56,'F1',8,'MEDIO'),(57,'X6',9,'GRANDE'),(58,'BRABUS - G63',11,'GRANDE'),(59,'A9',10,'GRANDE'),(60,'CYBER TRUCK',12,'PADRAO');
/*!40000 ALTER TABLE `modelo` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `motor`
--

DROP TABLE IF EXISTS `motor`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `motor` (
  `id_modelo` int NOT NULL,
  `potencia` int NOT NULL DEFAULT '0',
  `tipoCombustivel` enum('GASOLINA','ETANOL','FLEX','DIESEL','GNV','OUTROS') NOT NULL DEFAULT 'OUTROS',
  PRIMARY KEY (`id_modelo`),
  CONSTRAINT `fk_motor_produto` FOREIGN KEY (`id_modelo`) REFERENCES `modelo` (`id`) ON DELETE CASCADE,
  CONSTRAINT `motor_ibfk_1` FOREIGN KEY (`id_modelo`) REFERENCES `modelo` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `motor`
--

LOCK TABLES `motor` WRITE;
/*!40000 ALTER TABLE `motor` DISABLE KEYS */;
INSERT INTO `motor` VALUES (56,500,'GASOLINA'),(57,450,'FLEX'),(58,450,'GASOLINA'),(59,400,'FLEX'),(60,300,'GNV');
/*!40000 ALTER TABLE `motor` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `ordem_servico`
--

DROP TABLE IF EXISTS `ordem_servico`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `ordem_servico` (
  `id` int NOT NULL AUTO_INCREMENT,
  `numero` mediumtext NOT NULL,
  `total` double NOT NULL,
  `agenda` date NOT NULL,
  `desconto` double DEFAULT NULL,
  `e_status` enum('ABERTA','FECHADA','CANCELADA') NOT NULL DEFAULT 'ABERTA',
  `id_veiculo` int NOT NULL,
  PRIMARY KEY (`id`),
  KEY `fk_ordem_servico_veiculo` (`id_veiculo`),
  CONSTRAINT `fk_ordem_servico_veiculo` FOREIGN KEY (`id_veiculo`) REFERENCES `veiculo` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=39 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `ordem_servico`
--

LOCK TABLES `ordem_servico` WRITE;
/*!40000 ALTER TABLE `ordem_servico` DISABLE KEYS */;
INSERT INTO `ordem_servico` VALUES (26,'26',180,'2025-04-01',10,'FECHADA',22),(27,'27',0,'2025-01-23',100,'FECHADA',23),(28,'28',80,'2025-07-25',0,'FECHADA',24),(29,'29',100,'2025-06-20',0,'FECHADA',25),(30,'30',162,'2025-07-24',10,'FECHADA',23),(31,'31',100,'2025-01-16',0,'FECHADA',22),(32,'32',100,'2025-07-28',50,'FECHADA',22),(33,'33',100,'2025-05-20',0,'FECHADA',22),(34,'34',200,'2025-06-18',0,'FECHADA',24),(36,'36',180,'2025-07-29',10,'FECHADA',24),(37,'37',180,'2025-09-24',10,'FECHADA',25),(38,'38',500,'2025-05-26',0,'FECHADA',23);
/*!40000 ALTER TABLE `ordem_servico` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `pessoa_fisica`
--

DROP TABLE IF EXISTS `pessoa_fisica`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `pessoa_fisica` (
  `id_cliente` int NOT NULL,
  `cpf` varchar(20) NOT NULL,
  `data_nascimento` date NOT NULL,
  PRIMARY KEY (`id_cliente`),
  CONSTRAINT `pessoa_fisica_ibfk_1` FOREIGN KEY (`id_cliente`) REFERENCES `cliente` (`id`),
  CONSTRAINT `pk_pessoa_fisica_cliente` FOREIGN KEY (`id_cliente`) REFERENCES `cliente` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `pessoa_fisica`
--

LOCK TABLES `pessoa_fisica` WRITE;
/*!40000 ALTER TABLE `pessoa_fisica` DISABLE KEYS */;
INSERT INTO `pessoa_fisica` VALUES (16,'061.584.949-5','2002-01-20'),(17,'034.878.0001-80','2002-06-26'),(20,'091.816.679-90','1999-05-26');
/*!40000 ALTER TABLE `pessoa_fisica` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `pessoa_juridica`
--

DROP TABLE IF EXISTS `pessoa_juridica`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `pessoa_juridica` (
  `id_cliente` int NOT NULL,
  `cnpj` varchar(20) NOT NULL,
  `inscricao_estadual` varchar(30) NOT NULL,
  PRIMARY KEY (`id_cliente`),
  CONSTRAINT `pessoa_juridica_ibfk_1` FOREIGN KEY (`id_cliente`) REFERENCES `cliente` (`id`),
  CONSTRAINT `pk_pessoa_juridica_cliente` FOREIGN KEY (`id_cliente`) REFERENCES `cliente` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `pessoa_juridica`
--

LOCK TABLES `pessoa_juridica` WRITE;
/*!40000 ALTER TABLE `pessoa_juridica` DISABLE KEYS */;
INSERT INTO `pessoa_juridica` VALUES (15,'15.114.0001-5','1249578787'),(19,'814.150.0001-96','99965510220');
/*!40000 ALTER TABLE `pessoa_juridica` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `pontuacao`
--

DROP TABLE IF EXISTS `pontuacao`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `pontuacao` (
  `id` int NOT NULL AUTO_INCREMENT,
  `quantidade` int NOT NULL DEFAULT '0',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=8 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `pontuacao`
--

LOCK TABLES `pontuacao` WRITE;
/*!40000 ALTER TABLE `pontuacao` DISABLE KEYS */;
INSERT INTO `pontuacao` VALUES (1,0),(2,40),(3,80),(4,33),(5,0),(6,60),(7,0);
/*!40000 ALTER TABLE `pontuacao` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `servico`
--

DROP TABLE IF EXISTS `servico`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `servico` (
  `id` int NOT NULL AUTO_INCREMENT,
  `descricao` varchar(100) NOT NULL,
  `valor` decimal(10,2) NOT NULL,
  `pontos` int NOT NULL,
  `categoria` enum('PEQUENO','MEDIO','GRANDE','MOTO','PADRAO') DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=34 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `servico`
--

LOCK TABLES `servico` WRITE;
/*!40000 ALTER TABLE `servico` DISABLE KEYS */;
INSERT INTO `servico` VALUES (22,'LIMPEZA SIMPLES - G',100.00,20,'GRANDE'),(23,'LIMPEZA SIMPLES - M',90.00,15,'MEDIO'),(24,'LIMPEZA SIMPLES - P',80.00,10,'PEQUENO'),(25,'LIMPEZA SIMPLES - MT',70.00,8,'MOTO'),(26,'POLIMENTO - G',100.00,20,'GRANDE'),(27,'POLIMENTO - M',90.00,15,'MEDIO'),(28,'POLIMENTO - P',80.00,10,'PEQUENO'),(29,'LIMPEZA COMPLETA - G',150.00,20,'GRANDE'),(30,'LIMPEZA COMPLETA - M',140.00,15,'MEDIO'),(31,'LIMPEZA COMPLETA - P',130.00,10,'PEQUENO'),(32,'LIMPEZA COMPLETA - MT',100.00,8,'MOTO'),(33,'POLIMENTO - MT',50.00,8,'MOTO');
/*!40000 ALTER TABLE `servico` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `veiculo`
--

DROP TABLE IF EXISTS `veiculo`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `veiculo` (
  `id` int NOT NULL AUTO_INCREMENT,
  `placa` varchar(50) NOT NULL,
  `observacoes` varchar(50) NOT NULL,
  `id_cor` int NOT NULL,
  `id_modelo` int NOT NULL,
  `id_cliente` int DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `fk_veiculo_cor` (`id_cor`),
  KEY `fk_veiculo_modedlo` (`id_modelo`),
  KEY `fk_veiculo_cliente` (`id_cliente`),
  CONSTRAINT `fk_veiculo_cliente` FOREIGN KEY (`id_cliente`) REFERENCES `cliente` (`id`),
  CONSTRAINT `fk_veiculo_cor` FOREIGN KEY (`id_cor`) REFERENCES `cor` (`id`),
  CONSTRAINT `fk_veiculo_modedlo` FOREIGN KEY (`id_modelo`) REFERENCES `modelo` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=26 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `veiculo`
--

LOCK TABLES `veiculo` WRITE;
/*!40000 ALTER TABLE `veiculo` DISABLE KEYS */;
INSERT INTO `veiculo` VALUES (22,'AAA -111','NOVO',8,56,15),(23,'BBB -222','ZERO BALA',8,57,16),(24,'CCC - 333','USADO',8,58,17),(25,'DDD - 444','IHUUUL',8,59,19);
/*!40000 ALTER TABLE `veiculo` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2025-07-29 13:20:31
