CREATE TABLE `base_category1` (
  `id`      bigint(20)     NOT NULL AUTO_INCREMENT COMMENT '编号',
  `name`    varchar(10)    NOT NULL COMMENT '分类名称',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=19 DEFAULT CHARSET=utf8 COMMENT='一级分类表';