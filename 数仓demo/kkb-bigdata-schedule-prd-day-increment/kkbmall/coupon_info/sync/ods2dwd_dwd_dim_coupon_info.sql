insert overwrite table dwd_kkbmall.dwd_dim_coupon_info partition(etl_date='20201002') 
select
   id,
   coupon_name,
   coupon_type,
   condition_amount,
   condition_num,
   activity_id,
   benefit_amount,
   benefit_discount,
   create_time,
   range_type,
   spu_id,
   tm_id,
   category3_id,
   limit_num,
   operate_time,
   expire_time
from ods_kkbmall.o_coupon_info
where etl_date='20201002';