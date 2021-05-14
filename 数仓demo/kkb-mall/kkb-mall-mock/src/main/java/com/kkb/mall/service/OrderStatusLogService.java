package com.kkb.mall.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.kkb.mall.bean.OrderInfo;
import com.kkb.mall.bean.OrderStatusLog;

import java.util.List;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author zc
 * @since 2020-02-24
 */
public interface OrderStatusLogService extends IService<OrderStatusLog> {
    public void  genOrderStatusLog(List<OrderInfo> orderInfoList);

}
