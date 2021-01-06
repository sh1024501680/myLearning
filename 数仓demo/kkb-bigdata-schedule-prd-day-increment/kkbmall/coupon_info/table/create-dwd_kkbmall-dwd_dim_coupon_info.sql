CREATE DATABASE IF NOT EXISTS dwd_kkbmall;

DROP TABLE IF EXISTS dwd_kkbmall.dwd_dim_coupon_info;

create  table dwd_kkbmall.dwd_dim_coupon_info(
`id`                string          COMMENT '购物券编号',
`coupon_name`       string          COMMENT '购物券名称',
`coupon_type`       string          COMMENT '购物券类型 1 现金券 2 折扣券 3 满减券 4 满件打折券', 
`condition_amount`  decimal(16,2)   COMMENT '满额数',
`condition_num`     bigint          COMMENT '满件数',
`activity_id`       string          COMMENT '活动编号',
`benefit_amount`    decimal(16,2)   COMMENT '减金额',
`benefit_discount`  decimal(16,2)   COMMENT '折扣',
`create_time`       string          COMMENT '创建时间',
`range_type`        string          COMMENT '范围类型 1、商品 2、品类 3、品牌',
`spu_id`            string          COMMENT '商品id',
`tm_id`             string          COMMENT '品牌id',
`category3_id`      string          COMMENT '品类id',
`limit_num`         bigint          COMMENT '最多领用次数',
`operate_time`      string          COMMENT '修改时间',
`expire_time`       string          COMMENT '过期时间'
) 
COMMENT '优惠券维度表'
PARTITIONED BY (etl_date string comment '跑批时间，格式：yyyyMMdd')
stored AS orc 
TBLPROPERTIES ('transactional' = 'false');