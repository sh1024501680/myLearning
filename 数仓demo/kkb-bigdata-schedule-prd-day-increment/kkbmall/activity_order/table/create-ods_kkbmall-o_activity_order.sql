CREATE DATABASE IF NOT EXISTS ods_kkbmall;

DROP TABLE IF EXISTS ods_kkbmall.o_activity_order;


CREATE TABLE `ods_kkbmall.o_activity_order` (
  `id`          bigint    COMMENT '编号',
  `activity_id` bigint    COMMENT '活动id ',
  `order_id`    bigint    COMMENT '订单编号',
  `create_time` string    COMMENT '发生日期'
)
COMMENT '活动订单关联表'
PARTITIONED BY (etl_date string comment '跑批时间，格式：yyyyMMdd')
stored AS orc 
TBLPROPERTIES ('transactional' = 'false');