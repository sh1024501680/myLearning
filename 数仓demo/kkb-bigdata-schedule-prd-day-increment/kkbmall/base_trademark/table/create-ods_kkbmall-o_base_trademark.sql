CREATE DATABASE IF NOT EXISTS ods_kkbmall;

DROP TABLE IF EXISTS ods_kkbmall.o_base_trademark;


CREATE TABLE `ods_kkbmall.o_base_trademark` (
  `tm_id`     string COMMENT '品牌id',
  `tm_name`   string COMMENT '品牌名称'
)
COMMENT '品牌表'
PARTITIONED BY (etl_date string comment '跑批时间，格式：yyyyMMdd')
stored AS orc 
TBLPROPERTIES ('transactional' = 'false');


