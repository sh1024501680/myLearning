CREATE TABLE `activity_rule` (
  `id`               int(11) NOT NULL            COMMENT '编号',
  `activity_id`      int(11) DEFAULT NULL        COMMENT '类型',
  `condition_amount` decimal(16,2) DEFAULT NULL  COMMENT '满减金额',
  `condition_num`    bigint(20) DEFAULT NULL     COMMENT '满减件数',
  `benefit_amount`   decimal(16,2) DEFAULT NULL  COMMENT '优惠金额',
  `benefit_discount` bigint(20) DEFAULT NULL     COMMENT '优惠折扣',
  `benefit_level`    bigint(20) DEFAULT NULL     COMMENT '优惠级别',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='优惠规则';