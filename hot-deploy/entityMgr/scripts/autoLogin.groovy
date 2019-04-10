import com.hanlin.fadp.webapp.control.LoginWorker

if(userLogin==null){
    LoginWorker.loginUserWithUserLoginId(request,response,"admin")
    logInfo "免登陆 "
}

return "success"