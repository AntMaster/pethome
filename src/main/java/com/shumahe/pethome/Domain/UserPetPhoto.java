package com.shumahe.pethome.Domain;

import lombok.Data;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;

@Entity
@Data
@Table(name = "userpetphoto")
@EntityListeners(AuditingEntityListener.class)
public class UserPetPhoto {

    @Id
    @GeneratedValue
    private Integer id;

    @Column(name = "petid")
    private Integer petId;

    @Column(name = "name")
    private String name;

    @Column(name = "description")
    private String description;

    @Column(name = "show")
    private Integer show;

    @CreatedDate
    @Column(name = "createtime")
    private Integer createTime;

}
