package com.shumahe.pethome.Form;

import lombok.Data;
import org.hibernate.validator.constraints.NotBlank;
import org.hibernate.validator.constraints.NotEmpty;


import javax.validation.constraints.NotNull;
import java.util.Date;

@Data
public class PublishPetForm {

    @NotBlank(message = "宠物昵称petName必填")
    private String petName;


    @NotNull(message = "宠物类别classifyId必填")
    private int classifyId;


    @NotNull(message = "宠物品种varietyId必填")
    private int varietyId;


    @NotBlank(message = "丢失时间lostTime必填")
    private String lostTime;


    @NotBlank(message = "丢失地点lostLocation必填")
    private String lostLocation;


    @NotNull(message = "地点经度latitude必填")
    private float latitude;


    @NotNull(message = "地点纬度longitude必填")
    private float longitude;


    @NotBlank(message = "主人姓名ownerName必填")
    private String ownerName;


    @NotBlank(message = "联系方式ownerContact必填")
    private String ownerContact;


    @NotBlank(message = "宠物图片petImage必填")
    private String petImage;


    @NotBlank(message = "openId必填")
    private String openId;

}
