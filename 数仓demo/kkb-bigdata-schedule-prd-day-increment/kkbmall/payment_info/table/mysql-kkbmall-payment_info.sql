CREATE TABLE `payment_info` (
  `id`               bigint(20)    NOT NULL AUTO_INCREMENT COMMENT '编号',
  `out_trade_no`     varchar(20)   DEFAULT NULL     COMMENT '对外业务编号',
  `order_id`         bigint(20)    DEFAULT NULL     COMMENT '订单编号',
  `user_id`          bigint(20)    DEFAULT NULL     COMMENT '用户编号',
  `alipay_trade_no`  varchar(100)  DEFAULT NULL     COMMENT '支付宝交易流水编号',
  `total_amount`     decimal(16,2) DEFAULT NULL     COMMENT '支付金额',
  `subject`          varchar(200)  DEFAULT NULL     COMMENT '交易内容',
  `payment_type`     varchar(20)   DEFAULT NULL     COMMENT '支付方式',
  `payment_time`     datetime      DEFAULT NULL     COMMENT '支付时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=5479 DEFAULT CHARSET=utf8 COMMENT='支付流水表';