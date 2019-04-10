$(function(){
	var index = getUrlParam('index');
	if(index)
		{
	     	$('#topUl>li').each(function(){
				 $(this).removeClass("current-menu-item");
			})
			 $("#topUl>li").eq(index).addClass("current-menu-item");
	    }else
	    	{
	    	 $("#topUl>li").eq(0).addClass("current-menu-item");
	    	}
	
})
   function getUrlParam(name) {
            var reg = new RegExp("(^|&)" + name + "=([^&]*)(&|$)"); //构造一个含有目标参数的正则表达式对象
            var r = window.location.search.substr(1).match(reg);  //匹配目标参数
            if (r != null) return unescape(r[2]); return null; //返回参数值
        }
function startMove(obj, name, iTarget)
{	

	clearInterval(obj.timer);

	obj.timer = setInterval(function(){
		var cur = obj.offsetTop;
		var speed = (iTarget - cur) / 8;
		speed = speed > 0 ? Math.ceil(speed) : Math.floor(speed);
		if(Math.abs(cur - iTarget) <= 0){
			cur = iTarget;
		}
		if(cur == iTarget)
		{
			clearInterval(obj.timer);
		}else{
			$(obj).css({'top':cur + speed + 'px', 'position':'fixed', 'width':'100%', 'z-index':'8'});
		}
	}, 30);
}

function adaptToScreen(){
	
	var top1 = document.documentElement.clientHeight + document.documentElement.scrollTop - $('#forver-buttom')[0].offsetHeight;
    if (document.documentElement.scrollHeight > document.documentElement.clientHeight){
   		$('#forver-buttom')[0].removeAttribute('style');
    }else{
    	startMove($('#forver-buttom')[0], 'top', top1);
    }	
}



