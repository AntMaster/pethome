var app = new Vue({
    el: "#album-info",
    data: {
        petType: '',
        maleActive: false,
        femaleActive: false,
        photo_unselect: {
            icon: 'img/icon/album_unselect.png',
            state: 0

        },
        photo_selected: {
            icon: 'img/icon/album_selected.png',
            state: 1
        },
        albumData: {},
        //管理器
        manager: {
            //开关
            open: false,
            //管理模式类型 0：删除 1：设置封面
            type: 0
        },
        //相片列表
        photoList: [],
        //已经选择的照片
        selectPhotos: [],
        //封面照片
        coverImage: '',
        //已选中 未确定的封面照片
        // tempCover:'',
        //照片控制数组
        photoConfArr: [],
    },
    mounted: function () {
        this.loadPhotoList();
    },
    updated:function () {
        setPhotoBoxStyle();
    },
    methods: {
        loadPhotoList: function () {
            $.ajax({
                url: '/pethome/pet/photo/' + GetQueryString("openid"),
                type: 'GET',
                dataType: 'json',
                data: {
                    albumId: GetQueryString("albumid")
                },
                success: function (res) {
                    console.log(res);
                    if(res.code == 1){
                        app.albumData = res.data;
                        app.photoList = res.data.petPhotoDTOS;
                        app.confPhotoList();
                    } else {
                        $.alert(res.msg);
                    }
                }
            });
        },
        clickPhoto: function (index, photoId) {
            //是否为管理模式
            if (!this.manager.open) {
                return false;
            }
            //管理模式下，判断管理类型
            if (this.manager.type == 0) {
                //删除模式
                //判断是否处于选中状态
                if (this.photoConfArr[index].state == 0) {
                    //如果处于未选中状态 进行选中操作
                    this.photoConfArr.splice(index, 1, this.photo_selected);
                    this.selectPhotos.push(photoId);
                } else {
                    //选中处于选中状态 进行取消选中操作
                    this.photoConfArr.splice(index, 1, this.photo_unselect);
                    //拿到需要删除的相片id
                    for (var i = 0; i < this.selectPhotos.length; i++) {
                        if (photoId == this.selectPhotos[i]) {
                            //移除此图片
                            this.selectPhotos.splice(i, 1);
                        }
                    }
                }
            } else {
                //设置封面模式
                //判断是否已经有选中值
                //console.log(this.coverImage+'封面图片id');
                if (this.coverImage != '') {
                    //this.clearPreSelectedCoverImage(photoId);
                    //console.log('有已经选中的值');
                    //清空已有值
                    this.coverImage = '';
                    for (var i = 0; i < this.photoConfArr.length; i++) {
                        this.photoConfArr.splice(i, 1, this.photo_unselect);
                    }
                }
                //判断是否处于选中模式
                if (this.photoConfArr[index].state == 0) {
                    //如果处于未选中状态 进行选中操作
                    this.photoConfArr.splice(index, 1, this.photo_selected);
                    this.coverImage = photoId;
                } else {
                    //处于选中状态 进行取消选中操作
                    this.photoConfArr.splice(index, 1, this.photo_unselect);
                    //拿到需要删除的相片id
                    this.coverImage = '';
                }
            }
        },
        delPhoto: function () {
            //删除相片

        },
        setCover: function () {
            //设置封面
        },
        confPhotoList: function () {
            //照片配置数组(icon,选中和状态)
            for (var i = 0; i < this.photoList.length; i++) {
                this.photoConfArr.push(this.photo_unselect);
            }
        },
        photoManage: function () {
            //清空已选中
            this.selectPhotos = new Array();
            this.manager.type = 0;
            this.resetPhotoConfArr();
            //相片管理
            if (this.manager.open) {
                //关闭管理模式
                this.manager.open = false;
            } else {
                //开启管理模式
                this.manager.open = true;
            }
        },
        coverManage: function () {
            //清空已选中
            this.selectPhotos = new Array();
            this.resetPhotoConfArr();
            //开启封面
            this.manager.type = 1;
            if (this.manager.open) {
                //关闭管理模式
                this.manager.open = false;
            } else {
                //开启管理模式
                this.manager.open = true;
            }
        },
        resetPhotoConfArr: function () {
            this.photoConfArr = new Array();
            this.confPhotoList();
        }
    }
});

//修改相册名称
$(document).on('click', '.editAlbumName', function () {
    $.modal({
        title: '修改相册名称',
        text: '<input type="" class="album-edit" placeholder="请添加相册名称"/>',
        buttons: [{
            text: '取消',
            bold: true,
            onClick: function () {
                $.alert('取消了')
            }
        }, {
            text: '保存',
            bold: true,
            onClick: function () {
                $.alert('修改已保存')
            }
        },]
    })
});

$("#upPic").change(function (e) {

    //var type = $(this).data().type;

    var data = new FormData();
    $.each(e.target.files, function (i, file) {
        data.append("file", file);
    });

    data.append("albumid", GetQueryString("albumid"));


    $.ajax({
        url: "/pethome/upload/album",
        type: 'PUT',
        data: data,
        cache: false,
        contentType: false,
        processData: false,
        success: function (respond) {
            console.log(respond)
        }
    });
});

//设置照片框样式
function setPhotoBoxStyle(){
    var photoWidth = $(".photoList-box li").width();
    $(".photoList-box li").css('height',photoWidth);
}
$(function(){
    setPhotoBoxStyle();
})