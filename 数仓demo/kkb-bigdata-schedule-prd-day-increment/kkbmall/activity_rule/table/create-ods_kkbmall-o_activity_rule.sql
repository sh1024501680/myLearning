CREATE DATABASE IF NOT EXISTS ods_kkbmall;

DROP TABLE IF EXISTS ods_kkbmall.o_activity_rule;


CREATE TABLE `ods_kkbmall.o_activity_rule` (
  `id`               int             COMMENT '编号',
  `activity_id`      int             COMMENT '类型',
  `condition_amount` decimal(16,2)   COMMENT '满减金额',
  `condition_num`    bigint          COMMENT '满减件数',
  `benefit_amount`   decimal(16,2)   COMMENT '优惠金额',
  `benefit_discount` bigint          COMMENT '优惠折扣',
  `benefit_level`    bigint          COMMENT '优惠级别'
)
COMMENT '活动规则表'
PARTITIONED BY (etl_date string comment '跑批时间，格式：yyyyMMdd')
stored AS orc 
TBLPROPERTIES ('transactional' = 'false');