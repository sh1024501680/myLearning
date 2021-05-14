package com.kkb.mall.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.kkb.mall.bean.UserInfo;

/**
 * <p>
 * 用户表 服务类
 * </p>
 *
 * @author zc
 * @since 2020-02-23
 */
public interface UserInfoService extends IService<UserInfo> {

    public void  genUserInfos(Boolean ifClear);

}
