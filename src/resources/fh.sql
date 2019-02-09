
CREATE DATABASE IF NOT EXISTS fh;
USE fh;

--
-- Estructura de tabla para la tabla `clientes`
--

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

CREATE TABLE `ingredientes` (
  `id` int(11) NOT NULL PRIMARY KEY AUTO_INCREMENT,
  `nombre` varchar(100) NOT NULL,
  `stock` tinyint(1) NOT NULL DEFAULT '1'
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;


--
-- Estructura de tabla para la tabla `pedidos`
--

CREATE TABLE `pedidos` (
  `id` int(11) NOT NULL PRIMARY_KEY AUTO_INCREMENT,
  `alias_cliente` varchar(20) NOT NULL,
  `platos` int(11) NOT NULL,
  `fecha` date NOT NULL, 
	FOREIGN KEY (`alias_cliente`) REFERENCES `clientes` (`alias`);
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `platos`
--

CREATE TABLE `platos` (
  `id` int(11) NOT NULL PRIMARY KEY AUTO_INCREMENT,
  `nombre` varchar(30) NOT NULL,
  `ingrediente` int(11) DEFAULT NULL,
	FOREIGN KEY (`ingrediente`) REFERENCES `ingredientes` (`id`);
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

