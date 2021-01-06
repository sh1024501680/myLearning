SELECT 
`id`,                   
`consignee`,            
`consignee_tel`,        
`final_total_amount`,   
`order_status`,         
`user_id`,              
`delivery_address`,     
`order_comment`,        
`out_trade_no`,         
`trade_body`,           
`create_time`,          
`operate_time`,         
`expire_time`,          
`tracking_no`,          
`parent_order_id`,      
`img_url`,              
`province_id`,          
`benefit_reduce_amount`,
`original_total_amount`,
`feight_fee`          
FROM ods_kkbmall.order_info
WHERE operate_time >='${start_time}' AND operate_time <='${end_time}'

-- 格式：start_time end_time yyyy-mm-dd hh

-- example



SELECT `id`, `user_id`, `sku_id`, `spu_id`, `order_id`, `appraise`, `comment_txt`, `create_time`, `operate_time` FROM kkbmall.comment_info
