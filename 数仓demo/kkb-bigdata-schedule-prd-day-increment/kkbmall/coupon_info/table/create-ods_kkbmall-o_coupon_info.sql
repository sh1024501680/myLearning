CREATE DATABASE IF NOT EXISTS ods_kkbmall;

DROP TABLE IF EXISTS ods_kkbmall.o_coupon_info;


CREATE TABLE `ods_kkbmall.o_coupon_info` (
  `id`               bigint         COMMENT '购物券编号',
  `coupon_name`      string         COMMENT '购物券名称',
  `coupon_type`      string         COMMENT '购物券类型 1 现金券 2 折扣券 3 满减券 4 满件打折券',
  `condition_amount` decimal(10,2)  COMMENT '满额数',
  `condition_num`    bigint         COMMENT '满件数',
  `activity_id`      bigint         COMMENT '活动编号',
  `benefit_amount`   decimal(16,2)  COMMENT '减金额',
  `benefit_discount` bigint         COMMENT '折扣',
  `create_time`      string         COMMENT '创建时间',
  `range_type`       string         COMMENT '范围类型 1、商品 2、品类 3、品牌',
  `spu_id`           bigint         COMMENT '商品id',
  `tm_id`            bigint         COMMENT '品牌id',
  `category3_id`     bigint         COMMENT '品类id',
  `limit_num`        int            COMMENT '最多领用次数',
  `operate_time`     string         COMMENT '修改时间',
  `expire_time`      string         COMMENT '过期时间'
)
COMMENT '优惠券表'
PARTITIONED BY (etl_date string comment '跑批时间，格式：yyyyMMdd')
stored AS orc 
TBLPROPERTIES ('transactional' = 'false');