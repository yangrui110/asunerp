<#assign fieldTypeList=["","文本","数值","精确到年","精确到月","精确到日","精确到秒"]>
<#assign unitsList=["","吨","千克","米","厘米","小时","天"]>
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
            case '维度':
                activePartById('part2');
                break;
            case '属性':
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
        td = $('<td><label></label><input name="dimField"></td>');
        tr.append(td);
        td = $('<td><label></label><select name="dimFieldType"><#list fieldTypeList as item><option>${item}</option></#list></select></td>');
        tr.append(td);
        td = $('<td><label></label><input name="dimFieldDescription"></td>');
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
       var mongoCollectionName= $(ele).prev().text();
        var td=$(ele).parent().next();
        td.find('select').remove();
        td.find('label').text("");
        if(mongoCollectionName){
            $.post('getFieldList',{mongoCollectionName:mongoCollectionName},function(result){
                var select=$('<select name="key-map-'+td.parent().find('[name="attrField"]').val()+'-rel-dimFieldname"></select>')
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
                $(this).attr('name',$(this).attr('name').replace('key-map-'+pre+'-dimFieldname','key-map-'+$(ele).val()+'-dimFieldname'))
                $(this).attr('name',$(this).attr('name').replace('key-map-'+pre+'-rel-dimFieldname','key-map-'+$(ele).val()+'-rel-dimFieldname'))
        });
    }
    function formSubmit() {
        var check=function(names){
            var b=true;
            $(names.split(",")).each(function(i,name){
                $('[name="'+name+'"]').each(function(k,input){
                    if(!$(input).val()){
                        b=false;
                    }
                })
            })
            return b;
        }
        if(!check("dimType,attrField,attrType")){
            alert("模型名称,维度名称,属性名称必填!");
            return;
        }

        $('form').ajaxSubmit({
            success: function (data) {
                if (data == "success") {
                    alert("保存成功");
                } else {
                    alert(data);
                }
            }
        });
    }
</script>

<form method="post" name="form">


<div style="width: 900px;">
    <a class="button" href="mongoCollectionList">返回元数据列表</a>
    <div>
    <div style="width: 100px; float: left; padding:10px;">
        <div class="my-menu my-button active" onclick="buttonClick(this)">基本信息</div>
        <div class="my-menu my-button" onclick="buttonClick(this)">维度</div>
        <div class="my-menu my-button" onclick="buttonClick(this)">属性</div>
        <div class="my-submit" onclick="formSubmit()">保存</div>
    </div>
    <div style="width: 1040px; height: 100%;float: left; ">
        <div id="part1">
            <h2>基本信息</h2>
            <table>
                <tr  name="editTr">
                    <td style="width: 200px;">模型名称
                        <div class="my-required"></div>
                    </td>
                    <td>
                        <label>${parameters.mongoCollectionName?if_exists}</label>
                        <#if !parameters.mongoCollectionName?exists>
                            <input name="mongoCollectionName" type="text" class="my-full-item">
                        </#if>
                    </td>
                </tr>
                <tr  name="editTr">
                    <td>备注</td>
                    <td><label>${parameters['description']?if_exists}</label><input name="description" value="${parameters['description']?if_exists}" type="text" class="my-full-item"></td>
                </tr>
            </table>
        </div>
        <div id="part2" style="display: none;">
            <h2>维度定义</h2>
            <div style="width: 100%;">
                <span class="my-button" onclick="addTr2BaseFieldTable()">新增</span>
                <span class="my-button" onclick="insertIntoBaseFieldTable()">插入</span>
                <span class="my-button" onclick="deleteTr()">删除行</span>
                <span class="my-button" onclick="moveTr('MoveUp')">上移</span>
                <span class="my-button" onclick="moveTr()">下移</span>
            </div>
            <table>
                <tr>
                    <td style="width: 33px;">序号</td>
                    <td style="width: 220px;">名称</td>
                    <td style="width: 150px;">类型</td>
                    <td>备注</td>
                </tr>
                <#if parameters['dimField']?exists>
                    <#list parameters['dimField'] as item>
                        <#assign i=item_index/>
                        <tr name="editTr">
                            <td name="num">${i+1}</td>
                            <td><label>${item}</label><input name="dimField" value="${item}"></td>
                            <td>
                                <label>${parameters['dimFieldType'][i]}</label>
                                <select name="dimFieldType">
                                    <#list fieldTypeList as fieldType>
                                        <option value="${fieldType}">${fieldType}</option>
                                    </#list>
                                </select>
                            </td>
                            <td><label>${parameters['dimFieldDescription'][i]}</label><input name="dimFieldDescription" value="${parameters['dimFieldDescription'][i]}"></td>
                        </tr>
                    </#list>
                </#if>

            </table>
        </div>
        <div id="part3" style="display: none;">
            <h2>属性定义</h2>
            <div style="width: 100%;">
                <script>
                    function addFk(){
                        var trNum=$('#part3').find('tr').length-1;
                        var tr = $('<tr name="editTr"></tr>')
                        var td = $('<td name="num"></td>');
                        tr.append(td);
                        td = $('<td><label></label><input name="attrField"></td>');
                        tr.append(td);
                        td = $('<td><label></label><select name="attrFieldType"><#list fieldTypeList as item><option>${item}</option></#list></select></td>');
                        tr.append(td);
                        td = $('<td><label></label><input name="attrFieldDescription"></td>');
                        tr.append(td);

                        $('#part3').find('table').append(tr);
                        reInit('#part3');
                    }
                </script>
                <span class="my-button" onclick="addFk()">新增</span>
                <span class="my-button" onclick="deleteTr()">删除</span>
            </div>
            <table>
                <tr>
                    <td style="width: 33px; ">序号</td>
                    <td style="width: 220px; ">名称</td>
                    <td style="width: 150px; ">类型</td>
                    <!--<td style="width: 220px; ">单位</td>-->
                    <td style="width: 220px; ">备注</td>
                </tr>
                <#if parameters['attrField']?exists>
                    <#list parameters['attrField'] as item>
                        <#assign i=item_index/>
                        <tr name="editTr">
                            <td name="num">${i+1}</td>
                            <td><label>${item}</label><input onchange="changeKeyMapName(this)" name="attrField" value="${item}"></td>
                            <td>
                                <label>${parameters['attrFieldType'][i]}</label>
                                <select name="attrFieldType">
                                    <#list fieldTypeList as fieldType>
                                        <option value="${fieldType}">${fieldType}</option>
                                    </#list>
                                </select>
                            </td>
                            <td><label>${parameters['attrFieldDescription'][i]}</label><input name="attrFieldDescription" value="${parameters['attrFieldDescription'][i]}"></td>
                        </tr>
                    </#list>
                </#if>
            </table>
        </div>
    </div>
    </div>
</div>
</form>