package com.shumahe.pethome.Controller;

import com.shumahe.pethome.DTO.UserDTO;
import com.shumahe.pethome.Service.MemberService;
import com.shumahe.pethome.Util.ResultVOUtil;
import com.shumahe.pethome.VO.ResultVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
@RequestMapping("/user")
public class MemberController {


    @Autowired
    private MemberService memberService;

    /**
     * 用户宠卡新增
     */

    /**
     * 用户宠卡列表
     */

    /**
     * 用户认证
     */

    /**
     *检查用户是否认证
     */

    /**
     * 用户中心
     */
    @GetMapping("mine")
    public ResultVO findMyInfo(@RequestParam("openId") String openId) {
        
        UserDTO userDTO = memberService.findMyInfo(openId);
        return ResultVOUtil.success(userDTO);
    }


}
