package com.shumahe.pethome.Controller;

import com.shumahe.pethome.Enums.ResultEnum;
import com.shumahe.pethome.Exception.PetHomeException;

import com.shumahe.pethome.Service.BaseService.DynamicBaseService;
import com.shumahe.pethome.Service.DynamicService;
import com.shumahe.pethome.Util.ResultVOUtil;
import com.shumahe.pethome.VO.ResultVO;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;


@RestController
@RequestMapping("/dynamic")
public class DynamicController {


    @Autowired
    DynamicService dynamicService;


    @Autowired
    DynamicBaseService dynamicBaseService;

    /**
     * 关注列表(我的关注 + 关注我的)
     *
     * @param openId
     * @return
     */
    @GetMapping("/like/list")
    public ResultVO<List<Map<String, Object>>> likeList(@RequestParam("openid") String openId, @RequestParam("type") int type) {

        if (type != 1 && type != 2) {
            throw new PetHomeException(ResultEnum.PARAM_ERROR.getCode(), "参数不正确,type=1为我的关注,type=2为关注我的");
        }


        List<Map<String, String>> likeResult = dynamicService.findMyLike(openId, type);

        return ResultVOUtil.success(likeResult);
    }


    /**
     * 转发列表(我的关注 + 关注我的)
     * @param openId
     * @param type
     * @return
     */

    @GetMapping("/share/list")
    public ResultVO<List<Map<String, String>>> shareList(@RequestParam("openid") String openId, @RequestParam("type") int type) {


        if (type != 1 && type != 2) {
            throw new PetHomeException(ResultEnum.PARAM_ERROR.getCode(), "参数不正确,type=1为我的转发,type=2为转发我的");
        }

        List<Map<String, String>> likeResult = dynamicService.findMyShare(openId, type);

        return ResultVOUtil.success(likeResult);

    }


    /**
     * 分享操作(主页)
     *
     *  PS:基于微信
     */


    /**
     * 关注列表(我的)
     */


    /**
     * 被关注列表(我的)
     */

    /**
     * 分享列表(我的)
     */


    /**
     * 被分享列表(我的)
     */

}
