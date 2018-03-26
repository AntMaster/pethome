var app = new Vue({
    el: "#card-info",
    data: {
        //1：猫  2：狗
        //默认 猫
        petType: 2,
        maleActive: false,
        femaleActive: false,
        //未选中icon
        album_unselect: {
            icon: 'img/icon/album_unselect.png',
            state: 0
        },
        album_selected: {
            icon: 'img/icon/album_selected.png',
            state: 1
        },
        pet: {},
        //相册列表
        petAlbums: [],
        //选中相册
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
        //管理按钮
        manageButtonText: "管理相册",
        //已经选中的相册数组
        selectedAlbumList: [],
        selectedAlbumListIndex: [],
        //相册配置列表 用于控制选中状态
        albumConfArr: []
    },
    mounted: function () {

        this.loadAlbumList();

    },
    updated: function () {
        //适配宠卡
        petCardAdaptive();
        //自适应相册item
        fitAlbumItem();
    },
    methods: {
        loadAlbumList: function () {
            $.ajax({
                url: '/pethome/pet/album/' + GetQueryString("openid"),
                type: 'GET',
                dataType: 'json',
                data: {
                    petId: GetQueryString("petid")
                },
                success: function (res) {
                    console.log(res);
                    if (res.code === 1) {
                        app.pet = res.data;
                        app.petAlbums = res.data.petAlbumDTOS;
                        //配置相册状态控制数组
                        app.configAlbumList();
                    }
                }
            });
        },
        goPetPhoto: function (id) {
            window.location.href = "./album-info.html?openId=" + GetQueryString("openId") + "&albumId=" + id;
        },
        configAlbumList: function () {
            //初始化控制相册选中状态的数组，默认未选中
            for (var index = 0; index < this.petAlbums.length; index++) {
                this.albumConfArr.push(this.album_unselect);
            }
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
        clickAlbum: function (index, albumId) {
            if (!this.isManageModel) {
                //非管理状态下直接进入相册详情
                window.location.href = "album-info.html?openid=" + GetQueryString("openid") + "&albumid=" + albumId;
                return false;
            }
            //判断选中的相册是否是选中状态 0未选中 1选中
            if (this.albumConfArr[index].state == 0) {
                //如果处于未选中状态 进行选中操作
                this.albumConfArr.splice(index, 1, this.album_selected);
                this.selectedAlbumList.push(albumId);
                this.selectedAlbumListIndex.push(index);
            } else {
                //选中处于选中状态 进行取消选中操作
                this.albumConfArr.splice(index, 1, this.album_unselect);
                //拿到需要删除的相册id
                for (var i = 0; i < this.selectedAlbumList.length; i++) {
                    if (albumId == this.selectedAlbumList[i]) {
                        //移除此相册
                        this.selectedAlbumList.splice(i, 1);
                        this.selectedAlbumListIndex.splice(i, 1);
                    }
                }
            }
        },
        //管理相册
        manageAlbum: function () {
            //如果是取消操作
            if (this.isManageModel) {
                //清空选中项
                this.selectedAlbumList = new Array();
                //清空配置
                this.configAlbumList = new Array();
                this.manageButtonText = "管理相册";
                this.isManageModel = false;
            } else {
                this.manageButtonText = "取消";
                this.isManageModel = true;
            }
        },
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
                    url: '/pethome/pet/album/' + GetQueryString("openid"),
                    type: 'PUT',
                    dataType: 'json',
                    data: {
                        petId: GetQueryString("petid"),
                        name: albumName
                    },
                    success: function (res) {
                        if (res.code === 1) {
                            var album = {
                                id: res.data.id,
                                name: res.data.name,
                                photoCount: 0
                            }
                            app.albumConfArr.push(app.album_unselect);
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

                if(app.selectedAlbumListIndex.length == 0){
                    $.alert("请选中要删除的相册哟~")
                    return;
                }
                $.ajax({
                    url: '/pethome/pet/album/' + GetQueryString("openid"),
                    type: 'DELETE',
                    dataType: 'json',
                    contentType: 'application/json',
                    data: JSON.stringify(app.selectedAlbumList),
                    success: function (res) {
                        if (res.code === 1) {
                            for (var i = 0; i < app.selectedAlbumListIndex.length; i++) {
                                app.petAlbums.splice(app.petAlbums[app.selectedAlbumListIndex[i]], 1);
                                app.albumConfArr.splice(app.albumConfArr[app.selectedAlbumListIndex[i]], 1)
                            }
                            $.alert('删除成功')
                        }
                    }
                });
            }
        },]
    })
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

function fitAlbumItem() {
    var awidth = $(".album-pic").width();
    $(".album-pic").css("height", awidth);
    $(".album-new").css("height", awidth);
}

$(function () {
    //适配宠卡
    petCardAdaptive();
    //自适应相册item
    fitAlbumItem();
});



