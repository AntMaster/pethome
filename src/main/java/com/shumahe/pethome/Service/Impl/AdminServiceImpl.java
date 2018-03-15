package com.shumahe.pethome.Service.Impl;

import com.shumahe.pethome.DTO.PrivateMsgDTO;
import com.shumahe.pethome.DTO.PublicMsgDTO;
import com.shumahe.pethome.DTO.PublishDTO;
import com.shumahe.pethome.Domain.*;
import com.shumahe.pethome.Enums.ResultEnum;
import com.shumahe.pethome.Exception.PetHomeException;
import com.shumahe.pethome.Repository.*;
import com.shumahe.pethome.Service.AdminService;
import com.shumahe.pethome.Service.BaseService.PublishBaseService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class AdminServiceImpl implements AdminService {


    @Autowired
    PetPublishRepository petPublishRepository;

    @Autowired
    PublishBaseService publishBaseService;

    @Autowired
    UserDynamicRepository userDynamicRepository;

    @Autowired
    UserBasicRepository userBasicRepository;

    @Autowired
    UserTalkRepository userTalkRepository;

    @Autowired
    PublishTalkRepository publishTalkRepository;

    /**
     * 查询寻宠 、 寻主
     */
    @Override
    public List<PublishDTO> findAll(Integer publishType, PageRequest pageRequest) {

        Page<PetPublish> pets = petPublishRepository.findByPublishTypeOrderByCreateTimeDesc(publishType, pageRequest);
        List<PetPublish> publishList = pets.getContent();
        if (publishList.isEmpty()) {
            throw new PetHomeException(ResultEnum.RESULT_EMPTY);
        }

        List<PublishDTO> publishDTOS = publishBaseService.findPetExtends(publishList);
        return publishDTOS;
    }

    /**
     * 显示 隐藏
     */
    @Override
    public PetPublish modifyShowState(Integer id, Integer publishState) {
        PetPublish publish = petPublishRepository.findById(id);
        if (publish == null) {
            throw new PetHomeException(ResultEnum.RESULT_EMPTY);
        }

        publish.setPublishState(publishState);

        PetPublish save = petPublishRepository.save(publish);
        return save;
    }

    /**
     * 转发 || 关注
     */
    @Override
    public Map<String, List<Map<String, String>>> findDynamic(Integer id, Integer dynamicType, PageRequest pageRequest) {

        PetPublish publish = petPublishRepository.findById(id);

        if (publish == null) {
            throw new PetHomeException(ResultEnum.RESULT_EMPTY);
        }

        List<UserDynamic> dynamics = userDynamicRepository.findByPublishIdAndDynamicTypeOrderByCreateTimeDesc(id, dynamicType, pageRequest);
        if (dynamics.isEmpty()) {
            throw new PetHomeException(ResultEnum.RESULT_EMPTY);
        }

        List<String> usersId = dynamics.stream().map(e -> e.getUserIdFrom()).distinct().collect(Collectors.toList());

        List<UserBasic> users = userBasicRepository.findByOpenIdIn(usersId);


        List<Map<String, String>> res = new ArrayList<>();

        dynamics.stream().forEach(dynamic -> {

            Map<String, String> _temp = new HashMap<>();

            _temp.put("dynamicDate", dynamic.getCreateTime().toString().split(" ")[0]);
            _temp.put("dynamicTime", dynamic.getCreateTime().toString());

            users.stream().forEach(user -> {

                if (dynamic.getUserIdFrom().trim().equals(user.getOpenId().trim())) {
                    _temp.put("nickName", user.getNickName());
                    _temp.put("userImage", user.getHeadImgUrl());
                    _temp.put("openId", user.getOpenId());

                }
            });
            res.add(_temp);
        });

        Map<String, List<Map<String, String>>> dynamicResult = res.stream().collect(Collectors.groupingBy(e -> e.get("dynamicDate")));

        return dynamicResult;
    }

    /**
     * 私信
     */
    @Override
    public List<PrivateMsgDTO> findPrivateMsg(Integer id, PageRequest pageRequest) {

        PetPublish publish = petPublishRepository.findById(id);

        if (publish == null) {
            throw new PetHomeException(ResultEnum.RESULT_EMPTY);
        }

        List<UserTalk> talks = userTalkRepository.findByPublishIdOrderByIdDesc(id, pageRequest);
        if (talks.isEmpty()) {
            throw new PetHomeException(ResultEnum.RESULT_EMPTY);
        }

        List<String> usersId = talks.stream().map(e -> e.getUserIdFrom()).distinct().collect(Collectors.toList());

        List<UserBasic> users = userBasicRepository.findByOpenIdIn(usersId);


        List<PrivateMsgDTO> msgDTOS = new ArrayList<>();

        talks.stream().forEach(msg -> {

            PrivateMsgDTO msgDTO = new PrivateMsgDTO();
            BeanUtils.copyProperties(msg, msgDTO);

            users.stream().forEach(user -> {
                if (msg.getUserIdFrom().trim().equals(user.getOpenId().trim())) {
                    msgDTO.setUserIdFromName(user.getNickName());
                    msgDTO.setUserIdFromPhoto(user.getHeadImgUrl());
                }
            });
            msgDTOS.add(msgDTO);
        });

        return msgDTOS;
    }


    /**
     * 显示 隐藏 私信
     */
    @Override
    public UserTalk modifyPrivateShow(Integer id, Integer showState) {
        UserTalk msg = userTalkRepository.findOne(id);
        if (msg == null) {
            throw new PetHomeException(ResultEnum.RESULT_EMPTY);
        }


        msg.setShowState(showState);

        UserTalk save = userTalkRepository.save(msg);
        return save;

    }

    /**
     * 互动
     */
    @Override
    public List<PublicMsgDTO> findPublicMsg(Integer id, PageRequest pageRequest) {


        PetPublish publish = petPublishRepository.findById(id);

        if (publish == null) {
            throw new PetHomeException(ResultEnum.RESULT_EMPTY);
        }

        List<PublishTalk> talks = publishTalkRepository.findByPublishIdOrderByReplyDateDesc(id, pageRequest);
        if (talks.isEmpty()) {
            throw new PetHomeException(ResultEnum.RESULT_EMPTY);
        }

        List<String> usersId = talks.stream().map(e -> e.getReplierFrom()).distinct().collect(Collectors.toList());

        List<UserBasic> users = userBasicRepository.findByOpenIdIn(usersId);


        List<PublicMsgDTO> msgDTOS = new ArrayList<>();

        talks.stream().forEach(msg -> {

            PublicMsgDTO msgDTO = new PublicMsgDTO();
            BeanUtils.copyProperties(msg, msgDTO);

            users.stream().forEach(user -> {
                if (msg.getReplierFrom().trim().equals(user.getOpenId().trim())) {
                    msgDTO.setReplierFromName(user.getNickName());
                    msgDTO.setReplierFromPhoto(user.getHeadImgUrl());
                }
            });
            msgDTOS.add(msgDTO);
        });

        return msgDTOS;


    }


    /**
     * 显示 隐藏 互动
     */
    @Override
    public PublishTalk modifyPublicShow(Integer id, Integer showState) {

        PublishTalk msg = publishTalkRepository.findOne(id);
        if (msg == null) {
            throw new PetHomeException(ResultEnum.RESULT_EMPTY);
        }
        msg.setShowState(showState);
        PublishTalk save = publishTalkRepository.save(msg);
        return save;
    }

}
