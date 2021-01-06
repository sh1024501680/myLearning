CREATE DATABASE IF NOT EXISTS ods_kkbmall;

DROP TABLE IF EXISTS ods_kkbmall.o_order_info;


CREATE TABLE `ods_kkbmall.o_order_refund_info` (
  `id`                  bigint           COMMENT '编号',
  `user_id`             bigint           COMMENT '用户id',
  `order_id`            bigint           COMMENT '订单编号',
  `sku_id`              bigint           COMMENT 'skuid',
  `refund_type`         string           COMMENT '退款类型',
  `refund_num`          bigint           COMMENT '退货件数',
  `refund_amount`       decimal(16,2)    COMMENT '退款金额',
  `refund_reason_type`  string           COMMENT '原因类型',
  `refund_reason_txt`   string           COMMENT '原因内容',
  `create_time`         string           COMMENT '创建时间'
)
COMMENT '退单表'
PARTITIONED BY (etl_date string comment '跑批时间，格式：yyyyMMdd')
stored AS orc 
TBLPROPERTIES ('transactional' = 'false');