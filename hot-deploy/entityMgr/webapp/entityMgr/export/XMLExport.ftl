<div class="center" style="margin-bottom: 40px;">
    <form  method="post" id="form1">
        <table cellpadding="0" cellspacing="0" class="input-table"
               width="100%" id="insertMarks">
            <tbody>
            <tr>
                <td class="input-label">每个文件最大记录数目</td>
                <td><input type="text" name="eachCount" id="_eachCount" tip="默认全部" style="width:210px;"/></td>

                <td class="input-label">数据库</td>
                <td colspan="3">
                    <!--<select id="database-type"  name="databaseName">
                        <#if entityGroups?has_content>
                            <#list entityGroups as groupName>
                                <option <#if groupName == parameters.defaultGroup>selected="selected"</#if> value="${groupName}" >${groupName}</option>
                            </#list>
                        </#if>
                    </select>-->
                    <select name="dataSourceName" onchange="changeDataSource(this)" value="${parameters.dataSourceName?if_exists}" style="height: 40px;width: 100%;">
                        <option value="">==请选择==</option>
                        <#if dataSourceList?exists >
                            <#list dataSourceList as item>
                                <option value="${item.dataSourceName}">${item.dataSourceName}&nbsp;&nbsp;${item.description}</option>
                            </#list>
                        </#if>
                    </select>
                </td>
            </tr>

            <!--<tr>
                <td colspan="2">
                    <input type="submit" value="导出" class="_btn" style="width:120px;" tip="请双击导出按钮" />
                </td>
                <td colspan="2">
                    <input type="reset" value="重置" class="_btn" style="width:120px;" />
                </td>
            </tr>-->

            </tbody>
        </table>
        </from>
</div>
<script>
    function changeDataSource() {
        ckeck();
    }
    //其实要有一种方法，让其跳转到本页面：开始我使用的是利用action,但也可以使用post请求
    //解决不匹配问题

    function checkNum() {

        if ((!isNaN($("#_eachCount").val()) && parseInt($("#_eachCount").val()) > 0) || $("#_eachCount").val() == "") {
            return true;
        }
        alert("请输入正整数!");
        return false;
    }


    function ckeck() {
        //不需要对路径进行验证
        var options = {
            target: '#Tip', // 后台将把传递过来的值赋给该元素
            url: 'XMLQueryList', // 提交给哪个执行
            type: 'POST',
            success: function (returnData) {

            }
        };

        if (checkNum())
            $('#form1').ajaxSubmit(options);

        return false;


    }


</script>

<div id="Tip"></div>
<script>
    function enterIn(evt) {
        var evt = evt ? evt : (window.event ? window.event : null);//兼容IE和FF
        if (evt.keyCode == 13) {
            var filterByEntityName = $('#filterByEntityName').val().trim();
            var groupName = $('#database-type').val().trim();
            var pageSize = $('#select').val().trim();
            var requestPage = 1;
            //	alert(groupName);
            $.post('XMLQueryList',
                    {'filterByEntityName': filterByEntityName, 'databaseName': groupName, 'pageSize': pageSize, 'requestPage': requestPage},
                    function (returnedData) {
                        $('#Tip').html(returnedData);
                    });
            return false;
        }

    }
</script>