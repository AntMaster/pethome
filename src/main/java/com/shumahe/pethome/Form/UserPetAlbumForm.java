package com.shumahe.pethome.Form;

import lombok.Data;
import org.hibernate.validator.constraints.NotBlank;

import javax.validation.constraints.NotNull;

@Data
public class UserPetAlbumForm {


    @NotBlank(message = "宠物主键不能为空")
    private Integer petId;


    @NotBlank(message = "相册不能为空")
    private String name;


    private String description;


}
