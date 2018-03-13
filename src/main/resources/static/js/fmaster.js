var app = new Vue({
    el: "#fpet",
    data: {
        petType: '',
        maleActive: false,
        femaleActive: false,
        formData: {
            PublishType: '',
            ClassifyID: '',
            VarietyID: '',
            PetName: '',
            PetSex: '',
            PetDescription: '',
            PetImage: '',
            LostTime: '',
            LostLocation: '',
            Latitude: '',
            Longitude: '',
            OwnerName: '',
            OwnerContact: ''
        }
    },
    methods: {
        selectSex(sex) {
            if (sex == 1) {
                //公
                this.maleActive = !this.maleActive;
                this.femaleActive = false;
                this.maleActive ? this.formData.PetSex = 1 : this.formData.PetSex = '';
            } else {
                //母
                this.femaleActive = !this.femaleActive;
                this.maleActive = false;
                this.femaleActive ? this.formData.PetSex = 2 : this.formData.PetSex = '';
            }
        },
        selectVariety() {

        }
    }
});

//event monitor
$(document).on('click', '.create-actions', function() {
    var buttons1 = [{
            text: '请选择',
            label: true
        },
        {
            text: '喵',
            onClick: function() {
                app.formData.ClassifyID = 2;
                app.petType = '喵';
            }
        },
        {
            text: '汪',
            onClick: function() {
                app.formData.ClassifyID = 3;
                app.petType = '汪';
            }
        }
    ];
    var buttons2 = [{
        text: '取消'
    }];
    var groups = [buttons1, buttons2];
    $.actions(groups);
});
$(document).on('open', '.popup-petVariety', function() {
    $(".mask").fadeIn(500);
});
$(document).on('close', '.popup-petVariety', function() {
    $(".mask").fadeOut(500);
});
$("#datetime-picker").datetimePicker({
    toolbarTemplate: '<header class="bar bar-nav">\
    <button class="button button-link pull-right close-picker">确定</button>\
    <h1 class="title">选择日期和时间</h1>\
    </header>',
    onClose: function() {
        console.log($("#datetime-picker").val());
        app.formData.LostTime = $("#datetime-picker").val()
    }
});
$(document).on("pageInit", function(e, pageId, $page) {
    if (pageId == 'locationPage') {
        //地图组件初始化
        Vue.use(VueBaiduMap.default, {
            ak: 'LOnzr56cOpw0LoZ5dt8GSGdej9YRjGrn',
            methods: {
                locationSuccess() {
                    alert("locate success");
                },
                locationError() {
                    alert("locate error");
                }
            }
        });
        //选择地址页面
        var app = new Vue({
            el: "#locationPage",
            data: {
                testPoint: "{lng: 30.4660040000, lat: 114.4239750000}",
                searchStr: '',
                maskShow: false,
                resultShow: false,
            },
            methods: {
                search(w) {
                    alert(w);
                }
            },
            watch: {
                searchStr: function(newStr, oldStr) {
                    if (newStr.length != 0) {
                        this.maskShow = true;
                        this.resultShow = true;
                    } else {
                        this.maskShow = false;
                        this.resultShow = false;
                    }
                }
            },
        });
    }
});