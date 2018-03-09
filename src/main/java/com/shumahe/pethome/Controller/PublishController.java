package com.shumahe.pethome.Controller;


import com.shumahe.pethome.DTO.PublishDTO;
import com.shumahe.pethome.Domain.PetPublish;
import com.shumahe.pethome.Enums.ResultEnum;
import com.shumahe.pethome.Exception.PetHomeException;
import com.shumahe.pethome.Form.PetSearchForm;
import com.shumahe.pethome.Form.PublishMasterForm;
import com.shumahe.pethome.Form.PublishPetForm;
import com.shumahe.pethome.Repository.PetPublishRepository;
import com.shumahe.pethome.Service.PublishService;
import com.shumahe.pethome.Util.ResultVOUtil;
import com.shumahe.pethome.VO.PublishVO;
import com.shumahe.pethome.VO.ResultVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/publish")
@Slf4j
public class PublishController {


    @Autowired
    PublishService publishService;


    /**
     * PS:按时间查所有
     */

    /**
     * 首页列表 （动态 + 寻主 + 寻宠）
     *
     * @param publishType
     * @param page
     * @param size
     * @return
     */

    @GetMapping("/index/list")
    public ResultVO<List<PublishVO>> publishList(@RequestParam("publishType") Integer publishType,
                                                 @RequestParam(value = "page", defaultValue = "0") Integer page,
                                                 @RequestParam(value = "size", defaultValue = "10") Integer size) {

        if (StringUtils.isEmpty(publishType)) {
            throw new PetHomeException(ResultEnum.PARAM_ERROR);
        }

        PageRequest request = new PageRequest(page, size);
        return publishService.findAll(publishType, request);
    }

    /**
     * 寻宠发布
     *
     * @param petForm
     * @param bindingResult
     * @return
     */
    @PostMapping("/pet/create")
    public ResultVO<Map<String, String>> create(@Valid PublishPetForm petForm, BindingResult bindingResult) {

        //验证表单数据是否正确
        if (bindingResult.hasErrors()) {
            log.error("【发布寻宠】参数不正确,petForm={}", petForm);
            throw new PetHomeException(ResultEnum.PARAM_ERROR.getCode(), bindingResult.getFieldError().getDefaultMessage());
        }

        //查询结果
        PetPublish petPublish = publishService.createPet(petForm);

        //组装返回数据
        Map<String, String> returnData = new HashMap<>();
        returnData.put("openId", petPublish.getPublisherId());
        returnData.put("publishId", String.valueOf(petPublish.getId()));
        return ResultVOUtil.success(returnData);
    }

    /**
     * 寻主发布
     *
     * @param masterForm
     * @param bindingResult
     * @return
     */
    @PostMapping("/master/create")
    public ResultVO<Map<String, String>> create(@Valid PublishMasterForm masterForm, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            log.error("【发布寻主】参数不正确,petForm={}", masterForm);
            throw new PetHomeException(ResultEnum.PARAM_ERROR.getCode(), bindingResult.getFieldError().getDefaultMessage());
        }

        //查询结果
        PetPublish petPublish = publishService.createMaster(masterForm);

        //组装返回数据
        Map<String, String> returnData = new HashMap<>();
        returnData.put("openId", petPublish.getPublisherId());
        returnData.put("publishId", String.valueOf(petPublish.getId()));

        return ResultVOUtil.success(returnData);

    }


    /**
     * 发布列表(我的)
     * PS:按时间查个人
     */
    @GetMapping("/user/publishlist")
    public List<PublishDTO> myPublishList(@RequestParam("openId") String openId,
                                          @RequestParam(value = "page", defaultValue = "0") Integer page,
                                          @RequestParam(value = "size", defaultValue = "10") Integer size) {

        PageRequest pageRequest = new PageRequest(page, size);
        List<PublishDTO> myPublishList = publishService.findMyPublishList(openId, pageRequest);
        return myPublishList;
    }


    /**
     * 待处理列表(我的)
     * PS:按时间查个人
     */
    @GetMapping("/user/notFoundList")
    public List<PublishDTO> findNotFound(@RequestParam("openId") String openId,
                                         @RequestParam(value = "page", defaultValue = "0") Integer page,
                                         @RequestParam(value = "size", defaultValue = "10") Integer size) {

        PageRequest pageRequest = new PageRequest(page, size);
        List<PublishDTO> myPublishList = publishService.findNotFound(openId, pageRequest);


        return myPublishList;
    }


    /**
     * 查看发布详情信息（）
     *
     * @param publishId
     * @return
     */
    @GetMapping("/detail")
    public ResultVO<PublishVO> publishDetail(@RequestParam("publishId") Integer publishId) {

        return null;
    }


    /**
     * 发布详情(详情) PS:详细描述,同时浏览次数+1
     */


    /**
     * 基于条件(含模糊条件)搜索发布列表  (搜索)
     */





/***************************************************************/


    /**
     *个人中心 PS:个人信息+我的任务+我的消息
     */


}
