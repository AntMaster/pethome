package com.shumahe.pethome.Form;

import lombok.Data;
import org.hibernate.validator.constraints.NotBlank;


import javax.validation.constraints.NotNull;
import java.util.Date;
@Data
public class PublishMasterForm {


    @NotBlank(message = "宠物图片petImage必填")
    private String petImage;


    @NotNull(message = "宠物类别classifyId必填")
    private int classifyId;


    @NotBlank(message = "宠物昵称petName必填")
    private String petName;


    @NotBlank(message = "发现时间findTime必填")
    private String findTime;

    @NotBlank(message = "发现地点lostLocation必填")
    private String lostLocation;


    @NotNull(message = "地点经度latitude必填")
    private float latitude;


    @NotNull(message = "地点纬度longitude必填")
    private float longitude;


    @NotBlank(message = "发现人ownerName必填")
    private String ownerName;


    @NotBlank(message = "联系方式ownerContact必填")
    private String ownerContact;

    @NotBlank(message = "openId必填")
    private String openId;

}
