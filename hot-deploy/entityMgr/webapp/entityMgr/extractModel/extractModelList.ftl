<form id="dataModelForm" method="post">
    <table class="query-table">
        <tr>
            <td>
                <span>
                    <label>模型编号</label>
                    <input type="text" value="${parameters.extractModelId?if_exists}" name="extractDataModelId">
                </span>
                <span>
                    <label>数据源标号</label>
                    <input type="text" value="${parameters.dataSourceName?if_exists}" name="dataSourceName">
                </span>
                <span><label>描述</label>
                    <input type="text" value="${parameters.description?if_exists}" name="description">
                    <input type="hidden" name="description_op" value="like">
                </span>
            </td>
            <td style="width: 70px;;"><input type="submit" class="button-weak" value="查询"></td>
        </tr>
    </table>
    <div style="clear: both;">
        <a class="button button-blue button-icon-add-i-white"  href="extractModel">添加</a>
        <button class="button button-blue  button-icon-delete-i-white"  onclick="return commonDelete('deleteExtractModel','extractModelId');">删除</button>
        <button  class="button button-blue  button-icon-edit-i-white" onclick="return commonEdit('extractModel?extractModelId=','extractModelId');">修改</button></td>

        <script>
            function syncData() {
                $('#dataModelForm').append('<input type="hidden" name="sync" value="Y">');
            }
        </script>
    </div>
<div class="table-wrapper">
    <table>
        <tr>
            <th style="width:50px"><input type="checkbox" id="check-all"></th>
            <th>模型编号</th>
            <th>模型描述</th>
            <th>数据源编号</th>
            <!--<th>数据表名</th>-->
            <th>模型sql</th>
            <!--<th>最近同步时间</th>-->
        </tr>
        <#if list??>

            <#list list as item>
                <tr>
                    <td><input type="checkbox" name="extractModelId" value="${item.extractModelId}"></td>
                    <td><a href="extractModel?extractModelId=${item.extractModelId}&editable=false">${item.extractModelId}</a></td>
                    <td>${item.description!}</td>
                    <td>${item.dataSourceName}</td>
                    <td>${item.modelSql!}</td>
                </tr>
            </#list>
            <#else>
                <tr><td>无数据</td></tr>
        </#if>
    </table>
</div>

