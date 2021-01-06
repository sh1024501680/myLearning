CREATE TABLE `coupon_info` (
  `id`               bigint(20) NOT NULL AUTO_INCREMENT    COMMENT '购物券编号',
  `coupon_name`      varchar(100)  DEFAULT NULL            COMMENT '购物券名称',
  `coupon_type`      varchar(10)   DEFAULT NULL            COMMENT '购物券类型 1 现金券 2 折扣券 3 满减券 4 满件打折券',
  `condition_amount` decimal(10,2)  DEFAULT NULL           COMMENT '满额数',
  `condition_num`    bigint(20)     DEFAULT NULL           COMMENT '满件数',
  `activity_id`      bigint(20)     DEFAULT NULL           COMMENT '活动编号',
  `benefit_amount`   decimal(16,2)  DEFAULT NULL           COMMENT '减金额',
  `benefit_discount` bigint(20)     DEFAULT NULL           COMMENT '折扣',
  `create_time`      datetime       DEFAULT NULL           COMMENT '创建时间',
  `range_type`       varchar(10)    DEFAULT NULL           COMMENT '范围类型 1、商品 2、品类 3、品牌',
  `spu_id`           bigint(20)     DEFAULT NULL           COMMENT '商品id',
  `tm_id`            bigint(20)     DEFAULT NULL           COMMENT '品牌id',
  `category3_id`     bigint(20)     DEFAULT NULL           COMMENT '品类id',
  `limit_num`        int(11)        DEFAULT NULL           COMMENT '最多领用次数',
  `operate_time`     datetime       DEFAULT NULL           COMMENT '修改时间',
  `expire_time`      datetime       DEFAULT NULL           COMMENT '过期时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8 COMMENT='优惠券表';