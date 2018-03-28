package com.shumahe.pethome.Service.BaseImpl;

import com.shumahe.pethome.Convert.Publish2PublishDTOConvert;
import com.shumahe.pethome.DTO.PublishDTO;
import com.shumahe.pethome.Domain.*;
import com.shumahe.pethome.Enums.DynamicTypeEnum;
import com.shumahe.pethome.Repository.*;
import com.shumahe.pethome.Service.BaseService.PublishBaseService;
import com.shumahe.pethome.Util.DateUtil;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@Service
public class PublishBaseServiceImpl implements PublishBaseService {

    @Autowired
    PublishTalkRepository publishTalkRepository;

    @Autowired
    UserBasicRepository userBasicRepository;

    @Autowired
    PublishViewRepository publishViewRepository;

    @Autowired
    PetVarietyRepository petVarietyRepository;

    @Autowired
    UserDynamicRepository userDynamicRepository;

    /**
     * 查询发布扩展信息(发布人详情 + 评论数详情)
     *
     * @param publishes
     * @return
     */
    @Override
    public List<PublishDTO> findPetExtends(List<PetPublish> publishes) {


        List<Integer> publishIds = publishes.stream().map(e -> e.getId()).collect(Collectors.toList());
        List<String> userIds = publishes.stream().map(e -> e.getPublisherId()).distinct().collect(Collectors.toList());

        /**
         * step 1  评论count
         */
        List<Object[]> commentCount = publishTalkRepository.findPublishCommentCount(publishIds);


        //Map<publishId,msgCount>
        List<Map<Integer, Integer>> msgCount = new ArrayList<>();

        if (!commentCount.isEmpty()) {
            commentCount.stream().forEach((Object[] count) -> {
                Map<Integer, Integer> _tempMsg = new HashMap<>();
                _tempMsg.put((Integer) count[0], (Integer) count[1]);
                msgCount.add(_tempMsg);
            });
        }


        /**
         * step 2  浏览count
         */
        List<Integer[]> viewObject = publishViewRepository.findViewCount(publishIds);
        List<Map<Integer, Integer>> viewCount = new ArrayList<>();

        if (!viewObject.isEmpty()) {
            viewObject.stream().forEach((Object[] count) -> {
                Map<Integer, Integer> _tempMsg = new HashMap<>();
                _tempMsg.put((Integer) count[0], (Integer) count[1]);
                viewCount.add(_tempMsg);
            });
        }

        /**
         * 私信
         */
        




        List<UserDynamic> dynamics = userDynamicRepository.findByPublishIdIn(publishIds);
        //转发和关注分组
        Map<Integer, List<UserDynamic>> collect = dynamics.stream().collect(Collectors.groupingBy(UserDynamic::getDynamicType));

        /**
         * step3 转发count
         */
        List<UserDynamic> shareDynamics = collect.get(DynamicTypeEnum.SHARE.getCode());
        Map<Integer, Long> publishShareCount = new HashMap<>();
        if (shareDynamics != null) {
            publishShareCount = shareDynamics.stream().collect(Collectors.groupingBy(UserDynamic::getPublishId, Collectors.counting()));
        }

        /**
         * step4 关注count
         */
        List<UserDynamic> likeDynamics = collect.get(DynamicTypeEnum.LIKE.getCode());
        Map<Integer, Long> publishLikeCount = new HashMap<>();
        if (likeDynamics != null) {
            publishLikeCount = likeDynamics.stream().collect(Collectors.groupingBy(UserDynamic::getPublishId, Collectors.counting()));
        }


        /**
         *  step 5 发布人ID查询发布人基本信息
         */
        List<UserBasic> userBasics = userBasicRepository.findByOpenIdIn(userIds);


        /**
         *  step 6 宠物品种
         */
        Map<Integer, Map<Integer, PetVariety>> petVariety = this.findPetVariety();


        /**
         *  step 7  合成发布信息 =  发布信息 + 评论信息 +  发布人基本信息
         */
        List<PublishDTO> publishDTOS = Publish2PublishDTOConvert.convert(publishes);

        publishDTOS.stream().forEach(publishDTO -> {

            //评论数
            if (!msgCount.isEmpty()) {
                msgCount.stream().forEach(msg -> msg.forEach((k, v) -> {
                    if (publishDTO.getId() == k) {
                        publishDTO.setPublicMsgCount(v);
                        return;
                    }
                }));
            }

            //浏览数
            if (!viewCount.isEmpty()) {
                viewCount.stream().forEach(msg -> msg.forEach((k, v) -> {
                    if (publishDTO.getId() == k) {
                        publishDTO.setViewCount(v);
                        return;
                    }
                }));
            }

            //发布人信息
            userBasics.stream().forEach(userBasic -> {
                if (publishDTO.getPublisherId().trim().equals(userBasic.getOpenId().trim())) {

                    publishDTO.setPublisherName(userBasic.getNickName());
                    publishDTO.setPublisherPhoto(userBasic.getHeadImgUrl());

                }
            });

            //品种
            Map<Integer, PetVariety> classifyMap = petVariety.get(publishDTO.getClassifyId());
            PetVariety variety = classifyMap.get(publishDTO.getVarietyId());
            if (variety != null)
                publishDTO.setVarietyName(variety.getName());
        });

        for (PublishDTO publishDTO : publishDTOS) {

            //转发
            if (publishShareCount.get(publishDTO.getId()) != null) {
                int shareCount = publishShareCount.get(publishDTO.getId()).intValue();
                publishDTO.setShareCount(shareCount);
            }

            //关注
            if (publishLikeCount.get(publishDTO.getId()) != null) {
                int likeCount = publishLikeCount.get(publishDTO.getId()).intValue();
                publishDTO.setLikeCount(likeCount);
            }
        }


        return publishDTOS;
    }


    @Override
    public Integer findPublishView(String openId, PetPublish petPublish) {

        Date nowStartTime = DateUtil.getNowStartTime();
        Date nowEndTime = DateUtil.getNowEndTime();
        PublishView view = publishViewRepository.findTopByViewerAndPublishIdAndViewTimeBetweenOrderByViewTimeDesc(openId, petPublish.getId(), nowStartTime, nowEndTime);

        /**
         * 更新今天的浏览时间
         */
        if (view == null) {

            PublishView publishView = new PublishView();
            publishView.setPublishId(petPublish.getId());
            publishView.setPublisherId(petPublish.getPublisherId());
            publishView.setViewer(openId);
            publishView.setViewTime(new Date());
            publishViewRepository.save(publishView);

        } else {
            view.setViewTime(new Date());
            publishViewRepository.save(view);
        }

        /**
         * 获取历史浏览时间
         */
        Integer viewCount = publishViewRepository.findByPublishId(petPublish.getId());
        return viewCount;
    }

    /**
     * 获取宠物类别
     */
    @Override
    public Map<Integer, Map<Integer, PetVariety>> findPetVariety() {

        List<PetVariety> petVarieties = petVarietyRepository.findAll();
        //按类别分组品种
        Map<Integer, List<PetVariety>> classifyMap = petVarieties.stream().collect(Collectors.groupingBy(PetVariety::getClassifyId));

        Map<Integer, Map<Integer, PetVariety>> varieties = new HashMap<>();
        classifyMap.forEach((k, v) -> varieties.put(k, v.stream().collect(Collectors.toMap(PetVariety::getId, Function.identity()))));

        return varieties;
    }
}

