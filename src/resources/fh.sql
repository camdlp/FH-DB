DROP DATABASE IF EXISTS fh;
CREATE DATABASE IF NOT EXISTS fh;
USE fh;

--
-- Estructura de tabla para la tabla `clientes`
--
DROP TABLE IF EXISTS clientes;

CREATE TABLE `clientes` (
  `id` int(11) NOT NULL PRIMARY KEY AUTO_INCREMENT,
  `alias` varchar(20) NOT NULL UNIQUE KEY,
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
  `id` int(11) PRIMARY KEY NOT NULL AUTO_INCREMENT,
  `nombre` varchar(100) UNIQUE KEY NOT NULL,
  `stock` tinyint(1) NOT NULL DEFAULT '1'
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;


--
-- Estructura de tabla para la tabla    
 
--
DROP TABLE IF EXISTS platos;

CREATE TABLE `platos` (
  `id` int(11) NOT NULL PRIMARY KEY AUTO_INCREMENT,
  `nombre` varchar(50) UNIQUE KEY NOT NULL,
  `disponible` tinyint(1) NOT NULL DEFAULT '1'
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

--
-- Estructura de tabla para la tabla `pedidos`
--
DROP TABLE IF EXISTS pedidos;

CREATE TABLE `pedidos` (
  `id` int(11) NOT NULL PRIMARY KEY AUTO_INCREMENT,
  `alias_clientes` varchar(20),  
  `fecha` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP, 
	FOREIGN KEY (`alias_clientes`) REFERENCES `clientes` (`alias`) ON UPDATE CASCADE ON DELETE SET NULL        
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

-- --------------------------------------------------------



--
-- Estructura de tabla para la tabla `pedidos`
--
DROP TABLE IF EXISTS platos_ingredientes;

CREATE TABLE `platos_ingredientes` (
  `id_plato` int(11) NOT NULL,
  `id_ingrediente` int(11),
  `cantidad` varchar(30) NOT NULL DEFAULT 1,
    PRIMARY KEY(`id_plato`, `id_ingrediente`),
	FOREIGN KEY (`id_plato`) REFERENCES `platos` (`id`) ON UPDATE CASCADE ON DELETE CASCADE , 
        FOREIGN KEY (`id_ingrediente`) REFERENCES `ingredientes` (`id`) ON UPDATE CASCADE ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

-- --------------------------------------------------------



--
-- Estructura de tabla para la tabla `pedidos`
--
DROP TABLE IF EXISTS pedidos_platos;

CREATE TABLE `pedidos_platos` (
  `id_pedido` int(11) NOT NULL,
  `id_plato` int(11) NOT NULL,
  `cantidad` varchar(30) NOT NULL DEFAULT 1,
    PRIMARY KEY(`id_pedido`, `id_plato`),
	FOREIGN KEY (`id_pedido`) REFERENCES `pedidos` (`id`) ON UPDATE CASCADE ON DELETE CASCADE, 
        FOREIGN KEY (`id_plato`) REFERENCES `platos` (`id`) ON UPDATE CASCADE ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

-- --------------------------------------------------------




INSERT INTO `clientes` (`alias`, `pass`, `correo`, `staff`) 
    VALUES ('carlos', 'password', 'carlos@gmail.com', '1'), ('carmen', 'password', 'carmen@gmail.com', '1'),
('jorge', 'password', 'jorge@gmail.com', '1'),('sosio', 'password', 'sosio@gmail.com', '0');

INSERT INTO `ingredientes` (`id`, `nombre`, `stock`) 
    VALUES (NULL, 'espinaca', '1'), (NULL, 'comino', '1'), (NULL, 'pollo', '1'), (NULL, 'ajo', '1'), 
(NULL, 'edulcorante', '1'), (NULL, 'chocolate negro', '1'), (NULL, 'lechuga', '1'), (NULL, 'tomate', '1'), (NULL, 'maíz', '1'),
 (NULL, 'atún', '1'), (NULL, 'queso light', '1'), (NULL, 'leche desnatada', '1'), (NULL, 'manzana', '1');

INSERT INTO `platos` (`id`, `nombre`) 
    VALUES (NULL, 'Espinacas al ajillo'), (NULL, 'Pollo a la plancha'), (NULL, 'Ensalada simple'), (NULL, 'Ensalada de pasta');

INSERT INTO `pedidos` (`id`, `alias_clientes`) 
    VALUES (NULL, 'carlos'), (NULL, 'carmen'), (NULL, 'jorge'), (NULL, 'sosio');

INSERT INTO `platos_ingredientes` (`id_plato`, `id_ingrediente`, `cantidad`)
    VALUES(1, 1, 1), (1, 2, 1), (2, 4, 1), (2, 3, 1), (3, 1, 1), (3, 6, 1), (4, 5, 1), (4, 8, 1);

INSERT INTO `pedidos_platos` (`id_pedido`, `id_plato`, `cantidad`)
    VALUES(1, 1, 1), (1, 2, 1),(2, 3, 1), (2, 4, 1), (3, 3, 1), (3, 4, 1),(4, 1, 1), (4, 4, 1);