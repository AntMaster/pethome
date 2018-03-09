package com.shumahe.pethome.DTO;

import lombok.Data;

@Data
public class CommentDTO {

    /**
     * 评论ID
     */
    private Integer id;

    /**
     * 发布ID
     */
    private Integer publishId;

    /**
     * 评论数量
     */
    private Integer commentCount;


    public CommentDTO(int publishId, int commentCount) {

        this.publishId = publishId;
        this.commentCount = commentCount;

    }

}
