var interact = new Vue({
    el: "#chatPage",
    data: {
        showMsgList :[]
    },
    mounted: function () {

        this.privateMsg();
    },

    methods: {

        privateMsg: function () {
            $.ajax({
                url: '/pethome/user/private/' + GetQueryString("openid"),
                type: 'GET',
                dataType: 'json',
                data: null,
                success: function (res) {
                    if (res.code) {
                        interact.showMsgList = res.data;
                    }
                }
            });
        },
        reply: function (index, userIdFromName, talkId, publishId) {
            $.modal({
                title: '回复',
                afterText: '<div class="" ><textarea  id = "replayContent" class="modal-reply-input"></textarea></div>',
                buttons: [
                    {
                        text: '取消'
                    },
                    {
                        text: '确认',
                        bold: true,
                        onClick: function () {

                            var content = $("#replayContent").val();
                            if (!content) {
                                alert("请输入回复内容");
                                return;
                            }

                            $.ajax({
                                url: '/pethome/message/private/' + publishId,
                                type: 'PUT',
                                dataType: 'json',
                                data: {
                                    talkId: talkId,
                                    userIdFrom: GetQueryString("openid"),
                                    userIdAccept: userIdFromName,
                                    content: content
                                },
                                success: function (res) {
                                    if (res.code === 1) {
                                        app.showMsgList.detail.push(res.data);
                                    } else {
                                        alert(res.msg);
                                    }
                                }
                            });
                        }
                    }
                ]
            })
        }
    }
});

