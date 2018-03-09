package com.shumahe.pethome.Domain;

import com.shumahe.pethome.Enums.ReadStateEnum;
import com.shumahe.pethome.Enums.ShowStateEnum;
import lombok.Data;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.util.Date;

@Entity
@Data
@Table(name = "publishtalk")
@EntityListeners(AuditingEntityListener.class)
public class PublishTalk {

    @Id
    @GeneratedValue
    private Integer id;

    @Column(name = "publishid")
    private Integer publishId;


    @Column(name = "publisherid")
    private String publisherId;


    @Column(name = "replierfrom")
    private String replierFrom;


    @Column(name = "replieraccept")
    private String replierAccept;

    @Column(name = "content")
    private String content;


    @CreatedDate
    @Column(name = "replydate")
    private Date replyDate;


    @Column(name = "readstate")
    private Integer readState = ReadStateEnum.NOT_READ.getCode();


    @Column(name = "showstate")
    private Integer showState = ShowStateEnum.SHOW.getCode();


    @Column(name = "lastmodify")
    private Date lastModify;

}
