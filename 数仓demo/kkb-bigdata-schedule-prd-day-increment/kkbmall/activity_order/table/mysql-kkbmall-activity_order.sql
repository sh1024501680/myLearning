CREATE TABLE `activity_order` (
  `id`          bigint(20) NOT NULL AUTO_INCREMENT COMMENT '编号',
  `activity_id` bigint(20) DEFAULT NULL            COMMENT '活动id ',
  `order_id`    bigint(20) DEFAULT NULL            COMMENT '订单编号',
  `create_time` datetime   DEFAULT NULL            COMMENT '发生日期',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=735 DEFAULT CHARSET=utf8 COMMENT='活动与订单关联表';