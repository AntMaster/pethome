var app = new Vue({
    el: "#fpet",
    data: {
        petType: '',
        maleActive: false,
        femaleActive: false,
        formData: {
            publishType: '',
            classifyID: '',
            varietyID: '',
            petName: '',
            petSex: '',
            petDescription: '',
            lostTime: '',
            lostLocation: '',
            latitude: '',
            longitude: '',
            ownerName: '',
            ownerContact: '',
            petImage: ''
        }
    },
    methods: {
        selectSex: function (sex) {
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
        selectVariety: function () {

        },
        submitRelease: function () {

            $.ajax({
                url: '/pethome/publish/pet/Aileen',
                type: 'PUT',
                contentType: 'application/json',
                dataType: 'json',
                data: app.formData,
                success: function (res) {
                    console.log(res);
                    if (res.code === 1) {
                        app.dynamicArr = res.data;
                    }
                }
            });
        }

    }
});

//event monitor
$(document).on('click', '.create-actions', function () {
    var buttons1 = [{
        text: '请选择',
        label: true
    },
        {
            text: '喵',
            onClick: function () {
                app.formData.ClassifyID = 2;
                app.petType = '喵';
            }
        },
        {
            text: '汪',
            onClick: function () {
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
$(document).on('open', '.popup-petVariety', function () {
    $(".mask").fadeIn(500);
});
$(document).on('close', '.popup-petVariety', function () {
    $(".mask").fadeOut(500);
});
$("#datetime-picker").datetimePicker({
    toolbarTemplate: '<header class="bar bar-nav">\
    <button class="button button-link pull-right close-picker">确定</button>\
    <h1 class="title">选择日期和时间</h1>\
    </header>',
    onClose: function () {
        console.log($("#datetime-picker").val());
        app.formData.LostTime = $("#datetime-picker").val()
    }
});
$(document).on("pageInit", function (e, pageId, $page) {
    if (pageId == 'locationPage') {
        //地图组件初始化
        Vue.use(VueBaiduMap.default, {
            ak: 'LOnzr56cOpw0LoZ5dt8GSGdej9YRjGrn'
        });
        //选择地址页面
        var locationPage = new Vue({
            el: "#locationPage",
            data: {
                testPoint: "{lng: 30.4660040000, lat: 114.4239750000}",
                scrollWheelOpen: true,
                keyword: '',
                maskShow: false,
                resultShow: false,
                location: "武汉",
                resList: []
            },
            methods: {
                search: function (w) {
                    //alert(w);
                },
                handleSearchComplete: function (res) {
                    //获取检索结果
                    if (res == undefined) return false;
                    if (res.zr.length > 0) {
                        this.resList = res.zr;
                    }
                },
                selectAddress: function (address, point) {
                    $.router.back("fpet.html");
                    app.formData.LostLocation = address;
                    app.formData.Latitude = point.lat;
                    app.formData.Longitude = point.lng;
                }
            },
            watch: {
                keyword: function (newStr, oldStr) {
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
$(function () {
    var address = GetQueryString("address");
    //alert(address);
});