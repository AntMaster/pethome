//document.documentElement.style.fontSize = document.documentElement.clientWidth / 640 * 100 + 'px';

function getAuthURL(){
    return "http://girl.nat300.top/pethome/wechat/webAuth?returnUrl=http://girl.nat300.top/pethome/";
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
        '<a href="card-index.html?openid='+openid+'" class="external"><img src="img/icon/menu-card.png"><span>创建宠卡</span></a>'+
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