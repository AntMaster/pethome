package com.shumahe.pethome.Service.Impl;

import com.shumahe.pethome.DTO.PrivateMsgDTO;
import com.shumahe.pethome.DTO.PublicMsgDTO;
import com.shumahe.pethome.DTO.PublishDTO;
import com.shumahe.pethome.Domain.PetPublish;
import com.shumahe.pethome.Domain.PublishTalk;
import com.shumahe.pethome.Domain.UserBasic;
import com.shumahe.pethome.Domain.UserTalk;
import com.shumahe.pethome.Enums.ResultEnum;
import com.shumahe.pethome.Exception.PetHomeException;
import com.shumahe.pethome.Form.ReplyPrivateForm;
import com.shumahe.pethome.Form.ReplyPublishForm;
import com.shumahe.pethome.Repository.PetPublishRepository;
import com.shumahe.pethome.Repository.PublishTalkRepository;
import com.shumahe.pethome.Repository.UserBasicRepository;
import com.shumahe.pethome.Repository.UserTalkRepository;
import com.shumahe.pethome.Service.MessageService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;


import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.*;
import java.util.stream.Collectors;


@Service
public class MessageServiceImpl implements MessageService {



    @Autowired
    UserTalkRepository userTalkRepository;

    @Autowired
    UserBasicRepository userBasicRepository;

    @Autowired
    PetPublishRepository petPublishRepository;


    @Autowired
    PublishTalkRepository publishTalkRepository;

    /**
     * 查询 我的私信
     *
     * @param openId
     * @param pageRequest
     * @return
     */
    @Override
    public List<List<LinkedHashMap<String, String>>> findMyPrivateTalk(String openId, PageRequest pageRequest) {

        /**
         * 私信列表
         */
        List<UserTalk> talkMessages = userTalkRepository.findPrivateMyTalk(openId, openId);


        if (talkMessages.size() == 0) {
            throw new PetHomeException(ResultEnum.RESULT_EMPTY.getCode(), "私信消息为空");
        }

        /**
         * 我的所有私信过的人（key = 用户OpenID ，value =用户发布ID）
         */
        LinkedHashMap<String, Integer> userMap = new LinkedHashMap<>();
        talkMessages.stream().forEach(user -> {

            if (!user.getUserIdFrom().equals(openId)) {

                userMap.put(user.getUserIdFrom(), user.getPublishId());

            }

        });

        List<String> userOpenIds = new ArrayList<>();
        List<Integer> publishIds = new ArrayList<>();
        userMap.forEach((userId, publishId) -> {
            userOpenIds.add(userId);
            publishIds.add(publishId);
        });

        /**
         * 将字段提取出来 放入另一个集合 用于查询扩展信息
         */
        List<UserBasic> userBasics = userBasicRepository.findByOpenIdIn(userOpenIds);

        List<PetPublish> _tempPublishes = petPublishRepository.findByIdIn(publishIds);
        /**
         * 调整PetPublish顺序 按userMap顺序
         */
        List<PetPublish> publishes = new ArrayList<>();
        userMap.forEach((k, v) -> {
            _tempPublishes.forEach(publish -> {
                if (v == publish.getId()) {
                    publishes.add(publish);
                }
            });
        });


        UserBasic myself = userBasicRepository.findByOpenId(openId);


        /**
         * 组装私信信息
         * 宠物信息     ： 昵称 头像 发布类型 丢失日期
         * 人员信息    ： 我的昵称 对方昵称   对方头像
         *
         */
        /**
         *  私信分组
         */
        List<List<UserTalk>> msgGroup = new ArrayList<>();

        publishes.forEach(publish -> {

            List<UserTalk> _tempTalk = new ArrayList<>();

            //对每个对话进行分组
            talkMessages.forEach(msg -> {

                if (publish.getId() == msg.getPublishId()) {

                    _tempTalk.add(msg);
                }

            });
            msgGroup.add(_tempTalk);

        });


        /**
         * 对分组后的私信进行数据填充
         */
        List<List<LinkedHashMap<String, String>>> msgResult = new ArrayList<>();

        msgGroup.forEach(msgs -> {

            List<LinkedHashMap<String, String>> _tempMsgGroup = new ArrayList<>();

            msgs.forEach(msg -> {

                LinkedHashMap<String, String> msgMap = new LinkedHashMap<>();
                userBasics.forEach(user -> {

                    if (msg.getUserIdFrom().equals(user.getOpenId())) {
                        msgMap.put("userFrom", user.getNickName());
                        msgMap.put("userIdFrom", user.getOpenId());
                        msgMap.put("userImgFrom", user.getHeadImgUrl());
                    }

                });


                publishes.forEach(publish -> {

                    if (msg.getPublishId() == publish.getId()) {

                        msgMap.put("petImg", publish.getPetImage());
                        msgMap.put("petName", publish.getPetName());
                        msgMap.put("petLostTime", publish.getLostTime().toString());
                        msgMap.put("publishType", String.valueOf(publish.getPublishType()));
                    }
                });

                msgMap.put("myName", myself.getNickName());
                msgMap.put("msgDetail", msg.getContent());
                msgMap.put("talkTime", msg.getTalkTime().toString());

                _tempMsgGroup.add(msgMap);
            });

            msgResult.add(_tempMsgGroup);
        });


        return msgResult;
    }

