insert overwrite table dwd_kkbmall.dwd_dim_sku_info partition(etl_date='20201002') select
   sku.id,
   sku.spu_id,
   sku.price,
   sku.sku_name,
   sku.sku_desc,
   sku.weight,
   sku.tm_id,
   ob.tm_name,
   sku.category3_id,
   c2.id category2_id,
   c1.id category1_id,
   c3.name category3_name,
   c2.name category2_name,
   c1.name category1_name,
   spu.spu_name,
   sku.create_time
from
(
select * from ods_kkbmall.sku_info where etl_date='20201002'
)sku join
(
select * from ods_kkbmall.o_base_trademark where etl_date='20201002'
)ob on sku.tm_id=ob.tm_id
join
(
select * from ods_kkbmall.o_spu_info where etl_date='20201002'
)spu on spu.id = sku.spu_id
join
(
select * from ods_kkbmall.o_base_category3 where etl_date='20201002'
)c3 on sku.category3_id=c3.id join
(
select * from ods_kkbmall.o_base_category2 where etl_date='20201002'
)c2 on c3.category2_id=c2.id join
(
select * from ods_kkbmall.o_base_category1 where etl_date='20201002'
)c1 on c2.category1_id=c1.id;