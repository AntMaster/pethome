
var app = new Vue({
    el: "#minePage",
    data: {

    },
    methods: {

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
                taskArr: [
                    { classifyID: 2, publishType: 1, petFindState: 2 },
                    { classifyID: 3, publishType: 2, petFindState: 2 },
                    { classifyID: 2, publishType: 2, petFindState: 1 }
                ]
            },
            mounted: function () {
                $.init();
            },
            methods: {
                navChange(t){
                    this.isPend = !this.isPend;
                }
            }
        });
    }
    if(pageId == "msgPage"){
        var message = new Vue({
            el: "#msgPage",
            data: {
                navActive: true,
                taskArr: [
                    { classifyID: 2, publishType: 1, petFindState: 2 },
                    { classifyID: 3, publishType: 2, petFindState: 2 },
                    { classifyID: 2, publishType: 2, petFindState: 1 }
                ]
            },
            mounted: function () {
                $.init();
            },
            methods: {
                navChange(t){
                    this.navActive = !this.navActive;
                }
            }
        });
    }
    if(pageId == "interactPage"){
        var interact = new Vue({
            el: "#interactPage",
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
    }
    if(pageId == 'attentionPage'){
        var message = new Vue({
            el: "#attentionPage",
            data: {
                navActive: true,
                taskArr: [
                    { classifyID: 2, publishType: 1, petFindState: 2 },
                    { classifyID: 3, publishType: 2, petFindState: 2 },
                    { classifyID: 2, publishType: 2, petFindState: 1 }
                ]
            },
            mounted: function () {
                $.init();
            },
            methods: {
                navChange(t){
                    this.navActive = !this.navActive;
                }
            }
        });
    }
    if(pageId == 'chatPage'){
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
    }
});