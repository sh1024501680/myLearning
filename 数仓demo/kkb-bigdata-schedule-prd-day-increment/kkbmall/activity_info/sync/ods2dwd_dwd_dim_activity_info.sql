insert overwrite table dwd_kkbmall.dwd_dim_activity_info partition  
(etl_date='2020-06-14') 
select
   id,
   activity_name,
   activity_type,
   start_time,
   end_time,
   create_time
from ods_kkbmall.o_activity_info
where etl_date='20201002';