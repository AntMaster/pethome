package com.shumahe.pethome.Domain;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity
@Data
public class UserApprove {

    @Id
    @GeneratedValue
    private Integer id;

    private Integer userId;

    private Integer approveType;

    private String organizationName;

    private String dutyer;

    private String dutyerPhone;

    private String dutyerNo;

    private String credentials;

    private Integer approveState;

    private String description;

}
