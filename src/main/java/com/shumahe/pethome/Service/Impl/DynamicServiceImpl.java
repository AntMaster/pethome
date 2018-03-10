package com.shumahe.pethome.Service.Impl;

import com.shumahe.pethome.Domain.UserDynamic;
import com.shumahe.pethome.Enums.DynamicTypeEnum;
import com.shumahe.pethome.Enums.ResultEnum;
import com.shumahe.pethome.Exception.PetHomeException;
import com.shumahe.pethome.Repository.PetPublishRepository;
import com.shumahe.pethome.Repository.UserBasicRepository;
import com.shumahe.pethome.Repository.UserDynamicRepository;
import com.shumahe.pethome.Service.BaseService.DynamicBaseService;
import com.shumahe.pethome.Service.DynamicService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class DynamicServiceImpl implements DynamicService {

    @Autowired
    UserDynamicRepository userDynamicRepository;

    @Autowired
    UserBasicRepository userBasicRepository;

    @Autowired
    PetPublishRepository petPublishRepository;
    
    
    @Autowired
    DynamicBaseService dynamicBaseService;

    /**
     * 关注列表(我的关注+关注我的)
     * @param openId
     * @param type
     * @return
     */
    @Override
    public List<Map<String, String>> findMyLike(String openId, int type) {

        /**
         * 1.查询dynamic表基本信息
         *
         * 2.查询member得到用户信息
         *
         * 3.查询publish表得到发布信息
         *
         */

        //step 1
        List<UserDynamic> userDynamics = new ArrayList<>();

        if (type == 1) {//我的关注

            userDynamics = userDynamicRepository.findByUserIdFromAndDynamicTypeOrderByCreateTimeDesc(openId, DynamicTypeEnum.LIKE.getCode());

        } else if (type == 2) {//关注我的

            userDynamics = userDynamicRepository.findByUserIdArriveAndDynamicTypeOrderByCreateTimeDesc(openId, DynamicTypeEnum.LIKE.getCode());
        }

        //data empty
        if (userDynamics.size() == 0) {
            throw new PetHomeException(ResultEnum.RESULT_EMPTY);
        }

        /**
         * baseDynamicService 根据互动类型返回最终结果
         */
        List<Map<String, String>> likeList = dynamicBaseService.findLikeOrShareList(userDynamics,type);


        return likeList;
    }

    /**
     * 转发列表(我的转发+转发我的)
     * @param openId
     * @param type
     * @return
     */
    @Override
    public List<Map<String, String>> findMyShare(String openId, int type) {

        /**
         * 1.查询dynamic表基本信息
         *
         * 2.查询member得到用户信息
         *
         * 3.查询publish表得到发布信息
         *
         */

        //step 1
        List<UserDynamic> userDynamics = new ArrayList<>();

        if (type == 1) {//我的转发

            userDynamics = userDynamicRepository.findByUserIdFromAndDynamicTypeOrderByCreateTimeDesc(openId, DynamicTypeEnum.SHARE.getCode());

        } else if (type == 2) {//转发我的

            userDynamics = userDynamicRepository.findByUserIdArriveAndDynamicTypeOrderByCreateTimeDesc(openId, DynamicTypeEnum.SHARE.getCode());
        }

        //data empty
        if (userDynamics.size() == 0) {
            throw new PetHomeException(ResultEnum.RESULT_EMPTY);
        }

        /**
         * baseDynamicService 根据互动类型返回最终结果
         */
        List<Map<String, String>> shareList = dynamicBaseService.findLikeOrShareList(userDynamics,type);


        return shareList;

    }


}
