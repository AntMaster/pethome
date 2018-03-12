package com.shumahe.pethome.DTO;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Data
public class UserPetAlbumDTO {


    private Integer id;

    private String name;

    private String coverPath;

    private Integer photoCount;

}
