<h4 align="center">数据编辑</h4>
<a class="button" href="mongoDataList?mongoCollectionName=${parameters.mongoCollectionName}" >< 返回数据列表</a>
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
                    <td>${item.name}</td>
                    <td>${item.type}</td>
                    <td >
                        <input style="width: 80%;" name="${item.name}" value="${item.value?if_exists}">
                    </td>
                </tr>
            </#list>
        </table>
        <div class="button"  onclick="submitForm()">保存</div>
        <script>
            function submitForm(){
                $('form').ajaxSubmit({
                    success:function(data){
                        if(data.error){
                            alert(data.error);
                        }else{
                            alert("保存成功");
                        }
                    }
                });
            }
        </script>
    </div>
</form>