CREATE TABLE `comment_info` (
  `id`           bigint(20)    NOT NULL AUTO_INCREMENT COMMENT '编号',
  `user_id`      bigint(20)    DEFAULT NULL            COMMENT '用户名称',
  `sku_id`       bigint(20)    DEFAULT NULL            COMMENT 'skuid',
  `spu_id`       bigint(20)    DEFAULT NULL            COMMENT '商品id',
  `order_id`     bigint(20)    DEFAULT NULL            COMMENT '订单编号',
  `appraise`     varchar(10)   DEFAULT NULL            COMMENT '评价 1 好评 2 中评 3 差评',
  `comment_txt`  varchar(2000) DEFAULT NULL            COMMENT '评价内容',
  `create_time`  datetime      DEFAULT NULL            COMMENT '创建时间',
  `operate_time` datetime      DEFAULT NULL            COMMENT '修改时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1319088967626371079 DEFAULT CHARSET=utf8 COMMENT='商品评论表';