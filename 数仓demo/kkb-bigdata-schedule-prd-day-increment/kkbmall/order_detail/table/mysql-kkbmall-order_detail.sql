

CREATE TABLE `order_detail` (
  `id`           bigint(20)     NOT NULL AUTO_INCREMENT COMMENT '编号',
  `order_id`     bigint(20)     DEFAULT NULL    COMMENT '订单编号',
  `sku_id`       bigint(20)     DEFAULT NULL    COMMENT 'sku_id',
  `sku_name`     varchar(200)   DEFAULT NULL    COMMENT 'sku名称（冗余)',
  `img_url`      varchar(200)   DEFAULT NULL    COMMENT '图片名称（冗余)',
  `order_price`  decimal(10,2)  DEFAULT NULL    COMMENT '购买价格(下单时sku价格）',
  `sku_num`      varchar(200)   DEFAULT NULL    COMMENT '购买个数',
  `create_time`  datetime       DEFAULT NULL    COMMENT '创建时间',
  `source_type`  varchar(20)    DEFAULT NULL    COMMENT '来源类型',
  `source_id`    bigint(20)     DEFAULT NULL    COMMENT '来源编号',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=9035 DEFAULT CHARSET=utf8 COMMENT='订单明细表';