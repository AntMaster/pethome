var app = new Vue({
    el: "#card-index",
    data: {
        show: true,
        isMiao: false,
        cardFormModel: {
            NickName: '',
            ClassifyID: 3,
            Birthday: '',
            PetSex: true,
            ContraceptionState: false,
            Description: '',
            ChipNo: ''
        }
    },
    methods: {
        create() {

        },
        selectClass(classId) {
            if (classId == 2) {
                this.cardFormModel.ClassifyID = 2;
            } else {
                this.cardFormModel.ClassifyID = 3;
            }
        },
        selectSex(sex) {
            sex == 0 ? this.cardFormModel.PetSex = false : this.cardFormModel.PetSex = true;
        },
        selectContraceptionState(state) {
            state == 0 ? this.cardFormModel.ContraceptionState = false : this.cardFormModel.ContraceptionState = true;
        },
        submit() {

        }
    }
});
$(function() {
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
        $(".avatar img").css({ "width": "3rem", "height": "3rem", "border-radius": "1.5rem" });
    }
    //card rotate
    var cardF = document.querySelector(".card-front");
    var cardB = document.querySelector(".card-back");
    cardF.onclick = function() {
        cardF.style.transform = "rotateY(180deg)";
        cardB.style.transform = "rotateY(0deg)";
    }
})

$("#datetime-picker").datetimePicker({
    toolbarTemplate: '<header class="bar bar-nav">\
    <button class="button button-link pull-right close-picker">确定</button>\
    <h1 class="title">选择日期和时间</h1>\
    </header>',
    onClose: function() {
        app.cardFormModel.Birthday = $("#datetime-picker").val()
    }
});