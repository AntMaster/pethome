var interact = new Vue({
    el: "#chatPage",
    data: {},
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

                        console.log(res);
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