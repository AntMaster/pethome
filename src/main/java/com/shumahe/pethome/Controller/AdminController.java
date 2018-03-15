package com.shumahe.pethome.Controller;

import com.shumahe.pethome.DTO.PrivateMsgDTO;
import com.shumahe.pethome.DTO.PublicMsgDTO;
import com.shumahe.pethome.DTO.PublishDTO;
import com.shumahe.pethome.Domain.PetPublish;
import com.shumahe.pethome.Domain.PublishTalk;
import com.shumahe.pethome.Domain.UserTalk;
import com.shumahe.pethome.Enums.ResultEnum;
import com.shumahe.pethome.Exception.PetHomeException;
import com.shumahe.pethome.Service.AdminService;
import com.shumahe.pethome.Util.ResultVOUtil;
import com.shumahe.pethome.VO.ResultVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.bind.annotation.*;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@RestController
@Slf4j
@CrossOrigin
@RequestMapping("/admin")
public class AdminController {


    @Autowired
    AdminService adminService;

    /**
     * 寻宠 寻主 列表
     *
     * @param publishType
     * @param page
     * @param size
     * @return
     */
    @GetMapping("/publish")
    public ResultVO findAll(@RequestParam(value = "publishType", defaultValue = "0") Integer publishType,
                            @RequestParam(value = "page", defaultValue = "0") Integer page,
                            @RequestParam(value = "size", defaultValue = "10") Integer size) {

        if (publishType == 0) {
            throw new PetHomeException(ResultEnum.PARAM_ERROR);
        }

        PageRequest request = new PageRequest(page, size);
        List<PublishDTO> all = adminService.findAll(publishType, request);
        return ResultVOUtil.success(all);
    }

    /**
     * 显示 隐藏 发布
     *
     * @param id
     * @param publishState
     * @return
     */
    @PostMapping("/publish/{id}")
    public ResultVO modifyShowState(@PathVariable("id") Integer id, @RequestParam("publishState") Integer publishState) {

        if (publishState != 0 && publishState != 1) {
            throw new PetHomeException(ResultEnum.PARAM_ERROR);
        }

        PetPublish petPublish = adminService.modifyShowState(id, publishState);
        return ResultVOUtil.success(petPublish);
    }

    /**
     * 转发 关注
     */
    @GetMapping("/forward/{id}")
    public ResultVO findDynamic(@PathVariable("id") Integer id,
                                @RequestParam(value = "dynamicType", defaultValue = "0") Integer dynamicType,
                                @RequestParam(value = "page", defaultValue = "0") Integer page,
                                @RequestParam(value = "size", defaultValue = "100") Integer size) {

        if (dynamicType == 0) {
            throw new PetHomeException(ResultEnum.PARAM_ERROR);
        }

        PageRequest pageRequest = new PageRequest(page, size);
        Map<String, List<Map<String, String>>> dynamic = adminService.findDynamic(id, dynamicType, pageRequest);
        return ResultVOUtil.success(dynamic);
    }

    /**
     * 私信
     */
    @GetMapping("/private/{id}")
    public ResultVO findPrivate(@PathVariable("id") Integer id,
                                @RequestParam(value = "page", defaultValue = "0") Integer page,
                                @RequestParam(value = "size", defaultValue = "100") Integer size) {

        PageRequest pageRequest = new PageRequest(page, size);
        List<PrivateMsgDTO> privateMsg = adminService.findPrivateMsg(id, pageRequest);
        return ResultVOUtil.success(privateMsg);

    }

    /**
     * 显示 隐藏 私信
     */
    @PostMapping("/private/{id}")
    public ResultVO modifyPrivateShow(@PathVariable("id") Integer id, @RequestParam("showState") Integer showState) {

        UserTalk userTalk = adminService.modifyPrivateShow(id, showState);
        return ResultVOUtil.success(userTalk);

    }

    /**
     * 互动
     */
    @GetMapping("/public/{id}")
    public ResultVO findPublish(@PathVariable("id") Integer id,
                                @RequestParam(value = "page", defaultValue = "0") Integer page,
                                @RequestParam(value = "size", defaultValue = "100") Integer size) {

        PageRequest pageRequest = new PageRequest(page, size);
        List<PublicMsgDTO> publicMsg = adminService.findPublicMsg(id, pageRequest);
        return ResultVOUtil.success(publicMsg);

    }

    /**
     * 显示 隐藏 互动
     */
    @PostMapping("/public/{id}")
    public ResultVO modifyPublicShow(@PathVariable("id") Integer id, @RequestParam("showState") Integer showState) {

        PublishTalk userTalk = adminService.modifyPublicShow(id, showState);
        return ResultVOUtil.success(userTalk);

    }

}
