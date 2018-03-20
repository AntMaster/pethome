package com.shumahe.pethome.Controller;

import com.shumahe.pethome.Config.SMSConfig;
import com.shumahe.pethome.DTO.UserDTO;
import com.shumahe.pethome.Domain.UserBasic;
import com.shumahe.pethome.Enums.BooleanEnum;
import com.shumahe.pethome.Enums.ResultEnum;
import com.shumahe.pethome.Exception.PetHomeException;
import com.shumahe.pethome.Form.UserApproveForm;
import com.shumahe.pethome.Repository.UserBasicRepository;
import com.shumahe.pethome.Service.UserService;
import com.shumahe.pethome.Util.MathUtil;
import com.shumahe.pethome.Util.PhoneFormatCheckUtil;
import com.shumahe.pethome.Util.ResultVOUtil;
import com.shumahe.pethome.VO.ResultVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import java.util.HashMap;
import java.util.Map;

@RestController
@Slf4j
@RequestMapping("/user")
public class UserController {

    @Autowired
    UserBasicRepository userBasicRepository;

    @Autowired
    UserService userService;

    @Autowired
    SMSConfig smsConfig;

    @Autowired
    RestTemplate restTemplate;

    /**
     * 用户中心
     *
     * @param openId
     * @return
     */
    @GetMapping("/{openid}")
    public ResultVO findMyInfo(@PathVariable("openid") String openId) {

        UserDTO userDTO = userService.findMyInfo(openId);
        return ResultVOUtil.success(userDTO);
    }

    /**
     * 判断用户认证
     *
     * @param openId
     * @return
     */
    @GetMapping("/auth/{openId}")
    public ResultVO findUserAuto(@PathVariable("openId") String openId) {

        UserBasic user = userBasicRepository.findByOpenId(openId);
        if (user == null) {
            throw new PetHomeException(ResultEnum.RESULT_EMPTY);
        }
        if (user.getApprove() == 1 || user.getAdminEntry() == 1) {
            return ResultVOUtil.success(true);
        }
        return ResultVOUtil.success(false);
    }


    @PutMapping("/auth")
    private ResultVO saveOrganization(HttpServletRequest request,@Valid UserApproveForm userApproveForm, BindingResult bindingResult) {

        //验证表单数据是否正确
        if (bindingResult.hasErrors()) {
            log.error("【组织认证】参数不正确,userApproveForm={}", userApproveForm);
            throw new PetHomeException(ResultEnum.PARAM_ERROR.getCode(), bindingResult.getFieldError().getDefaultMessage());
        }

        Integer code = userApproveForm.getMessageCode();
        String mobile = userApproveForm.getDutyerPhone();
        String openId = userApproveForm.getUserId();

        HttpSession session = request.getSession();
        Map<String, String> userSms = (Map<String, String>) session.getAttribute(openId);
        if (userSms == null || !code.equals(userSms.get("code")) || mobile.equals(userSms.get("mobile"))) {
            throw new PetHomeException(ResultEnum.RESULT_EMPTY.getCode(), "验证码已过期,请稍后再获取");
        }

        UserBasic user = userBasicRepository.findByOpenId(openId);
        if (user == null) {
            throw new PetHomeException(ResultEnum.RESULT_EMPTY.getCode(), "没有该用户");
        }

        userService.saveOrganization(userApproveForm);

        return null;
    }

    /**
     * 获取短信验证码
     */
    @GetMapping("/sms/{openId}")
    private ResultVO getShortMessage(@PathVariable("openId") String openId,
                                     HttpServletRequest request,
                                     @RequestParam("mobile") String mobile) {

        boolean chinaPhoneLegal = PhoneFormatCheckUtil.isChinaPhoneLegal(mobile);
        if (!chinaPhoneLegal) {
            throw new PetHomeException(ResultEnum.PARAM_ERROR.getCode(), "手机号码不正确");
        }

        Integer code = MathUtil.getRandomNumber();
        String massage = "【宠爱有家】您的验证码：" + code + "，请在10分钟内按页面提示提交验证码";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> p = new LinkedMultiValueMap<>();
        p.add("account", smsConfig.getAccount());
        p.add("pswd", smsConfig.getPassword());
        p.add("mobile", mobile);
        p.add("msg", massage);

        HttpEntity<MultiValueMap<String, String>> entity = new HttpEntity<>(p, headers);
        String smsResult = restTemplate.postForObject(smsConfig.getUrl(), entity, String.class);

        String hasError = smsResult.split(",")[1];
        if (!String.valueOf(0).equals(hasError)) {
            throw new PetHomeException(ResultEnum.PARAM_ERROR.getCode(), "发送短信验证码失败!");
        }

        HttpSession session = request.getSession();
        Map<String, String> userSms = new HashMap<>();
        userSms.put("code", code.toString());
        session.setAttribute(openId, userSms);
        session.setMaxInactiveInterval(1 * 60);
        return ResultVOUtil.success(true);
    }

    /**
     * 验证短信验证码
     */
    @PostMapping("/sms/{openId}")
    private ResultVO checkShortMessage(HttpServletRequest request,
                                       @PathVariable("openId") String openId,
                                       @RequestParam("code") String code,
                                       @RequestParam("mobile") String mobile) {

        HttpSession session = request.getSession();
        Map<String, String> userSms = (Map<String, String>) session.getAttribute(openId);
        if (userSms == null || !code.equals(userSms.get("code")) || mobile.equals(userSms.get("mobile"))) {
            throw new PetHomeException(ResultEnum.RESULT_EMPTY.getCode(), "验证码已过期,请稍后再获取");
        }

        UserBasic user = userBasicRepository.findByOpenId(openId);
        if (user == null) {
            throw new PetHomeException(ResultEnum.RESULT_EMPTY.getCode(), "没有该用户");
        }
        user.setMobile(String.valueOf(mobile));
        user.setApprove(BooleanEnum.TRUE.getCode());
        userBasicRepository.save(user);
        session.removeAttribute(openId);
        return ResultVOUtil.success(true);
    }

}


