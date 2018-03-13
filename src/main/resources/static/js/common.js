//document.documentElement.style.fontSize = document.documentElement.clientWidth / 640 * 100 + 'px';

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