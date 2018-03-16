var app = new Vue({
    el: "#card-index",
    data: {
        show: true,
        isMiao: false,
        cardFormModel: {
            nickName: null,
            classifyId: 3,
            headImgUrl: null,
            birthday: null,
            sex: 1,
            contraception: false,
            description: null,
            chipNo: null
        }
    },
    methods: {
        create: function () {

        },
        selectClass: function (classId) {
            if (classId == 2) {
                this.cardFormModel.classifyId = 2;
            } else {
                this.cardFormModel.classifyId = 3;
            }
        },
        selectSex: function (sex) {
            sex == 0 ? this.cardFormModel.sex = 0 : this.cardFormModel.sex = 1;
        },
        selectContraceptionState: function (state) {
            state == 0 ? this.cardFormModel.contraception = false : this.cardFormModel.contraception = true;
        },
        submit: function () {

            $.ajax({
                url: '/pethome/pet/'+GetQueryString("openId"),
                type: 'PUT',
                contentType: "application/x-www-form-urlencoded",
                dataType: 'json',
                data: app.cardFormModel,
                success: function (res) {
                    if (res.code === 1) {
                        //app.dynamicArr = res.data;
                        window.location.href = "./card-list.html"
                    } else {
                        alert(res.msg);
                    }
                }
            });
        }
    }
});
$(function () {
    //set ui
    var cardWidth = $(".card-create").width();
    $(".card-create").css("height", cardWidth * 1.3);
    //判断设备宽度
    var ScreenWidth = document.body.clientWidth;
    var ScreenHeight = document.body.clientHeight;
    if (ScreenWidth < 340 || ScreenHeight < 590) {
        //当前为iphone5宽度
        $(".item-title").css("font-size", ".65rem");
        $(".sex-input span").css("margin-right", ".6rem");
        $(".card-form-item").css("margin-bottom", ".7rem");
        $(".avatar img").css({"width": "3rem", "height": "3rem", "border-radius": "1.5rem"});
    }
    //card rotate
    var cardF = document.querySelector(".card-front");
    var cardB = document.querySelector(".card-back");
    cardF.onclick = function () {
        cardF.style.transform = "rotateY(180deg)";
        cardB.style.transform = "rotateY(0deg)";
    }
})

$("#datetime-picker").datetimePicker({
    toolbarTemplate: '<header class="bar bar-nav">\
    <button class="button button-link pull-right close-picker">确定</button>\
    <h1 class="title">选择日期和时间</h1>\
    </header>',
    onClose: function () {
        app.cardFormModel.birthday = $("#datetime-picker").val()
    }
});


$(".avatar-upload").change(function (e) {

    var data = new FormData();
    $.each(e.target.files, function (i, file) {
        data.append("file", file);
    });

    $.ajax({
        url: "/pethome/upload/pet",
        type: 'PUT',
        data: data,
        cache: false,
        contentType: false,
        processData: false,
        success: function (respond) {
            if (respond.code)
                app.cardFormModel.headImgUrl = respond.data;
        }
    });
});
<!--ddd-->