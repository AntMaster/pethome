var app = new Vue({
    el: "#detailPage",
    data: {

        //消息类型：互动/私信
        msgListType: "interactMsg",
        //是否认证
        isAuthority: false,
        petFindState: 2,
        imgList: [],
        //当前展示列表
        showMsgList: [],
        detailData: {},
        commentContent: ''

    },
    mounted: function () {
        //初始化
        this.init();
    },
    methods: {
        init: function () {

            $.ajax({
                url: '/pethome/publish/detail/' + GetQueryString("openid"),
                type: 'GET',
                contentType: "application/x-www-form-urlencoded",
                dataType: 'json',
                data: {
                    id: GetQueryString("id")
                },
                success: function (res) {
                    if (res.code === 1) {

                        app.detailData = res.data;
                        app.showMsgList = res.data.publicTalk;

                    } else {
                        alert(res.msg);
                    }
                }
            });
        },
        //找到了
        found: function (publisherId) {

            if (GetQueryString("openid") != publisherId) {
                alert("您不是主题发布者,不能操作此选项");
            }

            $.ajax({
                url: '/pethome/publish/pet/find/' + GetQueryString("openid"),
                type: 'POST',
                dataType: 'json',
                data: {
                    id: GetQueryString("id")
                },
                success: function (res) {
                    if (res.code) {
                        console.log(res)
                    }
                }
            });
        },
        //展开所有评论
        expand: function () {

        },
        //分享
        share: function () {

        },
        //关注
        attention: function () {

        },
        //切换消息列表
        changeList: function (type) {
            var data = null;
            var url = null;
            if (type == 0) {
                this.msgListType = "interactMsg"
                url = '/pethome/message/public/' + GetQueryString("id");

            } else {

                this.msgListType = "privateMsg"
                url = '/pethome/message/private/' + GetQueryString("id");
                data = {openId: GetQueryString("openid")}//私信
            }

            $.ajax({
                url: url,
                type: 'GET',
                dataType: 'json',
                data: data,
                success: function (res) {


                    if (res.code === 1) {

                        var result = null;
                        if (type == 0) {
                            result = res.data.publicTalk;
                        } else {
                            result = res.data.privateTalk;
                        }

                        if (!result) { //没有值
                            if (!app.showMsgList) {
                                app.showMsgList = new Array();
                            }
                            app.showMsgList.push([res.data]);

                        } else {//有值

                            app.showMsgList = result;
                        }
                    } else if (res.code == 2) {

                        app.showMsgList = [];

                    } else {
                        alert(res.msg);
                    }
                }
            });


        },
        //发送评论
        sendComment: function () {

            if (!this.commentContent) {
                alert("请输入回复内容");
                return;
            }

            var data = null;
            var url = null;

            if (this.msgListType == "interactMsg") {

                this.msgListType = "interactMsg";
                url = '/pethome/message/public/' + GetQueryString("id");
                data = {
                    replierFrom: GetQueryString("openid"),
                    replierAccept : GetQueryString("openid"),
                    content: this.commentContent,
                }

            } else {

                this.msgListType = "privateMsg";
                url = '/pethome/message/private/' + GetQueryString("id")
                data = {
                    userIdFrom: GetQueryString("openid"),
                    userIdAccept: this.detailData.publisherId,
                    content: this.commentContent
                }
            }

            $.ajax({
                url: url,
                type: 'PUT',
                dataType: 'json',
                data: data,
                success: function (res) {

                    if (res.code == 1) {

                        if (!app.showMsgList) {
                            app.showMsgList = new Array();
                        }
                        app.showMsgList.push([res.data]);
                        app.commentContent = '';
                        if (app.msgListType == "interactMsg")
                            app.detailData.publicMsgCount++;
                    } else {
                        alert(res.msg);
                    }
                }
            });
        },

        //回复评论
        reply: function (index, talkId, replierFrom) {
            $.modal({
                title: '回复',
                text: '',
                afterText: '<textarea class="reply-modal-textarea" id="replayContent" placehodler="这里输入回复内容"></textarea>',
                buttons: [{
                    text: '确定',
                    onClick: function () {

                        var content = $("#replayContent").val();

                        if (!content) {
                            alert("请输入回复内容");
                            return;
                        }

                        var data = null;
                        var url = null;
                        if (app.msgListType == "interactMsg") {
                            url = '/pethome/message/public/' + GetQueryString("id");
                            data = {
                                talkId: talkId,
                                replierFrom: GetQueryString("openid"),
                                replierAccept: replierFrom,
                                content: content
                            }
                        } else {

                            url = '/pethome/message/private/' + GetQueryString("id");
                            data = {
                                talkId: talkId,
                                userIdFrom: GetQueryString("openid"),
                                userIdAccept: replierFrom,
                                content: content
                            }
                        }

                        $.ajax({
                            url: url,
                            type: 'PUT',
                            dataType: 'json',
                            data: data,
                            success: function (res) {
                                if (res.code === 1) {
                                    app.showMsgList[index].push(res.data);
                                } else {
                                    alert(res.msg);
                                }
                            }
                        });
                    }
                }]
            })
        }
    }
});