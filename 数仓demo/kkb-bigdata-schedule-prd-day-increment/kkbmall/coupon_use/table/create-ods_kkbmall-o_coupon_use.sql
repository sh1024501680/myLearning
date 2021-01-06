CREATE DATABASE IF NOT EXISTS ods_kkbmall;

DROP TABLE IF EXISTS ods_kkbmall.o_coupon_use;


CREATE TABLE `ods_kkbmall.o_coupon_use` (
  `id`             bigint   COMMENT '编号',
  `coupon_id`      bigint   COMMENT '购物券ID',
  `user_id`        bigint   COMMENT '用户ID',
  `order_id`       bigint   COMMENT '订单ID',
  `coupon_status`  string   COMMENT '购物券状态',
  `get_time`       string   COMMENT '领券时间',
  `using_time`     string   COMMENT '使用时间',
  `used_time`      string   COMMENT '支付时间',
  `expire_time`    string   COMMENT '过期时间'
)
COMMENT '优惠券领用表'
PARTITIONED BY (etl_date string comment '跑批时间，格式：yyyyMMdd')
stored AS orc 
TBLPROPERTIES ('transactional' = 'false');