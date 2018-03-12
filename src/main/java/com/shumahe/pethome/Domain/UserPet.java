package com.shumahe.pethome.Domain;

import lombok.Data;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.util.Date;

/*@Entity
@Data
@Table(name = "userpet")
@EntityListeners(AuditingEntityListener.class)
*/
public class UserPet {

    @Id
    @GeneratedValue
    private Integer id;


    private Integer userId;


    private String headImgUrl;


    private String nickName;


    private Integer classifyId;


    private Integer varietyId;


    private Integer sex;


    private Date birthday;


    private Integer contraception;


    private String description;


    private String chipNo;

    /**
     * 创建时间
     */
    @CreatedDate
    @Column(name = "createtime")
    private Date createTime;


}
