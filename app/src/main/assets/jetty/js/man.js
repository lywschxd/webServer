var http_url = location.href;

//截图
var screencap = function(){
	$.get("/screencap", function(obj){
		alert(obj.msg);
		if (obj.suc == 0) {
			window.open(obj.url)
		}
	}, "json");
}

//获得图片,后面跟图片名字
var getpicture = function(){
	$.get("/screencap?name=2.jpg", function(obj){
		alert(obj.msg);
		if (obj.suc == 0) {
			window.open(obj.url)
		}
	}, "json");
}

//本地APP上载
var app_upload = {
	init : function(){
		$('#app_upload').click(function(){
			$("#myfile").get(0).click();
		});
		$("#myfile").attr("name", "myfile");
		$("#myfile").change(this.fileChangeHandel);
	}, fileChangeHandel : function(e){
		var upFile = this.files[0];
		var upName = upFile.name;
		if (upName.indexOf(".apk") > 1) {
			var fd = new FormData();
			fd.append("myfile", upFile);
			var XMLHttp = new XMLHttpRequest();
			XMLHttp.open("post", "/upload", true);
			XMLHttp.setRequestHeader("X-Requested-With", "XMLHttpRequest");
			XMLHttp.upload.onprogress = function(event) {
				if(event.lengthComputable) {
					var com = (event.loaded / event.total * 100 | 0);
					if (com > 99) {
						$("#fileNameSpan").html("检测文件完整性,稍后...");
					} else {
						$("#fileNameSpan").html("上传 “" + upName + "”，已完成 " + com + "%");
					}
				}
			}
			XMLHttp.onload = function (e) {
				if (XMLHttp.status === 200) {
					location.reload();
				}
			}
			XMLHttp.send(fd);
		} else {
			alert("请选择后缀为 *.apk 的文件!");
		}
	}
}
window.onload = function(){
	app_upload.init();
}