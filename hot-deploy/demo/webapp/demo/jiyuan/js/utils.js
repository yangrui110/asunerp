var HttpUtils = (function() {

	function openSigninPage() {
		alert("未登录，请先登录");
	}

	function ajax(type, params) {
		/*if(params.needSignin && StringUtils.isBlank(localStorage.getItem('token'))) {
			return openSigninPage();
		}*/

		$.ajax({
			type: type,
			url: params.url,
			dataType: 'json',
			data: params.data,
			contentType:"application/json",
			success: params.success,
            complete:function(){
                initBase.hideModal();
			},
            beforeSend:function() {
                initBase.showModal();
            },
            xhrFields: {
                withCredentials: true
            },
            crossDomain: true,
			error:function(xhr, errorType, error) {
				console.log("易出错"+xhr+"---"+errorType+"---"+error);
				var errorCallback = params.dataError;
				if(errorCallback!=''&&errorCallback!=null) {
					errorCallback(xhr, errorType, error);
				} else {

				}
			}
		 });
	}

	return {
		doGet: function(params) {
			ajax('GET', params);
		},
		doPost: function(params) {
			ajax('POST', params);
		},
		doPut: function(params) {
			ajax('PUT', params);
		},
		doDelete: function(params) {
			ajax('DELETE', params);
		},
		doUpload: function(params) {
			upload(params);
		}
	};
})();

var UserUtils = (function() {
	return {
		setUserProfile: function(u) {
			localStorage.setItem('user', JSON.stringify(u || {}));
		},
		getUserProfile: function() {
			return JSON.parse(localStorage.getItem('user') || "{}");
		},
		clearUserProfile: function() {
			localStorage.removeItem('user');
		},
		getUserId: function() {
			return JSON.parse(localStorage.getItem('user') || "{}").id;
		},
		setToken: function(token) {
			localStorage.setItem('token', token);
		},
		getToken: function() {
			return localStorage.getItem('token');
		},
		clearToken: function() {
			localStorage.removeItem('token');
		}
	};
})();
var DateUtils = (function() {
	function format(date, fmt) {
		var o = {
			"M+": date.getMonth() + 1, //月份   
			"d+": date.getDate(), //日   
			"h+": date.getHours(), //小时   
			"m+": date.getMinutes(), //分   
			"s+": date.getSeconds(), //秒   
			"q+": Math.floor((date.getMonth() + 3) / 3), //季度   
			"S": date.getMilliseconds() //毫秒   
		};
		if(/(y+)/.test(fmt))
			fmt = fmt.replace(RegExp.$1, (date.getFullYear() + "").substr(4 - RegExp.$1.length));
		for(var k in o)
			if(new RegExp("(" + k + ")").test(fmt))
				fmt = fmt.replace(RegExp.$1, (RegExp.$1.length == 1) ? (o[k]) : (("00" + o[k]).substr(("" + o[k]).length)));

		return fmt;
	}

	return {
		format: function(date, fmt) {
			return format(date, fmt);
		},
		parse: function(dateStr, fmt) {
			return new Date(Date.parse(dateStr));
		},
		ceil4Quarter: function() {
			var now = new Date();
			var mins = Math.ceil(now.getMinutes() / 15) * 15;
			now.setMinutes(mins);

			return format(now, 'yyyy-MM-dd hh:mm');
		},
		floor4Quarter: function() {
			var now = new Date();
			var mins = Math.floor(now.getMinutes() / 15) * 15;
			now.setMinutes(mins);

			return format(now, 'yyyy-MM-dd hh:mm');
		},
		getCurrentYear: function() {
			return(new Date()).getFullYear();
		},
		getCurrentTime: function() {
			return new Date();
		},
		getCurrentTimeText: function() {
			return format(new Date(), 'yyyy-MM-dd hh:mm');
		},
		addDays: function(date, intervals) {
			return new Date(date.getTime() + 86400000 * intervals);
		},
		addMonths: function(date, months) {
			var t = new Date(date.getTime());

			t.setMonth(t.getMonth() + months);

			if(t.getDay() == 31) {
				t.setTime(t.getTime() - 86400000);
			}

			return t;
		},
		addYears: function(date, years) {
			var t = new Date(date.getTime());

			t.setYear(t.getFullYear() + years);

			return t;
		},
		getIntervalMonths: function(s, e) {
			var intervals = (e.getFullYear() - s.getFullYear()) * 12;

			var t = DateUtils.addYears(s, (e.getFullYear() - s.getFullYear()));

			if(t.getTime() >= e.getTime()) {
				while(t.getTime() >= e.getTime()) {
					t = DateUtils.addMonths(t, -1);
					intervals = intervals - 1;
				}
			} else {
				while(t.getTime() <= e.getTime()) {
					t = DateUtils.addMonths(t, 1);
					intervals = intervals + 1;
				}
				intervals = intervals - 1;
			}

			return intervals;
		}
	};
})();

