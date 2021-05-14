CREATE TABLE `order_refund_info` (
  `id`                  bigint(20)    NOT NULL AUTO_INCREMENT COMMENT '编号',
  `user_id`             bigint(20)    DEFAULT NULL            COMMENT '用户id',
  `order_id`            bigint(20)    DEFAULT NULL            COMMENT '订单编号',
  `sku_id`              bigint(20)    DEFAULT NULL            COMMENT 'skuid',
  `refund_type`         varchar(20)   DEFAULT NULL            COMMENT '退款类型',
  `refund_num`          bigint(20)    DEFAULT NULL            COMMENT '退货件数',
  `refund_amount`       decimal(16,2) DEFAULT NULL            COMMENT '退款金额',
  `refund_reason_type`  varchar(200)  DEFAULT NULL            COMMENT '原因类型',
  `refund_reason_txt`   varchar(20)   DEFAULT NULL            COMMENT '原因内容',
  `create_time`         datetime DEFAULT NULL                 COMMENT '创建时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=653 DEFAULT CHARSET=utf8 COMMENT='退单表';