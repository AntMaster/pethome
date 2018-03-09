package com.shumahe.pethome.Domain;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.util.Date;

@Entity
@Data
public class UserPet {

    @Id
    @GeneratedValue
    private Integer id;

    private Integer userId;

    private String headImageUrl;

    private String nickName;

    private Integer classifyId;

    private Integer varietyId;

    private Integer sex;

    private Date birthday;

    private Integer contraceptionState;

    private String description;

    private String chipNo;

}
