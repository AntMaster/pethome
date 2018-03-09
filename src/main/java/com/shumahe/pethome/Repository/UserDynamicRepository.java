package com.shumahe.pethome.Repository;


import com.shumahe.pethome.Domain.UserDynamic;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UserDynamicRepository extends JpaRepository<UserDynamic,Integer>{

    /**
     *  根据我的openId,互动类型 查询我的关注
     * @param openId
     * @param dynamicType
     * @return
     */
    List<UserDynamic> findByUserIdFromAndDynamicTypeOrderByCreateTimeDesc(String openId ,int dynamicType);


    /**
     * 根据我的openId,互动类型 查询关注我的
     * @param openId
     * @param dynamicType
     * @return
     */
    List<UserDynamic> findByUserIdArriveAndDynamicTypeOrderByCreateTimeDesc(String openId,int dynamicType);


}
