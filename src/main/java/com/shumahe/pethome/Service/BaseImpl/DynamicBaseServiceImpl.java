package com.shumahe.pethome.Service.BaseImpl;

import com.shumahe.pethome.Domain.PetPublish;
import com.shumahe.pethome.Domain.UserBasic;
import com.shumahe.pethome.Domain.UserDynamic;
import com.shumahe.pethome.Enums.ResultEnum;
import com.shumahe.pethome.Exception.PetHomeException;
import com.shumahe.pethome.Repository.PetPublishRepository;
import com.shumahe.pethome.Repository.UserBasicRepository;
import com.shumahe.pethome.Service.BaseService.DynamicBaseService;
import com.shumahe.pethome.Util.CollectionUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class DynamicBaseServiceImpl implements DynamicBaseService {


    @Autowired
    UserBasicRepository userBasicRepository;

    @Autowired
    PetPublishRepository petPublishRepository;

    /**
     * @param userDynamics
     * @param type         type = 1 我的（关注/转发）  type = 2  （关注/转发）我的
     * @return
     */
    @Override
    public List<Map<String, String>> findLikeOrShareList(List<UserDynamic> userDynamics, int type) {


        if (userDynamics.size() == 0) {

            throw new PetHomeException(ResultEnum.RESULT_EMPTY.getCode(), "findLikeAndShareList方法参数为空");
        }

        /**
         * 本人信息
         */
        String myselfOpenId = null;
        if (type == 1) {

            myselfOpenId = userDynamics.get(1).getUserIdFrom();

        } else if (type == 2) {
            myselfOpenId = userDynamics.get(1).getUserIdArrive();

        }

        UserBasic myselfInfo = userBasicRepository.findByOpenId(myselfOpenId);


        /**
         *
         * 1.查询（关注我的，转发我的，我关注的，关注我的）发布信息
         *
         * 2.查询（关注我的，转发我的，我关注的，关注我的）人员信息
         *
         * 3.整理 动态信息 + 发布信息 + 人员信息
         *
         *
         */

        List<String> friendsId = new ArrayList<>();
        List<Integer> publishIds = new ArrayList<>();
        userDynamics.forEach(dynamic -> {
            if (type == 1) {

                friendsId.add(dynamic.getUserIdArrive());//其他人OpenID
                dynamic.setUserIdFrom(dynamic.getUserIdArrive());//将自己openID全部转化为其他人-->后面整理数据不用再判断,随便取

            } else if (type == 2) {

                friendsId.add(dynamic.getUserIdFrom());//其他人OpenID
                dynamic.setUserIdArrive(dynamic.getUserIdFrom());//将自己openID全部转化为其他人

            }
            publishIds.add(dynamic.getPublishId());
        });

        //其他人openId去重
        List<String> finalFriendsId = CollectionUtil.removeRepeatStringItem(friendsId);


        /**
         * step 1
         */
        List<UserBasic> friends = userBasicRepository.findByOpenIdIn(finalFriendsId);

        /**
         * step 2
         */
        List<PetPublish> publishes = petPublishRepository.findByIdIn(publishIds);


        /**
         * step 3
         */
        List<Map<String, String>> resultSorted = new ArrayList<>();

        userDynamics.forEach(dynamic -> {

            Map<String, String> _tempMap = new HashMap<>();

            //userInfo
            friends.forEach(friend -> {

                if (dynamic.getUserIdArrive().equals(friend.getOpenId())) {//getUserIdArrive()与getUserIdFrom()都可以,service层已转化

                    _tempMap.put("friend", friend.getNickName());
                    _tempMap.put("friendOpenId", friend.getOpenId());
                    _tempMap.put("friendHeadImage", friend.getHeadImgUrl());
                    _tempMap.put("likeType", String.valueOf(type));

                }
            });

            //publishInfo
            publishes.forEach(publish -> {
                if (dynamic.getPublishId() == publish.getId()) {

                    _tempMap.put("petImage", publish.getPetImage());
                    _tempMap.put("petName", publish.getPetName());
                    _tempMap.put("publishType", String.valueOf(publish.getPublishType()));
                    _tempMap.put("petClassify", String.valueOf(publish.getClassifyId()));
                    _tempMap.put("lostTime", publish.getLostTime().toString());
                }

            });

            _tempMap.put("myNickName", myselfInfo.getNickName());
            _tempMap.put("myHeadImage", myselfInfo.getHeadImgUrl());
            _tempMap.put("createTime", dynamic.getCreateTime().toString());

            resultSorted.add(_tempMap);

        });

        return resultSorted;
    }
}


