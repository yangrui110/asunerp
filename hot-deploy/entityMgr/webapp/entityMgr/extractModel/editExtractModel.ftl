<#macro paraField label alias value>
    <tr class="paraTR"><td>${label}</td><td><input name="paraAlias" value="${alias}"></td><td><input name="paraValue" value="${value}"></td></tr>
</#macro>
<div class="main-title">
    <a href="extractModelList">< 返回</a>
    <b>模型定义</b>
</div>
<div class="edit-form"></div>
    <#if parameters._ERROR_MESSAGE_?exists>
        <div  class="message error">
            发生如下错误: ${parameters._ERROR_MESSAGE_}
        </div>
    </#if>

<form method="post">
    <div class="edit-form">
        <ul>
            <#if parameters.extractModelId??>
                <li>
                    <label class="item-label">模型编号:</label>
                    <label class="item-con">${parameters.extractModelId!}</label>
                </li>
            </#if>
            <li>
                <label class="item-label">数据源:</label>

                <div class="item-con">
                    <select class="require" name="dataSourceName" value="${parameters.dataSourceName?if_exists}">
                        <option value="">==请选择==</option>
                        <#if dataSourceList?exists >
                            <#list dataSourceList as item>
                                <option value="${item.dataSourceName}">
                                    ${item.dataSourceName}&nbsp;&nbsp;${item.description}
                                </option>
                            </#list>
                        </#if>
                    </select>
                </div>
            </li>

            <li>
                <label class="item-label">模型描述:</label>
                <div class="item-con"><input type="text" value="${parameters.description!}" name="description"></div>
            </li>

            <li>
                <label class="item-label">sql语句:</label>
                <div class="item-con">
                    <textarea class="require" style="width: 800px;" type="text" name="modelSql" rows="5">${parameters.modelSql?if_exists}</textarea>
                    <script>
                        $('[name=modelSql]').keyup(function(){
                            var sql=$(this).val();
                             var part=  sql.match(/\\%/g);
                            var paraField=$('[name=paraAlias]');
                            if(part==null){
                                $('.paraTR').remove();
                                return;
                            }
                            for(var i=paraField.length;i<part.length;i++){
                                $('#paraArea').append('<tr class="paraTR"><td>参数'+i+'</td><td><input name="paraAlias"></td><td><input name="paraValue"></td></tr>')
                            }
                            for(var i=part.length;i<paraField.length;i++){
                                $('#paraArea tr:last').remove();
                            }
                        })
                    </script>
                    <div class="item-con">
                        <table id="paraArea" class="data-table" style="width:800px;">
                            <tr><th>参数</th><th>别名</th><th>测试值</th></tr>
                            <#if parameters.modelParaList??>
                                <#assign i=0/>
                                <#list parameters.modelParaList as item>
                                   <@paraField label="参数"+i alias=item.paraAlias! value=item.paraValue!/>
                                    <#assign i=i+1/>
                                </#list>
                            </#if>
                        </table>
                    </div>
                </div>

            </li>
        </ul>
    </div>


    <script>
        function triggerTesting() {
            $('#testing').val("Y");
            $('form').submit();
        }
    </script>
    <#if !(parameters.editable??)||parameters.editable!="false">
        <div class="submit-box">
            <input type="hidden" id="testing" name="testing" value="N">
            <input class="button-orange" type="submit" value="测试" onclick="triggerTesting();">
            <input class="button-orange" type="submit" value="保存">
        </div>
    </#if>


</form>
<div class="gap"></div>

<#if columns?has_content>
    <div>当前显示${parameters.currentPageRow!}条，共${parameters.totalRow!}条</div>
    <div class="data-container">


    <table class="data-table">

        <tr>
            <#list columns as column>
                <th>${column}</th>
            </#list>
        </tr>
        <#if records?has_content>
            <#assign alt_row = false>
                <#list records as record>
                    <tr
                    <#if alt_row> class="alternate-row"</#if>
                    >
                    <#list record as field>
                        <td>${field?if_exists}</td>
                    </#list>
                    </tr>
                    <#assign alt_row = !alt_row>
                </#list>
        </#if>
    </table>
    </div>
</#if>
<script>
    $('select').each(function () {
        $(this).find("option[value=" + $(this).attr("value") + "]").attr("selected", true);
    })
    $(function(){
        <#if parameters.editable??&&parameters.editable=="false">
            $("input,select,textarea").attr("disabled",true);
        </#if>
    })
</script>