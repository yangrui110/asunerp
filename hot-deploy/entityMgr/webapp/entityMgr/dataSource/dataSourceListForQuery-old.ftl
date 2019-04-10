<form id="dataSourceListForm" method="post" style="min-width: 900px;">

    <table>
        <tr>
            <td>数据源标识</td>
            <td><input type="text" value="${parameters.dataSourceName?if_exists}" name="dataSourceName"></td>
            <td>数据源类型</td>
            <td>
                <select onchange="pageGo(1)" name="fieldTypeName">
                    <option value="">==请选择==</option>
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

        <tr>
            <td>描述</td>
            <td colspan="3">
                <input type="text" value="${parameters.description?if_exists}" name="description" style="width: 100%;">
                <input type="hidden" name="description_op" value="like">
            </td>
        </tr>
        <tr>
            <td colspan="6" align="center"><input type="submit" value="查询"></td>
        </tr>
    </table>

    <table>
        <!--<tr>
            <td colspan="9">
                <a class="button"  href="dataSource">添加数据源</a>
            </td>
        </tr>-->
        <tr>
            <td style="width: 240px;">操作</td>
            <td>数据源标识</td>
            <td>描述</td>
            <td>数据源类型</td>
            <td>schema</td>
            <td>数据库</td>
            <td>ip(域名)</td>
            <td>端口</td>
            <td>用户名</td>
        </tr>
        <#if list?exists>

            <#list list as item>
                <tr>
                    <td>
                        <a class="button"  href="viewTableList?dataSourceName=${item.dataSourceName}">查看表</a>
                    <td>${item.dataSourceName}</td>
                    <td>${item.description}</td>
                    <td>${item.fieldTypeName}</td>
                    <td>${item.schemaName?if_exists}</td>
                    <td>${item.databaseName?if_exists}</td>
                    <td>${item.ip?if_exists}</td>
                    <td>${item.port?if_exists}</td>
                    <td>${item.jdbcUsername?if_exists}</td>
                </tr>
            </#list>
        </#if>
    </table>
