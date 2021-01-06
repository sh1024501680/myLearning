CREATE DATABASE IF NOT EXISTS ods_kkbmall;

DROP TABLE IF EXISTS ods_kkbmall.o_order_info;


CREATE TABLE `ods_kkbmall.o_order_info` (
  `id`                      bigint           COMMENT '编号',
  `consignee`               string           COMMENT '收货人',
  `consignee_tel`           string           COMMENT '收件人电话',
  `final_total_amount`      decimal(16,2)    COMMENT '总金额',
  `order_status`            string           COMMENT '订单状态',
  `user_id`                 bigint           COMMENT '用户id',
  `delivery_address`        string           COMMENT '送货地址',
  `order_comment`           string           COMMENT '订单备注',
  `out_trade_no`            string           COMMENT '订单交易编号（第三方支付用)',
  `trade_body`              string           COMMENT '订单描述(第三方支付用)',
  `create_time`             string           COMMENT '创建时间',
  `operate_time`            string           COMMENT '操作时间',
  `expire_time`             string           COMMENT '失效时间',
  `tracking_no`             string           COMMENT '物流单编号',
  `parent_order_id`         bigint           COMMENT '父订单编号',
  `img_url`                 string           COMMENT '图片路径',
  `province_id`             int              COMMENT '地区',
  `benefit_reduce_amount`   decimal(16,2)    COMMENT '优惠金额',
  `original_total_amount`   decimal(16,2)    COMMENT '原价金额',
  `feight_fee`              decimal(16,2)    COMMENT '运费'
)
COMMENT '订单信息表'
PARTITIONED BY (etl_date string comment '跑批时间，格式：yyyyMMdd')
stored AS orc 
TBLPROPERTIES ('transactional' = 'false');