<a class="button" href="viewTableData?dataSourceName=${parameters.dataSourceName}&entityName=${parameters.entityName}" >< 返回数据列表</a>
<#list pkMap.keySet() as key>
        <input name="pk_${key}" value="${pkMap(key)}" style="display: none;">
</#list>
<form method="post">
    <div style="min-width: 920px;">
        <table>
            <tr>
                <td style="width: 200px;">字段名</td>
                <td style="width: 100px;">字段类型</td>
                <td>字段值</td>
            </tr>
            <#list list as item>
                <tr>
                    <td>${item.name}<#if item.isPk><b class="my-required">*</b></#if></td>
                    <td>${item.type}</td>
                    <td >
                        <input style="width: 80%;" name="${item.name}" value="${item.value?if_exists}">
                    </td>
                </tr>
            </#list>
            </table>
        <a  class="button-weak" onclick="submitForm()">保存</a>
        <script>
            function submitForm(){
                $('form').ajaxSubmit({
                    success:function(data){
                        if(data.error){
                            showInfo(data.error)
                        }else{
                            showInfo("保存成功")
                        }
                    }
                });
            }
        </script>
    </div>
</form>