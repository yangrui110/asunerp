<h2>字典项管理</h2>
<div class="gap"></div>
<div id="table"></div>
<script>
    fadp.dataTable('#table', { entityName: 'Dictionary'});
</script>
<!--



<h1 align="center">数据字典</h1>
<form id="dataSourceListForm" method="post" style="min-width: 900px;">

    <table>
        <tr>
            <td>模块</td>
            <td>
                <input type="text" value="${parameters.module?if_exists}" name="module">
                <input type="hidden" name="module_op" value="like">
            </td>
            <td>描述</td>
            <td>
                <input type="text" value="${parameters.description?if_exists}" name="description" style="width: 100%;">
                <input type="hidden" name="description_op" value="like">
            </td>
        </tr>
        <tr>
            <td>键</td>
            <td><input type="text" value="${parameters.value?if_exists}" name="value"></td>
            <td>值</td>
            <td><input type="text" value="${parameters.label?if_exists}" name="label"></td>
        </tr>
        <tr>
            <td colspan="4" align="center"><input type="submit" value="查询"></td>
        </tr>
    </table>
    <h2>
        <#if parameters._EVENT_MESSAGE_?exists>
            ${parameters._EVENT_MESSAGE_}
        </#if>
        <#if parameters._ERROR_MESSAGE_?exists>
            发生如下错误: ${parameters._ERROR_MESSAGE_}
        </#if>
    </h2>
    <button type="button" class="button"  onclick="addDic()">添加字典项</button>
    <table id="dataList" class="data-table">
        <tr>
            <th style="width: 110px;">操作</th>
            <th>模块</th>
            <th>键</th>
            <th>值</th>
            <th>描述</th>
        </tr>

        <#if list?exists>

            <#list list as item>
                <tr>
                    <td>
                        <button class="button" type="button" onclick="deleteDic(this)">删除</button>
                        <button  class="button"  type="button"  onclick="edit(this)">编辑</button>
                    </td>
                    <td><label  name="_module">${item.module?if_exists}</label></td>
                    <td><label  name="_value">${item.value?if_exists}</label></td>
                    <td><label  name="_label">${item.label?if_exists}</label></td>
                    <td><label  name="_description">${item.description?if_exists}</label></td>
                </tr>
            </#list>
        </#if>
    </table>
    <script>
        var saveButton='<button class="button" type="button"  onclick="saveDic(this)">保存</button>';
        var editButton='<button class="button" type="button" onclick="edit(this)">编辑</button>';
        var deleteButton='<button class="button" type="button" onclick="deleteDic(this)">删除</button>';
        function addDic(){
            var newEle =$(
                    '<tr>' +
                    '<td>' +
                    deleteButton +
                    saveButton+
                    ' </td>' +
                    ' <td><input type="text"  name="_module"></td>' +
                    ' <td><input  type="text" name="_value"></td>' +
                    '<td><input  type="text" name="_label"></td>' +
                    '<td><input  type="text" name="_description"></td>' +
                    ' </tr>');
            newEle.insertAfter( $('#dataList').find("tr").eq(1));
        }
        function edit($ele){
            changeEle($ele,"module");
            changeEle($ele,"value");
            changeEle($ele,"label");
            changeEle($ele,"description");
            $(saveButton).insertBefore($($ele));
            $($ele).remove();
        }
        function changeEle($ele,name){
            var ele=$($ele).parent().parent().find('label[name="_'+name+'"]');
            var value=ele.text();
            var name=ele.attr("name");
            ele.parent().append($('<input type="text" name="'+name+'" value="'+value+'">'));
            ele.remove();
        }
        function changeBackEle($ele,name){
            var ele=$($ele).parent().parent().find('input[name="_'+name+'"]');
            var value=ele.val();
            var name=ele.attr("name");
            ele.parent().append($('<label name="'+name+'">'+value+'</label>'));
            ele.remove();
        }
        function deleteDic($ele){
            var module=getValueByName($ele,"module");
            var value=getValueByName($ele,"value");
            var obj={};
            obj.module=module;
            obj.value=value;
            var tr=$($ele).parent().parent();
            $.post('deleteDictionary',obj,function(code){
                if(code=="success"){
                    var tr=$($ele).parent().parent();
                   tr.remove();
                }else{
                    alert(code);
                }
            });
        }
        function getValueByName($ele,name){
            var v1= $($ele).parent().parent().find('input[name="_'+name+'"]').val();
            if(!v1){
                v1= $($ele).parent().parent().find('label[name="_'+name+'"]').text();
            }
            return v1;
        }
        function saveDic($ele){
            var obj={};
            obj.module=getValueByName($ele,"module");
            obj.value=getValueByName($ele,"value");
            obj.label=getValueByName($ele,"label");
            obj.description=getValueByName($ele,"description");
            $.post("dictionary",obj,function(code){
                if(code=="success"){
                    changeBackEle($ele,"module");
                    changeBackEle($ele,"value");
                    changeBackEle($ele,"label");
                    changeBackEle($ele,"description");
                    $(editButton).insertBefore($($ele));
                    $($ele).remove();
                }else{
                    alert(code);
                }
            });
        }
    </script>-->
