CREATE DATABASE IF NOT EXISTS ods_kkbmall;

DROP TABLE IF EXISTS ods_kkbmall.o_activity_info;


CREATE TABLE `ods_kkbmall.o_activity_info` (
  `id`            bigint    COMMENT '活动id',
  `activity_name` string    COMMENT '活动名称',
  `activity_type` string    COMMENT '活动类型',
  `activity_desc` string    COMMENT '活动描述',
  `start_time`    string    COMMENT '开始时间',
  `end_time`      string    COMMENT '结束时间',
  `create_time`   string    COMMENT '创建时间'
)
COMMENT '活动信息表'
PARTITIONED BY (etl_date string comment '跑批时间，格式：yyyyMMdd')
stored AS orc 
TBLPROPERTIES ('transactional' = 'false');