var StringUtils = (function() {

	return {
		isBlank: function(str) {
			return str == 'undefined' || str == null || str.length == 0;
		},
		trim: function(str) {
			if(!StringUtils.isBlank(str)) {
				return str;
			} else {
				return '';
			}
		},
		split: function(str, sChar) {
			return str.split(sChar);
		}
	};
})();

var CacheUtils = (function() {
	return {
		setCache: function(k, v) {
			localStorage.setItem(k, JSON.stringify(v || {}));
		},
		loadCache: function(k) {
			return JSON.parse(localStorage.getItem(k));
		},
		clearCache: function(k) {
			localStorage.removeItem(k);
		}
	}
})();

var cookie = (function(){
	return {
        setCookie: function (key, val, time) {//设置cookie方法
            var date = new Date(); //获取当前时间
            var expiresDays = time;  //将date设置为n天以后的时间
            date.setTime(date.getTime() + expiresDays * 24 * 3600 * 1000); //格式化为cookie识别的时间
            document.cookie = key + "=" + val + ";expires=" + date.toGMTString();  //设置cookie
        },
        setCookieNoTime: function (key, val) {//设置cookie方法
            document.cookie =  encodeURI(key + "=" + val);  //设置cookie
        },
        getCookie: function (key) {//获取cookie方法
            /*获取cookie参数*/


            var getCookie = decodeURI(document.cookie.replace(/[ ]/g, ""));  //获取cookie，并且将获得的cookie格式化，去掉空格字符
            var arrCookie = getCookie.split(";")  //将获得的cookie以"分号"为标识 将cookie保存到arrCookie的数组中
            var tips;  //声明变量tips
            for (var i = 0; i < arrCookie.length; i++) {   //使用for循环查找cookie中的tips变量
                var arr = arrCookie[i].split("=");   //将单条cookie用"等号"为标识，将单条cookie保存为arr数组
                if (key == arr[0]) {  //匹配变量名称，其中arr[0]是指的cookie名称，如果该条变量为tips则执行判断语句中的赋值操作
                    tips = arr[1];   //将cookie的值赋给变量tips
                    break;   //终止for循环遍历
                }
            }
            return tips;
        },
        deleteCookie: function (key) { //删除cookie方法

            var date = new Date(); //获取当前时间
            date.setTime(date.getTime() - 10000); //将date设置为过去的时间
            document.cookie = key + "=v; expires =" + date.toGMTString();//设置cookie

        }
    }
})();

var initBase=(function(){
	return {
        setError:function (msg){
			$("#error").text(msg);
			$("#error").css("display","block");
    	},
		hideError:function(){
            $("#error").css("display","none");
		},
        getRandNum:function (n){
			var result='';
			for(i=0;i<n;i++){
				result+=Math.floor(Math.random()*10);
			}
			return result;
		},
		validateEmpty:function(validateStr,tip){
        	if(validateStr==''||validateStr==undefined){
        		initBase.setError(tip);
        		return true;
			}else return false;
		},
        validatePhone:function(phone){
			initBase.validateEmpty(phone,"手机号码不能为空");
			var myreg=/^[1][3,4,5,7,8][0-9]{9}$/;
			if (!myreg.test(phone)) {
				initBase.setError("手机号格式不正确");
				return false;
			} else {
				return true;
			}
		},
        getQueryString:function(name) {
			var reg = new RegExp('(^|&)' + name + '=([^&]*)(&|$)', 'i');
			var r = window.location.search.substr(1).match(reg);
			if (r != null) {
				return decodeURI(r[2]);
			}
			return null;
		},
		initUserInfo:function(callback){
            var user=cookie.getCookie("user");
            if(user) {
                user = JSON.parse(user);
                if(user.id) {
                    var data={entityName:'BaseUfKhglView',PK:{id:user.id}};
                    apiClient.getUserOne(JSON.stringify(data),function(data){
                    	callback(data);
					})

                }
            }
		},
		logout:function(){
        	cookie.deleteCookie("user");
        	window.location.href="login.html";
		},
		showModal:function(){
            $("#loading").css("display","block");
		},
        hideModal:function(){
            $("#loading").css("display","none");
        },
	}
})();
