package com.shumahe.pethome.Controller;


import com.google.gson.JsonObject;
import com.shumahe.pethome.Config.ProjectUrlConfig;
import com.shumahe.pethome.Config.WechatAccountConfig;
import com.shumahe.pethome.Domain.MemberTagsMapping;
import com.shumahe.pethome.Domain.UserBasic;
import com.shumahe.pethome.Enums.MemberTagsEnum;
import com.shumahe.pethome.Enums.ResultEnum;
import com.shumahe.pethome.Exception.PetHomeException;
import com.shumahe.pethome.Repository.MemberTagsMappingRepository;
import com.shumahe.pethome.Repository.UserBasicRepository;
import com.shumahe.pethome.Util.ResultVOUtil;
import com.shumahe.pethome.VO.ResultVO;
import lombok.extern.slf4j.Slf4j;
import me.chanjar.weixin.common.api.WxConsts;
import me.chanjar.weixin.common.bean.WxJsapiSignature;
import me.chanjar.weixin.common.bean.menu.WxMenu;
import me.chanjar.weixin.common.bean.menu.WxMenuButton;
import me.chanjar.weixin.common.exception.WxErrorException;
import me.chanjar.weixin.mp.api.WxMpInMemoryConfigStorage;
import me.chanjar.weixin.mp.api.WxMpService;
import me.chanjar.weixin.mp.bean.menu.WxMpMenu;
import me.chanjar.weixin.mp.bean.result.WxMpOAuth2AccessToken;
import me.chanjar.weixin.mp.bean.result.WxMpUser;
import net.minidev.json.JSONObject;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Arrays;
import java.util.List;


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


    @Autowired
    private  MemberTagsMappingRepository memberTagsMappingRepository;

    /**
     * 服务器配置
     *
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

    /**
     * JSAPI
     *
     * @param url
     * @return
     */
    @GetMapping("/jsApiSignature")
    @ResponseBody
    public ResultVO forward(@RequestParam(name = "url") String url) {

        WxJsapiSignature jsapiSignature = null;
        try {
            jsapiSignature = wxMpService.createJsapiSignature(url);
        } catch (WxErrorException e) {
            throw new PetHomeException(ResultEnum.WECHAT_MP_ERROR.getCode(), e.getMessage());
        }
        return ResultVOUtil.success(jsapiSignature);
    }

    /**
     * 网页授权
     *
     * @param returnUrl
     * @return
     */
    @GetMapping("/webAuth")
    public String authorize(@RequestParam("returnUrl") String returnUrl) {


        //1. 配置
        //2. 调用方法
        String url = projectUrlConfig.getWechatMpAuthorize() + "/pethome/wechat/userinfo";

        String redirectUrl;
        try {
            redirectUrl = wxMpService.oauth2buildAuthorizationUrl(url, WxConsts.OAuth2Scope.SNSAPI_USERINFO, URLEncoder.encode(returnUrl, "UTF-8"));
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
    /**
     * 网页授权
     *
     * @param code
     * @param returnUrl
     * @return
     * @throws WxErrorException
     */
    @GetMapping("/userinfo")
    public String userInfo(@RequestParam("code") String code,
                           @RequestParam("state") String returnUrl) throws WxErrorException {

        WxMpOAuth2AccessToken wxMpOAuth2AccessToken = wxMpService.oauth2getAccessToken(code);//如果code使用过，5分钟内再次使用会报错。
        WxMpUser user = wxMpService.oauth2getUserInfo(wxMpOAuth2AccessToken, null);


        saveUser(user);
        return "redirect:" + returnUrl + "?openid=" + wxMpOAuth2AccessToken.getOpenId();
    }

    @Transactional
    protected  void saveUser(WxMpUser user) {

        UserBasic userBasic = userBasicRepository.findByAppIdAndOpenId(wechatAccountConfig.getMpAppId(), user.getOpenId());
        if (userBasic == null) {
            userBasic = new UserBasic();
            userBasic.setOpenId(user.getOpenId());
            userBasic.setAppId(wechatAccountConfig.getMpAppId());
        }

        userBasic.setNickName(user.getNickname());
        userBasic.setHeadImgUrl(user.getHeadImgUrl());
        userBasic.setSex(user.getSexId());
        UserBasic save = userBasicRepository.save(userBasic);

        MemberTagsMapping tagsMapping = new MemberTagsMapping();
        tagsMapping.setMemberId(save.getId());
        tagsMapping.setTagId(MemberTagsEnum.Volunteer.getCode());
        memberTagsMappingRepository.save(tagsMapping);

    }

    @GetMapping("/menu")
    @ResponseBody
    public ResultVO findWxMenu() {
        try {
            WxMpMenu wxMpMenu = wxMpService.getMenuService().menuGet();
            return ResultVOUtil.success(wxMpMenu);

        } catch (WxErrorException e) {
            throw new PetHomeException(ResultEnum.WECHAT_MP_ERROR.getCode(), e.getMessage());
        }
    }

    @PostMapping("/menu")
    public ResultVO saveWxMenu() {
        try {

            WxMenuButton button = new WxMenuButton();
            button.setName("宠爱有家");
            button.setType("view");
            button.setUrl("http://girl.nat300.top/pethome/wechat/webAuth?returnUrl=http://girl.nat300.top/pethome/index.html");
            List<WxMenuButton> wxMenuButtons = Arrays.asList(button);

            WxMenu wxMenu = new WxMenu();
            wxMenu.setButtons(wxMenuButtons);

            wxMpService.getMenuService().menuCreate(wxMenu);
            return ResultVOUtil.success(wxMenu);

        } catch (WxErrorException e) {
            throw new PetHomeException(ResultEnum.WECHAT_MP_ERROR.getCode(), e.getMessage());
        }
    }
}
