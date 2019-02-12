
CREATE DATABASE IF NOT EXISTS fh;
USE fh;

--
-- Estructura de tabla para la tabla `clientes`
--
DROP TABLE IF EXISTS clientes;

CREATE TABLE `clientes` (
  `alias` varchar(20) NOT NULL PRIMARY KEY,
  `pass` varchar(20) NOT NULL,
  `correo` varchar(50) NOT NULL,
  `staff` tinyint(1) NOT NULL DEFAULT '0'
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `ingredientes`
--
DROP TABLE IF EXISTS ingredientes;
CREATE TABLE `ingredientes` (
  `id` int(11) NOT NULL PRIMARY KEY AUTO_INCREMENT,
  `nombre` varchar(100) UNIQUE KEY NOT NULL,
  `stock` tinyint(1) NOT NULL DEFAULT '1'
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;


--
-- Estructura de tabla para la tabla `platos`
--
DROP TABLE IF EXISTS platos;

CREATE TABLE `platos` (
  `id` int(11) NOT NULL PRIMARY KEY AUTO_INCREMENT,
  `nombre` varchar(30) UNIQUE KEY NOT NULL,
  `nombre_ingredientes` varchar(100) DEFAULT NULL,
	FOREIGN KEY (`nombre_ingredientes`) REFERENCES `ingredientes` (`nombre`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

--
-- Estructura de tabla para la tabla `pedidos`
--
DROP TABLE IF EXISTS pedidos;

CREATE TABLE `pedidos` (
  `id` int(11) NOT NULL PRIMARY KEY AUTO_INCREMENT,
  `alias_clientes` varchar(20) NOT NULL,
  `nombre_platos` varchar(30) NOT NULL,
  `fecha` date NOT NULL, 
	FOREIGN KEY (`alias_clientes`) REFERENCES `clientes` (`alias`), 
        FOREIGN KEY (`nombre_platos`) REFERENCES `platos` (`nombre`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

-- --------------------------------------------------------



INSERT INTO `clientes` (`alias`, `pass`, `correo`, `staff`) 
    VALUES ('c', 'c', 'c@c.com', '1'), ('a', 'a', 'c@c.com', '1'),('b', 'b', 'c@c.com', '1'),('d', 'd', 'c@c.com', '0');

INSERT INTO `ingredientes` (`id`, `nombre`, `stock`) 
    VALUES (NULL, 'espinacas', '1'), (NULL, 'comino', '1');

INSERT INTO `platos` (`id`, `nombre`, `nombre_ingredientes`) 
    VALUES (NULL, 'Espinacas al ajillo', 'espinacas'), (NULL, 'Pollo a la plancha', 'comino');

INSERT INTO `pedidos` (`id`, `alias_clientes`, `nombre_platos`, `fecha`) 
    VALUES (NULL, 'a', 'Espinacas al ajillo', CURRENT_TIMESTAMP), (NULL, 'b', 'Espinacas al ajillo', CURRENT_TIMESTAMP);