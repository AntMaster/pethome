package com.shumahe.pethome.Service.Impl;

import com.shumahe.pethome.DTO.UserDTO;
import com.shumahe.pethome.Domain.MemberTags;
import com.shumahe.pethome.Domain.MemberTagsMapping;
import com.shumahe.pethome.Domain.UserApprove;
import com.shumahe.pethome.Domain.UserBasic;
import com.shumahe.pethome.Enums.PetFindStateEnum;
import com.shumahe.pethome.Enums.ReadStateEnum;
import com.shumahe.pethome.Enums.ResultEnum;
import com.shumahe.pethome.Exception.PetHomeException;
import com.shumahe.pethome.Form.UserApproveForm;
import com.shumahe.pethome.Repository.*;
import com.shumahe.pethome.Service.UserService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    UserTalkRepository userTalkRepository;

    @Autowired
    UserBasicRepository userBasicRepository;

    @Autowired
    PetPublishRepository petPublishRepository;


    @Autowired
    PublishTalkRepository publishTalkRepository;


    @Autowired
    private MemberTagsRepository memberTagsRepository;


    @Autowired
    private MemberTagsMappingRepository memberTagsMappingRepository;


    @Autowired
    private UserApproveRepository userApproveRepository;

    /**
     * 我的中心
     *
     * @param openId
     */
    @Override
    public UserDTO findMyInfo(String openId) {


        UserBasic my = userBasicRepository.findByOpenId(openId);
        if (my.getOpenId() == null) {
            throw new PetHomeException(ResultEnum.RESULT_EMPTY);
        }


        //标签名称
        List<MemberTagsMapping> tagsMapping = memberTagsMappingRepository.findByMemberIdOrderByTagId(my.getId());
        List<Integer> tagID = tagsMapping.stream().map(e -> e.getTagId()).collect(Collectors.toList());
        List<MemberTags> tags = memberTagsRepository.findByIdIn(tagID);
        List<String> tagsName = tags.stream().map(e -> e.getName()).collect(Collectors.toList());

        //待处理条数
        int petCount = petPublishRepository.notReadPetCount(openId, PetFindStateEnum.NOT_FOUND.getCode());


        //未读私信条数
        int privateCount = userTalkRepository.notReadTalksCount(openId, ReadStateEnum.NOT_READ.getCode());


        //未读互动条数
        int publishCount = publishTalkRepository.notReadTalksCount(openId, openId, openId, ReadStateEnum.NOT_READ.getCode());

        //转发条数
        //List<UserDynamic> shareMe = userDynamicRepository.findByUserIdArriveAndDynamicTypeOrderByCreateTimeDesc(openId, DynamicTypeEnum.SHARE.getCode());


        //关注条数
        //List<UserDynamic> likeMe = userDynamicRepository.findByUserIdArriveAndDynamicTypeOrderByCreateTimeDesc(openId, DynamicTypeEnum.LIKE.getCode());


        UserDTO userDTO = new UserDTO(my.getOpenId(), my.getNickName(), my.getHeadImgUrl());
        userDTO.setApprove(my.getApprove());
        userDTO.setUnFinishCount(petCount);
        userDTO.setPrivateMsgCount(privateCount);
        userDTO.setPublicMsgCount(publishCount);
        if (!tagsName.isEmpty())
            userDTO.setTagName(tagsName.get(0));


        return userDTO;

    }

    /**
     * 企业认证
     *
     * @param userApproveForm
     * @return
     */
    @Override
    public UserApprove saveOrganization(UserApproveForm userApproveForm) {

        UserApprove userApprove = new UserApprove();
        BeanUtils.copyProperties(userApproveForm, userApprove);

        UserApprove save = userApproveRepository.save(userApprove);

        return save;
    }

}
