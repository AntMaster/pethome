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