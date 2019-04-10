<style type="text/css">
    .form-item-value{
    }
    .form-item-value >input ,select ,label{
        float: left;
        margin-left: 5px;
        margin-right: 0;
    }
    .form-item-label{
        text-align: right;
        float: right;
    }
    .table{
        margin-top: 5px;
    }
</style>

<form id="dataModelForm" method="post">
    <div class="query-part" style="display: none;">
        <table>
            <#if conditions?exists>

                <#list conditions as item>
                    <tr>
                        <td class="form-item-label" width="250px;">${item.label}</td>
                        <td class="form-item-value">
                            <#if textTypes?seq_contains(item.fieldType)>
                                <select class="selectBox" name="${item.fieldName}_op" value="${parameters[item.fieldName+'_op']?if_exists}" style="width: 70px;">
                                    <option value="equals">等于</option>
                                    <option value="like">like</option>
                                    <option value="contains">包含</option>
                                    <option value="empty">空</option>
                                    <option value="notEqual">不为空</option>
                                </select>
                                <input type="text" name="${item.fieldName}" value="${parameters[item.fieldName]?if_exists}">
                                <#elseif dateTypes?seq_contains(item.fieldType)>
                                    <input type="text" name="${item.fieldName}_fld0_value" value="${parameters[item.fieldName+'_fld0_value']?if_exists}" style="width: 200px;">
                                    <select class="selectBox" name="${item.fieldName}_fld0_op" value="${parameters[item.fieldName+'_fld0_op']?if_exists}" style="width: 80px;">
                                        <option value="equals">等于</option>
                                        <option value="sameDay">当天</option>
                                        <option value="greaterThanFromDayStart">大于开始日期</option>
                                        <option value="greaterThan">大于</option>
                                    </select>

                                    <input type="text" name="${item.fieldName}_fld1_value" value="${parameters[item.fieldName+'_fld1_value']?if_exists}" style="width: 200px;">

                                    <select class="selectBox" name="${item.fieldName}_fld1_op" value="${parameters[item.fieldName+'_fld1_op']?if_exists}" style="width: 80px;">
                                        <option value="opLessThan">小于</option>
                                        <option value="upToDay">到今天为止</option>
                                        <option value="upThruDay">到指定日期</option>
                                        <option value="empty">空</option>
                                    </select>

                                    <#else>
                                        <input type="text" name="${item.fieldName}" value="${parameters[item.fieldName]?if_exists}">
                            </#if>

                        </td>
                    </tr>
                </#list>
            </#if>
        </table>
        <div style="padding-left: 40%"><input type="submit" class="button-weak" value="查询"></div>

    </div>
    <div class="gap query-switch" style="height: 20px;"><label style="float:right;cursor: pointer;">展开</label></div>
    <script>
        $('.query-switch').find('label').click(function(){
            var label=$(this);
            if(label.text()=="展开"){
                $('.query-part').show();
                label.text("收起");
            }else{
                $('.query-part').hide();
                label.text("展开");
            }
        })
    </script>
    <#assign urlPara="?dataSourceName="+parameters.dataSourceName+"&entityName="+parameters.entityName>
    <a class=" button-blue  button-icon-add-i-white"  href="editTableData${urlPara}">添加</a>
    <button class=" button-blue  button-icon-delete-i-white"  onclick="return commonDelete('deleteTableData${urlPara}','pk');">删除</button>
    <button  class=" button-blue  button-icon-edit-i-white" onclick="return commonEdit('editTableData${urlPara}','pk');">修改</button></td>
        <div class="table-wrapper">
            <table >
                <tr>
                    <th style="width:50px"><input type="checkbox" id="check-all"></th>
                    <#list columns as column>
                        <th>${column}</th>
                    </#list>
                </tr>
                <#if columns?has_content>
                    <#if records?has_content>
                        <#assign alt_row = false>
                            <#list records as record>
                                <tr <#if alt_row> class="alternate-row"</#if>>
                                <#list record as field>
                                    <#if field_index==0>
                                        <td><input type="checkbox" name="pk" value="&${field}"></td>
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
        </div>

        <script>
            $('select').each(function () {
                $(this).find("option[value=" + $(this).attr("value") + "]").attr("selected", true);
            })
        </script>