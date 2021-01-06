CREATE TABLE `spu_info` (
  `id`           bigint(20)    NOT NULL AUTO_INCREMENT COMMENT '商品id',
  `spu_name`     varchar(200)  DEFAULT NULL            COMMENT '商品名称',
  `description`  varchar(1000) DEFAULT NULL            COMMENT '商品描述(后台简述）',
  `category3_id` bigint(20)    DEFAULT NULL            COMMENT '三级分类id',
  `tm_id`        bigint(20)    DEFAULT NULL            COMMENT '品牌id',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=13 DEFAULT CHARSET=utf8 COMMENT='商品表';