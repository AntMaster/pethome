package com.shumahe.pethome.Controller;


import com.shumahe.pethome.Config.ProjectUrlConfig;
import com.shumahe.pethome.Config.WechatAccountConfig;
import com.shumahe.pethome.Domain.UserBasic;
import com.shumahe.pethome.Enums.ResultEnum;
import com.shumahe.pethome.Enums.SexEnum;
import com.shumahe.pethome.Exception.PetHomeException;
import com.shumahe.pethome.Repository.UserBasicRepository;
import com.shumahe.pethome.Util.ResultVOUtil;
import com.shumahe.pethome.VO.ResultVO;
import lombok.extern.slf4j.Slf4j;
import me.chanjar.weixin.common.api.WxConsts;
import me.chanjar.weixin.common.bean.WxJsapiSignature;
import me.chanjar.weixin.common.bean.menu.WxMenu;
import me.chanjar.weixin.common.exception.WxErrorException;
import me.chanjar.weixin.mp.api.WxMpInMemoryConfigStorage;
import me.chanjar.weixin.mp.api.WxMpService;
import me.chanjar.weixin.mp.bean.menu.WxMpMenu;
import me.chanjar.weixin.mp.bean.result.WxMpOAuth2AccessToken;
import me.chanjar.weixin.mp.bean.result.WxMpUser;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.List;

import static org.hibernate.validator.internal.util.Contracts.assertNotNull;

/**
 * Created by zhangyu
 */
@Controller
@RequestMapping("/wechat")
@Slf4j
public class WechatController {
    //http://girl.nat300.top/pethome/wechat/authorize?returnUrl=http://girl.nat300.top/pethome/index.html
    //http://girl.nat300.top/pethome/index.html?openid=oCLNDwc8bUgjnBUibOX1yfPh5Ni0
    @Autowired
    private WxMpService wxMpService;

    @Autowired
    private ProjectUrlConfig projectUrlConfig;

    @Autowired
    private UserBasicRepository userBasicRepository;

    @Autowired
    private WechatAccountConfig wechatAccountConfig;


    /**
     * @param signature
     * @param timestamp
     * @param nonce
     * @param echostr
     * @return
     */
    @GetMapping(path = "/serverConfig")
    @ResponseBody
    public String authGet(@RequestParam(name = "signature") String signature,
                          @RequestParam(name = "timestamp") String timestamp,
                          @RequestParam(name = "nonce") String nonce,
                          @RequestParam(name = "echostr") String echostr) {


        log.info("\n接收到来自微信服务器的认证消息：[{}, {}, {}, {}]", signature, timestamp, nonce, echostr);

        if (StringUtils.isAnyBlank(signature, timestamp, nonce, echostr)) {
            throw new IllegalArgumentException("请求参数非法，请核实!");
        }

        if (wxMpService.checkSignature(timestamp, nonce, signature)) {
            return echostr;
        }

        return "非法请求";
    }


    @GetMapping("/jsApiSignature")
    @ResponseBody
    public ResultVO forward(@RequestParam(name = "url") String url)  {

        WxJsapiSignature jsapiSignature = null;
        try {
            jsapiSignature = wxMpService.createJsapiSignature(url);
        } catch (WxErrorException e) {
            e.printStackTrace();
        }
        return ResultVOUtil.success(jsapiSignature);
    }


    @GetMapping("/webAuth")
    public String authorize() {//@RequestParam("returnUrl") String returnUrl


        //1. 配置
        //2. 调用方法
        String url = projectUrlConfig.getWechatMpAuthorize() + "/pethome/wechat/userinfo";

        String redirectUrl;
        try {
            redirectUrl = wxMpService.oauth2buildAuthorizationUrl(url, WxConsts.OAuth2Scope.SNSAPI_USERINFO, URLEncoder.encode("http://girl.nat300.top/pethome/index.html", "UTF-8"));
        } catch (UnsupportedEncodingException e) {
            log.error("【有异常】{}", e);
            throw new PetHomeException(999, e.getMessage());
        }
        return "redirect:" + redirectUrl;
    }

    /**
     * code仅可以使用1次，第一次获取到的code使用过之后，传去别的地方用就会报这个错。要想再用，就得在代码中重新构建微信请求连接去请求获得新的code（所以5分钟之内有两次请求code的就会报）
     * <p>
     * 关于网页授权access_token和普通access_token的区别
     * 1、微信网页授权是通过OAuth2.0机制实现的，在用户授权给公众号后，公众号可以获取到一个网页授权特有的接口调用凭证（网页授权access_token），通过网页授权access_token可以进行授权后接口调用，如获取用户基本信息；
     *
     * @param code
     * @param returnUrl
     * @return
     */
    @GetMapping("/userinfo")
    public String userInfo(@RequestParam("code") String code,
                           @RequestParam("state") String returnUrl) throws WxErrorException {


        WxMpOAuth2AccessToken wxMpOAuth2AccessToken;
        WxMpUser user;
        try {

            wxMpOAuth2AccessToken = wxMpService.oauth2getAccessToken(code);//如果code使用过，5分钟内再次使用会报错。
            user = wxMpService.oauth2getUserInfo(wxMpOAuth2AccessToken, null);


            //saveUser(user);

        } catch (WxErrorException e) {
            log.error("【微信网页授权】{}", e);
            throw new PetHomeException(ResultEnum.WECHAT_MP_ERROR.getCode(), e.getError().getErrorMsg());
        }


        return "redirect:" + returnUrl + "?openid=" + wxMpOAuth2AccessToken.getOpenId();
    }

    private void saveUser(WxMpUser user) {

        UserBasic userBasic = new UserBasic();
        userBasic.setOpenId(user.getOpenId());
        userBasic.setAppId(wechatAccountConfig.getMpAppId());
        userBasic.setNickName(user.getNickname());
        userBasic.setHeadImgUrl(user.getHeadImgUrl());
        Integer sex = 2;
        if (user.getSex().equals(SexEnum.MALE.getMessage())) {
            sex = SexEnum.MALE.getCode();
        } else if (user.getSex().equals(SexEnum.FEMALE.getMessage())) {
            sex = SexEnum.FEMALE.getCode();
        }
        userBasic.setSex(sex);

        List<UserBasic> userBasicList = userBasicRepository.findByAppIdAndOpenId(userBasic.getAppId(), userBasic.getOpenId());
        if (userBasicList.size() == 0) {
            userBasicRepository.save(userBasic);
        }

    }


}
