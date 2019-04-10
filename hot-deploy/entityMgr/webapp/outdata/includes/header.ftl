<div class="pane ui-layout-north ">
    <style>
        .top-menu {
            overflow: hidden;
            height: 28px;
            /*min-width: 1000px;*/
            color: #fff;
        }

        .top-menu ul {
            list-style: none;
            margin-top: 0;
            margin-bottom: 0;;
        }

        .top-menu ul li {
            float: left;
            margin: 0 5px 0 5px;
        }

        .top-menu a, h1, p {
            text-decoration: none;
            color: #ffffff;
            line-height: 28px;
            height: 28px;;
            margin: 0;
        }

        .top-menu a:hover {
            color: #ffbb00;
        }

        .top-menu .active {
            color: #ffbb00;
        }
    </style>
    <#assign uriMap={
    "DataSourceList":"数据源",
    "EntityModelList":"数据管理",
    "extractModelList":"数据引接模型",
    "extract":"数据引接",
    "Logout":"退出系统"
    }>

    <#if uriMap[parameters._CURRENT_VIEW_]??>
        <#assign clPrevUri=parameters._CURRENT_VIEW_>
        <#assign temp=session.setAttribute("clPrevUri",clPrevUri)>
    <#else>
        <#assign clPrevUri=parameters.clPrevUri?default("")>
    </#if>
    <#macro menu uri label>
        <li><a href="${uri}" <#if uri?lower_case==clPrevUri?lower_case>class="active"</#if> >${label}</a></li>
    </#macro>
    <div class="top-menu">
        <h1 style="float: left;">外部数据引接</h1>
        <ul style="float: left">
            <@menu uri="dataSourceList" label="数据源"/>
            <@menu uri="entityModelList" label="数据管理"/>
            <@menu uri="extractModelList" label="数据引接模型"/>
            <@menu uri="extract" label="数据引接"/>
            <@menu uri="logout?index=4" label="退出系统"/>
        </ul>
        <a style="float: right;" href="logout">你好,${userLogin.userLoginId}</a>
    </div>

</div>
<script src="../../entityMgr/js/entityModelList.js"></script>
<script>
    function createSaveModelMainView() {
        fadp.ajax('getTreeJson?id=default', {}, function (data) {
            var list = [];
            $.each(data, function (i, v) {
                list.push(v.a_attr);
            })
            showTables(list);
        })
    }

    function restartSystem() {
        fadp.restart();
    }

    $(document).ready(function () {
        $('body').layout({
            applyDemoStyles: true,
            west__size: 220,
            north__size: 50,
            north__resizable: false,
            spacing_open: 3
        });
        $('.ui-layout-north').css({background: '#22aa44'});
    });
</script>

