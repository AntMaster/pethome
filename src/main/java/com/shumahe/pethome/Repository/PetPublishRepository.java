package com.shumahe.pethome.Repository;

import com.shumahe.pethome.Domain.PetPublish;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;


import java.util.List;

public interface PetPublishRepository extends JpaRepository<PetPublish, Integer>,JpaSpecificationExecutor {


    /**
     * 分页查询所有发布
     *
     * @param pageable
     * @return
     */
    Page<PetPublish> findByOrderByCreateTimeDesc(Pageable pageable);


    /**
     * 分页查询特定类型发布
     *
     * @param publishType
     * @param pageable
     * @return
     */
    Page<PetPublish> findByPublishTypeOrderByCreateTimeDesc(Integer publishType, Pageable pageable);


    /**
     * 根据发布ID查询发布
     *
     * @param publisherIds
     * @return
     */
    List<PetPublish> findByIdIn(List<Integer> publisherIds);


    /**
     * 根据openId查询发布
     *
     * @param openId
     * @return
     */
    List<PetPublish> findByPublisherIdOrderByCreateTimeDesc(String openId, Pageable pageable);


    /**
     * 根据openId和发布状态查询发布
     *
     * @param openId
     * @param petFindState
     * @param pageRequest
     * @return
     */
    List<PetPublish> findByPublisherIdAndFindStateOrderByCreateTimeDesc(String openId, int petFindState, PageRequest pageRequest);



    /**
     * 查询模糊查询 （宠物名称 like 宠物描述 like 丢失地点 like 发布人  in 发布类型  in 宠物性别 in 宠物分类 in  宠物品种 in  丢失状态 in）
     * @param petName
     * @param petDescription
     * @param lostLocation
     * @param publisher
     * @param publishType
     * @param petSex
     * @param petClassify
     * @param petVariety
     * @param lostState
     * @param Pageable
     * @return List
     */
    List<PetPublish> findByPetNameContainsOrPetDescriptionContainsOrLostLocationContainsOrPublisherIdInOrPublishTypeInOrPetSexInOrClassifyIdInOrVarietyIdInOrFindStateInOrderByCreateTimeDesc(String petName,String petDescription, String lostLocation ,List<String> publisher,List<Integer> publishType,List<Integer> petSex,List<Integer> petClassify,List<Integer> petVariety,List<Integer> lostState ,Pageable Pageable);


    /**
     *
     */











    /**
     * 根据用户openid和处理状态查询发布(不带分页)
     * @param openId
     * @param findState
     * @return
     */
    List<PetPublish> findByPublisherIdAndFindState(String openId,Integer findState);


}


/*
 */
/**
 * 根据发布类型查询发布信息（分类）
 *
 * @param publishState
 * @param pageable
 * @return 根据发布类型查询发布信息
 * @param publishState
 * @return 通过创建时间查询所有发布
 * @return 根据发布类型查询发布信息
 * @param publishState
 * @return 通过创建时间查询所有发布
 * @return
 *//*

    Page<PetPublish> findByPublishStateOrderById(int publishState, Pageable pageable);


    */
/**
 * 根据发布类型查询发布信息
 *
 * @param publishState
 * @return
 *//*

    List<PetPublish> findByPublishStateOrderById(int publishState);


    */
/**
 * 通过创建时间查询所有发布
 *
 * @return
 *//*



}
*/
