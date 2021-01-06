CREATE DATABASE IF NOT EXISTS ods_kkbmall;

DROP TABLE IF EXISTS ods_kkbmall.o_base_dic;


CREATE TABLE `ods_kkbmall.o_base_dic` (
  `dic_code`     string  COMMENT '编号',
  `dic_name`     string  COMMENT '编码名称',
  `parent_code`  string  COMMENT '父编号',
  `create_time`  string  COMMENT '创建日期',
  `operate_time` string  COMMENT '修改日期'
)
COMMENT '字典表'
PARTITIONED BY (etl_date string comment '跑批时间，格式：yyyyMMdd')
stored AS orc 
TBLPROPERTIES ('transactional' = 'false');