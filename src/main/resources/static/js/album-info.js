var app = new Vue({
    el: "#album-info",
    data: {
        petType: '',
        maleActive: false,
        femaleActive: false,
        formData: {},
        petPhotos:''
    },
    mounted: function () {
        this.loadPhotoList();
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
                    if (res.code === 1) {
                        console.log(res)
                    }
                }
            });
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