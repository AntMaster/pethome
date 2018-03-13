//tabbar
Vue.component('mc-tabbar', {
    data: function() {
        return {
            index: false,
            search: false,
            card: false,
            mine: false,
            publish: false
        }
    },
    props: ['page', 'lv'],
    template: '<div class="mc-tabbar">' +
        '<div class="left">' +
        '<a :href="toIndex()" class="item external" :class="{active:index}">' +
        '<div class="icon"><img :src="tabbarIcon(1)" /></div><span>首页</span>' +
        '</a>' +
        '<a :href="toSearch()" class="item external" :class="{active:search}">' +
        '<div class="icon"><img :src="tabbarIcon(2)" /></div><span>搜索</span>' +
        '</a>' +
        '</div>' +
        '<div class="center">' +
        '<a class="action external" :class="{active:publish}"><img :src="tabbarIcon(3)" /><span>发布</span></a>' +
        '</div>' +
        '<div class="right">' +
        '<a :href="toCard()" class="item external" :class="{active:card}">' +
        '<div class="icon"><img :src="tabbarIcon(4)" /></div><span>宠卡</span>' +
        '</a>' +
        '<a :href="toMine()" class="item external" :class="{active:mine}">' +
        '<div class="icon"><img :src="tabbarIcon(5)" /></div><span>我的</span>' +
        '</a>' +
        '</div>' +
        '</div>',
    mounted: function() {
        switch (this.page) {
            case "index":
                this.index = true;
                break;
            case "search":
                this.search = true;
                break;
            case "card":
                this.card = true;
                break;
            case "mine":
                this.mine = true;
                break;
            case "publish":
                this.publish = true;
                break;
            default:
                break;
        }
    },
    methods: {
        toIndex() {
            if (this.lv == 2) return "../index.html";
            return "index.html";
        },
        toSearch() {
            if (this.lv == 2) return "../search.html";
            return "search.html";
        },
        toCard() {
            if (this.lv == 2) return "../card.html";
            return "card.html";
        },
        toMine() {
            if (this.lv == 2) return "../mine.html";
            return "mine.html";
        },
        tabbarIcon(i) {
            switch (i) {
                case 1:
                    {
                        if (this.lv == 2) return "../img/icon/icon-wang.png";
                        return "img/icon/icon-wang.png";
                    }
                    break;
                case 2:
                    {
                        if (this.lv == 2) return "../img/icon/icon-wang.png";
                        return "img/icon/icon-wang.png";
                    }
                    break;
                case 3:
                    {
                        if (this.lv == 2) return "../img/icon/icon-publish.png";
                        return "img/icon/icon-publish.png";
                    }
                    break;
                case 4:
                    {
                        if (this.lv == 2) return "../img/icon/icon-wang.png";
                        return "img/icon/icon-wang.png";
                    }
                    break;
                case 5:
                    {
                        if (this.lv == 2) return "../img/icon/icon-wang.png";
                        return "img/icon/icon-wang.png";
                    }
                    break;
                default:
                    break;
            }
        }
    }
});