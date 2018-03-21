var app = new Vue({
    el: "#minePage",
    data: {
        myself: {},
        param: '',
        privateMsgArr: []
    },
    mounted: function () {
        this.loadMine();
    },
    created: function () {
        this.param = GetQueryString("openid");
    },
    methods: {
        loadMine: function () {

            $.ajax({
                url: '/pethome/user/' + GetQueryString("openid"),
                type: 'GET',
                dataType: 'json',
                data: null,
                success: function (res) {
                    if (res.code === 1) {
                        app.myself = res.data;
                    }
                }
            });
        }
    }
});
// $.init();
$(document).on("pageInit", function (e, pageId, $page) {

    if (pageId == "taskPage") {

        //我的任务
        var task = new Vue({
            el: "#taskPage",
            data: {
                isPend: true,
                taskArr: []
            },
            mounted: function () {
                //我的未找到
                $.ajax({
                    url: '/pethome/publish/task/' + GetQueryString("openid"),
                    type: 'GET',
                    dataType: 'json',
                    data: null,
                    success: function (res) {
                        if (res.code === 1) {
                            task.taskArr = res.data;
                            for (var i = 0; i < task.taskArr.length; i++) {
                                task.taskArr[i].petImage = task.taskArr[i].petImage.split(";")[0];
                            }
                        }
                    }
                });
            },

            methods: {

                waitDeal: function () {
                    this.isPend = true;

                    $.ajax({
                        url: '/pethome/publish/task/' + GetQueryString("openid"),
                        type: 'GET',
                        dataType: 'json',
                        data: null,
                        success: function (res) {
                            if (res.code === 1) {
                                task.taskArr = res.data;
                                for (var i = 0; i < task.taskArr.length; i++) {
                                    task.taskArr[i].petImage = task.taskArr[i].petImage.split(";")[0];
                                }
                            }
                        }
                    });
                },
                myPublish: function () {

                    this.isPend = false;
                    //我的未找到
                    $.ajax({
                        url: '/pethome/publish/' + GetQueryString("openid"),
                        type: 'GET',
                        dataType: 'json',
                        data: null,
                        success: function (res) {
                            if (res.code === 1) {
                                task.taskArr = res.data;
                                for (var i = 0; i < task.taskArr.length; i++) {
                                    task.taskArr[i].petImage = task.taskArr[i].petImage.split(";")[0];
                                }
                            }
                        }
                    });
                },
                findPet: function (index) {

                    if (this.taskArr[index].findState) {
                        return;
                    }

                    $.ajax({
                        url: '/pethome/publish/pet/find/' + GetQueryString("openid"),
                        type: 'POST',
                        dataType: 'json',
                        data: {
                            id: this.taskArr[index].id
                        },
                        success: function (res) {
                            if (res.code) {
                                task.taskArr[index].findState = res.data.findState;
                            }
                        }
                    });

                }
            }
        });
    }

    if (pageId == "msgPage") {

        var message = new Vue({
            el: "#msgPage",
            data: {
                navActive: true,
                taskArr: []
            },
            mounted: function () {
                $.init();
            },
            methods: {
                navChange: function (t) {
                    this.navActive = !this.navActive;
                }
            }
        });
    }

    if (pageId == "interactPage") {
        var interact = new Vue({
            el: "#interactPage",
            data: {},
            mounted: function () {

            },
            methods: {
                reply: function () {
                    $.modal({
                        title: '回复',
                        afterText: '<div class=""><textarea class="modal-reply-input"></textarea></div>',
                        buttons: [
                            {
                                text: '取消'
                            },
                            {
                                text: '确认',
                                bold: true,
                                onClick: function () {
                                    //发送回复
                                }
                            },
                        ]
                    })
                }
            }
        });
    }

    if (pageId == 'attentionPage') {
        var message = new Vue({
            el: "#attentionPage",
            data: {
                navActive: true,
                taskArr: []
            },
            mounted: function () {
                $.init();
            },
            methods: {
                navChange: function (t) {
                    this.navActive = !this.navActive;
                }
            }
        });
    }

    if (pageId == 'chatPage') {
        var interact = new Vue({
            el: "#chatPage",
            data: {
                showMsgList:[]
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
                reply: function () {
                    $.modal({
                        title: '回复',
                        afterText: '<div class=""><textarea class="modal-reply-input"></textarea></div>',
                        buttons: [
                            {
                                text: '取消'
                            },
                            {
                                text: '确认',
                                bold: true,
                                onClick: function () {
                                    //发送回复
                                }
                            },
                        ]
                    })
                }
            }
        });
    }
});