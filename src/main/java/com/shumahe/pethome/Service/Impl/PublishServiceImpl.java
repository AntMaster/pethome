package com.shumahe.pethome.Service.Impl;


import com.shumahe.pethome.DTO.PublicMsgDTO;
import com.shumahe.pethome.DTO.PublishDTO;
import com.shumahe.pethome.Domain.PetPublish;

import com.shumahe.pethome.Domain.UserBasic;
import com.shumahe.pethome.Domain.UserDynamic;
import com.shumahe.pethome.Enums.*;
import com.shumahe.pethome.Exception.PetHomeException;

import com.shumahe.pethome.Form.PublishMasterForm;
import com.shumahe.pethome.Form.PublishPetForm;
import com.shumahe.pethome.Repository.*;
import com.shumahe.pethome.Service.BaseService.PublishBaseService;
import com.shumahe.pethome.Service.MessageService;
import com.shumahe.pethome.Service.PublishService;
import com.shumahe.pethome.Util.ResultVOUtil;
import com.shumahe.pethome.VO.ResultVO;
import javafx.util.converter.DateStringConverter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import org.springframework.stereotype.Service;


import java.util.*;


import static org.springframework.beans.BeanUtils.*;

@Service
@Slf4j
public class PublishServiceImpl implements PublishService {


    @Autowired
    private PetPublishRepository petPublishRepository;


    @Autowired
    private UserTalkRepository userTalkRepository;


    @Autowired
    PetVarietyRepository petVarietyRepository;

    @Autowired
    UserBasicRepository userBasicRepository;


    @Autowired
    PublishBaseService publishBaseService;


    @Autowired
    MessageService messageService;


    @Autowired
    UserDynamicRepository userDynamicRepository;

    /**
     * 主页列表(动态+寻主+寻宠)
     *
     * @param publishType
     * @param pageable
     * @return
     */
    @Override
    public List<PublishDTO> findAll(String openId, Integer publishType, PageRequest pageable) {


        Page<PetPublish> result;
        if (publishType == 0) {
            /**动态*/
            result = petPublishRepository.findByPublishStateOrderByCreateTimeDesc(ShowStateEnum.SHOW.getCode(), pageable);

        } else {
            /**寻主/寻宠*/
            result = petPublishRepository.findByPublishTypeAndPublishStateOrderByCreateTimeDesc(publishType, ShowStateEnum.SHOW.getCode(), pageable);
        }

        List<PetPublish> publishList = result.getContent();
        if (publishList.isEmpty()) {
            throw new PetHomeException(ResultEnum.RESULT_EMPTY);
        }


        /**
         * BaseService查关联信息
         */
        List<PublishDTO> publishDTOS = publishBaseService.findPetExtends(publishList);

        /**
         * 查看我是否关注了该发布
         */
        List<UserDynamic> likes = userDynamicRepository.findByUserIdFromAndDynamicTypeOrderByCreateTimeDesc(openId, DynamicTypeEnum.LIKE.getCode());
        int[] publishIds = likes.stream().mapToInt(e -> e.getPublishId()).distinct().toArray();

        publishDTOS.forEach(a -> Arrays.stream(publishIds).forEach(e -> {
            if (a.getId() == e) {
                a.setLikeState(true);
            }
        }));

        return publishDTOS;

    }

    /**
     * 寻宠发布
     *
     * @param petForm
     * @return
     */
    @Override
    public PetPublish createPet(PublishPetForm petForm) {
        /**
         * 转换为PetPublish对象
         */
        PetPublish petPublish = new PetPublish();
        copyProperties(petForm, petPublish);

        //未转换成功的字段
        petPublish.setPublisherId(petForm.getOpenId());
        petPublish.setPublishType(PublishTypeEnum.SEARCH_PET.getCode());
        petPublish.setLostTime(new DateStringConverter().fromString(petForm.getLostTime()));

        return petPublishRepository.save(petPublish);
    }

    /**
     * 寻主发布
     *
     * @param masterForm
     * @return
     */
    @Override
    public PetPublish createMaster(PublishMasterForm masterForm) {
        /**
         * 转换为PetPublish对象
         */
        PetPublish petPublish = new PetPublish();
        copyProperties(masterForm, petPublish);

        //未转换成功的字段
        petPublish.setPublisherId(masterForm.getOpenId());
        petPublish.setPublishType(PublishTypeEnum.SEARCH_MASTER.getCode());
        petPublish.setLostTime(new DateStringConverter().fromString(masterForm.getFindTime()));

        return petPublishRepository.save(petPublish);
    }

    /**
     * 我的发布列表
     *
     * @param openId
     * @return
     */
    @Override
    public List<PublishDTO> findMyPublishList(String openId, PageRequest pageable) {

        /**
         * 查询发布信息
         */
        List<PetPublish> publishes = petPublishRepository.findByPublisherIdOrderByCreateTimeDesc(openId, pageable);

        /**
         * BaseService查发布关联信息
         */
        List<PublishDTO> petExtends = publishBaseService.findPetExtends(publishes);

        return petExtends;
    }

    /**
     * 我的待处理列表
     *
     * @param openId
     * @param pageRequest
     * @return
     */
    @Override
    public List<PublishDTO> findNotFound(String openId, PageRequest pageRequest) {

        List<PetPublish> notFounds = petPublishRepository.findByPublisherIdAndFindStateOrderByCreateTimeDesc(openId, PetFindStateEnum.NOT_FOUND.getCode(), pageRequest);

        /**
         * BaseService查发布关联信息
         */
        return publishBaseService.findPetExtends(notFounds);
    }

    /**
     * 宠物详情(发布+互动)
     *
     * @param publishId
     * @param openId
     * @return
     */
    @Override
    public PublishDTO findPetDetail(Integer publishId, String openId) {


        PublishDTO publishDTO = new PublishDTO();

        /**
         * 宠物信息
         */
        PetPublish pet = petPublishRepository.findById(publishId);
        if (pet.getId() == null) {
            throw new PetHomeException(ResultEnum.RESULT_EMPTY);
        }
        pet.setViewCount(pet.getViewCount() + 1);
        petPublishRepository.save(pet);

        /**
         * 互动信息
         */
        List<List<PublicMsgDTO>> msgDTOS = messageService.petPublicTalks(pet);


        /**
         * 发布者信息
         */
        UserBasic myself = userBasicRepository.findByOpenId(pet.getPublisherId());


        /**
         * 互动条数
         */
        if (openId.equals(pet.getPublisherId())) {
            publishDTO.setPublicMsgCount(userTalkRepository.findPrivateMsgCount(publishId));
        }


        BeanUtils.copyProperties(pet, publishDTO);

        publishDTO.setPublicTalkDTO(msgDTOS);
        publishDTO.setPublisherName(myself.getNickName());
        publishDTO.setPublisherPhoto(myself.getHeadImgUrl());

        return publishDTO;

    }

    /**
     * 已找到
     *
     * @param id
     * @param openId
     * @return
     */
    @Override
    public PetPublish petFound(String openId,Integer id) {

        PetPublish pet = petPublishRepository.findByIdAndPublisherId(id, openId);
        if (pet == null) {
            throw new PetHomeException(ResultEnum.RESULT_EMPTY.getCode(), "未查询到该发布");
        }
        pet.setFindState(PetFindStateEnum.FOUND.getCode());
        PetPublish save = petPublishRepository.save(pet);
        return save;
    }


}
