<div class="pane ui-layout-north " >
    <style>
        .top-menu{
            overflow: hidden;
            height: 28px;
            /*min-width: 1000px;*/
            color: #fff;
        }
        .top-menu ul{
            list-style:none;
            margin-top: 0;
            margin-bottom: 0;;
        }
        .top-menu ul li{
            float: left;
            margin: 0 5px 0 5px ;
        }
        .top-menu a,h1,p{
            text-decoration: none;
            color: #ffffff;
            line-height: 28px;
            height: 28px;;
            margin:0;
        }
        .top-menu a:hover{
            color: #ffbb00;
        }
        .top-menu .active{
            color: #ffbb00;
        }
    </style>
    <#assign uriMap={
            "DataSourceListForQuery":"业务核",
            "extract":"数据抽取",
            "CSVImport":"数据入库",
            "DataSourceList":"数据源",
            "extractModelList":"数据抽取模型",
            "EntityModelList":"数据管理",
            "saveModelList":"存储数据模型",
            "DictionaryList":"数据字典",
            "Logout":"退出系统",
            "restart":"重启系统",
            "baseDataClassify":"核心数据分级管理",
            "exportDemoData":"导出demo数据"
            }>

    <#if uriMap[parameters._CURRENT_VIEW_]??>
        <#assign clPrevUri=parameters._CURRENT_VIEW_>
            <#assign temp=session.setAttribute("clPrevUri",clPrevUri)>
                <#else>
                    <#assign clPrevUri=parameters.clPrevUri?default("")>
    </#if>
    <#macro menu uri label>
        <li ><a href="${uri}" <#if uri?lower_case==clPrevUri?lower_case>class="active"</#if> >${label}</a></li>
    </#macro>
    <div class="top-menu">
        <h1 style="float: left;">数据库管理工具</h1>
        <ul style="float: left">

            <@menu uri="dataSourceList" label="数据源"/>
            <@menu uri="extractModelList" label="数据抽取模型"/>
            <#--<@menu uri="saveModelList" label="存储数据模型"/>-->
            <@menu uri="extract" label="数据抽取"/>
            <#--<@menu uri="dataSourceListForQuery" label="业务核"/>-->
            <#--<@menu uri="XMLExport" label="数据抽取"/>-->
            <#--<@menu uri="CSVImport" label="数据入库"/>-->
            <@menu uri="entityModelList" label="数据管理"/>
            <#--<@menu uri="baseDataClassify" label="核心数据分级管理"/>-->
            <#--<@menu uri="dictionaryList" label="数据字典"/>-->
            <@menu uri="exportDemoDataPage" label="导出demo数据"/>
            <@menu uri="reInitDelegator" label="刷新实体定义"/>
            <@menu uri="logout?index=4" label="退出系统"/>
            <#--<li><a href="#" onclick="restartSystem()">重启系统</a></li>-->
        </ul>
         <a style="float: right;" href="logout">你好,${userLogin.userLoginId!}</a>
    </div>

</div>
<script src="../../entityMgr/js/entityModelList.js"></script>
<script>
    function createSaveModelMainView(){
        fadp.ajax('getTreeJson?id=default',{}, function (data) {
            var list=[];
            $.each(data, function (i,v) {
                list.push(v.a_attr);
            })
            showTables(list);
        })
    }
    function restartSystem(){
        fadp.restart();
    }
    $(document).ready(function () {
        $('body').layout({
            applyDemoStyles: true,
            west__size:220,
            north__size:50,
            north__resizable:false,
            spacing_open:3
        });
        $('.ui-layout-north').css({background: '#444444'});
    });
</script>

