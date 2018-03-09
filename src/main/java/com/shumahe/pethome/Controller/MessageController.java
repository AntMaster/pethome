package com.shumahe.pethome.Controller;


import com.shumahe.pethome.Domain.PublishTalk;
import com.shumahe.pethome.Domain.UserTalk;
import com.shumahe.pethome.Enums.ResultEnum;
import com.shumahe.pethome.Exception.PetHomeException;

import com.shumahe.pethome.Form.ReplyPrivateForm;
import com.shumahe.pethome.Form.ReplyPublishForm;
import com.shumahe.pethome.Service.MessageService;
import com.shumahe.pethome.Util.ResultVOUtil;
import com.shumahe.pethome.VO.ResultVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


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
     * 回复私信
     *
     * @param replyPrivateFrom
     * @param bindingResult
     * @return
     */
    @GetMapping("/private/replay")
    public ResultVO replayPrivateMsg(@Valid ReplyPrivateForm replyPrivateFrom, BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            log.error("【私信回复参数不正确】参数不正确,petForm={}", replyPrivateFrom);
            throw new PetHomeException(ResultEnum.PARAM_ERROR.getCode(), bindingResult.getFieldError().getDefaultMessage());
        }

        UserTalk save = messageService.replyPrivate(replyPrivateFrom);
        return ResultVOUtil.success(save);
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
     * 回复评论
     *
     * @param replyPrivateFrom
     * @param bindingResult
     * @return
     */
    @GetMapping("/public/replay")
    public ResultVO replayPublicMsg(@Valid ReplyPublishForm replyPrivateFrom, BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            log.error("【私信回复参数不正确】参数不正确,petForm={}", replyPrivateFrom);
            throw new PetHomeException(ResultEnum.PARAM_ERROR.getCode(), bindingResult.getFieldError().getDefaultMessage());
        }

        PublishTalk save = messageService.replyPublic(replyPrivateFrom);
        return ResultVOUtil.success(save);
    }

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
