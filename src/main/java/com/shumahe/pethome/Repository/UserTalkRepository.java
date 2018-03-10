package com.shumahe.pethome.Repository;

import com.shumahe.pethome.Domain.PublishTalk;
import com.shumahe.pethome.Domain.UserTalk;

import org.apache.catalina.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;


public interface UserTalkRepository extends JpaRepository<UserTalk, Integer> {

    /**
     * 根据openID查询我的私信
     *
     * @param openIdFrom
     * @param openIdAccept
     * @return
     */
    @Query(value = "SELECT * FROM UserTalk " +
            "WHERE PublishID IN (SELECT  DISTINCT PublishID FROM UserTalk WHERE UserIDFrom = ?1 OR UserIDAccept = ?2) AND ShowState = 1 " +
            "ORDER BY LASTModify DESC,TalkTime", nativeQuery = true)
    List<UserTalk> findPrivateMyTalk(String openIdFrom, String openIdAccept);


    /**
     * 查询一个主题的所有互动信息
     *
     * @param publishId
     * @return
     */
    @Query(value = "select * from usertalk where lastmodify in (select lastmodify from usertalk where publishid = ?1 and  useridfrom = ?2 AND useridaccept = ?3 ) order by talktime;  ", nativeQuery = true)
    List<UserTalk> findRecord(int publishId, String userIdFrom, String userIdAccept);


    /**
     * 查询私信条数
     *
     * @param publishId
     * @return
     */
    @Query(value = "select count(id) from UserTalk  where publishId = ?1 AND  showstate = 1", nativeQuery = true)
    int findPrivateMsgCount(Integer publishId);


    /**
     * 发布者查询    该主题私信消息
     *
     * @param publishId
     * @param
     * @return
     */
    List<UserTalk> findByPublishIdOrderByTalkId(Integer publishId);

    /**
     * 非发布者查询   该主题私信消息
     *
     * @param publishIdA
     * @param publishIdB
     * @param userIdFrom
     * @return
     */
    @Query(value = "SELECT * FROM usertalk where publishId = ?1 AND talkId in (select top(1) talkId from UserTalk  where publishId = ?2 AND useridfrom = ?3 ORDER  BY talktime ) AND showstate = 1;", nativeQuery = true)
    List<UserTalk> findByPublishIdAndTalkIdOrderByTalkTime(Integer publishIdA,Integer publishIdB,  String userIdFrom);


    /**
     * 根据openID查询我未读私信
     *
     * @param openIdFrom
     * @param openIdAccept
     * @return
     */
/*
    @Query(value = "SELECT * FROM UserTalk " +
            "WHERE PublishID IN (SELECT  DISTINCT PublishID FROM UserTalk WHERE UserIDFrom = ?1 OR UserIDAccept = ?2) AND ShowState = 1 AND  ReadState = 0" +
            "ORDER BY LASTModify DESC,TalkTime", nativeQuery = true)
    List<UserTalk> findMyNotViewTalk(String openIdFrom, String openIdAccept);
*/


}
