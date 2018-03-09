package com.shumahe.pethome.Service.Impl;


import com.shumahe.pethome.DTO.PublishDTO;
import com.shumahe.pethome.Domain.PetPublish;
import com.shumahe.pethome.Domain.PetVariety;
import com.shumahe.pethome.Domain.UserBasic;
import com.shumahe.pethome.Enums.*;
import com.shumahe.pethome.Exception.PetHomeException;
import com.shumahe.pethome.Form.PetSearchForm;
import com.shumahe.pethome.Form.PublishMasterForm;
import com.shumahe.pethome.Form.PublishPetForm;
import com.shumahe.pethome.Repository.PetPublishRepository;
import com.shumahe.pethome.Repository.PetVarietyRepository;
import com.shumahe.pethome.Repository.PublishTalkRepository;
import com.shumahe.pethome.Repository.UserBasicRepository;
import com.shumahe.pethome.Service.BaseService.PublishBaseService;
import com.shumahe.pethome.Service.PublishService;
import com.shumahe.pethome.Util.ResultVOUtil;
import com.shumahe.pethome.VO.PublishVO;
import com.shumahe.pethome.VO.ResultVO;
import javafx.util.converter.DateStringConverter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.lang.reflect.Array;
import java.util.*;
import java.util.stream.Collectors;

import static org.springframework.beans.BeanUtils.*;

@Service
@Slf4j
public class PublishServiceImpl implements PublishService {


    @Autowired
    private PetPublishRepository petPublishRepository;

    @Autowired
    private UserBasicRepository userBasicRepository;

    @Autowired
    PublishBaseService publishBaseService;

    @Autowired
    PetVarietyRepository petVarietyRepository;

    /**
     * 主页列表(动态+寻主+寻宠)
     *
     * @param publishType
     * @param pageable
     * @return
     */
    @Override
    public ResultVO<List<PublishVO>> findAll(Integer publishType, PageRequest pageable) {

        List<PublishVO> publishVOS = new ArrayList<>();

        List<Integer> idList = new ArrayList<>();//发布IDs
        List<String> userIdList = new ArrayList<>();//发布人IDs

        Page<PetPublish> result;
        if (publishType == 0) {
            /**动态*/
            result = petPublishRepository.findByOrderByCreateTimeDesc(pageable);

        } else {
            /**寻主/寻宠*/
            result = petPublishRepository.findByPublishTypeOrderByCreateTimeDesc(publishType, pageable);
        }

        if (result.getContent().size() == 0) {

            throw new PetHomeException(ResultEnum.RESULT_EMPTY);
        }

        List<PetPublish> publishList = result.getContent();

        for (PetPublish publish : publishList) {
            //发布Id
            idList.add(publish.getId());
            //发布人Id
            userIdList.add(publish.getPublisherId());

            PublishVO publishVO = new PublishVO();
            BeanUtils.copyProperties(publish, publishVO);
            publishVOS.add(publishVO);
        }

        /**
         * BaseService查关联信息
         */
        List<PublishVO> list = publishBaseService.findPublishDetail(publishVOS, idList, userIdList);

        return ResultVOUtil.success(list);

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





    /*@Override
    public List<PublishVO> findMyFinished() {
        return null;
    }


    @Override
    public List<PublishVO> findPetAll() {
        return null;
    }

    @Override

    public List<PublishVO> findMasterAll() {
        return null;
    }

    @Override
    public PublishVO detail() {
        return null;
    }

    @Override
    public PublishVO saerch() {
        return null;
    }

    */

}
