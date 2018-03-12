package com.shumahe.pethome.Form;

import lombok.Data;
import org.hibernate.validator.constraints.NotBlank;

import javax.validation.constraints.NotNull;

@Data
public class UserPetPhotoForm {


    @NotNull(message = "相册主键不能为空")
    private Integer albumId;

    private String name;

    private String description;

    @NotBlank(message = "照片路径不能为空")
    private String path;


    private Integer cover;


}
