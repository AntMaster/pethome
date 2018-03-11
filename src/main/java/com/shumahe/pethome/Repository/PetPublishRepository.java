package com.shumahe.pethome.Repository;

import com.shumahe.pethome.Domain.PetPublish;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;


import java.util.List;

public interface PetPublishRepository extends JpaRepository<PetPublish, Integer>, JpaSpecificationExecutor {


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
     * 查询一个发布
     *
     * @param id
     * @return
     */
    PetPublish findById(Integer id);


    /**
     * 查询一个发布
     *
     * @param id
     * @return
     */
    PetPublish findByIdAndPublisherId(Integer id, String publisherId);


    /**
     * 根据用户openid和处理状态查询发布(不带分页)
     *
     * @param openId
     * @param findState
     * @return
     */
    @Query(value = "select count(id) from petpublish where PublisherId = ?1 and FindState = ?2", nativeQuery = true)
    int notReadPetCount(String openId, Integer findState);


}


