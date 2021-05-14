CREATE DATABASE IF NOT EXISTS dwd_kkbmall;

DROP TABLE IF EXISTS dwd_kkbmall.dwd_dim_activity_info;

CREATE EXTERNAL TABLE `dwd_kkbmall.dwd_dim_activity_info` (
`id`            string COMMENT 'id', 
`province_name` string COMMENT '省市名称', 
`area_code`     string COMMENT '地区编码', 
`iso_code`      string COMMENT 'ISO 编码', 
`region_id`     string COMMENT '地区id', 
`region_name`   string COMMENT '地区名称'
) 
COMMENT '活动维度表'
PARTITIONED BY (etl_date string comment '跑批时间，格式：yyyyMMdd')
stored AS orc 
TBLPROPERTIES ('transactional' = 'false');