    /**
     * 回复私信
     *
     * @param replyPrivateFrom
     */

    @Override
    @Transactional
    public UserTalk replyPrivate(ReplyPrivateForm replyPrivateFrom) {

        UserTalk userTalk = new UserTalk();
        BeanUtils.copyProperties(replyPrivateFrom, userTalk);

        UserTalk save = userTalkRepository.save(userTalk);

        /**
         * 当前私信来往记录
         */
        List<UserTalk> currentTalkList = userTalkRepository.findByPublishIdIn(save.getPublishId());
        currentTalkList.forEach(talk -> talk.setLastModify(save.getTalkTime()));

        userTalkRepository.save(currentTalkList);

        return save;

    }


    /**
     * 查询 我的留言互动
     *
     * @param openId
     * @param pageRequest
     * @return
     */
    @Override
    public List<List<Map<String, String>>> findMyPublicTalk(String openId, PageRequest pageRequest) {


        // 与我有关的交流  (我发布的 || 我的回复别人的 || 别人回复我的)
        List<PublishTalk> talks = publishTalkRepository.findByReplierFromOrReplierAcceptOrPublisherId(openId, openId, openId);
        if (talks.isEmpty()) {
            throw new PetHomeException(ResultEnum.RESULT_EMPTY.getCode(), "留言互动消息为空");
        }
        // 与我有关的交流的主题
        List<Integer> publishIds = talks.stream().map(e -> e.getPublishId()).distinct().collect(Collectors.toList());

        /**
         * step 1 我互动过的发布
         */
        List<PetPublish> pets = petPublishRepository.findByIdIn(publishIds);


        /**
         * step 2 我互动过的发布 所有互动消息
         */
        List<PublishTalk> talkMessages = publishTalkRepository.findManyPublishTalk(publishIds);
        if (talkMessages.size() == 0) {
            throw new PetHomeException(ResultEnum.RESULT_EMPTY.getCode(), "留言互动消息为空");
        }


        /**
         * step 3 与我互动过的人员
         */
        List<String> userIds = talks.stream()
                .filter(e -> !e.getReplierFrom().equals(openId))
                .map(e -> e.getReplierFrom())
                .collect(Collectors.toList())
                .stream()
                .distinct()
                .collect(Collectors.toList());
        List<UserBasic> users = userBasicRepository.findByOpenIdIn(userIds);

        /**
         * step 4 自己的信息
         */
        UserBasic myself = userBasicRepository.findByOpenId(openId);


        /**
         * step 4 扩充我的消息
         * 宠物信息     ： 昵称 头像 发布类型 丢失日期
         * 人员信息    ： 我的昵称 对方昵称   对方头像
         */

        List<Map<String, String>> msgList = new ArrayList<>();

        talkMessages.stream().forEach(msg -> {

            Map<String, String> msgMap = new HashMap<>();
            pets.forEach(pet -> {

                if (msg.getPublishId().equals(pet.getId())) {
                    msgMap.put("petImg", pet.getPetImage());
                    msgMap.put("petName", pet.getPetName());
                    msgMap.put("petLostTime", pet.getLostTime().toString());
                    msgMap.put("publishType", String.valueOf(pet.getPublishType()));
                }
            });

            users.forEach(user -> {
                if (msg.getReplierFrom().equals(user.getOpenId())) {
                    msgMap.put("userFrom", user.getNickName());
                    msgMap.put("userIdFrom", user.getOpenId());
                    msgMap.put("userImgFrom", user.getHeadImgUrl());
                }
            });

            msgMap.put("myName", myself.getNickName());
            msgMap.put("msgDetail", msg.getContent());
            msgMap.put("talkTime", msg.getReplyDate().toString());
            msgMap.put("publishId", String.valueOf(msg.getPublishId()));
            msgList.add(msgMap);

        });


        List<List<Map<String, String>>> msgListGroup = new ArrayList<>();

        //e = 2 1 3
        msgList.stream().map(e -> e.get("publishId")).distinct().forEach(e -> {

            List<Map<String, String>> _tempList = new ArrayList<>();
            msgList.forEach(msg -> {
                if (msg.get("publishId").equals(e)) {
                    _tempList.add(msg);
                }
            });
            msgListGroup.add(_tempList);

        });

        return msgListGroup;
    }


