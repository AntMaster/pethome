package com.shumahe.pethome.Form;


import lombok.Data;
import org.hibernate.validator.constraints.NotBlank;

import javax.validation.constraints.NotNull;

@Data
public class UserPetForm {

    @NotBlank(message = "用户主键必填")
    private String userId;


    @NotNull(message = "爱宠类别必填")
    private Integer classifyId;


    @NotBlank(message = "爱宠头像必填")
    private int headImgUrl;


    @NotBlank(message = "爱宠昵称必填")
    private String nickName;


    @NotBlank(message = "爱宠性别必填")
    private String sex;


    @NotBlank(message = "爱宠生日必填")
    private String birthday;


    @NotBlank(message = "爱宠小特点必填")
    private String description;


    private String chipNo;

}
