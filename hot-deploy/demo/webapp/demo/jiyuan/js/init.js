
/**
 * 公用js，用来设置用户的登录信息
 * 1.绑定数据到id为loginUser的标签上
 * 2.点击退出按钮(id值为logOut)，清除cookie设置，显示未登录状态
 * 需要在引用这个js文件之前引入util.js文件
 * */
(function(){
   var data= cookie.getCookie("user");
   if(!StringUtils.isBlank(data)){
       var user=JSON.parse(data);
       if(user.xm!=''&&user.xm!=null)
       $("#loginUser").text('欢迎您，'+user.xm);
       else $("#loginUser").text("欢迎您,"+user.sj);
       $("#loginUser").attr("href","uc.html");
       $("#loginUser").attr("target","_blank");

       $("#logOut").text("退出");
       $("#logOut").attr("href","javascript:;");
   }else {
       $("#loginUser").text('登录');
       $("#loginUser").attr("href","login.html");
       $("#loginUser").attr("target","_blank");

       $("#logOut").text("注册");
       $("#logOut").attr("href","regsister.html");
       $("#loginUser").attr("target","_blank");
   }

   $("#logOut").click(function(){
       cookie.deleteCookie("user");
       if($("#logOut").text()=='退出')
            window.location.reload();
       /*
       $("#loginUser").text('登录');
       $("#loginUser").attr("href","login.html");
       $("#loginUser").attr("target","_blank");
*/
   });
})();