<#macro editTr paraName paraText>

    <tr name="editTr">
        <td>${paraText}</td>
        <td><label>${parameters[paraName]!}</label><input name="${paraName}" value="${parameters[paraName]!}" type="text" class="my-full-item"></td>
    </tr>
</#macro>

<script>
    var part = "基本信息";
    $(initListener);//初始化事件监听
    function buttonClick(btn) {
        data_tr=null;
        part = btn.innerText;
        switch (part) {
            case '基本信息':
                activePartById('part1');
                break;
            case '列信息':
                activePartById('part2');
                break;
            case '外键':
                activePartById('part3');
                break;

        }

        $('.my-menu').each(function () {
            $(this).removeClass("active");
        });
        $(btn).addClass("active");
    }

    function activePartById(partId) {
        part = partId;
        for (var i = 1; i < 4; i++) {
            if ("part" + i != partId) {
                $('#part' + i).css('display', 'none');
            } else {
                $('#part' + i).css('display', 'block');
            }
        }
    }
    var data_tr = null;
    /**移动列*/
    function moveTr(oper) {
        if (oper == "MoveUp") {    //向上移动
            if ($(data_tr).prev().prev().html() != null) { //获取tr的前一个相同等级的元素是否为空
                $(data_tr).insertBefore($(data_tr).prev()); //将本身插入到目标tr的前面
            }
        } else {
            if ($(data_tr).next().html() != null) {
                $(data_tr).insertAfter($(data_tr).next()); //将本身插入到目标tr的后面
            }
        }
        $(data_tr).parent().parent().find('td[name="num"]').each(function (i) {
            $(this).text(i + 1);
        })
    }
    /**删除行*/
    function deleteTr() {
        var table = $(data_tr).parent().parent();
        data_tr.remove();
        table.find('td[name="num"]').each(function (i) {
            $(this).text(i + 1);
        })
    }
    var fieldTypeListJson =${jsonData}.fieldTypeListJson;
    /**添加行*/
    function addTr2BaseFieldTable() {
        $('#part2').find('table').append(makeTr());
        reInit('#part2');
    }
    /**插入行*/
    function insertIntoBaseFieldTable() {
        if (data_tr == null) {
            return;
        }
        $(makeTr()).insertBefore(data_tr);
        reInit('#part2');
    }
    /**构建行*/
    function makeTr() {
        var tr = $('<tr name="editTr"></tr>')
        var td = $('<td name="num"></td>');
        tr.append(td);
        td = $('<td><label></label><input name="field-name"></td>');
        tr.append(td);
        td = $('<td><label></label></td>');
        tr.append(td);
        var select = $('<select name="field-type"></select>');
        $.each(fieldTypeListJson, function () {
            select.append('<option value="' + this + '">' + this + '</option>');
        });
        td.append(select);
        td = $('<td><label></label><input name="field-description"></td>');
        tr.append(td);
        td = $('<td><label></label><select name="field-not-null"><option>否</option><option>是</option></select></td>');
        tr.append(td);
        td = $('<td><label></label><select name="prim-key"><option>否</option><option>是</option></select></td>');
        tr.append(td);
        return tr;
    }

    function reInit(part) {
        initListener();
        $(part).find('table').find('td[name="num"]').each(function (i) {
            $(this).text(i + 1);
        });
    }

    function initListener() {
        $('td > input,td > select').css('display', 'none');
        $('tr[name="editTr"]>td').each(function (i, td) {
            if ($(td).find('input,select').length != 0) {

                $(td).find('input,select').val($(td).find('label').text());
            }
            $(td).click(function () {
                if (data_tr) {
                    $(data_tr).find('td').each(function () {
                        changeBackLabel(this);
                        $(this).removeClass("my-tr-active");
                    });
                }
                data_tr = $(td).parent();
                $(td).parent().find('td').each(function () {
                    $(this).addClass("my-tr-active");
                });
                if ($(td).find('input,select').length != 0) {
                    $(this).removeClass("my-tr-active");
                   change2input(td);
                }

            });

        });
    }
    function change2input(td){
        $(td).find('input,select').css('display', "");
        $(td).find('input,select').focus();
        $(td).find('label').css('display', "none");
    }
    function changeBackLabel(td) {
        $(td).find('label').text($(td).find('input,select').val());
        $(td).find('label').css('display', "");
        $(td).find('input,select').css('display', "none");
    }
    function getFieldList(ele){
       var entityName= $(ele).val();
        var td=$(ele).parent().next();
        td.find('select').remove();
        td.find('label').text("");
        if(entityName){
            $.post('getFieldList',{dataSourceName:"${parameters.dataSourceName!}",entityName:entityName},function(result){
                var select=$('<select name="key-map-'+td.parent().find('[name="fk-name"]').val()+'-rel-field-name"></select>')
                td.append(select);
                $.each(result,function(i,fieldName){
                    select.append('<option>'+fieldName+'</option>');
                });
                change2input(td);
            });
        }

    }
    function changeKeyMapName(ele){
        var pre=$(ele).prev().text();
        $(ele).prev().text($(ele).val());
        $(ele).parent().parent().find('select').each(function(){
                $(this).attr('name',$(this).attr('name').replace('key-map-'+pre+'-field-name','key-map-'+$(ele).val()+'-field-name'))
                $(this).attr('name',$(this).attr('name').replace('key-map-'+pre+'-rel-field-name','key-map-'+$(ele).val()+'-rel-field-name'))
        });
    }
    function formSubmit(){

            var submitData={};
            $('#part1 input').each(function (i,ele) {
                var name=$(ele).attr('name');
                if(name){
                    submitData[name]=$(ele).val();
                }
            })
            submitData.fieldData=[];
            var fieldData=submitData.fieldData
            $('#part2 [name=editTr]').each(function (i,ele) {
                getEditTrData(i,ele,fieldData);
            })

            submitData.fkData=[];
            var fkData=submitData.fkData
            $('#part3  [name=editTr]').each(function (i,ele) {
                getEditTrData(i,ele,fkData);
            })

            function getEditTrData(i,ele,list) {
                var item={};
                list.push(item);
                $(ele).find('input,select').each(function (j,input) {
                    var name=$(input).attr('name');
                    if(name){
                        item[name]=$(input).val();
                    }
                })
            }
        console.log(submitData);
        fadp.confirmInfo("当前操作将影响数据表结构，确定吗？", function () {
            fadp.ajax('entityModel',submitData, function (data) {
                showInfo("保存成功");
            })
        });
   /*         $('form').ajaxSubmit({
                success:function(data){
                    if(data.errorMsg){
                        fadp.showError(data.errorMsg);
                    }else{
                        showInfo("保存成功");
                    }

                }
            });*/

    }
    fadp.button('#editEntityModelForm');