    /**
     * 回复互动
     *
     * @param replyPublishForm
     */
    @Override
    @Transactional
    public PublishTalk replyPublic(ReplyPublishForm replyPublishForm) {

        PublishTalk publishTalk = new PublishTalk();
        BeanUtils.copyProperties(replyPublishForm, publishTalk);

        PublishTalk save = publishTalkRepository.save(publishTalk);

        /**
         * 当前回复来往记录
         */
        List<PublishTalk> currentTalkList = publishTalkRepository.findByPublishIdIn(save.getPublishId());
        currentTalkList.forEach(talk -> talk.setLastModify(save.getReplyDate()));

        publishTalkRepository.save(currentTalkList);

        return save;
    }

    /**
     * 查询发布的互动详情
     * @param pet
     * @return
     */
    @Override
    public List<List<PublicMsgDTO>> petPrivateTalks(PetPublish pet,String openId) {

            /**
             * step 1  某个发布全部互动消息
             */
            List<UserTalk> talks ;
            if (pet.getPublisherId().equals(openId)){//发布人是本人
                talks = userTalkRepository.findByPublishIdAndPublisherIdOrderByLastModify(pet.getId(),pet.getPublisherId());
            }else {

                talks = userTalkRepository.findByPublishIdAndPublisherIdAndUserIdFromOrderByLastModify(pet.getId(),pet.getPublisherId(),openId);

            }

            if (talks.isEmpty()) {
                throw new PetHomeException(ResultEnum.RESULT_EMPTY.getCode(), "留言互动消息为空");
            }


            /**
             * step 2 与我互动过的用户
             */
            List<String> tempUserIds = new ArrayList<>();
            talks.stream().forEach(e -> {
                tempUserIds.add(e.getUserIdFrom());
                if (!StringUtils.isEmpty(e.getUserIdAccept())) {
                    tempUserIds.add(e.getUserIdAccept());
                }
            });

            List<String> userIds = tempUserIds.stream().distinct().collect(Collectors.toList());

            List<UserBasic> users = userBasicRepository.findByOpenIdIn(userIds);


            /**
             * step 3 互动信息VO
             */
            List<PrivateMsgDTO> publicMsgDTOS = new ArrayList<>();

            talks.stream().forEach(talk -> {

                PrivateMsgDTO msgDTO = new PrivateMsgDTO();
                BeanUtils.copyProperties(talk, msgDTO);


                users.forEach(user -> {

                    if (talk.getUserIdFrom().trim().equals(user.getOpenId().trim())) {

                        msgDTO.setUserIdFromName(user.getNickName());
                        msgDTO.setUserIdFromPhoto(user.getHeadImgUrl());
                    }

                    if (!StringUtils.isEmpty(talk.getUserIdAccept()) && talk.getUserIdAccept().trim().equals(user.getOpenId().trim())) {
                        msgDTO.setUserIdAcceptName(user.getNickName());
                        msgDTO.setUserIdAuser.getHeadImgUrl());1233
                    }
                });
                publicMsgDTOS.add(msgDTO);
            });


