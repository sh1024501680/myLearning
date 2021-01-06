CREATE TABLE `sku_info` (
  `id`                bigint(20)     NOT NULL AUTO_INCREMENT COMMENT 'skuid(itemID)',
  `spu_id`            bigint(20)     DEFAULT NULL            COMMENT 'spuid',
  `price`             decimal(10,0)  DEFAULT NULL            COMMENT '价格',
  `sku_name`          varchar(200)   DEFAULT NULL            COMMENT 'sku名称',
  `sku_desc`          varchar(2000)  DEFAULT NULL            COMMENT '商品规格描述',
  `weight`            decimal(10,2)  DEFAULT NULL            COMMENT '重量',
  `tm_id`             bigint(20)     DEFAULT NULL            COMMENT '品牌(冗余)',
  `category3_id`      bigint(20)     DEFAULT NULL            COMMENT '三级分类id（冗余)',
  `sku_default_img`   varchar(200)   DEFAULT NULL            COMMENT '默认显示图片(冗余)',
  `create_time`       datetime       DEFAULT NULL            COMMENT '创建时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=17 DEFAULT CHARSET=utf8 COMMENT='库存单元表';