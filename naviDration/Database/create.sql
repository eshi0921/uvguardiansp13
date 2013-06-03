-- MySQL dump 10.13  Distrib 5.1.69, for redhat-linux-gnu (x86_64)
--
-- Host: localhost    Database: Fountain
-- ------------------------------------------------------
-- Server version	5.1.69

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `Fountain_Location`
--

DROP TABLE IF EXISTS `Fountain_Location`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `Fountain_Location` (
  `Fount_ID` int(10) unsigned NOT NULL,
  `Longitude` double NOT NULL,
  `Latitude` double NOT NULL,
  PRIMARY KEY (`Fount_ID`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `Fountain_Location`
--

LOCK TABLES `Fountain_Location` WRITE;
/*!40000 ALTER TABLE `Fountain_Location` DISABLE KEYS */;
INSERT INTO `Fountain_Location` VALUES (0,0,0);
/*!40000 ALTER TABLE `Fountain_Location` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `Fountain_Rating`
--

DROP TABLE IF EXISTS `Fountain_Rating`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `Fountain_Rating` (
  `Fount_ID` int(10) unsigned NOT NULL DEFAULT '0',
  `Rating` char(1) NOT NULL DEFAULT '',
  `User_ID` int(11) NOT NULL DEFAULT '-1',
  PRIMARY KEY (`Fount_ID`,`User_ID`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `Fountain_Rating`
--

LOCK TABLES `Fountain_Rating` WRITE;
/*!40000 ALTER TABLE `Fountain_Rating` DISABLE KEYS */;
/*!40000 ALTER TABLE `Fountain_Rating` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `MaxUserID`
--

DROP TABLE IF EXISTS `MaxUserID`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `MaxUserID` (
  `maxUID` int(10) unsigned NOT NULL DEFAULT '0',
  PRIMARY KEY (`maxUID`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `MaxUserID`
--

LOCK TABLES `MaxUserID` WRITE;
/*!40000 ALTER TABLE `MaxUserID` DISABLE KEYS */;
INSERT INTO `MaxUserID` VALUES (1);
/*!40000 ALTER TABLE `MaxUserID` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2013-06-03 11:42:36
