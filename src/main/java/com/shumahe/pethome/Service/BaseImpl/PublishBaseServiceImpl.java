package com.shumahe.pethome.Service.BaseImpl;

import com.shumahe.pethome.Convert.Publish2PublishDTOConvert;
import com.shumahe.pethome.DTO.PublicMsgDTO;
import com.shumahe.pethome.DTO.PublishDTO;
import com.shumahe.pethome.DTO.UserDTO;
import com.shumahe.pethome.Domain.PetPublish;
import com.shumahe.pethome.Domain.UserBasic;
import com.shumahe.pethome.Enums.SearchEnum;
import com.shumahe.pethome.Repository.PublishTalkRepository;
import com.shumahe.pethome.Repository.UserBasicRepository;
import com.shumahe.pethome.Service.BaseService.PublishBaseService;
import com.shumahe.pethome.VO.PublishTalkingVO;
import com.shumahe.pethome.VO.PublishVO;
import com.shumahe.pethome.VO.UserBasicVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
public class PublishBaseServiceImpl implements PublishBaseService {

    @Autowired
    PublishTalkRepository publishTalkRepository;

    @Autowired
    UserBasicRepository userBasicRepository;

    @Override
    public List<PublishVO> findPublishDetail(List<PublishVO> publishVOS, List<Integer> publishIds, List<String> userIds) {


        List<PublishTalkingVO> talkingVOS = new ArrayList<>();

        List<UserBasicVO> userBasicVOS = new ArrayList<>();

        /**
         * step 1  通过发布ID查询评论数量
         */
        List<Object[]> publishCommentCount = publishTalkRepository.findPublishCommentCount(publishIds);

        for (Object object : publishCommentCount) {

            PublishTalkingVO talkingVO = new PublishTalkingVO();
            Object[] item = (Object[]) object;
            talkingVO.setPublishId((Integer) item[0]);
            talkingVO.setCommentCount((Integer) item[1]);
            //往评论VO对象加数据
            talkingVOS.add(talkingVO);

        }

        /**
         *  step 2 通过发布人ID查询发布人基本信息
         */
        List<UserBasic> userInfo = userBasicRepository.findByOpenIdIn(userIds);
        for (UserBasic userBasic : userInfo) {

            UserBasicVO userVO = new UserBasicVO();
            BeanUtils.copyProperties(userBasic, userVO);
            userBasicVOS.add(userVO);
        }

        //

        /**
         *  step 3  合成发布信息+发布评论信息 +  发布人基本信息
         */
        publishVOS.forEach(publishVO -> {

            talkingVOS.forEach(talkingVO -> {
                if (talkingVO.getPublishId() == publishVO.getId()) {
                    publishVO.setCommentVO(talkingVO);//将评论数存入发布VO中
                }
            });

            userBasicVOS.forEach(userBasicVO -> {
                if (userBasicVO.getOpenId() == publishVO.getPublisherId()) {
                    publishVO.setUserBasicVO(userBasicVO);
                }
            });

            //若没有评论,初始化值
            if (publishVO.getCommentVO() == null) {
                PublishTalkingVO talkingVO = new PublishTalkingVO();
                publishVO.setCommentVO(talkingVO);
            }
        });

        return publishVOS;
    }

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

        commentCount.stream().forEach((Object[] count) -> {

            Map<Integer, Integer> _tempMsg = new HashMap<>();
            _tempMsg.put((Integer) count[0], (Integer) count[1]);
            msgCount.add(_tempMsg);

        });


        /**
         *  step 2 发布人ID查询发布人基本信息
         */
        List<UserBasic> userBasics = userBasicRepository.findByOpenIdIn(userIds);


        /**
         *  step 3  合成发布信息 + 发布评论信息 +  发布人基本信息
         */
        List<PublishDTO> publishDTOS = Publish2PublishDTOConvert.convert(publishes);

        publishDTOS.stream().forEach(publishDTO -> {

            msgCount.stream().forEach(msg -> msg.forEach((k, v) -> {
                if (publishDTO.getId() == k) {
                    publishDTO.setPublicMsgCount(v);
                    return;
                }
            }));

            userBasics.stream().forEach(userBasic -> {
                if (publishDTO.getPublisherId().equals(userBasic.getOpenId())) {
                    UserDTO _userDTO = new UserDTO(userBasic.getOpenId(), userBasic.getNickName(), userBasic.getHeadImgUrl());
                    publishDTO.setUserDTO(_userDTO);
                    return;
                }

            });
        });

        return publishDTOS;
    }


}
