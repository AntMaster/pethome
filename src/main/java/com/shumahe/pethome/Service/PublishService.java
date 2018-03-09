package com.shumahe.pethome.Service;


import com.shumahe.pethome.DTO.PublishDTO;
import com.shumahe.pethome.Domain.PetPublish;
import com.shumahe.pethome.Form.PetSearchForm;
import com.shumahe.pethome.Form.PublishMasterForm;
import com.shumahe.pethome.Form.PublishPetForm;
import com.shumahe.pethome.VO.PublishVO;
import com.shumahe.pethome.VO.ResultVO;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public interface PublishService {

    /**
     * 主页列表(动态+寻主+寻宠)
     * @param publishType
     * @param pageable
     * @return
     */
    ResultVO<List<PublishVO>> findAll(Integer publishType ,PageRequest pageable);
    /**
     * 寻宠发布
     * @param petForm
     * @return
     */
    PetPublish createPet(PublishPetForm petForm);

    /**
     * 寻主发布
     * @param masterForm
     * @return
     */
    PetPublish createMaster(PublishMasterForm masterForm);

    /**
     * 我的发布列表
     * @param openId
     * @param pageable
     * @return
     */
    List<PublishDTO> findMyPublishList(String openId, PageRequest pageable);

    /**
     * 我的待处理列表
     * @param openId
     * @param pageRequest
     * @return
     */
    List<PublishDTO> findNotFound(String openId ,PageRequest pageRequest);




/*
    *//**
     * 已完成发布列表(我的)
     * PS:按时间查个人已完成
     *//*
    List<PublishVO> findMyFinished();

    *//**
     * 寻主发布列表(主页)PS:简单描述
     *//*
    List<PublishVO> findPetAll();

    *//**
     * 寻宠发布列表(主页)PS:简单描述
     *//*
    List<PublishVO> findMasterAll();

    *//**
     * 发布详情(详情) PS:详细描述,同时浏览次数+1
     *//*
    PublishVO detail();

    *//**
     *基于条件(含模糊条件)搜索发布列表  (搜索)
     *//*
    PublishVO saerch();*/



/***************************************************************/


    /**
     *个人中心 PS:个人信息+我的任务+我的消息
     */


}
