CREATE DATABASE IF NOT EXISTS ods_kkbmall;

DROP TABLE IF EXISTS ods_kkbmall.o_order_detail;


CREATE TABLE `ods_kkbmall.o_order_detail` (
  `id`           bigint         COMMENT '编号',
  `order_id`     bigint         COMMENT '订单编号',
  `sku_id`       bigint         COMMENT 'sku_id',
  `sku_name`     string         COMMENT 'sku名称（冗余)',
  `img_url`      string         COMMENT '图片名称（冗余)',
  `order_price`  decimal(10,2)  COMMENT '购买价格(下单时sku价格）',
  `sku_num`      string         COMMENT '购买个数',
  `create_time`  string         COMMENT '创建时间',
  `source_type`  string         COMMENT '来源类型',
  `source_id`    bigint         COMMENT '来源编号'
)
COMMENT '订单明细表'
PARTITIONED BY (etl_date string comment '跑批时间，格式：yyyyMMdd')
stored AS orc 
TBLPROPERTIES ('transactional' = 'false');