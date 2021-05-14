CREATE TABLE `coupon_use` (
  `id`             bigint(20)   NOT NULL AUTO_INCREMENT COMMENT '编号',
  `coupon_id`      bigint(20)   DEFAULT NULL            COMMENT '购物券ID',
  `user_id`        bigint(20)   DEFAULT NULL            COMMENT '用户ID',
  `order_id`       bigint(20)   DEFAULT NULL            COMMENT '订单ID',
  `coupon_status`  varchar(10)  DEFAULT NULL            COMMENT '购物券状态',
  `get_time`       datetime     DEFAULT NULL            COMMENT '领券时间',
  `using_time`     datetime     DEFAULT NULL            COMMENT '使用时间',
  `used_time`      datetime     DEFAULT NULL            COMMENT '支付时间',
  `expire_time`    datetime     DEFAULT NULL            COMMENT '过期时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=24012 DEFAULT CHARSET=utf8 COMMENT='优惠券领用表';