<div class="main-title">
    <a href="dataSourceList" >< 返回</a>
    <b>编辑数据源</b>
</div>

<form method="post" id="dataSourceForm" autocomplete="nope">


    <table class="edit-table">
        <tr>
            <td>数据源描述</td>
            <td><input type="text" value="${parameters.description?if_exists}" name="description"></td>
            <td>数据源类型</td>
            <td>
                <select onchange="pageGo(1)" name="fieldTypeName">
                    <option>==请选择==</option>
                    <#list fieldTypeList as item>
                        <option
                        <#if item.value==parameters.fieldTypeName?default('')>selected="selected"</#if>
                        value="${item.value}">${item.value}</option>
                    </#list>
                </select>
            </td>
        </tr>
        <tr>
            <td>schema</td>
            <td><input type="text" value="${parameters.schemaName?if_exists}" name="schemaName"></td>
            <td>数据库</td>
            <td><input type="text" value="${parameters.databaseName?if_exists}" name="databaseName"></td>
        </tr>

        <tr>
            <td>ip(域名)</td>
            <td><input type="text" value="${parameters.ip?if_exists}" name="ip"></td>
            <td>port</td>
            <td><input type="text" value="${parameters.port?if_exists}" name="port"></td>
        </tr>

        <tr>
            <td>用户名</td>
            <td><input type="text" value="${parameters.jdbcUsername?if_exists}" name="jdbcUsername"></td>
            <td>密码</td>
            <td><input type="text" value="${parameters.jdbcPassword?if_exists}" name="jdbcPassword"></td>
        </tr>



    </table>

<input type="hidden" id="testing" name="testing" value="N">
<input type="button" value="测试" class="button-blue" onclick="return triggerSubmit('test');">
<input type="button" value="保存" class="button-blue"  onclick="return triggerSubmit('');">
    <div style="clear: both;"></div>
    <script>
        function triggerSubmit(str) {
            if(str=="test"){
                $('#testing').val("Y");
                $('form').submit();
                return false;
            }
            if( window.confirm("确定吗？")){
                $('form').submit();
            }else{
                $('#testing').val("N");
            }
            return false;
        }
    </script>
<h2>
<#if parameters._EVENT_MESSAGE_?exists>
        ${parameters._EVENT_MESSAGE_}
</#if>
<#if parameters._ERROR_MESSAGE_?exists>
       发生如下错误: ${parameters._ERROR_MESSAGE_}
</#if>
</h2>
</form>