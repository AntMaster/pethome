var app = new Vue({
    el: "#card-info",
    data: {
        //1：猫  2：狗
        //默认 猫
        petType: 1,
        maleActive: false,
        femaleActive: false,
        pet: {},
        petAlbums: [],
        formData: {},
        animateArr: ["shake", "pulse", "bounce", "wobble", "rubberBand", "bounceIn"],
        catTextArr: ["嗷呜~", "有点饿...", "囧...", "别摸了...", "要秃了！", "=´ω｀=", "ㄒoㄒ",
            "放肆！", "大胆！", "困...", "发呆ing", "想吃肉...", "小鱼干...", "233",
            "比心...", "嫌弃~", "要抱抱~", "举高高~", "咬你", "喵呜~", "心态爆炸",
            "捂脸", "哼...", "跪下！", "...", "无趣的人类", "搞不懂你们", "佛性", "有点慌", "害羞", "你想干嘛", "坏蛋", "挠挠~", "累...", "呼呼~",
            "安静的美男/女子", "抱抱", "喏...", "认真工作！"
        ],
        dogTextArr: ["嗷呜~", "呼呼~", "安静的美男/女子", "嗷呜~", "有点饿...", "囧...",
            "别摸了...", "要秃了！ ", "=´ω｀=", "ㄒoㄒ", "放肆！", "大胆！", "困...", "发呆ing", "想吃肉...", "233", "比心...", "嫌弃~", "要抱抱~", "举高高~", "咬你", "心态爆炸", "捂脸", "哼...", "跪下！", "...", "单身汪",
            "无趣的人类", "搞不懂你们", "佛性", "有点慌", "害羞", "你想干嘛",
            "坏蛋", "挠挠~", "累...", "抱抱", "喏..."
        ],
        petText: "嗷呜~",
        //相册管理
        isManageModel: false,
        selectedAlbumList: []

    },
    mounted: function () {

        this.loadAlbumList();

    },
    updated: function () {
        petCardAdaptive();

    },
    methods: {

        loadAlbumList: function () {
            $.ajax({
                url: '/pethome/pet/album/' + GetQueryString("openId"),
                type: 'GET',
                dataType: 'json',
                data: {
                    petId: GetQueryString("petId")
                },
                success: function (res) {
                    if (res.code === 1) {
                        app.pet = res.data;
                        app.petAlbums = res.data.petAlbumDTOS;
                    }
                }
            });
        },
        goPetPhoto: function (id) {
            window.location.href = "./album-info.html?openId="+ GetQueryString("openId") +"&albumId=" + id;
        },
        play: function (type) {
            //随机播放动画
            var r = parseInt(Math.random() * 10) % 6;
            var animateClass = this.animateArr[r];
            $(".avatar").addClass(animateClass);
            $(".petText").addClass(animateClass);
            setTimeout(function () {
                $(".avatar").removeClass(animateClass);
                $(".petText").removeClass(animateClass);
            }, 500);
            //随机播放文字
            this.playText(type);
            //播放声音
            playSound(type);
        },
        playText: function (type) {
            var textArr;
            type = 1 ? textArr = this.catTextArr : textArr = this.dogTextArr;
            var index = parseInt(Math.random() * 100) % textArr.length;
            this.petText = textArr[index];
            $(".petText").fadeIn();
        },
        openManageModel: function () {
            this.isManageModel = true;
        },
        isManageModel: function (index) {
            if (!this.isManageModel) return false;
            this.selectedAlbumList.push(indx);
        }
    }
});
//新建相册
$(document).on('click', '.album-new', function () {
    $.modal({
        title: '新建相册',
        text: '<input type="" class="album-create" placeholder="请添加相册名称"/>',
        buttons: [{
            text: '取消',
            bold: true,
            onClick: function () {
                //$.alert('取消了')
            }
        }, {
            text: '完成',
            bold: true,
            onClick: function (e) {
                var albumName = $(".album-create").val();
                if (albumName == '' || albumName == null) {
                    $.alert('相册名称必填');
                    return;
                }
                $.ajax({
                    url: '/pethome/pet/album/'+GetQueryString("openId"),
                    type: 'PUT',
                    dataType: 'json',
                    data: {
                        petId: GetQueryString("petId"),
                        name: albumName
                    },
                    success: function (res) {
                        if (res.code === 1) {
                            var album = {
                                id: res.data.id,
                                name: res.data.name,
                                photoCount: 0
                            }
                            app.petAlbums.push(album);
                        }
                    }
                });
            }
        },]
    })
});

//删除相册
$(document).on('click', '.del-mask', function () {
    $.modal({
        title: '删除相册',
        text: '确定删除本相册，并清空里面的所有照片',
        buttons: [{
            text: '取消',
            bold: true,
            onClick: function () {
                //$.alert('取消了')
            }
        }, {
            text: '删除',
            bold: true,
            onClick: function () {

                $.ajax({
                    url: '/pethome/pet/album/'+GetQueryString("oepnId"),
                    type: 'PUT',
                    dataType: 'json',
                    data: {
                        petId: GetQueryString("petId"),
                        name: albumName
                    },
                    success: function (res) {
                        if (res.code === 1) {
                            app.petAlbums = res.data.petAlbumDTOS;
                        }
                    }
                });

                $.alert('删除成功')
            }
        },]
    })
});

$(function () {
    //初始化一些样式
    var awidth = $(".album-pic").width();
    $(".album-pic").css("height", awidth);
    $(".album-new").css("height", awidth);
    petCardAdaptive();
});

function playSound(petType) {
    //0:猫叫  1：狗叫
    var soundSrc = "sound/cat.mp3";
    petType == 1 ? soundSrc = "sound/cat.mp3" : soundSrc = "sound/dog.mp3";
    var borswer = window.navigator.userAgent.toLowerCase();
    if (borswer.indexOf("ie") >= 0) {
        //IE内核浏览器
        var strEmbed = '<embed name="embedPlay" src=' + soundSrc + ' autostart="true" hidden="true" loop="false"></embed>';
        if ($("body").find("embed").length <= 0)
            $("body").append(strEmbed);
        var embed = document.embedPlay;
        //浏览器不支持 audion，则使用 embed 播放
        embed.volume = 100;
        //embed.play();这个不需要
    } else {
        //非IE内核浏览器
        var strAudio = "<audio id='audioPlay' src=" + soundSrc + " hidden='true'>";
        if ($("body").find("audio").length <= 0)
            $("body").append(strAudio);
        var audio = document.getElementById("audioPlay");

        //浏览器支持 audion
        audio.play();
    }
}


