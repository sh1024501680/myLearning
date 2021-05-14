CREATE DATABASE IF NOT EXISTS ods_kkbmall;

DROP TABLE IF EXISTS ods_kkbmall.o_order_status_log;


CREATE TABLE `ods_kkbmall.o_order_status_log` (
  `id`           bigint  COMMENT '订单状态ID',
  `order_id`     bigint  COMMENT '订单ID',
  `order_status` string  COMMENT '订单状态',
  `operate_time` string  COMMENT '操作时间'
)
COMMENT '订单状态表'
PARTITIONED BY (etl_date string comment '跑批时间，格式：yyyyMMdd')
stored AS orc 
TBLPROPERTIES ('transactional' = 'false');