package com.shumahe.pethome.Repository;

import com.shumahe.pethome.Domain.UserTalk;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;


public interface UserTalkRepository extends JpaRepository<UserTalk, Integer> {

    /**
     * 我的   私信
     *
     * @param openIdFrom
     * @param openIdAccept
     * @return
     */
    @Query(value = "SELECT * FROM UserTalk WHERE PublishID IN (SELECT  DISTINCT PublishID FROM UserTalk WHERE UserIDFrom = ?1 OR UserIDAccept = ?2) AND ShowState = 1 ORDER BY LASTModify DESC,TalkTime", nativeQuery = true)
    List<UserTalk> findPrivateMyTalk(String openIdFrom, String openIdAccept);


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
    List<UserTalk> findByPublishIdAndTalkIdOrderByTalkTime(Integer publishIdA, Integer publishIdB, String userIdFrom);


    /**
     * 一个主题 私信条数(未读 &&  已读)
     *
     * @param publishId
     * @return
     */
    @Query(value = "select count(id) from UserTalk  where publishId = ?1 AND  showstate = 1", nativeQuery = true)
    int findPrivateMsgCount(Integer publishId);


    /**
     * 我的 未读/已读私信
     * @param openId
     * @param readState
     * @return
     */
    @Query(value = "select count(id) from UserTalk  where publishId = ?1 AND  showstate = ?2", nativeQuery = true)
    int notReadTalksCount(String openId,Integer readState);





}
