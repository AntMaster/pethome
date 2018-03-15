package com.shumahe.pethome.Controller;

import com.shumahe.pethome.Util.ResultVOUtil;
import com.shumahe.pethome.VO.ResultVO;
import lombok.extern.slf4j.Slf4j;
import me.chanjar.weixin.common.bean.WxJsapiSignature;
import me.chanjar.weixin.common.exception.WxErrorException;
import me.chanjar.weixin.mp.api.WxMpService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;



@RestController
@RequestMapping("/entry")
@Slf4j
public class check {


    @Autowired
    private WxMpService wxMpService;

    /**
     * @param signature
     * @param timestamp
     * @param nonce
     * @param echostr
     * @return
     */
    @GetMapping(path = "/wechat")
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


    @GetMapping("/forward")
    public ResultVO forward(@RequestParam(name = "url") String url)  {

        WxJsapiSignature jsapiSignature = null;
        try {
            jsapiSignature = wxMpService.createJsapiSignature(url);
        } catch (WxErrorException e) {
            e.printStackTrace();
        }
        return ResultVOUtil.success(jsapiSignature);
    }
}

