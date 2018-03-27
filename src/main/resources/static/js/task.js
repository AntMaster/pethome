//我的任务
var task = new Vue({
    el: "#taskPage",
    data: {
        isPend: true,
        taskArr: []
    },
    mounted: function () {
        //初始化
        this.init(GetQueryString("type"));
    },
    methods: {

        init:function (type) {

            var url = null;
            //待处理
            if(type == 1){
                url= '/pethome/publish/task/' + GetQueryString("openid");
            }else {
                url= '/pethome/publish/' + GetQueryString("openid");
            }

            $.ajax({
                url: url,
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