package com.shumahe.pethome.Domain;

import com.shumahe.pethome.Enums.ApproveTypeEnum;
import com.shumahe.pethome.Enums.BooleanEnum;
import lombok.Data;

import javax.persistence.*;

@Entity
@Data
@Table(name = "member")
public class UserBasic {

    @Id
    @GeneratedValue()
    private Integer id;

    @Column(name = "nickname")
    private String nickName;

    @Column(name = "gender")
    private Integer sex;

    @Column(name = "headimgurl")
    private String headImgUrl;

    @Column(name = "openid")
    private String openId;

    @Column(name = "appid")
    private String appId;


    @Column(name = "approve")
    private Integer approve = BooleanEnum.FALSE.getCode();


}
