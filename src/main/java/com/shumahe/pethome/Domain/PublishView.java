package com.shumahe.pethome.Domain;


import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.Table;
import java.util.Date;

/*@Entity
@Data
@Table(name = "publishtalk")*/
public class PublishView {

    /**
     * id
     */
    private Integer id;

    /**
     * 浏览者
     */
    private Integer viewer;


    /**
     * 浏览时间
     */
    private Date viewTime;


    /**
     * 浏览主题
     */
    private Integer publishId;


    /**
     * 发布人ID
     */
    private Integer publisherId;


}
