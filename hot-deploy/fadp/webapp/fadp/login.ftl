<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <meta http-equiv="X-UA-Compatible" content="IE=8">
    <title>业务集成框架</title>
    <link rel="shortcut icon" href="../favicon.ico" type="image/x-icon"/>
</head>

<body>

<style>


    body {
        margin: 0;
        background-color: #EEEEEE;
    }

    .body-center {
        position: absolute;
        top: 0px;
        left: 0;
        bottom: 0;
        right: 0;
        text-align: center;
    }

    .main-login {
        position: absolute;
        top: 50%;
        left: 50%;
        margin-top: -100px;
        margin-left: -200px;
        /*top: 100px;*/
        width: 400px;
        height: 200px;
    }

    .login-form {
        position: absolute;
        width: 100%;
        height: 100%;
        z-index: 100;
        top: 0;
        left: 0;
        background: rgba(12, 122, 199, 0.1);
    }

    .login-form .title {
        color: #000;
    }

    .login-form input {
        background: rgba(12, 122, 199, 0.1);
        border: none;
        /*font-size: 22px;*/
    }

    .username,
    .password {
        position: absolute;
        left: 50px;
        height: 26px;
        width: 220px;
    }

    .username {
        top: 70px;
    }

    .password {
        top: 117px;
    }

    .submit-btn {
        position: absolute;
        top: 68px;
        left: 310px;

        width: 73px;
        height: 80px;
        cursor: pointer;
        font-size: 18px;
    }

    .errorMsg {
        color: #ee3333;
        font-size: 18px;
        position: absolute;
        left: 95px;
        top: 150px;
        /*background: rgba(12,122,199,1);*/
    }
</style>


<form method="post" action="" id="form">
    <div class="body-center">

        <div class="main-login">

            <div class="login-form" method="post">
                <h2 class="title">业务集成框架系统</h2>
                <input class="username" type="text" name="USERNAME" onmouseover="this.focus()" onfocus="this.select()"
                       placeholder="请输入用户名" autocomplete="off">

                <input class="password" name="PASSWORD" type="password" onmouseover="this.focus()"
                       onfocus="this.select()" placeholder="请输入密码" autocomplete="off">
                <button type="submit" class="submit-btn">登录</button>
                <div class="errorMsg">
                <#list context.errorMessageList as err>
                    ${err}</br>
                </#list>
                </div>
            </div>
        </div>
    </div>
</form>


</body>

</html>

