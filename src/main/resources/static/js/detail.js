var app = new Vue({
    el: "#detailPage",
    data: {

        //消息类型：互动/私信
        msgListType: true,
        //是否认证
        isAuthority: false,
        petFindState: 2,
        imgList: [],
        //当前展示列表
        showMsgList: [],
        //互动列表
        //interactmsgList: [],
        //私信列表
        //privateMsgList: [],
        detailData: {},
        commentContent:''

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
        //展开所有评论
        expand: function () {

        },
        //分享
        share: function () {

        },
        //关注
        attention: function () {

        },
        reply: function (index) {
            $.modal({
                title: '回复',
                text: '',
                afterText: '<textarea class="reply-modal-textarea" placehodler="这里输入回复内容"></textarea>',
                buttons: [{
                    text: '确定',
                    onClick: function () {

                    }
                }]
            })
        },
        //发送评论
        sendComment: function () {
            if(!this.commentContent){
                alert("请输入回复内容");
                return;
            }
            $.ajax({
                url: '/pethome/message/public/' + GetQueryString("id"),
                type: 'PUT',
                contentType: "application/x-www-form-urlencoded",
                dataType: 'json',
                data: {
                    replierFrom: GetQueryString("openid"),
                    content: this.commentContent
                },
                success: function (res) {
                    if (res.code === 1) {
                        //app.detailData = res.data;
                       /* if(!app.showMsgList){
                            app.showMsgList = new Array();

                        }*/
                        app.showMsgList[0].push(res.data);
                    } else {
                        alert(res.msg);
                    }
                }
            });
        },
        //切换消息列表
        changeList: function () {
            this.msgListType = !this.msgListType;
            if (this.msgListType) {
                //加载互动列表
            } else {
                //加载私信列表
            }
        }
    }
});