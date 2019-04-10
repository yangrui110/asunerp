<a class="button" href="dataSourceListForQuery" >< 返回模数据源列表</a>
<table>
    <#if list?exists>
        <tr><td>表名</td><td>描述</td></tr>
        <#list list as item>
            <tr>
                    <td><a href="viewTableData?dataSourceName=${parameters.dataSourceName}&entityName=${item.entityName}">${item.entityName}</a></td>
                    <td>${item.description}</td>
            </tr>
        </#list>
    </#if>
</table>