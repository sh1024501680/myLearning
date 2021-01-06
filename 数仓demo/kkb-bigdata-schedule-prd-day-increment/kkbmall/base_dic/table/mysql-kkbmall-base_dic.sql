CREATE TABLE `base_dic` (
  `dic_code`     varchar(10) NOT NULL        COMMENT '编号',
  `dic_name`     varchar(100) DEFAULT NULL   COMMENT '编码名称',
  `parent_code`  varchar(10) DEFAULT NULL    COMMENT '父编号',
  `create_time`  datetime DEFAULT NULL       COMMENT '创建日期',
  `operate_time` datetime DEFAULT NULL       COMMENT '修改日期',
  PRIMARY KEY (`dic_code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='编码字典表';