CREATE TABLE `user_info` (
  `id`           bigint(20)   NOT NULL AUTO_INCREMENT COMMENT '编号',
  `login_name`   varchar(200) DEFAULT NULL            COMMENT '用户名称',
  `nick_name`    varchar(200) DEFAULT NULL            COMMENT '用户昵称',
  `passwd`       varchar(200) DEFAULT NULL            COMMENT '用户密码',
  `name`         varchar(200) DEFAULT NULL            COMMENT '用户姓名',
  `phone_num`    varchar(200) DEFAULT NULL            COMMENT '手机号',
  `email`        varchar(200) DEFAULT NULL            COMMENT '邮箱',
  `head_img`     varchar(200) DEFAULT NULL            COMMENT '头像',
  `user_level`   varchar(200) DEFAULT NULL            COMMENT '用户级别',
  `birthday`     date         DEFAULT NULL            COMMENT '用户生日',
  `gender`       varchar(1)   DEFAULT NULL            COMMENT '性别 M男,F女',
  `create_time`  datetime     DEFAULT NULL            COMMENT '创建时间',
  `operate_time` datetime     DEFAULT NULL            COMMENT '修改时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=5001 DEFAULT CHARSET=utf8 COMMENT='用户表';