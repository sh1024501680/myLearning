CREATE TABLE `base_category2` (
  `id`           bigint(20)   NOT NULL AUTO_INCREMENT    COMMENT '编号',
  `name`         varchar(200) NOT NULL                   COMMENT '二级分类名称',
  `category1_id` bigint(20)   DEFAULT NULL               COMMENT '一级分类编号',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=126 DEFAULT CHARSET=utf8 COMMENT='二级分类表';