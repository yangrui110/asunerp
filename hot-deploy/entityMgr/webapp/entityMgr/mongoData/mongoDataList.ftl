<style>
    h2 {
        text-align: center;
    }
</style>
<script>
    $(function(){
        $('select').each(function(){
	        if($(this).attr("value")){
	            $(this).val($(this).attr("value"));
	        }
        })
    })
</script>
<a class="button" href="mongoCollectionList" >< 返回</a>
<form style="min-width: 924px;" method="post" id="dataModelForm">
    <h2>查询条件</h2>
    <table>
        <tr><td>字段</td><td>条件</td></tr>
        <#if dimCondition?exists>
            <#list dimCondition as item>
                <tr>
                    <td>${item.dimField}</td>
                    <td>
                    <#if item["options"]?exists>
                        <select name="${item.dimField}" value="${parameters[item.dimField]?if_exists}">
                            <option value="">不限</option>
                            <#list item["options"] as option>
                                <option>${option}</option>
                            </#list>
                        </select>
                    </#if>
                    </td>
                </tr>
            </#list>
        </#if>

        <#if attrCondition?exists>
            <#list attrCondition as item>
                <tr>
                    <td>${item.attrField}</td>
                     <td>  <input name="${item.attrField}" value="${parameters[item.attrField]?if_exists}"></td>
                </tr>
            </#list>
        </#if>

        <tr><td colspan="2" align="center"><input style="margin-top: 10px;" type="submit" value="查询"><div class="button" style="margin-left: 20px;" onclick="exportData();">导出数据</div></td></tr>
    </table>

<h4 align="center" style="margin-top:20px;">数据列表</h4>
<a class="button" href="mongoData?mongoCollectionName=${parameters.mongoCollectionName}">添加</a>
<div class="button" onclick="importData();">导入数据</div>

<script>

    function exportData(){
        var para={};
        $('#dataModelForm').find("input,select").each(function(){
            var name=$(this).attr("name");
            var value=$(this).val();
            if(name){
                para[name]=value;
            }
        })
        para["mongoCollectionName"]="${parameters.mongoCollectionName}";
        window.location.href="exportCSVMongoData?"+jQuery.param( para );
    }

    function importData(){
        var checkData= function (str) {
            if (str == "success") {
                location.reload();
            } else {
                alert(str);
            }
        }
        var importFileDialog = art.dialog({
            'title' : "选择文件",
            'ok' : function () {
                var option={};
                option.success=function (data) {
                    checkData(data);
                };
                option.error= function (data) {
                    checkData(data.responseText);
                }
                $("#importFileForm").ajaxSubmit(option);
            },
            'okValue' : '保存',
            'cancel' : function() {

            },
            'cancelValue' : '返回',
            'fixed' : true,
            'lock' : true
        });
        //importFileDialog.content(document.getElementById("importFileDialog"));
        importFileDialog.content(
        '<form id="importFileForm" method="post" enctype="multipart/form-data" action="importCSVDataToMongo?mongoCollectionName=${parameters.mongoCollectionName}">'
    	  +'<input name="file" type="file" value="上传csv文件" accept=".csv">'
   		 +'</form>');
    }
</script>
<table class="basic-table hover-bar" cellspacing="0">

    <script>
        function deleteTableData(pkParameters){
            $.get("deleteMongoData?"+pkParameters,function(data){
                if(data.error){
                    alert(data.error);
                }else{
                    location.reload();
                }
            })
        }
    </script>
    <#if columns?has_content>
        <tr class="header-row">
            <td style="width: 90px;">操作</td>
            <#list columns as column>
                <td>${column}</td>
            </#list>
        </tr>
        <#if records?has_content>
            <#assign alt_row = false>
                <#list records as record>
                    <tr <#if alt_row> class="alternate-row"</#if>>
                    <#list record as field>
                        <#if field_index==0>
                                <td>
                                    <#assign pkParameters="mongoCollectionName="+parameters.mongoCollectionName+"&_id="+field>
                                    <a class="button" href="mongoData?${pkParameters}">修改</a>
                                    <div style="margin-left: 10px;" class="button" onclick="deleteTableData('${pkParameters}')">删除</div>
                                </td>
                            <#else>
                                <td>${field?if_exists}</td>
                        </#if>

                    </#list>
                    </tr>
                    <#assign alt_row = !alt_row>
                </#list>
        </#if>
    </#if>
</table>

