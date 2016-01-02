//发送一个请求
function sendUrl(url) {
	$.get(url, function(data){
		if (data.suc == 0) { location.reload() } else { alert(data.msg) }
	}, "json");
}

//停止&允许APP
var startToStop = function(eval, id, pn){
	if(eval.className == "tingzhipic"){
		eval.className ="yunxingpic";
		document.getElementById(id + "text").innerText="运行";
		sendUrl("/app_stop?pn=" + pn);
	}else{
		eval.className ="tingzhipic";
		document.getElementById(id + "text").innerText="停止";
		sendUrl("/app_start?pn=" + pn);
	}
}

//卸载
var delApp = function(nm, pn){
	if (window.confirm('你确定要卸载应用 '+ nm + ' 吗,此操作不可恢复？')) {
		sendUrl("/app_del?pn=" + pn);
	}
}

//清理数据
var delCache = function(nm, pn){
	if (window.confirm('你确定要删除应用 '+ nm + ' 的全部数据吗,此操作不可恢复？')) {
		/*if (is_adb) {
			sendUrl("/app_delcache?pn=" + pn);
		} else {
			sendUrl("/app_action?pn=" + pn);
		}*/
		sendUrl("/app_delcache?pn=" + pn);
	}
}

//正在运行的APP
var app_manger = function(is_all, is_run){
	$.get("/app_list", function(obj){
		if (obj.suc == 0) {
			var table = "";
			var classs = "";
			is_adb = obj.isadb;
			var applist = obj.list;
			for(var i=0;i < applist.length;i++){
				var n = (i + 1) % 2;
				var isrun = applist[i].isrun;
				var issystem = applist[i].issystem;
				if(is_all == true || is_run == isrun){
					var nm = applist[i].appname;
					var pn = applist[i].packagename;
					var im = applist[i].img;

					if (n > 0) {
						table += "<div style='margin-bottom:7.6%;'>";
						classs = "leftdiv";
					} else {
						classs = "rightdiv";
					}
					table+="<div class='"+ classs +"'>";
					table+="<div class='lefttaobao'><img src='" + im + "' width='100%' height='100%'></img></div>";
					table+="<div class='taobaotext'>"+ nm +"</div>";
					table+="</div>";
					table+="<div class='tingzhiborder'>";
					if (isrun) {
						table+="<div class='tingzhipic' id='tingzhi" + i + "' onclick=\"startToStop(this, id, '"+ pn +"')\"></div>";
						table+="<div class='tingzhitext' id='tingzhi" + i +"text'>停止</div>";
					} else {
						table+="<div class='yunxingpic' id='tingzhi" + i + "' onclick=\"startToStop(this, id, '"+ pn +"')\"></div>";
						table+="<div class='tingzhitext' id='tingzhi" + i +"text'>运行</div>";
					}
					table+="</div>";
					table+="<div class='xiezaiborder'>";
					if(issystem) {
						table+="<div class='xiezaipicno'></div>";
					}else {
						table+="<div class='xiezaipic' onclick=\"delApp('"+ nm +"', '"+ pn +"')\"></div>";
					}
					table+="<div class='xiezaitext'>卸载</div>";
					table+="</div>";
					table+="<div class='xiezaiborder'>";
					table+="<div class='qingshujupic' onclick=\"delCache('"+ nm +"', '"+ pn +"')\"></div>";
					table+="<div class='qingshujutext'>清除</div>";
					table+="</div>";
					if (n < 1) {
						table +="</div>";
					}
				}
			}
			$("#table").html(table);
		}
	},"json");
}

window.onload = function(){
	app_manger(true, true);
}