</script>
<style>
    .data-table input,
    .data-table select
    {
     width: 90%;
    }
</style>
<form method="post" name="form" action="entityModel">

<div id="editEntityModelForm">
    <div>
        <div style="float: left; padding:10px;">
            <a class="my-menu button-blue" onclick="buttonClick(this)">基本信息</a>
            <a class="my-menu button-blue" onclick="buttonClick(this)">列信息</a>
            <a class="my-menu button-blue" onclick="buttonClick(this)">外键</a>
            <a class="button-weak my-submit" onclick="formSubmit()">保存</a>
        </div>
        <div style="clear: both;"></div>
        <div class="table-wrapper">
            <div id="part1">
                <input name="dataSourceName" type="hidden" value="${parameters.dataSourceName}">
                <#if parameters.entityName??>
                        <input name="entityName" type="hidden" value="${parameters.entityName}">
                </#if>
                <h2>基本信息</h2>
                <div >
                    <table>
                        <tr name="editTr">
                            <td style="width: 200px;">所属数据源
                                <span class="my-required" >*</span>
                            </td>
                            <td>
                                <label>${parameters.dataSourceName!}</label>
                            </td>
                        </tr>
                        <tr name="editTr">
                            <td style="width: 200px;">表名
                                <div class="my-required"></div>
                            </td>
                            <td>
                                <label>${parameters.entityName!}${parameters["new-entity-name"]!}</label>
                                <#if !parameters.entityName??>
                                    <input name="new-entity-name" type="text"
                                           value="${parameters['new-entity-name']?if_exists}" class="my-full-item">
                                </#if>
                            </td>
                        </tr>
                        <@editTr "entity-description" "备注"/>
                       <!-- <@editTr "package-name" "包名"/>-->
                        <@editTr "title" "中文名称"/>
                    </table>
                </div>

            </div>
            <div id="part2" style="display: none;">
                <h2>列信息</h2>

                <div style="width: 100%;">
                    <a class="button-blue" onclick="addTr2BaseFieldTable()">新增</a>
                    <a class="button-blue" onclick="insertIntoBaseFieldTable()">插入</a>
                    <a class="button-blue" onclick="deleteTr()">删除行</a>
                    <a class="button-blue" onclick="moveTr('MoveUp')">上移</a>
                    <a class="button-blue" onclick="moveTr()">下移</a>
                </div>
                <div class="table-wrapper">
                    <table>
                        <tr>
                            <th style="width: 33px;">序号</th>
                            <th style="width: 220px;">列名</th>
                            <th style="width: 150px;">类型</th>
                            <th>备注</th>
                            <th style="width: 80px;">不可空</th>
                            <th style="width: 80px;">主键</th>
                        </tr>
                        <#if parameters['field-name']?exists>
                            <#list parameters['field-name'] as item>
                                <#assign i=item_index/>
                                <tr name="editTr">
                                    <td name="num">${i+1}</td>
                                    <td><label>${item}</label><input name="field-name" value="${item}"></td>
                                    <td>
                                        <label>${parameters['field-type'][i]}</label>
                                        <select name="field-type">
                                            <#list fieldTypeList as fieldType>
                                                <option value="${fieldType}">${fieldType}</option>
                                            </#list>
                                        </select>
                                    </td>
                                    <td><label>${parameters['field-description'][i]}</label><input name="field-description"
                                                                                                   value="${parameters['field-description'][i]}">
                                    </td>
                                    <td>
                                        <label>${parameters['field-not-null'][i]}</label>
                                        <select name="field-not-null">
                                            <option value="否">否</option>
                                            <option value="是">是</option>
                                        </select>
                                    </td>
                                    <td>
                                        <label>${parameters['prim-key'][i]}</label>
                                        <select name="prim-key">
                                            <option value="否">否</option>
                                            <option value="是">是</option>
                                        </select>
                                    </td>
                                </tr>
                            </#list>
                        </#if>

                    </table>
                </div>

            </div>
            <div id="part3" style="display: none;">
                <h2>外键</h2>

                <div style="width: 100%;">
                    <script>
                        function addFk() {
                            var trNum = $('#part3').find('tr').length - 1;
                            var tr = $('<tr name="editTr"></tr>')
                            var td = $('<td name="num"></td>');
                            tr.append(td);
                            td = $('<td><label>fk-' + trNum + '</label><input  onchange="changeKeyMapName(this)" name="fk-name" value="fk-' + trNum + '"></td>');
                            tr.append(td);
                            td = $('<td><label></label><select name="fk-type"><option>one</option><option>many</option></select></td>');
                            tr.append(td);
                            td = $('<td><label></label></td>');
                            tr.append(td);
                            var select = $('<select  name="key-map-fk-' + trNum + '-field-name"></select>');
                            td.append(select);
                            $('#part2').find('input[name="field-name"]').each(function () {
                                select.append('<option>' + $(this).val() + '</option>');
                            });
                            td = $('<td><label></label></td>');
                            tr.append(td);
                            var select = $('<select  onchange="getFieldList(this);"  name="rel-entity-name"></select>');
                            td.append(select);
                            var entityNameJson =${jsonData}.entityNamesJson;
                            select.append('<option selected>请选择</option>');
                            $.each(entityNameJson, function () {
                                select.append('<option>' + this["entityName"] + '</option>');
                            });
                            tr.append('<td><label></label></td>')
                            $('#part3').find('table').append(tr);
                            reInit('#part3');
                        }
                    </script>
                    <a class="button-blue" onclick="addFk()">新增</a>
                    <a class="button-blue" onclick="deleteTr()">删除</a>
                </div>
                <div class="table-wrapper">
                    <table>
                        <tr>
                            <th style="width: 45px; ">序号</th>
                            <th style="width: 220px; ">外键名称</th>
                            <th style="width: 150px; ">类型</th>
                            <th style="width: 220px; ">列</th>
                            <th style="width: 220px; ">参考表</th>
                            <th style=" width: 220px;">参考列</th>
                        </tr>
                        <#if parameters['fk-name']?exists>
                            <#list parameters['fk-name'] as item>
                                <#assign i=item_index/>
                                <tr name="editTr">
                                    <td name="num">${i+1}</td>
                                    <td><label>${item}</label><input onchange="changeKeyMapName(this)" name="fk-name"
                                                                     value="${item}"></td>
                                    <td>
                                        <label>${parameters['fk-type'][i]}</label>
                                        <select name="fk-type">
                                            <option>one</option>
                                            <option>many</option>
                                        </select>
                                    </td>
                                    <td>
                                        <label>${parameters["key-map-"+item+"-field-name"][0]}</label>
                                        <select name="key-map-${item}-field-name">
                                            <#if parameters['field-name']?exists>
                                                <#list parameters['field-name'] as fieldName>
                                                    <option>${fieldName}</option>
                                                </#list>
                                            </#if>
                                        </select>
                                    </td>
                                    <td>
                                        <label>${parameters['rel-entity-name'][i]}</label>
                                        <select onselect="getFieldList(this);" name="rel-entity-name">
                                            <#if entityNameList?exists>
                                                <option>请选择</option>
                                                <#list entityNameList as relEntity>
                                                    <option>${relEntity.entityName}</option>
                                                </#list>
                                            </#if>
                                        </select>
                                    </td>
                                    <td>
                                        <label>${parameters['key-map-'+item+'-rel-field-name'][0]}</label>
                                        <select name="key-map-${item}-rel-field-name">
                                            <#if fieldNameList?exists>
                                                <#list fieldNameList as field>
                                                    <option>${field}</option>
                                                </#list>
                                            </#if>
                                        </select>
                                    </td>
                                </tr>
                            </#list>
                        </#if>
                    </table>
                </div>
            </div>
        </div>
    </div>
</div>
</form>