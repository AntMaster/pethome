package com.shumahe.pethome.Service;

import com.shumahe.pethome.DTO.PrivateMsgDTO;
import com.shumahe.pethome.DTO.PublicMsgDTO;
import com.shumahe.pethome.DTO.PublishDTO;
import com.shumahe.pethome.Domain.PetPublish;
import com.shumahe.pethome.Domain.PublishTalk;
import com.shumahe.pethome.Domain.UserTalk;
import com.shumahe.pethome.Form.ReplyPrivateForm;
import com.shumahe.pethome.Form.ReplyPublishForm;
import org.springframework.data.domain.PageRequest;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public interface MessageService {


    /**
     * 查询 我的私信
     * @param openId
     * @param pageRequest
     * @return
     */
    List<List<LinkedHashMap<String, String>>> findMyPrivateTalk(String openId , PageRequest pageRequest);

    /**
     * 回复私信
     * @param replyPrivateForm
     */
    UserTalk replyPrivate(ReplyPrivateForm replyPrivateForm);


    /**
     * 查询 我的互动
     * @param openId
     * @param pageRequest
     * @return
     */
    List<List<Map<String, String>>> findMyPublicTalk(String openId , PageRequest pageRequest);


    /**
     * 回复互动
     * @param replyPublishForm
     */
    PublishTalk replyPublic(ReplyPublishForm replyPublishForm);


    /**
     * 查询发布的互动详情
     * @param pet
     * @return
     */
    List<List<PrivateMsgDTO>> petPrivateTalks(PetPublish pet, String openId);


    /**
     * 查询发布的互动详情
     * @param pet
     * @return
     */
    List<List<PublicMsgDTO>> findPetPublicTalks(PetPublish pet);




    /**
     * 评论(发布详情)
     */

    /**
     * 回复(发布详情)
     */

    /**
     * 互动(评论和回复)列表 (我的)
     */

    /**
     * 私信(个人)入口在哪?
     */

    /**
     * 私信列表(个人)
     */



}
