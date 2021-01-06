CREATE TABLE `favor_info` (
  `id`           bigint(20) NOT NULL AUTO_INCREMENT COMMENT '编号',
  `user_id`      bigint(20) DEFAULT NULL            COMMENT '用户名称',
  `sku_id`       bigint(20) DEFAULT NULL            COMMENT 'skuid',
  `spu_id`       bigint(20) DEFAULT NULL            COMMENT '商品id',
  `is_cancel`    varchar(1) DEFAULT NULL            COMMENT '是否已取消 0 正常 1 已取消',
  `create_time`  datetime   DEFAULT NULL            COMMENT '创建时间',
  `cancel_time`  datetime   DEFAULT NULL            COMMENT '修改时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1319088953453817866 DEFAULT CHARSET=utf8 COMMENT='商品收藏表';