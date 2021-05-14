CREATE DATABASE IF NOT EXISTS ods_kkbmall;

DROP TABLE IF EXISTS ods_kkbmall.o_order_info;

CREATE TABLE `ods_kkbmall.sku_info` (
  `id`                bigint         COMMENT 'skuid(itemID)',
  `spu_id`            bigint         COMMENT 'spuid',
  `price`             decimal(10,0)  COMMENT '价格',
  `sku_name`          string         COMMENT 'sku名称',
  `sku_desc`          string         COMMENT '商品规格描述',
  `weight`            decimal(10,2)  COMMENT '重量',
  `tm_id`             bigint         COMMENT '品牌(冗余)',
  `category3_id`      bigint         COMMENT '三级分类id（冗余)',
  `sku_default_img`   string         COMMENT '默认显示图片(冗余)',
  `create_time`       string         COMMENT '创建时间'
) 
COMMENT '库存单元表'
PARTITIONED BY (etl_date string comment '跑批时间，格式：yyyyMMdd')
stored AS orc 
TBLPROPERTIES ('transactional' = 'false');

