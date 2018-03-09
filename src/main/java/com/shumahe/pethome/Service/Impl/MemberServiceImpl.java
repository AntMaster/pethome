package com.shumahe.pethome.Service.Impl;


import com.shumahe.pethome.Domain.PetPublish;
import com.shumahe.pethome.Domain.UserBasic;
import com.shumahe.pethome.Domain.UserDynamic;
import com.shumahe.pethome.Domain.UserTalk;
import com.shumahe.pethome.Enums.DynamicTypeEnum;
import com.shumahe.pethome.Enums.PetFindStateEnum;
import com.shumahe.pethome.Enums.ResultEnum;
import com.shumahe.pethome.Exception.PetHomeException;
import com.shumahe.pethome.Repository.*;
import com.shumahe.pethome.Service.MemberService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class MemberServiceImpl implements MemberService {


    @Autowired
    private UserBasicRepository userBasicRepository;

    @Autowired
    private PetPublishRepository petPublishRepository;


    @Autowired
    private UserDynamicRepository userDynamicRepository;


    @Autowired
    private UserTalkRepository userTalkRepository;

    @Autowired
    private PublishTalkRepository publishTalkRepository;


    @Autowired
    private MemberTagsRepository memberTagsRepository;


    @Autowired
    private MemberTagsMappingRepository memberTagsMappingRepository;


    /**
     * 我的中心
     *
     * @param openId
     */
    @Override
    public void findMyInfo(String openId) {

        Map<String, String> myselfDetail = new HashMap<>();

        UserBasic myselfBasic = userBasicRepository.findByOpenId(openId);
        if (myselfBasic.getOpenId() == null) {
            throw new PetHomeException(ResultEnum.RESULT_EMPTY);
        }

        //待处理条数
        List<PetPublish> notFounds = petPublishRepository.findByPublisherIdAndFindState(openId, PetFindStateEnum.NOT_FOUND.getCode());


        //私信条数
        List<UserTalk> myNotViewTalks = userTalkRepository.findMyNotViewTalk(openId, openId);


        //转发条数
        List<UserDynamic> shareMe = userDynamicRepository.findByUserIdArriveAndDynamicTypeOrderByCreateTimeDesc(openId, DynamicTypeEnum.SHARE.getCode());


        //关注条数
        List<UserDynamic> likeMe = userDynamicRepository.findByUserIdArriveAndDynamicTypeOrderByCreateTimeDesc(openId, DynamicTypeEnum.LIKE.getCode());


        //互动条数



    }
}
