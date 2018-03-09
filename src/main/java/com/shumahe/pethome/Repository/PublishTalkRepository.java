package com.shumahe.pethome.Repository;


import com.shumahe.pethome.Domain.PublishTalk;

import com.shumahe.pethome.Domain.UserTalk;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;


import java.util.List;


public interface PublishTalkRepository extends JpaRepository<PublishTalk, Integer>, JpaSpecificationExecutor {


    /**
     * 查询我互动过的主题
     *
     * @param replierFrom
     * @param replierAccept
     * @param publisher
     * @return
     */
    List<PublishTalk> findByReplierFromOrReplierAcceptOrPublisherId(String replierFrom, String replierAccept, String publisher);


    /**
     * 查询我的全部互动详情
     *
     * @param publishIds
     * @return
     */
    @Query(value = "SELECT * FROM PublishTalk WHERE PublishID IN ?1 AND ShowState = 1 ORDER BY LASTModify DESC,ReplyDate", nativeQuery = true)
    List<PublishTalk> findMyPublicTalk(List<Integer> publishIds);



    /**
     * 跟我互动过的人
     * @param publishIds
     * @param openId
     * @return
     */
    @Query(value = "SELECT DISTINCT ReplierFrom FROM PublishTalk WHERE PublishID IN (?1) AND ReplierFrom <> ?2 AND ShowState = 1 GROUP BY ReplierFrom,PublishID;", nativeQuery = true)
    List<PublishTalk> findMyPublicTalker(List<Integer> publishIds,String openId);



    /**
     * 根据发布ID查询评论数量
     *
     * @param publishId
     * @return
     */
    @Query(value = "SELECT PublishID,COUNT(PublishID) AS CommentCount FROM PublishTalk " +
            "WHERE ReplierFrom IS NOT NULL AND ReplierArrive IS NULL AND PublishID IN (?1) GROUP BY PublishID ORDER BY PublishID DESC", nativeQuery = true)
    List<Object[]> findPublishCommentCount(List<Integer> publishId);


    /**
     * 根据openID查询我未读留言互动
     *
     * @param openIdFrom
     * @param openIdAccept
     * @return
     */
    @Query(value = "SELECT * FROM UserTalk WHERE PublishID IN (?) AND ShowState = 1 AND  ReadState = 0 ORDER BY LASTModify DESC,TalkTime", nativeQuery = true)
    List<UserTalk> findMyNotViewTalk(String openIdFrom, String openIdAccept);



    /**
     * 查询当前互动所有来往记录
     * @param publishId
     * @return
     */
    List<PublishTalk> findByPublishIdIn(int publishId);



    /**
     * 通过发布ID查询评论
     *
     * @return
     */
    List<PublishTalk> findByPublishIdOrderByPublishId(int publishId);

    /**
     * 通过发布ID查询评论条数
     * where：
     * ReplierIdIsNotNull  &   NextReplierIdIsNull &   PublishIdIn
     * order:
     * PublishId
     *
     * @return
     */
    //Page<PublishTalk> findByReplierIdIsNotNullAndNextReplierIdIsNullAndPublishIdInOrderByPublishIdDesc(List<Integer> publishIds, Pageable pageable);

/*
    @Query(value = "SELECT PublishID,COUNT(PublishID) AS CommentCount FROM PublishTalk " +
            "WHERE ReplierFrom IS NOT NULL AND ReplierArrive IS NULL AND PublishID IN (?1) GROUP BY PublishID ORDER BY PublishID DESC",nativeQuery = true)
    List<Object[]> findPublishCommentCount1(List<Integer> publishId);
*/
/*

    String a = "SELECT PublishID,COUNT(PublishID) AS CommentCount FROM PublishTalk " +
            "WHERE ReplierFrom IS NOT NULL AND ReplierArrive IS NULL AND PublishID IN (?1) GROUP BY PublishID ORDER BY PublishID DESC";
*/



}
