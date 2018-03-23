package com.shumahe.pethome.Service.Impl;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.shumahe.pethome.DTO.PublicMsgDTO;
import com.shumahe.pethome.DTO.UserDTO;
import com.shumahe.pethome.Domain.*;
import com.shumahe.pethome.Enums.*;
import com.shumahe.pethome.Exception.PetHomeException;
import com.shumahe.pethome.Form.UserApproveForm;
import com.shumahe.pethome.Repository.*;
import com.shumahe.pethome.Service.BaseService.DynamicBaseService;
import com.shumahe.pethome.Service.UserService;
import org.apache.catalina.User;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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


    @Autowired
    private UserDynamicRepository userDynamicRepository;


    @Autowired
    private DynamicBaseService dynamicBaseService;


    /**
     * 我的中心
     *
     * @param openId
     */
    @Override
    public UserDTO findMyInfo(String openId) {


        UserBasic my = userBasicRepository.findByOpenId(openId);
        if (my == null) {
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
        int publishCount = publishTalkRepository.notReadTalksCount(openId, ReadStateEnum.NOT_READ.getCode());

        //转发条数
        //List<UserDynamic> shareMe = userDynamicRepository.findByUserIdArriveAndDynamicTypeOrderByCreateTimeDesc(openId, DynamicTypeEnum.SHARE.getCode());


        //关注条数
        //List<UserDynamic> likeMe = userDynamicRepository.findByUserIdArriveAndDynamicTypeOrderByCreateTimeDesc(openId, DynamicTypeEnum.LIKE.getCode());


        UserDTO userDTO = new UserDTO(my.getOpenId(), my.getNickName(), my.getHeadImgUrl());
        userDTO.setApproveState(my.getApproveState());
        userDTO.setApproveType(my.getApproveType());
        userDTO.setMobile(my.getMobile());
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
    @Transactional
    public UserApprove saveOrganization(UserApproveForm userApproveForm,UserBasic basic) {


        UserApprove userApprove = new UserApprove();
        BeanUtils.copyProperties(userApproveForm, userApprove);
        UserApprove save = userApproveRepository.save(userApprove);

        basic.setApproveType(ApproveTypeEnum.ASSOCIATION.getCode());
        basic.setApproveState(ApproveStateEnum.WAITING.getCode());
        userBasicRepository.save(basic);

        return save;
    }

    /**
     * 查询 我的互动
     *
     * @param openId
     * @param pageRequest
     * @return
     */
    @Override
    public List<Map<String, Object>> findMyPublicTalk(String openId, PageRequest pageRequest) {


        //更新未读-->已读
        // List<UserTalk> userTalks = userTalkRepository.findByPublisherIdAndReadState(openId, ReadStateEnum.NOT_READ.getCode());

        List<PublishTalk> userTalks = publishTalkRepository.findByReplierAcceptAndReadState(openId, ReadStateEnum.NOT_READ.getCode());
        if (!userTalks.isEmpty()) {
            userTalks.stream().forEach(e -> e.setReadState(ReadStateEnum.READ.getCode()));
            publishTalkRepository.save(userTalks);
        }

        // 我参与过的交流  (我发布的 || 我的回复别人的 || 别人回复我的)
        List<PublishTalk> talks = publishTalkRepository.findByReplierFromOrReplierAcceptOrPublisherId(openId, openId, openId);
        if (talks.isEmpty())
            throw new PetHomeException(ResultEnum.RESULT_EMPTY.getCode(), "留言互动消息为空");

        List<Integer> publishIds = talks.stream().map(e -> e.getPublishId()).distinct().collect(Collectors.toList());


        /**
         * step 1 我互动过的主题 所有互动消息
         */
        List<PublishTalk> talkMessages = publishTalkRepository.findManyPublishTalk(publishIds);

        /**
         * step 2 我互动过的主题
         */

        List<PetPublish> pets = petPublishRepository.findByIdIn(publishIds);

        /**
         * step 3 与我互动过的人员
         */
        List<String> userIds = talkMessages.stream().map(e -> e.getReplierFrom()).distinct().collect(Collectors.toList());
        List<UserBasic> users = userBasicRepository.findByOpenIdIn(userIds);


        Map<Integer, PetPublish> themesMap = pets.stream().collect(Collectors.toMap(PetPublish::getId, Function.identity()));
        Map<String, UserBasic> usersMap = users.stream().collect(Collectors.toMap(e -> e.getOpenId().trim(), Function.identity()));


        //消息分组
        List<Map<Integer, List<PublishTalk>>> collect = talkMessages.stream().collect(Collectors.groupingBy(PublishTalk::getPublishId, Collectors.groupingBy(PublishTalk::getTalkId))).values().stream().collect(Collectors.toList());

        List<List<PublishTalk>> talksGroup = new ArrayList<>();
        collect.forEach(e -> e.forEach((k, v) -> {
            talksGroup.add(v);
        }));


        //最终结果
        List<Map<String, Object>> finalRes = new ArrayList<>();

        talksGroup.stream().forEach(e -> {

            //一条互动
            Map<String, Object> onePrivate = new HashMap<>();

            //对话详情
            List<Map<String, String>> detailList = new ArrayList<>();

            for (int i = e.size() - 1; i >= 0; i--) {
                PublishTalk curTalk = e.get(i);
                if (!curTalk.getReplierFrom().equals(openId)) {

                    onePrivate.put("userIdFrom", curTalk.getReplierFrom());
                    onePrivate.put("talkTime", curTalk.getReplyDate().toString());
                    onePrivate.put("content", curTalk.getContent());
                    onePrivate.put("talkId", curTalk.getTalkId());

                    PetPublish curTheme = themesMap.get(curTalk.getPublishId());
                    onePrivate.put("publishType", curTheme.getPublishType());
                    onePrivate.put("publisherId", curTheme.getPublisherId());
                    onePrivate.put("publishId", curTheme.getId());
                    onePrivate.put("petName", curTheme.getPetName());
                    onePrivate.put("petImage", curTheme.getPetImage());
                    onePrivate.put("lostTime", curTheme.getLostTime().toString().split(" ")[0]);

                    UserBasic curUser = usersMap.get(curTalk.getReplierFrom().trim());
                    onePrivate.put("publisherName", curUser.getNickName());
                    onePrivate.put("userIdFromName", curUser.getNickName());
                    onePrivate.put("userIdFromImage", curUser.getHeadImgUrl());

                    e.remove(curTalk);
                    break;
                }
            }

            e.forEach(detail -> {

                UserBasic curUserFrom = usersMap.get(detail.getReplierFrom());
                UserBasic curUserAccept = usersMap.get(detail.getReplierAccept());

                Map<String, String> detailMap = new HashMap<>();
                detailMap.put("userIdFrom", detail.getReplierFrom());
                detailMap.put("userIdFromName", curUserFrom.getNickName());
                detailMap.put("userIdAccept", detail.getReplierAccept());
                detailMap.put("userIdAcceptName", curUserAccept.getNickName());
                detailMap.put("content", detail.getContent());

                detailList.add(detailMap);
            });

            onePrivate.put("detail", detailList);

            if (onePrivate.get("userIdFromName") != null) {
                finalRes.add(onePrivate);
            }
        });


        return finalRes;


        /**
         * step 4 自己的信息
         */
        //UserBasic myself = userBasicRepository.findByOpenId(openId);


        /**
         * step 4 扩充我的消息
         * 宠物信息     ： 昵称 头像 发布类型 丢失日期
         * 人员信息    ： 我的昵称 对方昵称   对方头像
         *
         */

       /* List<Map<String, String>> msgList = new ArrayList<>();

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
        */


    }


    /**
     * 关注列表(我的关注+关注我的)
     *
     * @param openId
     * @param type
     * @return
     */
    @Override
    public List<Map<String, String>> findMyDynamic(String openId, Integer type) {


        List<UserDynamic> userDynamics = new ArrayList<>();


        if (type == 1) {//我的关注

            userDynamics = userDynamicRepository.findByUserIdFromAndDynamicTypeOrderByCreateTimeDesc(openId, DynamicTypeEnum.LIKE.getCode());

        } else if (type == 2) {//关注我的

            userDynamics = userDynamicRepository.findByUserIdArriveAndDynamicTypeOrderByCreateTimeDesc(openId, DynamicTypeEnum.LIKE.getCode());

        } else if (type == 7) {//我的转发

            userDynamics = userDynamicRepository.findByUserIdFromAndDynamicTypeOrderByCreateTimeDesc(openId, DynamicTypeEnum.SHARE.getCode());

        } else if (type == 8) {//转发我的

            userDynamics = userDynamicRepository.findByUserIdArriveAndDynamicTypeOrderByCreateTimeDesc(openId, DynamicTypeEnum.SHARE.getCode());
        }


        //data empty
        if (userDynamics.size() == 0) {
            throw new PetHomeException(ResultEnum.RESULT_EMPTY);
        }

        /**
         * baseDynamicService 根据互动类型返回最终结果
         */
        List<Map<String, String>> likeList = dynamicBaseService.findLikeOrShareList(userDynamics, type);


        return likeList;
    }


    /**
     * 查询 我的私信
     *
     * @param openId
     * @param pageRequest
     * @return
     */
    @Override
    public List<Map<String, Object>> findMyPrivateTalk(String openId, PageRequest pageRequest) {

        //更新未读-->已读
        List<UserTalk> userTalks = userTalkRepository.findByUserIdAcceptAndReadState(openId, ReadStateEnum.NOT_READ.getCode());
        if (!userTalks.isEmpty()) {
            userTalks.stream().forEach(e -> e.setReadState(ReadStateEnum.READ.getCode()));
            userTalkRepository.save(userTalks);
        }


        /**
         * 私信消息
         */
        //我发布的
        List<UserTalk> myCreate = userTalkRepository.findMyCreate(openId);
        //我参与的
        List<UserTalk> myJoin = userTalkRepository.findMyJoin(openId, openId, openId, openId);

        if (myCreate.isEmpty() && myJoin.isEmpty()) {
            throw new PetHomeException(ResultEnum.RESULT_EMPTY.getCode(), "私信消息为空");
        }

        myCreate.addAll(myJoin);


        //与私信有关的人
        List<String> users = myCreate.stream().map(UserTalk::getUserIdFrom).distinct().collect(Collectors.toList());
        //全部私信主题
        List<Integer> themes = myCreate.stream().map(UserTalk::getPublishId).distinct().collect(Collectors.toList());

        //私信人信息 主题信息 自身信息
        List<UserBasic> userData = userBasicRepository.findByOpenIdIn(users);
        List<PetPublish> themeData = petPublishRepository.findByIdIn(themes);
        Map<String, UserBasic> usersMap = userData.stream().collect(Collectors.toMap(e -> e.getOpenId().trim(), Function.identity()));
        Map<Integer, PetPublish> themesMap = themeData.stream().collect(Collectors.toMap(PetPublish::getId, Function.identity()));


        //主题分组 再按talkId分组
        List<List<UserTalk>> talksByTheme = new ArrayList<>(myCreate
                .stream()
                .collect(Collectors.groupingBy(UserTalk::getPublishId))
                .values());
        //分组结果
        List<List<UserTalk>> talksByUser = new ArrayList<>();
        talksByTheme.forEach(e -> e.stream().collect(Collectors.groupingBy(UserTalk::getTalkId)).values().forEach(a -> talksByUser.add(a)));


        //最终结果
        List<Map<String, Object>> finalRes = new ArrayList<>();

        talksByUser.stream().forEach(e -> {

            //一条互动
            Map<String, Object> onePrivate = new HashMap<>();


            //对话详情
            List<Map<String, String>> detailList = new ArrayList<>();

            for (int i = e.size() - 1; i >= 0; i--) {
                UserTalk curTalk = e.get(i);
                if (!curTalk.getUserIdFrom().equals(openId)) {

                    onePrivate.put("userIdFrom", curTalk.getUserIdFrom());
                    onePrivate.put("talkTime", curTalk.getTalkTime().toString());
                    onePrivate.put("content", curTalk.getContent());
                    onePrivate.put("talkId", curTalk.getTalkId());

                    PetPublish curTheme = themesMap.get(curTalk.getPublishId());
                    onePrivate.put("publishType", curTheme.getPublishType());
                    onePrivate.put("publisherId", curTheme.getPublisherId());
                    onePrivate.put("publishId", curTheme.getId());
                    onePrivate.put("petName", curTheme.getPetName());
                    onePrivate.put("petImage", curTheme.getPetImage());
                    onePrivate.put("lostTime", curTheme.getLostTime().toString().split(" ")[0]);

                    UserBasic curUser = usersMap.get(curTalk.getUserIdFrom().trim());
                    onePrivate.put("publisherName", curUser.getNickName());
                    onePrivate.put("userIdFromName", curUser.getNickName());
                    onePrivate.put("userIdFromImage", curUser.getHeadImgUrl());

                    e.remove(curTalk);
                    break;
                }
            }

            e.forEach(detail -> {

                UserBasic curUserFrom = usersMap.get(detail.getUserIdFrom());
                UserBasic curUserAccept = usersMap.get(detail.getUserIdAccept());

                Map<String, String> detailMap = new HashMap<>();
                detailMap.put("userIdFrom", detail.getUserIdFrom());
                detailMap.put("userIdFromName", curUserFrom.getNickName());
                detailMap.put("userIdAccept", detail.getUserIdAccept());
                detailMap.put("userIdAcceptName", curUserAccept.getNickName());
                detailMap.put("content", detail.getContent());

                detailList.add(detailMap);
            });

            onePrivate.put("detail", detailList);

            finalRes.add(onePrivate);

        });

        return finalRes;
        /**
         * {
         *     userIdFrom : 'Alieen',
         *     talkTime : '2018-10-12 12:31:11'
         *     content : '加我微信123456789'
         *     publisherType : '1'
         *     publisherName : '猫大王的幸福生活'
         *     petName:'咪咪',
         *     petImage:'/upload/picture/publish/xx.jpg'
         *     lostTime:'2018-10-12 12:31:11'
         *     detail:[{
         *         userIdFrom:
         *         userIdFromName:
         *         userIdAccept:
         *         userIdAcceptName:
         *         content:
         *     },{
         *         userIdFrom:
         *         userIdFromName:
         *         userIdAccept:
         *         userIdAcceptName:
         *         content:
         *     }]
         * }
         *
         *
         */

        /*List<List<Map<String, String>>> msgResult = new ArrayList<>();

        talksByUser.forEach(msgs -> {

            List<Map<String, String>> _tempList = new ArrayList<>();

            msgs.forEach(msg -> {

                Map<String, String> msgMap = new HashMap<>();
                userData.forEach(user -> {
                    if (msg.getUserIdFrom().equals(user.getOpenId())) {
                        msgMap.put("userFrom", user.getNickName());
                        msgMap.put("userIdFrom", user.getOpenId());
                        msgMap.put("userImgFrom", user.getHeadImgUrl());
                    }
                });


                themeData.forEach(publish -> {
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

                _tempList.add(msgMap);
            });

            msgResult.add(_tempList);
        });
        System.out.println(123);
*/


         /*if (i == (e.size()-1)) {

                    UserTalk userTalk = e.get(i);
                    onePrivate.put("userIdFrom", userTalk.getUserIdFrom());
                    onePrivate.put("talkTime", userTalk.getTalkTime().toString());
                    onePrivate.put("content", userTalk.getContent());
                    onePrivate.put("talkId", userTalk.getTalkId());

                    themeData.forEach(theme -> {
                        if (theme.getId().equals(userTalk.getPublishId())) {

                            onePrivate.put("publisherType", theme.getPublishType().toString());
                            onePrivate.put("publisherId", theme.getPublisherId());
                            onePrivate.put("publishId", theme.getId());

                            onePrivate.put("petName", theme.getPetName());
                            onePrivate.put("petImage", theme.getPetImage());
                            onePrivate.put("lostTime", theme.getLostTime().toString().split(" ")[0]);
                        }
                    });
                    userData.forEach(user -> {

                        //发布人信息
                        if (user.getOpenId().trim().equals(userTalk.getPublisherId().trim())) {
                            onePrivate.put("publisherName", user.getNickName());
                        }
                        //来信人信息
                        if(user.getOpenId().trim().equals(userTalk.getUserIdFrom().trim())){
                            onePrivate.put("userIdFromName", user.getNickName());
                            onePrivate.put("userIdFromImage", user.getHeadImgUrl());
                        }

                        userInfoCache.put(user.getOpenId().trim(), user.getNickName());//缓存用户信息
                    });

                } else {

                    UserTalk userTalk = e.get(i);
                    Map<String, String> detail = new HashMap<>();
                    detail.put("userIdFrom", userTalk.getUserIdFrom());
                    detail.put("userIdFromName", userInfoCache.get(userTalk.getUserIdFrom().trim()));
                    detail.put("userIdAccept", userTalk.getUserIdAccept());
                    detail.put("userIdAcceptName", userInfoCache.get(userTalk.getUserIdAccept().trim()));
                    detail.put("content", userTalk.getContent());

                    detailList.add(detail);
                }
                onePrivate.put("detail", detailList);
*/


    }

}
