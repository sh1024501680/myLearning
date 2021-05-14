CREATE DATABASE IF NOT EXISTS ods_kkbmall;

DROP TABLE IF EXISTS ods_kkbmall.o_favor_info;


CREATE TABLE `ods_kkbmall.o_favor_info` (
  `id`           bigint    COMMENT '编号',
  `user_id`      bigint    COMMENT '用户名称',
  `sku_id`       bigint    COMMENT 'skuid',
  `spu_id`       bigint    COMMENT '商品id',
  `is_cancel`    string    COMMENT '是否已取消 0 正常 1 已取消',
  `create_time`  string    COMMENT '创建时间',
  `cancel_time`  string    COMMENT '修改时间'
)
COMMENT '商品收藏表'
PARTITIONED BY (etl_date string comment '跑批时间，格式：yyyyMMdd')
stored AS orc 
TBLPROPERTIES ('transactional' = 'false');