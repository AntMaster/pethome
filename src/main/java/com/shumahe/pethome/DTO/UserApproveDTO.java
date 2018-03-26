package com.shumahe.pethome.DTO;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.shumahe.pethome.Enums.ApproveStateEnum;
import com.shumahe.pethome.Enums.ApproveTypeEnum;
import lombok.Data;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.util.HashMap;
import java.util.Map;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserApproveDTO {


    private Integer id;


    private String userId;

    private String userImage;

    private Integer approveType ;


    private String organizationName;


    private String organizationImage;


    private String dutyer;


    private String dutyerPhone;


    private String dutyerNo;


    private String credentials;


    private Integer approveState ;


    private String description;



}
