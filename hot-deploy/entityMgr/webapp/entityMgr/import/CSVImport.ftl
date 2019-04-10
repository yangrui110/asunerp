<h1 align="center">导入数据</h1>
<h2>
        ${msg?if_exists}
</h2>
<script>
    function submitForm(){
//        if($('#dataSourceName').val()!='whpu'){
//            alert("只能对本系统数据库进行该操作");
//            return false;
//        }else{
//            return true;
//        }
    }

</script>
<form method="post" onsubmit="return submitForm()" style="min-width: 1000px;"  enctype="multipart/form-data" >
    <table>
        <tr>
            <td class="input-label">文件类型</td>
            <td>
                <select id="file-type">
                    <option selected="selected" value="1">CSV</option>
                </select>
            </td>
            <td class="input-label">文件字符集</td>
            <td>
                <select id="char-set" name="charSet">
                    <option selected="selected" value="GBK">GBK</option>
                    <option value="UTF-8">UTF-8</option>

                </select>
            </td>
        </tr>
        <tr>
            <td class="input-label">数据源
            </td>
            <td colspan="3">
                <script>
                    function changeDataSource($this) {
                        var oldValue = $($this).val();

                            $('form')[0].reset();
                            $('#entityName').val("");
                            $($this).val(oldValue);
                            $('form').submit();

                    }
                </script>
                <select id="dataSourceName" name="dataSourceName" onchange="changeDataSource(this)" value="${parameters.dataSourceName?if_exists}" style="width: 100%;">
                    <option value="">==请选择==</option>
                    <#if dataSourceList?exists >
                        <#list dataSourceList as item>
                            <option value="${item.dataSourceName}">${item.dataSourceName}&nbsp;&nbsp;${item.description}</option>
                        </#list>
                    </#if>
                </select>
            </td>
        </tr>
        <tr>
            <td  class="input-label">数据表</td>
            <td colspan="3">
                <input id="entityName" name="entityName" type="text" list="entityList" value="${parameters.entityName?if_exists}" style="width: 100%;"/>

                <datalist id="entityList">
                    <option value="">==请选择==</option>
                    <#if entityList?exists >
                        <#list entityList as item>
                            <option value="${item}">${item}</option>
                        </#list>
                    </#if>
                </datalist>
            </td>
        </tr>
        <tr>
            <td class="input-label">附件</td>
            <td colspan="3">
                <input type="text" id="file-text" readonly="readonly"/>
                <div id="mask-file">选择文件
                    <input type="file" name="file" class="file-style" onchange="fileChange()" id="choose-file" style="width:100px;"/>
                </div>
                <span style="line-height:24px; height:24px; margin-left:40px; float:left">(注:文件大小不能超过8M)</span>
            </td>
        </tr>
        <tr>
            <td colspan="4" align="center">
                <input type="submit" value="保存">
            </td>
        </tr>
    </table>

</form>
<script>
   function fileChange(){
        $("#file-text").attr('value', $("#choose-file").val());
    };
    $('select').each(function(){
        $(this).find("option[value="+$(this).attr("value")+"]").attr("selected",true);
    })
</script>