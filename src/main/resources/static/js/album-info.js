var app = new Vue({
	el: "#album-info",
	data: {
		petType: '',
		maleActive: false,
		femaleActive: false,
		formData: {

		}
	},
	methods: {}
});

//修改相册名称
$(document).on('click', '.editAlbumName', function() {
	$.modal({
		title: '修改相册名称',
		text: '<input type="" class="album-edit" placeholder="请添加相册名称"/>',
		buttons: [{
			text: '取消',
			bold:true,
			onClick: function() {
				$.alert('取消了')
			}
		}, {
			text: '保存',
			bold:true,
			onClick: function() {
				$.alert('修改已保存')
			}
		}, ]
	})
});
