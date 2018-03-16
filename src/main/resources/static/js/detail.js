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
        detailData: {}
    },
    mounted: function() {
        //初始化
        this.init();
    },
    methods: {
        init: function() {

           /* //加载数据
            this.$http.get('test/test.json', {
                params: {
                    userId: ''
                },
                headers: {
                    token: ''
                }
            }).then(res => {
                this.detailData = res.data;
                //分割图片
                this.imgList = res.data.petImage.split(';');
                this.showMsgList = this.detailData.publicTalkDTO;
            }, error => {
                //this.msgList = error;
            });*/








        },
        //展开所有评论
        expand: function() {

        },
        //分享
        share: function() {

        },
        //关注
        attention: function() {

        },
        //切换消息列表
        changeList: function() {
            this.msgListType = !this.msgListType;
            if (this.msgListType) {
                //加载互动列表
            } else {
                //加载私信列表
            }
        }
    }
});