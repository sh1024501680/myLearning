CREATE TABLE `activity_info` (
  `id`            bigint NOT NULL AUTO_INCREMENT COMMENT '活动id',
  `activity_name` string     DEFAULT NULL      COMMENT '活动名称',
  `activity_type` string     DEFAULT NULL      COMMENT '活动类型',
  `activity_desc` string     DEFAULT NULL      COMMENT '活动描述',
  `start_time`    string     DEFAULT NULL      COMMENT '开始时间',
  `end_time`      string     DEFAULT NULL      COMMENT '结束时间',
  `create_time`   string     DEFAULT NULL      COMMENT '创建时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8 COMMENT='活动表';