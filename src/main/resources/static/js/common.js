//document.documentElement.style.fontSize = document.documentElement.clientWidth / 640 * 100 + 'px';

function openShareRemind(){

    $(".remindBox").fadeIn();
}
function remindHide(){
    $(".remindBox").fadeOut();
}

function getAuthURL(){
    return "http://girl.nat300.top/pethome/wechat/webAuth?returnUrl=http://girl.nat300.top/pethome/";
}

function getAuthUrl() {
    return  "https://open.weixin.qq.com/connect/oauth2/authorize?appid=wx41fd8229749f5df8&redirect_uri=" +
            "http://girl.nat300.top/pethome/index.html&response_type=code&scope=snsapi_userinfo&state=a#wechat_redirect";
}

function  getResourceUlr() {
    return "http://girl.nat300.top/pethome/";
}

Date.prototype.Format = function (fmt) { //author: meizz
    var o = {
        "M+": this.getMonth() + 1, //月份
        "d+": this.getDate(), //日
        "H+": this.getHours(), //小时
        "m+": this.getMinutes(), //分
        "s+": this.getSeconds(), //秒
        "q+": Math.floor((this.getMonth() + 3) / 3), //季度
        "S": this.getMilliseconds() //毫秒
    };
    if (/(y+)/.test(fmt)) fmt = fmt.replace(RegExp.$1, (this.getFullYear() + "").substr(4 - RegExp.$1.length));
    for (var k in o)
        if (new RegExp("(" + k + ")").test(fmt)) fmt = fmt.replace(RegExp.$1, (RegExp.$1.length == 1) ? (o[k]) : (("00" + o[k]).substr(("" + o[k]).length)));
    return fmt;
}


function GetQueryString(name) {
    var reg = new RegExp("(^|&)" + name + "=([^&]*)(&|$)");
    var r = window.location.search.substr(1).match(reg);
    if (r != null) return decodeURI(r[2]);
    return null;
}

function petCardAdaptive() {
    var petCardWidth = $(".petCard-item").width();
    var ScreenWidth = document.body.clientWidth;
    if (ScreenWidth < 350) {
        $(".petCard-item").css("height", petCardWidth * 0.7);
        $(".avatar").css({ "height": "3rem", "width": "3rem", "border-radius": "1.5rem" })
    } else {
        $(".petCard-item").css("height", petCardWidth * 0.62);
    }
}

//打开和插入菜单
function openMenu(){

    var openid = GetQueryString("openid")

    var menuBox = $(".menu-box");
    if(menuBox.length > 0){
        $(".menu-box").fadeIn();
        return false;
    }
    var menuDiv = '<div class="menu-box" onClick="">'+
        //logo
        '<div class="menu-head"><img src="img/menu-head.png"></div>'+
        //button
        '<div class="menu-button-box">'+
        '<a href= "fpet.html?openid='+openid+'" class="external"><img src="img/icon/menu-fpet.png"><span>发布寻宠</span></a>'+
        '<a href="fmaster.html?openid='+openid+'" class="external"><img src="img/icon/menu-fmaster.png"><span>发布寻主</span></a>'+
        '<a onclick="entryPet()"  class="external"><img src="img/icon/menu-card.png"><span>创建宠卡</span></a>'+
        '</div>'+
        //closeBtn
        '<div class="menu-close"><img onClick="menuHide()" src="img/icon/menu-close.png"></div>'
    '</div>';
    $(".page").prepend(menuDiv);
    $(".menu-box").fadeIn();
}
//隐藏菜单
function menuHide(){
    $(".menu-box").fadeOut();

}


function entryPet(){

    $.ajax({
        url: '/pethome/pet/' + GetQueryString("openid"),
        type: 'GET',
        dataType: 'json',
        data: null,
        success: function (res) {

            if(res.code == 1){
                window.location.href = "./card-list.html?openid=" + GetQueryString("openid");
            }else {
                window.location.href = "./card-index.html?openid=" + GetQueryString("openid");
            }
          /*  if (res.code === 1) {

                if (temp_lv == 2) {

                    window.location.href = "../card-list.html?openid=" + GetQueryString("openid");

                } else {

                    window.location.href = "card-list.html?openid=" + GetQueryString("openid");
                }

            } else {

                if (temp_lv == 2) {
                    window.location.href = "../card-index.html?openid=" + GetQueryString("openid");
                } else {
                    window.location.href = "card-index.html?openid=" + GetQueryString("openid");
                }
            }*/
        }
    });

    //href="card-index.html?openid='+openid+'"

}

varietyArrDataSource = {
    "2": [{
        "id": 3,
        "classifyId": 2,
        "name": "田园猫",
        'icon':'img/petvariety/cat3.png'
    },
        {
            "id": 13,
            "classifyId": 2,
            "name": "英短",
            'icon':'img/petvariety/cat13.png'
        },
        {
            "id": 14,
            "classifyId": 2,
            "name": "美短",
            'icon':'img/petvariety/cat14.png'
        },
        {
            "id": 15,
            "classifyId": 2,
            "name": "加菲猫",
            'icon':'img/petvariety/cat15.png'
        },
        {
            "id": 16,
            "classifyId": 2,
            "name": "蓝猫",
            'icon':'img/petvariety/cat16.png'
        },
        {
            "id": 19,
            "classifyId": 2,
            "name": "波斯猫",
            'icon':'img/petvariety/cat19.png'
        },
        {
            "id": 39,
            "classifyId": 2,
            "name": "橘猫",
            'icon':'img/petvariety/cat39.png'
        },
        {
            "id": 41,
            "classifyId": 2,
            "name": "其它",
            'icon':'img/petvariety/cat41.png'
        }
    ],
    "3": [{
        "id": 9,
        "classifyId": 3,
        "name": "田园犬",
        'icon':'img/petvariety/dog9.png'
    },
        {
            "id": 5,
            "classifyId": 3,
            "name": "金毛",
            'icon':'img/petvariety/dog5.png'
        },
        {
            "id": 6,
            "classifyId": 3,
            "name": "哈士奇",
            'icon':'img/petvariety/dog6.png'
        },
        {
            "id": 20,
            "classifyId": 3,
            "name": "萨摩耶",
            'icon':'img/petvariety/dog20.png'
        },
        {
            "id": 10,
            "classifyId": 3,
            "name": "泰迪",
            'icon':'img/petvariety/dog10.png'
        },
        {
            "id": 7,
            "classifyId": 3,
            "name": "拉布拉多",
            'icon':'img/petvariety/dog7.png'
        },
        {
            "id": 25,
            "classifyId": 3,
            "name": "雪纳瑞",
            'icon':'img/petvariety/dog25.png'
        },
        {
            "id": 18,
            "classifyId": 3,
            "name": "阿拉斯加",
            'icon':'img/petvariety/dog18.png'
        },
        {
            "id": 22,
            "classifyId": 3,
            "name": "比熊",
            'icon':'img/petvariety/dog22.png'
        },
        {
            "id": 29,
            "classifyId": 3,
            "name": "边牧",
            'icon':'img/petvariety/dog29.png'
        },
        {
            "id": 21,
            "classifyId": 3,
            "name": "博美",
            'icon':'img/petvariety/dog21.png'
        },
        {
            "id": 42,
            "classifyId": 3,
            "name": "其他",
            'icon':'img/petvariety/dog42.png'
        }
    ]
}

function hasNullItem(obj){
    for (var key in obj) {
        if(!obj[key]){
            return false;
        }
    }
    return true;
}


function WechatJsApi() {

}