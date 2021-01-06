CREATE DATABASE IF NOT EXISTS ods_kkbmall;

DROP TABLE IF EXISTS ods_kkbmall.o_base_category3;

CREATE TABLE `ods_kkbmall.o_base_category3` (
  `id`            bigint     COMMENT '编号',
  `name`          string     COMMENT '分类名称',
  `category2_id`  bigint     COMMENT '一级分类编号'
) 
COMMENT '二级分类表'
PARTITIONED BY (etl_date string comment '跑批时间，格式：yyyyMMdd')
stored AS orc 
TBLPROPERTIES ('transactional' = 'false');
