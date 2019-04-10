<h2>元数据列表</h2>
<table>
    <tr>
    <tr>
        <td colspan="3">
            <a class="button"  href="mongoCollection">添加</a>
        </td>
    </tr>
    </tr>
    <tr>
        <td style="width: 130px;">操作</td>
        <td>名称</td>
        <td>描述</td>
    </tr>
    <#if list?exists>
        <#list list as item>
            <tr>
                <td>
                    <a class="button" href="deleteMongoCollection?mongoCollectionName=${item.mongoCollectionName}">删除</a>
                    <a class="button" href="mongoCollection?mongoCollectionName=${item.mongoCollectionName}">修改</a>
                    <a class="button" href="mongoDataList?mongoCollectionName=${item.mongoCollectionName}">数据</a>
                </td>
                <td>${item.mongoCollectionName}</td>
                <td>${item.mongoCollectionDescription}</td>
            </tr>
        </#list>
    </#if>
</table>