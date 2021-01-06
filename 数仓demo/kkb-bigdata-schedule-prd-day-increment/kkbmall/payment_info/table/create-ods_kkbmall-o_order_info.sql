CREATE DATABASE IF NOT EXISTS ods_kkbmall;

DROP TABLE IF EXISTS ods_kkbmall.o_payment_info;


CREATE TABLE `ods_kkbmall.o_payment_info` (
  `id`               bigint          COMMENT '编号',
  `out_trade_no`     string          COMMENT '对外业务编号',
  `order_id`         bigint          COMMENT '订单编号',
  `user_id`          bigint          COMMENT '用户编号',
  `alipay_trade_no`  string          COMMENT '支付宝交易流水编号',
  `total_amount`     decimal(16,2)   COMMENT '支付金额',
  `subject`          string          COMMENT '交易内容',
  `payment_type`     string          COMMENT '支付方式',
  `payment_time`     string          COMMENT '支付时间'
)
COMMENT '支付流水表'
PARTITIONED BY (etl_date string comment '跑批时间，格式：yyyyMMdd')
stored AS orc 
TBLPROPERTIES ('transactional' = 'false');