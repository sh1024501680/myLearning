CREATE DATABASE IF NOT EXISTS ods_kkbmall;

DROP TABLE IF EXISTS ods_kkbmall.o_base_category1;

CREATE TABLE `ods_kkbmall.o_base_category1` (
  `id`      bigint     COMMENT '编号',
  `name`    string     COMMENT '分类名称'
) 
COMMENT '一级分类表'
PARTITIONED BY (etl_date string comment '跑批时间，格式：yyyyMMdd')
stored AS orc 
TBLPROPERTIES ('transactional' = 'false');
