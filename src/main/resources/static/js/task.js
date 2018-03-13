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