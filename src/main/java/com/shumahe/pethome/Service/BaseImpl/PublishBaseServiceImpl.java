package com.shumahe.pethome.Service.BaseImpl;

import com.shumahe.pethome.Convert.Publish2PublishDTOConvert;
import com.shumahe.pethome.DTO.PublishDTO;
import com.shumahe.pethome.Domain.PetPublish;
import com.shumahe.pethome.Domain.PublishView;
import com.shumahe.pethome.Domain.UserBasic;
import com.shumahe.pethome.Repository.PublishTalkRepository;
import com.shumahe.pethome.Repository.PublishViewRepository;
import com.shumahe.pethome.Repository.UserBasicRepository;
import com.shumahe.pethome.Service.BaseService.PublishBaseService;
import com.shumahe.pethome.Util.DateUtil;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
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
         * step 1  发布ID查询评论数量
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
         * step 2  发布ID查询浏览数量
         */
        List<Integer[]> viewObject = publishViewRepository.findViewCount(publishIds);
        //Map<publishId,msgCount>
        List<Map<Integer, Integer>> viewCount = new ArrayList<>();

        if (!viewObject.isEmpty()) {

            viewObject.stream().forEach((Object[] count) -> {

                Map<Integer, Integer> _tempMsg = new HashMap<>();
                _tempMsg.put((Integer) count[0], (Integer) count[1]);
                viewCount.add(_tempMsg);

            });
        }


        /**
         *  step 3 发布人ID查询发布人基本信息
         */
        List<UserBasic> userBasics = userBasicRepository.findByOpenIdIn(userIds);


        /**
         *  step 4  合成发布信息 =  发布信息 + 评论信息 +  发布人基本信息
         */
        List<PublishDTO> publishDTOS = Publish2PublishDTOConvert.convert(publishes);

        publishDTOS.stream().forEach(publishDTO -> {

            if (!msgCount.isEmpty()) {
                msgCount.stream().forEach(msg -> msg.forEach((k, v) -> {
                    if (publishDTO.getId() == k) {
                        publishDTO.setPublicMsgCount(v);
                        return;
                    }
                }));
            }

            if (!viewCount.isEmpty()) {
                viewCount.stream().forEach(msg -> msg.forEach((k, v) -> {
                    if (publishDTO.getId() == k) {
                        publishDTO.setViewCount(v);
                        return;
                    }
                }));
            }

            userBasics.stream().forEach(userBasic -> {
                if (publishDTO.getPublisherId().trim().equals(userBasic.getOpenId().trim())) {

                    publishDTO.setPublisherName(userBasic.getNickName());
                    publishDTO.setPublisherPhoto(userBasic.getHeadImgUrl());

                }

            });
        });

        return publishDTOS;


    }


    @Override
    public Integer getPublishView(String openId, PetPublish petPublish) {

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
}
