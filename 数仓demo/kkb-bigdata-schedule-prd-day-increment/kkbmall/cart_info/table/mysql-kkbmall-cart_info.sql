CREATE TABLE `cart_info` (
  `id`           bigint(20)     NOT NULL AUTO_INCREMENT COMMENT '编号',
  `user_id`      bigint(20)     DEFAULT NULL            COMMENT '用户id',
  `sku_id`       bigint(20)     DEFAULT NULL            COMMENT 'skuid',
  `cart_price`   decimal(10,2)  DEFAULT NULL            COMMENT '放入购物车时价格',
  `sku_num`      bigint(11)     DEFAULT NULL            COMMENT '数量',
  `img_url`      varchar(200)   DEFAULT NULL            COMMENT '图片文件',
  `sku_name`     varchar(200)   DEFAULT NULL            COMMENT 'sku名称 (冗余)',
  `create_time`  datetime       DEFAULT NULL            COMMENT '创建时间',
  `operate_time` datetime       DEFAULT NULL            COMMENT '修改时间',
  `is_ordered`   bigint(20)     DEFAULT NULL            COMMENT '是否已经下单',
  `order_time`   datetime       DEFAULT NULL            COMMENT '下单时间',
  `source_type`  varchar(20)    DEFAULT NULL            COMMENT '来源类型',
  `source_id`    bigint(20)     DEFAULT NULL            COMMENT '来源编号',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=3816 DEFAULT CHARSET=utf8 COMMENT='购物车表 用户登录系统时更新冗余';