package com.shumahe.pethome.Controller;


import com.shumahe.pethome.DTO.PrivateMsgDTO;
import com.shumahe.pethome.DTO.PublicMsgDTO;
import com.shumahe.pethome.DTO.PublishDTO;
import com.shumahe.pethome.DTO.UserDTO;
import com.shumahe.pethome.Domain.PetPublish;

import com.shumahe.pethome.Enums.ResultEnum;
import com.shumahe.pethome.Exception.PetHomeException;

import com.shumahe.pethome.Form.ReplyPrivateForm;
import com.shumahe.pethome.Form.ReplyPublishForm;
import com.shumahe.pethome.Repository.PetPublishRepository;
import com.shumahe.pethome.Repository.PublishTalkRepository;
import com.shumahe.pethome.Service.MessageService;
import com.shumahe.pethome.Util.ResultVOUtil;
import com.shumahe.pethome.VO.ResultVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;


import javax.validation.Valid;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;


@RestController
@RequestMapping("/message")
@Slf4j
public class MessageController {

    @Autowired
    MessageService messageService;


    @Autowired
    PetPublishRepository petPublishRepository;


    @Autowired
    PublishTalkRepository publishTalkRepository;

    /**
     * 我的私信列表
     *
     * @param openId
     * @param page
     * @param size
     * @return
     */
    @GetMapping("/private/list")
    public ResultVO findMyPrivateTalk(@RequestParam("openId") String openId,
                                      @RequestParam(value = "page", defaultValue = "0") Integer page,
                                      @RequestParam(value = "size", defaultValue = "200") Integer size) {

        PageRequest pageRequest = new PageRequest(page, size);
        List<List<LinkedHashMap<String, String>>> myPrivateTalk = messageService.findMyPrivateTalk(openId, pageRequest);

        return ResultVOUtil.success(myPrivateTalk);
    }


    /**
     * 我的评论列表
     *
     * @param openId
     * @param page
     * @param size
     * @return
     */
    @GetMapping("/public/list")
    public ResultVO findMyPublicTalk(@RequestParam("openId") String openId,
                                     @RequestParam(value = "page", defaultValue = "0") Integer page,
                                     @RequestParam(value = "size", defaultValue = "200") Integer size) {

        PageRequest pageRequest = new PageRequest(page, size);
        List<List<Map<String, String>>> myPrivateTalk = messageService.findMyPublicTalk(openId, pageRequest);

        return ResultVOUtil.success(myPrivateTalk);

    }


    /**
     * 主题  互动详情   √
     *
     * @param publishId
     * @param openId
     * @return
     */
    @GetMapping("/public/detail")
    public ResultVO petPublicTalk(@RequestParam("publishId") Integer publishId, @RequestParam("openId") String openId) {

        if (publishId == 0) {
            throw new PetHomeException(ResultEnum.PARAM_ERROR);
        }

        PetPublish pet = petPublishRepository.findById(publishId);
        List<List<PublicMsgDTO>> petPublicTalks = messageService.petPublicTalks(pet);

        long talkCount = petPublicTalks.stream().mapToInt(num -> num.size()).summaryStatistics().getSum();


        PublishDTO publishDTO = new PublishDTO();
        publishDTO.setPublicMsgCount((int) talkCount);
        publishDTO.setPublicTalkDTO(petPublicTalks);
        return ResultVOUtil.success(publishDTO);
    }

    /**
     * 主题  私信详情   √
     *
     * @param publishId
     * @param openId
     * @return
     */
    @GetMapping("/private/detail")
    public ResultVO petPrivateTalk(@RequestParam("publishId") Integer publishId, @RequestParam("openId") String openId) {

        if (publishId == 0) {
            throw new PetHomeException(ResultEnum.PARAM_ERROR);
        }

        PetPublish pet = petPublishRepository.findById(publishId);
        List<List<PrivateMsgDTO>> petPrivateTalks = messageService.petPrivateTalks(pet, openId);

        long talkCount = petPrivateTalks.stream().mapToInt(num -> num.size()).summaryStatistics().getSum();


        PublishDTO publishDTO = new PublishDTO();
        publishDTO.setPublicMsgCount((int) talkCount);
        publishDTO.setPrivateTalkDTO(petPrivateTalks);
        return ResultVOUtil.success(publishDTO);
    }

    /**
     * 回复互动 √
     *
     * @param replyPrivateFrom
     * @param bindingResult
     * @return
     */
    @PutMapping("/public/{publishId}")
    public ResultVO replayPublicMsg(@PathVariable("publishId") Integer publishId, @Valid ReplyPublishForm replyPrivateFrom, BindingResult bindingResult) {


        PetPublish pet = petPublishRepository.findById(publishId);
        if (pet == null) {
            log.error("【回复互动参数不正确】发布ID无效");
            throw new PetHomeException(ResultEnum.PARAM_ERROR.getCode(), "【请求链接错误】,发布ID无效");
        }


        if (bindingResult.hasErrors()) {
            log.error("【私信回复参数不正确】参数不正确,petForm={}", replyPrivateFrom);
            throw new PetHomeException(ResultEnum.PARAM_ERROR.getCode(), bindingResult.getFieldError().getDefaultMessage());
        }

        /**
         * 判断是评论还是互动
         */
        boolean talkIdEmpty = StringUtils.isEmpty(replyPrivateFrom.getTalkId());
        boolean accepterEmpty = StringUtils.isEmpty(replyPrivateFrom.getReplierAccept());
        if (talkIdEmpty || accepterEmpty) {
            if (!(talkIdEmpty && accepterEmpty)) {
                log.error("【回复互动参数不正确】,若为评论,则评论ID和接收人都为-1");
                throw new PetHomeException(ResultEnum.PARAM_ERROR.getCode(), "【回复互动参数不正确】,若为评论,评论ID和接收人都不传;若为互动,则评论ID和接收人都必须传");
            }
        }

        PublicMsgDTO msgDTO = messageService.replyPublic(replyPrivateFrom, pet);
        return ResultVOUtil.success(msgDTO);
    }


    /**
     * 回复私信
     *
     * @param replyPrivateFrom
     * @param bindingResult
     * @return
     */
    @PutMapping("/private/{publishId}")
    public ResultVO replayPrivateMsg(@PathVariable("publishId") Integer publishId, @Valid ReplyPrivateForm replyPrivateFrom, BindingResult bindingResult) {


        PetPublish pet = petPublishRepository.findById(publishId);
        if (pet == null) {
            log.error("【回复私信参数不正确】发布ID无效");
            throw new PetHomeException(ResultEnum.PARAM_ERROR.getCode(), "【请求链接错误】,发布ID无效");
        }

        if (bindingResult.hasErrors()) {
            log.error("【回复私信参数不正确】参数不正确,petForm={}", replyPrivateFrom);
            throw new PetHomeException(ResultEnum.PARAM_ERROR.getCode(), bindingResult.getFieldError().getDefaultMessage());
        }

        PrivateMsgDTO msg = messageService.replyPrivate(replyPrivateFrom, pet);
        return ResultVOUtil.success(msg);
    }


    /**
     * 用户中心
     *
     * @param openId
     * @return
     */
    @GetMapping("/{openid}")
    public ResultVO findMyInfo(@PathVariable("openid") String openId) {

        UserDTO userDTO = messageService.findMyInfo(openId);
        return ResultVOUtil.success(userDTO);
    }


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
