CREATE DATABASE IF NOT EXISTS ods_kkbmall;

DROP TABLE IF EXISTS ods_kkbmall.o_cart_info;


CREATE TABLE `ods_kkbmall.o_cart_info` (
  `id`           bigint           COMMENT '编号',
  `user_id`      bigint           COMMENT '用户id',
  `sku_id`       bigint           COMMENT 'skuid',
  `cart_price`   decimal(10,2)    COMMENT '放入购物车时价格',
  `sku_num`      bigint           COMMENT '数量',
  `img_url`      string           COMMENT '图片文件',
  `sku_name`     string           COMMENT 'sku名称 (冗余)',
  `create_time`  string           COMMENT '创建时间',
  `operate_time` string           COMMENT '修改时间',
  `is_ordered`   bigint           COMMENT '是否已经下单',
  `order_time`   string           COMMENT '下单时间',
  `source_type`  string           COMMENT '来源类型',
  `source_id`    bigint           COMMENT '来源编号'
)
COMMENT '购物车表 用户登录系统时更新冗余'
PARTITIONED BY (etl_date string comment '跑批时间，格式：yyyyMMdd')
stored AS orc 
TBLPROPERTIES ('transactional' = 'false');