package com.shumahe.pethome.Service.BaseService;


import com.shumahe.pethome.DTO.PublishDTO;
import com.shumahe.pethome.Domain.PetPublish;
import com.shumahe.pethome.VO.PublishVO;

import java.util.List;

public interface PublishBaseService {


    /**
     * 通过发布ID获取寻主寻宠发布的人员信息+评论信息
     *
     * @param publishIds
     * @return
     */
    List<PublishVO> findPublishDetail(List<PublishVO> publishVOS, List<Integer> publishIds, List<String> userIds);


    /**
     * 查询发布扩展信息(发布人详情 + 评论数详情)
     *
     * @param petPublishes
     * @return
     */
    List<PublishDTO> findPetExtends(List<PetPublish> petPublishes);


}
