$(function() {
	var name_item = $("#select-nav").attr("name-item");
	$('.nav-link[name-item="' + name_item + '"]').addClass("active");

	//#region Item table

	$('table[name-table="table"]').each(function() {
		var nameId = $(this).attr("id");
		var link = "/admin/" + nameId + "/management";
		var id = "#" + nameId;

		$(id).DataTable({
			"paging": true,
			"lengthChange": false,
			"info": true,
			"autoWidth": false,
			"responsive": true,
			"oLanguage": {
				"sSearch": "Tìm kiếm:",
				"sNext": "Trước"
			},
			language: {
				"decimal": "",
				"emptyTable": "Không có dữ liệu trong bảng",
				"info": "Hiển thị từ _START_ đến _END_ trong tổng số _TOTAL_ trang",
				"infoEmpty": "Hiển thị từ 0 đến 0 trong tổng số 0 trang",
				"search": "Tìm kiếm:",
				"searchPlaceholder": 'Nhập từ khóa cần tìm...',
				"zeroRecords": "Không tìm thấy kết quả",
				"paginate": {
					"first": "Trang đầu",
					"last": "Trang cuối",
					"next": "Sau",
					"previous": "Trước"
				},
			},
			dom: 'Bfrtip',
			buttons: [{
				text: '<i class="fas fa-plus"></i> Thêm mới',
				className: "btn no-border bg-gradient-success btn-sm btn-add",
				action: function(e, dt, node, config) {
					window.location = link;
				}
			}]
		});

		switch (nameId) {
			case "contacts":
			case "subscribes":
			case "orders":
			case "account-users": {
				var idHide = '#tabs-list-' + nameId;
				$(idHide).find(".dt-buttons.btn-group.flex-wrap").hide();
				break;
			}
		}
	});
	//#endregion Item table

	//#region delete or restore item

	$(".action-item").on("click", ".btn-delete-restore", function() {
		const Toast = Swal.mixin({
			toast: true,
			position: 'top-end',
			showConfirmButton: false,
		})
		Toast.fire({
			toast: true,
			title: 'Bạn có chắc muốn thay đổi trạng thái của mục này?',
			icon: 'warning',
			showConfirmButton: true,
			confirmButtonColor: '#3085d6',
			confirmButtonText: 'Đồng ý',
			cancelButtonColor: '#d33',
			showCancelButton: true,
			cancelButtonText: 'Hủy',
		}).then((result) => {
			if (result.isConfirmed) {
				var id = $(this).attr("item-id");
				var action = $(this);
				var parent = $(this).parent();
				var status = parent.prev();
				var url = parent.find("a").attr("href");
				url = url.slice(0, url.lastIndexOf("/")) + "_ajax";

				let data = { id: id, };

				jQuery.ajax({
					url: url,
					type: "post",
					contentType: "application/json",
					data: JSON.stringify(data),
					dataType: "json",
					success: function(jsonResult) {

						if (jsonResult.status) {
							Toast.fire({
								icon: 'success',
								title: 'Khôi phục thành công',
								timer: 2000,
							})
							status.html('<i class="fas fa-eye"></i> Hiện');
							status.removeClass("status-hide")
							status.addClass("status-show")
							action.html('<i class="fas fa-trash-alt"></i>');
						} else {
							Toast.fire({
								icon: 'success',
								title: 'Xóa thành công',
								timer: 2000,
							})
							status.html('<i class="fas fa-eye-slash"></i> Ẩn');
							status.removeClass("status-show")
							status.addClass("status-hide")
							action.html('<i class="fas fa-sync-alt"></i>');
						}
					}
				});
			}
		})
	});

	//#endregion delete or restore item
});
function updatePassword(baseUrl) {
	var passNew = $("#passNew").val();
	var passNewConfirm = $("#passNewConfirm").val();
	let data = {
		id: $("#id").val(),
		password: $("#passOld").val(),
	};
	$.ajax({
		url: baseUrl + "/ajax/account/check-pass",
		type: "post",
		contentType: "application/json",
		data: JSON.stringify(data),
		dataType: "json",
		success: function(jsonResult) {
			if (jsonResult.status) {
				if (passNew === passNewConfirm) {
					data.password = passNew;
					$.ajax({
						url: baseUrl + "/ajax/account/change-pass",
						type: "post",
						contentType: "application/json",
						data: JSON.stringify(data),
						dataType: "json",
						success: function(jsonResult) {
							Swal.fire(
								'Thành công',
								'Đổi mật khẩu thành công',
								'success'
							)
						}
					});
				} else {
					Swal.fire(
						'Thất bại',
						'Mật khẩu mới không trùng nhau',
						'error'
					)
				}
			} else {
				Swal.fire(
					'Thất bại',
					'Mật khẩu cũ không chinh xác',
					'error'
				)
			}
		}
	});
}

function updateOrderStatus(id, status) {
	(async () => {
		var note;
		if (status == 4) {
			const { value: text } = await Swal.fire({
				input: 'textarea',
				inputLabel: "Nhập lý do hủy đơn",
				inputPlaceholder: 'Nhập nội dung ...',
				inputAttributes: {
					'aria-label': 'Nhập nội dung'
				},
				showCancelButton: true
			})
			note = text;
		}

		let data = { id: id, userId: status, note: note };
		$.ajax({
			url: "/admin/orders/ajax/updateOrderStatus",
			type: "post",
			contentType: "application/json",
			data: JSON.stringify(data),
			dataType: "json",
			success: function(jsonResult) {
				if (jsonResult.message) {
					Swal.fire('Thành công', jsonResult.message, 'success');
					$("#status-"+id).text(jsonResult.status);
					if(status == 4){
						$("#status-"+id).css("color","red");
						$("#note-"+id).text(note);
					}
					$("#action-"+id).html('<a href="/admin/orders/detail/' + id + '" style="width: 100%;" class="btn bg-gradient-primary btn-sm"> <i class="fas fa-eye"></i> </a>');
				} else {
					Swal.fire('Thất bại', "Lỗi", 'error');
				}
			}
		});
	})()
}