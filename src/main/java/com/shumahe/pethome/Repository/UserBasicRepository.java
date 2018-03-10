package com.shumahe.pethome.Repository;

import com.shumahe.pethome.Domain.UserBasic;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UserBasicRepository extends JpaRepository<UserBasic,Integer> {


    /**
     * 多个用户信息
     * @param openId
     * @return
     */
    List<UserBasic> findByOpenIdIn(List<String> openId);

    /**
     * 一个用户信息
     * @param openId
     * @return
     */
    UserBasic findByOpenId(String openId);


    /**
     * 根据昵称查询用户
     * @param nickname
     * @return
     */
    List<UserBasic> findByNickNameContains(String nickname);



    List<UserBasic> findByAppIdAndOpenId(String appId,String openId);



}
