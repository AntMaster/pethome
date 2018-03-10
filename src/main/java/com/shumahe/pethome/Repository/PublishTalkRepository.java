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
     *  评论排序
     *
     *  1 针对多个主题之间先后顺序排序 		最后回复时间（LastModify desc）倒序
     *  2 针对一个主题的多个评论间先后顺序排序	评论（talkId  ASC）顺序
     *  3 针对一个评论间多个互动间先后顺序排序	互动（replyDate ASC）顺序
     */

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
     *
     * 查询多个发布的所有互动信息
     * @param publishIds
     * @return
     */
    @Query(value = "SELECT * FROM PublishTalk WHERE PublishID IN ?1 AND ShowState = 1 ORDER BY LASTModify DESC,talkId ASC,ReplyDate ASC", nativeQuery = true)
    List<PublishTalk> findManyPublishTalk(List<Integer> publishIds);


    /**
     * 查询一个主题的所有互动信息
     *
     * @param publishId
     * @return
     */
    @Query(value = "SELECT * FROM PublishTalk WHERE PublishID = ?1 AND ShowState = 1 ORDER BY LASTModify DESC,talkId ASC,ReplyDate ASC", nativeQuery = true)
    List<PublishTalk> findOnePublicTalk(Integer publishId);


    /**
     * 查询多个主题的互动数量
     *
     * @param publishId
     * @return
     */
    @Query(value = "SELECT PublishID,COUNT(PublishID) AS CommentCount FROM PublishTalk WHERE PublishID IN (?1) AND ShowState = 1 GROUP BY PublishID ", nativeQuery = true)
    List<Object[]> findPublishCommentCount(List<Integer> publishId);


    /**
     * 查询一个主题的互动详情
     *
     * @param publishId
     * @return
     */
    List<PublishTalk> findByPublishId(int publishId);




    /**
     * 查询当前互动所有来往记录
     *
     * @param publishId
     * @return
     */
    List<PublishTalk> findByPublishIdIn(int publishId);

    /**
     * 查询一个发布的留言互动
     *
     * @return
     */
    List<PublishTalk> findByPublishIdOrderByLastModifyDescReplyDate(int publishId);


    /**
     * 查询评论条数
     *
     * @param publishId
     * @return
     */
    @Query(value = "select count(id) from PublishTalk  where publishId = ? AND  showstate = 1", nativeQuery = true)
    int findPublicMsgCount(Integer publishId);



    /**
     * 根据openID查询我未读留言互动
     *
     * @param openIdFrom
     * @param openIdAccept
     * @return
     */
/*
    @Query(value = "SELECT * FROM xxx WHERE PublishID IN (?) AND ShowState = 1 AND  ReadState = 0 ORDER BY LASTModify DESC,TalkTime", nativeQuery = true)
    List<UserTalk> findMyNotViewTalk(String openIdFrom, String openIdAccept);

*/

    /**
     * 跟我互动过的人
     *
     * @param publishIds
     * @param openId
     * @return
     */
/*
    @Query(value = "SELECT DISTINCT ReplierFrom FROM PublishTalk WHERE PublishID IN (?1) AND ReplierFrom <> ?2 AND ShowState = 1 GROUP BY ReplierFrom,PublishID;", nativeQuery = true)
    List<PublishTalk> findMyPublicTalker(List<Integer> publishIds, String openId);
*/


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
