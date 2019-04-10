<html>
<head>
    <meta charset="UTF-8">
    <meta http-equiv="X-UA-Compatible" content="IE=8">
    <title>系统管理</title>
    <link rel="shortcut icon" href="../favicon.ico" type="image/x-icon"/>
    <script src="../js/jquery-1.9.1.min.js"></script>
    <script src="../js/jquery-ui-latest.js"></script>
    <script src="../js/jquery.layout-latest.js"></script>
    <script src="../js/jquery-ui-timepicker-addon.js"></script>
    <script src="../js/jquery.md5.js"></script>

    <script src="../js/jsTree/jstree.js"></script>
    <script src="../js/validator/jquery.form-validator.min.js"></script>
    <script src="../js/plupload/js/plupload.full.min.js"></script>

    <script src="../js/jquery.fadp.plugin.js"></script>
    <link rel="stylesheet" href="../css/fadp.css">


    <link rel="stylesheet" href="../css/jquery.addOn.css">
    <link type="text/css" rel="stylesheet" href="../css/layout-default-latest.css"/>

    <link rel="stylesheet" href="../css/validator.css">
    <link rel="stylesheet" href="../css/jquery-ui.css">


    <link rel="stylesheet" href="../css/jquery-ui.css">
    <link rel="stylesheet" href="../css/jstree/default/style.css">


</head>
<style>
    .ui-layout-north label
    ,.ui-layout-south
    {
        color: #fff;
    }
    .ui-layout-north,
    .ui-layout-south{
        background: #444;
    }
    .ui-layout-center{
        overflow: auto;
        padding:10px;
    }
</style>

<style>
    <#--侧边栏开始-->
    /*reset*/

    ol, ul { list-style: none;
        margin:0;}
    a{ text-decoration:none;}
    /*reset*/

    /*主要样式*/
    .subNavBox{width:200px;border:solid 1px #e5e3da;margin:100px auto;}
    .subNav{border-bottom:solid 1px #e5e3da;cursor:pointer;font-weight:bold;font-size:14px;color:#999;line-height:28px;padding-left:10px;background:url(../images/jiantou1.jpg) no-repeat;background-position:95% 50%}
    .subNav:hover{color:#277fc2;}
    .currentDd{color:#277fc2}
    .currentDt{background-image:url(../images/jiantou.jpg);}
    .navContent{display: none;border-bottom:solid 1px #e5e3da;}
    .navContent li a{display:block;width:200px;heighr:28px;text-align:center;font-size:14px;line-height:28px;color:#333}
    .navContent li a:hover{color:#fff;background-color:#277fc2}
    .navContent li .active{color:#fff;background-color:#277fc2}

    <#--侧边栏结束-->
</style>

<body>

    <div class="ui-layout-north" style="height: 40px;padding: 10px;">
        <label style="float: left;margin-bottom: 0px;font-size: 20px;">系统管理</label>
        <label style="float: right;font-size: 16px;" href="logout">你好,${userLogin.userLoginId}</label>
    </div>
    <div class="ui-layout-west">
    <#macro menu uri label>
        <li><a href="${uri}" >${label}</a></li>
    </#macro>
        <div class="subNav">用户管理</div>
        <ul class="navContent ">
            <@menu uri="userLoginList" label="用户列表"/>
        </ul>

        <div class="subNav">权限管理</div>
        <ul class="navContent">
           <#-- <@menu uri="#" label="权限列表"/>-->
            <@menu uri="userLoginSecurityGroupList" label="权限分配"/>
        </ul>
        <div class="subNav">日志管理</div>
        <ul class="navContent">
            <@menu uri="visit" label="日志列表"/>
        </ul>
<script>
    $(".subNav").click(function(){
        $(this).toggleClass("currentDd").siblings(".subNav").removeClass("currentDd")
        $(this).toggleClass("currentDt").siblings(".subNav").removeClass("currentDt")

        // 修改数字控制速度， slideUp(500)控制卷起速度
        $(this).next(".navContent").slideToggle(500).siblings(".navContent").slideUp(500);
    });
    $(".subNav").next(".navContent").find(".active").closest(".navContent").prev().click();

</script>

    </div>
    <div class="ui-layout-center">
