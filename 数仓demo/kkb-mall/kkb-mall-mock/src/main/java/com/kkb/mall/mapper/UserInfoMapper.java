package com.kkb.mall.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.kkb.mall.bean.UserInfo;
import org.apache.ibatis.annotations.Update;

/**
 * <p>
 * 用户表 Mapper 接口
 * </p>
 *
 */
public interface UserInfoMapper extends BaseMapper<UserInfo> {

    @Update("truncate table user_info")
    public  void truncateUserInfo();

}
