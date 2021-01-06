CREATE TABLE `order_status_log` (
  `id`           bigint(11)  NOT NULL AUTO_INCREMENT,
  `order_id`     bigint(11)   DEFAULT NULL,
  `order_status` varchar(11)  DEFAULT NULL,
  `operate_time` datetime     DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=80993 DEFAULT CHARSET=utf8;