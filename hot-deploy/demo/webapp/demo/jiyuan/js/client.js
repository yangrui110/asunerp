
/*
    findUserAddresses:function(callback,eCallback){
        HttpUtils.doGet({
            url: apiUrl + '/users/' + UserUtils.getUserId() + '/address',
            needSignin: true,
            success: function(data) {
                onSuccess(data, callback);
            },
            error:function(xhr, type, error){
                onError(xhr, type, error, eCallback)
            }
        });
    }*/

//var serverHost="jiyuan.resoft-tech.com:1234";
var serverHost="localhost";
var serverUrl="../control/"
var apiClient = (function() {
	var apiUrl = serverUrl;

	function openSigninPage() {
	}

	function onSignout() {
		UserUtils.clearToken();
		UserUtils.clearUserProfile();
	}

	function onSuccess(data, callback) {
		/*if (data) {
			callback = callback || function() {};
			if (data.code == 200) {
				callback(data.data);
			} else {
				alert(data.msg);
			}
		} else {
			alert('网络错误');
		}*/
		if(data.error) alert(data.error);
		else {
			callback(data);
		}
	}

	return {
        findActivites:function(data,callback){
            HttpUtils.doPost({
                url: apiUrl +"getActivesPageData",
				data:data,
                success: function(data) {
                   // onSuccess(data, callback);
                    onSuccess(data,callback);
                }
            });
		},
		findActivityOne:function(data,callback){
        	HttpUtils.doPost({
                url: apiUrl +"getActiveOne",
                data:data,
                success: function(data) {
                    onSuccess(data,callback);
                }
			});
		},
		loginUser:function(data,callback){
        	HttpUtils.doPost({
				url:apiUrl+"loginUser",
				data:data,
				success:function(data){
                    onSuccess(data,callback);
				}
			});
		},
		registerUser:function(data,callback){
        	HttpUtils.doPost({
				url:apiUrl+"registerUser",
				data:data,
				success:function (data) {
                    onSuccess(data,callback);
                }
			})
		},
		getRandCode:function(data,callback){
        	HttpUtils.doPost({
				url:apiUrl+"getRandCode",
				data:data,
				success:function(data){
					onSuccess(data,callback);
				}
			})
		},
        getMobileCode:function(data,callback){
            HttpUtils.doPost({
                url:apiUrl+"getPhoneCode",
				data:data,
                success:function(data){
                    onSuccess(data,callback);
                }
            })
        },
		getPass:function(data,callback){
        	HttpUtils.doPost({
                url:apiUrl+"getPass",
                data:data,
                success:function(data){
                    onSuccess(data,callback);
                }
			})
		},
		updatePassword:function(data,callback){
        	HttpUtils.doPost({
				url:apiUrl+"updatePassword",
				data:data,
				success:function (data) {
                    onSuccess(data,callback);
                }
			})
		},
        upPs:function(data,callback){
            HttpUtils.doPost({
                url:apiUrl+"upPs",
                data:data,
                success:function (data) {
                    onSuccess(data,callback);
                }
            })
        },
		getUserOne:function(data,callback){
        	HttpUtils.doPost({
				url:apiUrl+"getUserOne",
				data:data,
				success:function(data){onSuccess(data,callback)}
			})
		},
		bindEmail:function(data,callback){
        	HttpUtils.doPost({
				url:apiUrl+"bindEmail",
				data:data,
				success:function(data){
					onSuccess(data,callback);
				}
			})
		},
		updateUser:function(data,callback){
        	HttpUtils.doPost({
				url:apiUrl+"updateUser",
				data:data,
				success:function(data){
                    onSuccess(data,callback);
				}
			});
		},
		getProducts:function(data,callback){
        	HttpUtils.doPost({
				url:apiUrl+"getProducts",
				data:data,
				success:function(data){
                    onSuccess(data,callback);
				}
			})
		},
		findPassword:function(data,callback){
        	HttpUtils.doPost({
				url:apiUrl+"findPassword",
				data:data,
				success:function(data){
					onSuccess(data,callback);
				}
			})
		},
		getMessagePage:function (data,callback) {
			HttpUtils.doPost({
				url:apiUrl+"getMessages",
				data:data,
				success:function(data){
					onSuccess(data,callback);
				}
			})
        },
		findPageOrder:function(data,callback){
        	HttpUtils.doPost({
				url:apiUrl+"getOrderPageData",
				data:data,
				success:function(data){
					onSuccess(data,callback);
				}
			})
		}
	};
})();