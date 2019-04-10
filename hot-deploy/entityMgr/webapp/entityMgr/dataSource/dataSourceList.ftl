<form id="dataSourceListForm" method="post" >
    <table  class="query-table">
        <tr>
            <td colspan="6"></td>
            <td rowspan="4"  align="center"><input type="submit" value="查询" class="button-weak"></td>
        </tr>
        <tr>
            <td class="query-table-label">数据源标识</td>
            <td><input type="text" value="${parameters.dataSourceName?if_exists}" name="dataSourceName"></td>
            <td class="query-table-label">数据源类型</td>
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
            <td class="query-table-label">schema(模式)</td>
            <td><input type="text" value="${parameters.schemaName?if_exists}" name="schemaName"></td>
        </tr>
        <tr>

            <td class="query-table-label">数据库</td>
            <td><input type="text" value="${parameters.databaseName?if_exists}" name="databaseName"></td>

            <td class="query-table-label">ip(域名)</td>
            <td><input type="text" value="${parameters.ip?if_exists}" name="ip"></td>
            <td class="query-table-label">port</td>
            <td><input type="text" value="${parameters.port?if_exists}" name="port"></td>
        </tr>

        <tr>
            <td class="query-table-label">用户名</td>
            <td><input type="text" value="${parameters.jdbcUsername?if_exists}" name="jdbcUsername"></td>
            <td class="query-table-label">描述</td>
            <td colspan="3">
                <input type="text" value="${parameters.description?if_exists}" name="description" style="width: 100%;">
                <input type="hidden" name="description_op" value="like">
            </td>
        </tr>


    </table>

    <a class=" button-blue  button-icon-add-i-white"  href="dataSource">添加</a>
    <button class=" button-blue  button-icon-delete-i-white"  onclick="return commonDelete('deleteDataSource','dataSourceName');">删除</button>
    <#--<button  class=" button-blue  button-icon-edit-i-white" onclick="return commonEdit('dataSource?dataSourceName=','dataSourceName');">修改</button></td>-->
    <a  class=" button-blue  button-icon-edit-i-white" onclick="return showEditUI();">修改</a></td>
    <div class="table-wrapper">
        <table>
            <tr>
                <th style="width:50px"><input type="checkbox" id="check-all"></th>
                <th>数据源标识</th>
                <th>描述</th>
                <th>数据源类型</th>
                <th>schema</th>
                <th>数据库</th>
                <th>ip(域名)</th>
                <th>端口</th>
                <th>用户名</th>
            </tr>
            <#if list?exists>

                <#list list as item>
                    <tr>
                        <td><input type="checkbox" name="dataSourceName" value="${item.dataSourceName}"></td>
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
    </div>

<script>
    function showEditUI() {
        var arr=$("[name =dataSourceName ]:checked");
        var dataSourceName=arr.val();
        if(arr.length!=1){
            fadp.showError("请选择一条数据");
        }else{
            var content="<label>描述：</label><input type='text' id='description'>";
            var dialog = fadp.dialog(content, {
                title: '提示',
                showReset: false,
                autoOpen: true,
                buttons: [{
                    text: "确定", click: function () {
                        var description=$("#description").val();
                        genericSave(description,{"dataSourceName":dataSourceName});
                        dialog.remove();

                    }
                }]
            })
        }
    }
    function genericSave(description,pk) {
        if (description!=null) {
            var saveData = {
                entityName: "DatabaseSeq",
                fieldMap: {description:description,dataSourceName:pk.dataSourceName},
                PK:pk,
                dataSourceName: "default"
            };

            fadp.ajax('genericSave', saveData, function () {
                window.location.reload();
            })
        }
    }
</script>