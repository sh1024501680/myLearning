CREATE DATABASE IF NOT EXISTS ods_kkbmall;

DROP TABLE IF EXISTS ods_kkbmall.o_user_info;

CREATE TABLE `ods_kkbmall.o_user_info` (
  `id`           bigint    COMMENT '编号',
  `login_name`   string   COMMENT '用户名称',
  `nick_name`    string   COMMENT '用户昵称',
  `passwd`       string   COMMENT '用户密码',
  `name`         string   COMMENT '用户姓名',
  `phone_num`    string   COMMENT '手机号',
  `email`        string   COMMENT '邮箱',
  `head_img`     string   COMMENT '头像',
  `user_level`   string   COMMENT '用户级别',
  `birthday`     string   COMMENT '用户生日',
  `gender`       string   COMMENT '性别 M男,F女',
  `create_time`  string   COMMENT '创建时间',
  `operate_time` string   COMMENT '修改时间'
) 
COMMENT '用户表'
PARTITIONED BY (etl_date string comment '跑批时间，格式：yyyyMMdd')
stored AS orc 
TBLPROPERTIES ('transactional' = 'false');