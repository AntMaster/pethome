package com.shumahe.pethome.Domain;

import lombok.Data;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.util.Date;

@Entity
@Data
@EntityListeners(AuditingEntityListener.class)
@Table(name = "UserPetAlbum")
public class UserPetAlbum {

    @Id
    @GeneratedValue()
    private Integer id;

    @Column(name = "petname")
    private Integer petName;


    @Column(name = "name")
    private String name;


    @Column(name = "description")
    private String description;


    @Column(name = "show")
    private Integer show;

    @CreatedDate
    @Column(name = "createtime")
    private Date createTime;


}
