package com.kkb.mall.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.kkb.mall.bean.CouponUse;
import com.kkb.mall.bean.OrderInfo;

import java.util.List;

/**
 * <p>
 * 优惠券领用表 服务类
 * </p>
 *
 * @author zhangchen
 * @since 2020-02-26
 */
public interface CouponUseService extends IService<CouponUse> {

    public void genCoupon(Boolean ifClear);

    public  void  usedCoupon(List<OrderInfo> orderInfoList);

    public List<CouponUse> usingCoupon(List<OrderInfo> orderInfoList);

    public  void  saveCouponUseList(List<CouponUse> couponUseList);


}
