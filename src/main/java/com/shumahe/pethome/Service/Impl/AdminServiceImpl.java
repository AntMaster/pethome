package com.shumahe.pethome.Service.Impl;

import com.shumahe.pethome.DTO.PrivateMsgDTO;
import com.shumahe.pethome.DTO.PublicMsgDTO;
import com.shumahe.pethome.DTO.PublishDTO;
import com.shumahe.pethome.DTO.UserApproveDTO;
import com.shumahe.pethome.Domain.*;
import com.shumahe.pethome.Enums.ResultEnum;
import com.shumahe.pethome.Exception.PetHomeException;
import com.shumahe.pethome.Repository.*;
import com.shumahe.pethome.Service.AdminService;
import com.shumahe.pethome.Service.BaseService.PublishBaseService;
import com.shumahe.pethome.Util.DateUtil;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.function.Function;
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


    @Autowired
    UserApproveRepository userApproveRepository;

    @Autowired
    PublishViewRepository publishViewRepository;


    /**
     * 查询寻宠 、 寻主
     */
    @Override
    public Map<String, Object> findAll(Integer publishType, PageRequest pageRequest) {

        Page<PetPublish> pets = petPublishRepository.findByPublishTypeOrderByCreateTimeDesc(publishType, pageRequest);
        List<PetPublish> publishList = pets.getContent();
        if (publishList.isEmpty()) {
            throw new PetHomeException(ResultEnum.RESULT_EMPTY);
        }

        List<PublishDTO> publishDTOS = publishBaseService.findPetExtends(publishList);

        Map<String, Object> res = new HashMap<>();
        res.put("total", pets.getTotalElements());
        res.put("pages", pets.getTotalPages());
        res.put("size", pets.getSize());
        res.put("page", pets.getNumber());
        res.put("data", publishDTOS);

        return res;
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

    /**
     * 企业认证
     *
     * @param approveState
     * @param request
     * @return
     */
    @Override
    public Map<String, Object> findApprove(Integer approveState, PageRequest request) {

        Page<UserApprove> all;
        if (approveState == 0) {
            all = userApproveRepository.findAll(request);
        } else {
            all = userApproveRepository.findByApproveState(approveState, request);
        }

        Map<String, Object> res = new HashMap<>();
        res.put("last", all.isLast());
        res.put("totalPages", all.getTotalPages());
        res.put("totalElements", all.getTotalElements());
        res.put("number", all.getNumber());
        res.put("size", all.getSize());
        res.put("first", all.isFirst());
        res.put("numberOfElements", all.getNumberOfElements());
        List<UserApprove> approves = all.getContent();
        if (approves.isEmpty()) {
            return res;
        }

        List<String> userId = approves.stream().map(UserApprove::getUserId).distinct().collect(Collectors.toList());
        List<UserBasic> users = userBasicRepository.findByOpenIdIn(userId);
        Map<String, UserBasic> userMap = users.stream().collect(Collectors.toMap(e -> e.getOpenId().trim(), Function.identity()));

        List<UserApproveDTO> userApprove = approves.stream().map(e -> {

            UserApproveDTO userApproveDTO = new UserApproveDTO();
            BeanUtils.copyProperties(e, userApproveDTO);
            userApproveDTO.setUserImage(userMap.get(e.getUserId()).getHeadImgUrl());
            return userApproveDTO;

        }).collect(Collectors.toList());

        res.put("content", userApprove);
        return res;
    }

    /**
     * 浏览记录
     *
     * @param id
     * @return
     */
    @Override
    public Map<String, Object> findView(Integer id, Integer day) {

        Calendar now = Calendar.getInstance();
        now.set(Calendar.DATE, now.get(Calendar.DATE) - day);

        Date startTime = DateUtil.getStartTime(now.getTime());
        Date endTime = DateUtil.getEndTime(now.getTime());
        List<PublishView> viewers = publishViewRepository.findByPublishIdAndViewTimeBetweenOrderByViewTimeDesc(id, startTime, endTime);

        if (viewers.isEmpty()) {

        }
        return null;
    }

    /**
     * 认证审核
     *
     * @param id
     * @param approveType
     * @param msg
     * @return
     */
    @Transactional
    @Override
    public boolean modifyApprove(Integer id, Integer approveType, String msg) {

        UserApprove approve = userApproveRepository.findOne(id);
        if (approve == null)
            throw new PetHomeException(ResultEnum.RESULT_EMPTY);

        approve.setApproveState(approveType);
        approve.setDescription(msg);

        userApproveRepository.save(approve);


        UserBasic userBasic = userBasicRepository.findByOpenId(approve.getUserId());
        if (userBasic == null)
            throw new PetHomeException(ResultEnum.RESULT_EMPTY);

        userBasic.setApproveState(approveType);
        userBasicRepository.save(userBasic);
        return true;
    }
}
