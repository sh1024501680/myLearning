CREATE TABLE `base_category3` (
  `id`           bigint(20)   NOT NULL AUTO_INCREMENT COMMENT '编号',
  `name`         varchar(200) NOT NULL                COMMENT '三级分类名称',
  `category2_id` bigint(20)   DEFAULT NULL            COMMENT '二级分类编号',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1150 DEFAULT CHARSET=utf8 COMMENT='三级分类表';