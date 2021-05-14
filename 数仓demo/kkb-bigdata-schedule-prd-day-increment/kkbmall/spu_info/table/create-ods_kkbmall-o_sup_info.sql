CREATE DATABASE IF NOT EXISTS ods_kkbmall;

DROP TABLE IF EXISTS ods_kkbmall.o_order_info;


CREATE TABLE `ods_kkbmall.o_spu_info` (
  `id`           bigint   COMMENT '商品id',
  `spu_name`     string  COMMENT '商品名称',
  `description`  string  COMMENT '商品描述(后台简述）',
  `category3_id` bigint   COMMENT '三级分类id',
  `tm_id`        bigint   COMMENT '品牌id'
)
COMMENT '订单信息表'
PARTITIONED BY (etl_date string comment '跑批时间，格式：yyyyMMdd')
stored AS orc 
TBLPROPERTIES ('transactional' = 'false');