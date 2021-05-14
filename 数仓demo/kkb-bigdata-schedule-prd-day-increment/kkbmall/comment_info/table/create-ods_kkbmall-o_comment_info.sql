CREATE DATABASE IF NOT EXISTS ods_kkbmall;

DROP TABLE IF EXISTS ods_kkbmall.o_comment_info;


CREATE TABLE `ods_kkbmall.o_comment_info` (
  `id`           bigint     COMMENT '编号',
  `user_id`      bigint     COMMENT '用户名称',
  `sku_id`       bigint     COMMENT 'skuid',
  `spu_id`       bigint     COMMENT '商品id',
  `order_id`     bigint     COMMENT '订单编号',
  `appraise`     string     COMMENT '评价 1 好评 2 中评 3 差评',
  `comment_txt`  string     COMMENT '评价内容',
  `create_time`  string     COMMENT '创建时间',
  `operate_time` string     COMMENT '修改时间'
)
COMMENT '商品评论表'
PARTITIONED BY (etl_date string comment '跑批时间，格式：yyyyMMdd')
stored AS orc 
TBLPROPERTIES ('transactional' = 'false');