            List<List<PublicMsgDTO>> petTalks = new ArrayList<>();
            /**
             * step 4 VO按评论分组（one comment ---> some talk）
             */

            publicMsgDTOS.stream().map(e -> e.getTalkId()).distinct().forEach(e -> {

                List<PublicMsgDTO> sameTalkId = new ArrayList<>();

                publicMsgDTOS.forEach(talk -> {
                    if (e.equals(talk.getTalkId())) {

                        sameTalkId.add(talk);
                    }
                });
                petTalks.add(sameTalkId);
            });

            return petTalks;

    }


    /**
     * 查询发布的评论详情
     *
     * @param pet
     * @return
     */
    @Override
    public List<List<PublicMsgDTO>> findPetPublicTalks(PetPublish pet) {


        /**
         * step 1  某个发布全部互动消息
         */
        List<PublishTalk> talks = publishTalkRepository.findOnePublicTalk(pet.getId());
        if (talks.isEmpty()) {
            throw new PetHomeException(ResultEnum.RESULT_EMPTY.getCode(), "留言互动消息为空");
        }

        /**
         * step 2 与我互动过的用户
         */
        List<String> tempUserIds = new ArrayList<>();
        talks.stream().forEach(e -> {
            tempUserIds.add(e.getReplierFrom());
            if (!StringUtils.isEmpty(e.getReplierAccept())) {
                tempUserIds.add(e.getReplierAccept());
            }
        });
        List<String> userIds = tempUserIds.stream().distinct().collect(Collectors.toList());

        List<UserBasic> users = userBasicRepository.findByOpenIdIn(userIds);


        /**
         * step 3 互动信息VO
         */
        List<PublicMsgDTO> publicMsgDTOS = new ArrayList<>();

        talks.stream().forEach(talk -> {

            PublicMsgDTO msgDTO = new PublicMsgDTO();
            BeanUtils.copyProperties(talk, msgDTO);

            users.forEach(user -> {

                if (talk.getReplierFrom().trim().equals(user.getOpenId().trim())) {

                    msgDTO.setReplierFromName(user.getNickName());
                    msgDTO.setReplierFromPhoto(user.getHeadImgUrl());
                }

                if (!StringUtils.isEmpty(talk.getReplierAccept()) && talk.getReplierAccept().trim().equals(user.getOpenId().trim())) {
                    msgDTO.setReplierAcceptName(user.getNickName());
                    msgDTO.setReplierAcceptPhoto(user.getHeadImgUrl());
                }
            });
            publicMsgDTOS.add(msgDTO);
        });


        List<List<PublicMsgDTO>> petTalks = new ArrayList<>();
        /**
         * step 4 VO按评论分组（one comment ---> some talk）
         */

        publicMsgDTOS.stream().map(e -> e.getTalkId()).distinct().forEach(e -> {

            List<PublicMsgDTO> sameTalkId = new ArrayList<>();

            publicMsgDTOS.forEach(talk -> {
                if (e.equals(talk.getTalkId())) {

                    sameTalkId.add(talk);
                }
            });
            petTalks.add(sameTalkId);
        });

        return petTalks;
    }

}

