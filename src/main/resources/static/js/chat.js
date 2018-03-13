var interact = new Vue({
    el: "#chatPage",
    data: {
       
    },
    mounted: function () {
       
    },
    methods: {
        reply(){
            $.modal({
                title:  '回复',
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