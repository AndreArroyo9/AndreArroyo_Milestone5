CREATE DATABASE IF NOT EXISTS `photography`;
USE `photography`;

CREATE TABLE IF NOT EXISTS `photographers` (
  `PhotographerId` int(11) NOT NULL AUTO_INCREMENT,
  `Name` varchar(50) DEFAULT NULL,
  `Awarded` tinyint(1) DEFAULT NULL,
  PRIMARY KEY (`PhotographerId`)
) 

INSERT INTO `photographers` (`PhotographerId`, `Name`, `Awarded`) VALUES
	(1, 'Ansel Adams', 0),
	(2, 'Mark Rothko', 1),
	(3, 'Vincent Van Gogh', 0);
CREATE TABLE IF NOT EXISTS `pictures` (
  `PictureId` int(11) NOT NULL AUTO_INCREMENT,
  `Title` varchar(50) DEFAULT NULL,
  `DatePicture` date DEFAULT NULL,
  `File` varchar(50) DEFAULT NULL,
  `Visits` int(11) DEFAULT NULL,
  `Photographer` int(11) DEFAULT NULL,
  PRIMARY KEY (`PictureId`),
  KEY `FK_Pictures_Photographers` (`Photographer`),
  CONSTRAINT `FK_Pictures_Photographers` FOREIGN KEY (`Photographer`) REFERENCES `photographers` (`PhotographerId`)
)
INSERT INTO `pictures` (`PictureId`, `Title`, `DatePicture`, `File`, `Visits`, `Photographer`) VALUES
	(1, 'Picture 1', '2024-02-25', 'files/ansealdams1.jpg', 1, 1),
	(2, 'Picture 2', '2024-05-02', 'files/ansealdams2.jpg', 1, 1),
	(3, 'Picture 3', '2024-10-12', 'files/rothko1.jpg', 3, 2),
	(4, 'Picture 4', '2024-09-21', 'files/vangogh1.jpg', 5, 3),
	(5, 'Picture 5', '2024-12-06', 'files/vangogh2.jpg', 2, 3);