<h2>数据引接</h2>
<div class="gap"></div>
<div id="main"></div>
<style>
    .choose-img {
        margin-left: 5px;
        margin-top: auto;
        cursor: pointer;
        background-image: url(../../entityMgr/images/button/add-black-simple.png);
        display: inline-block;
        width: 16px;
        height: 16px;
    }

    .entity-list-main .active {
        background-color: #bbddff;
    }
</style>
<div style="display: none;">
    <div class="add-dialog-content">
        <div class="add-form add-part-1">
            <div class="item"><label class="label">抽取模型id</label>

                <div class="value">
                    <input name="extractModelId" data-validation="required"><span class="choose-img chooseExtract"></span>
                </div>
            </div>
            <div class="item"><label class="label">存储数据库id</label>
                <div class="value">
                    <select name="saveDatasource"  data-validation="required">
                        <#list datasourceList as datasource>
                            <option value="${datasource.dataSourceName}">${datasource.dataSourceName}(${datasource.description})</option>
                        </#list>
                    </select>
                </div>
            </div>
            <div class="item"><label class="label">存储模型id</label>

                <div class="value">
                    <input name="saveModelId" data-validation="required"><span class="choose-img chooseSave"></span>
                </div>
            </div>
            <div class="item"><label class="label">描述</label>

                <div class="value"><textarea style="height: 100px;" name="description"></textarea></div>
            </div>

            <div class="item"><label class="label">数据同步方式</label>

                <div class="value">
                <select name="syncType">
                    <option>累加</option>
                    <option>根据最后同步时间累加</option>
                    <option>根据参数累加</option>
                    <option>累加及更新</option>
                    <option>根据最后同步时间累加及更新</option>
                    <option>根据参数累加及更新</option>
                </select>
                </div>
            </div>
        </div>

        <div style="display: none;" class="add-part-2">
        </div>
        <div class="gap"></div>
        <div>
            <button class="button-weak" onclick="nextStep(this)">下一步</button>
        </div>
    </div>


</div>

<script>
    var addUI = $('.add-dialog-content');
    var editUI = addUI.clone();
    function nextStep(ele) {
        var btn = $(ele);
        var pt1 = btn.parents('.add-dialog-content').find('.add-part-1');
        var pt2 = btn.parents('.add-dialog-content').find('.add-part-2');
        var str = btn.text();

        if (str == "下一步" && pt1.isValid()) {

            var extractModelId = pt1.find('[name=extractModelId]').val();
            var saveDatasource = pt1.find('[name=saveDatasource]').val();
            var saveModelId = pt1.find('[name=saveModelId]').val();
            pt1.hide();

            fadp.ajax('getExtractSaveFieldMapping', {
                extractModelId: extractModelId,
                saveDatasource: saveDatasource,
                saveModelId: saveModelId
            }, function (data) {
                pt2.empty();
                pt2.append('<label class="label">字段对应</label><div class="gap"></div>');
                var table = $('<table ><thead><tr><th>存储模型字段</th><th>抽取模型字段</th><th>默认值</th></tr></thead></table>')
                $.each(data.fieldMapping, function (i, item) {
                    var field = item.saveField;
                    var tr = $('<tr><td>' + field.text + '<input name="saveField" style="display:none;" value="' + field.name + '"></td></tr>');
                    var select = $('<select name="extractField"><option value="">请选择</option></select>');
                    $.each(data.extractField, function (j, field2) {
                        select.append($('<option ' + ((field2 == item.extractField) ? 'selected' : '') + '>' + field2 + '</option>'));
                    });
                    var td = $('<td></td>');
                    td.append(select);
                    tr.append(td);

                    tr.append('<td><input name="defaultValue" value="' + (item.defaultValue || '') + '"</td>');
                    table.append(tr)

                })
                var wrapper = $('<div style="width: 600px;" class="table-wrapper"></div>')
                wrapper.append(table)
                pt2.append(wrapper);
                pt2.show();
                btn.text("上一步");
            });

        } else if (str == "上一步") {
            pt2.hide();
            pt1.show();
            btn.text("下一步");
        }
    }
    var table = fadp.dataTable('#main', {dataSourceName: 'default', entityName: 'ExtractData'}, {
        addUI: addUI,
        editUI: editUI,
        saveFun: function (dialog, oldEntity) {
            var extractField = dialog.find('[name=extractField]');
            var error;
            if (extractField.length == 0) {
                error = "填写完整";
            }
            $.each(extractField, function (i, field) {
                if (!$(field).val()&&!$(field).parent().parent().find("[name=defaultValue]").val()) {
                    error = "请选择抽取模型字段或填写默认值"
                }
            });
            if (error) {
                fadp.showError(error);
            } else {
                var saveData = {
                    entityName: table.submitData.entityName,
                    fieldMap: {},
                    dataSourceName: table.submitData.dataSourceName
                };
                var pt1 = dialog.find('.add-part-1');
                pt1.find('[name]').each(function (i, ele) {
                    var input = $(ele);
                    saveData.fieldMap[input.attr('name')] = input.val();
                });

                var pt2 = dialog.find('.add-part-2');
                var list = [];
                saveData.fieldMap.fieldMapping = list;
                pt2.find('tbody tr').each(function (i, tr) {
                    var map = {};
                    list.push(map);
                    $(tr).find('input,select').each(function (i, ele) {
                        var input = $(ele);
                        map[input.attr('name')] = input.val();
                    })
                });

                if (oldEntity) {
                    saveData.PK = table.getSelectedPk()[0];
                }
                fadp.ajax('genericSave', saveData, function () {
                    dialog.hide();
                    table.refresh();
                })

            }

        }
    });
    table.element.bind('fadp.dataTable.ready', function () {
        var btn = $('<button class="button-blue">同步</button>');
        fadp.button(btn);
        table.element.find('.top-bar-left').append(btn);
        btn.click(function () {
            if (table.checkSelected(true)) {
                var extractData=table.getSelectedData()[0];
                var syncType=extractData.syncType;
                if(syncType.indexOf('参数')>-1){
                    fadp.ajax('getOne',{dataSourceName:'default',entityName:'ExtractModel',PK:extractData}, function (data) {
                       var modelParaList=data.modelParaList;
                        var div=$('<div></div>');

                        $.each(modelParaList, function (i, item) {
                            var item=$('<div class="item"><label class="label">'+item.paraAlias+'</label><div class="value"><input name="'+item.paraAlias+'" data-validation="required"></div></div>');
                            div.append(item);
                        });
                        fadp.validate(div);
                        fadp.dialog(div, {
                            autoOpen:true,
                            showReset: false, buttons: [{
                                text: '确定', click: function () {
                                    if(div.isValid()){
                                        var paraList=[];
                                        $.each(modelParaList, function (i, item) {
                                            var name=item.paraAlias;
                                            paraList.push({paraName:name,paraValue:div.find('[name='+name+']').val()});
                                        });
                                        extractData.paraList=paraList;
                                        sync(extractData);
                                    }

                                }
                            }]
                        })
                    })
                }else{
                    sync(extractData);
                }
                function sync(submitData){
                    fadp.ajax('extractData',submitData , function (data2) {
                        fadp.showInfo("同步完成");
                        table.refresh();
                    });
                }
            }
        })
    })
    var context = {}, context2 = {};
    choose(addUI.find('.chooseExtract'), context, 'extractDialog', 'ExtractModel', 'extractModelId');
    choose(editUI.find('.chooseExtract'), context2, 'extractDialog', 'ExtractModel', 'extractModelId');

    function choose(selector, context, dialogName, entityName, pkField) {
        $(selector).click(function (e) {
            if (!context[dialogName]) {
                var chooseExtract = fadp.dataTable('<div></div>', {dataSourceName: 'default', entityName: entityName}, {
                    showAddButton: false,
                    showEditButton: false,
                    showDeleteButton: false
                });
                chooseExtract.element.bind('fadp.dataTable.ready', function () {
                    context[dialogName] = fadp.dialog(chooseExtract.element, {
                        showReset: false, buttons: [{
                            text: "确定", click: function () {
                                var pkList = chooseExtract.getSelectedPk();
                                if (pkList.length == 0) {
                                    fadp.showError("请选择");
                                } else {
                                    $(e.target).prev().val(chooseExtract.getSelectedPk()[0][pkField]);
                                    $(e.target).parents('.add-part-1').isValid();
                                    context[dialogName].hide();
                                }
                            }
                        }]
                    });
                    context[dialogName].show();
                })

            } else {
                context[dialogName].show();
            }
        })
    }

    choose2(addUI.find('.chooseSave'), context);
    choose2(editUI.find('.chooseSave'), context2);
    function choose2(selector, context) {
        $(selector).click(function (e) {
            if (!context['chooseSaveDialog']) {
                var allTableList;
                currentDatabase =$(e.target).parents(".dialog-wrapper").find("[name=saveDatasource]").val();
                fadp.ajax('getTreeJson?id=' + currentDatabase + '', {}, function (data) {
                    var list = [];
                    $.each(data, function (i, v) {
                        list.push(v.a_attr);
                    })
                    showTables(list);
                    allTableList = list;
                    context['chooseSaveDialog'].show();
                })
                var tableToShow, showList = true;
                var entityMap = {};

                function showTables(arr) {//显示所有表
                    $.each(arr, function (i, v) {
                        entityMap[v['dataSourceName'] + '::' + v['entityName']] = v;
                    })
                    var div = $('<div class="entity-list-main" >' +
                            '<div class="button-bar" id="button_bar"><input style="float: right;" placeholder="搜索" class="search"></div>' +
                            '<div class="tableList" ></div>' +
                            '<div  class="bottom"><div class="right"><a class="item button-small button-icon-table-item" title="列表"></a><a title="详细信息" class="list button-small button-icon-table-list-info"></a></div></div>' +
                            '</div>');
                    fadp.button(div.find('.top-bar'));

                    div[0].onselectstart = new Function("return false");
                    fadp.button(div.find('.bottom'));
                    div.find('.bottom .item').click(function () {
                        showList = false;
                        addTableItem(container);
                    })
                    div.find('.bottom .list').click(function () {
                        showList = true;
                        addTableItem(container);
                    })
                    var container = div.find('.tableList');
                    tableToShow = arr;
                    addTableItem(container);
                    var wrapper = $('<div style="width: 950px;height: 550px;;"></div>');
                    wrapper.append(div);
                    context['chooseSaveDialog'] = fadp.dialog(wrapper, {
                        showReset: false, buttons: [{
                            text: "确定", click: function () {
                                var input = wrapper.find('span.active input');
                                if (input.length == 0) {
                                    fadp.showError("请选择");
                                } else {
                                    $(e.target).prev().val(input.val().split("::")[1]);
                                    $(e.target).parents('.add-part-1').isValid();
                                    context['chooseSaveDialog'].hide();
                                }
                            }
                        }]
                    });
                    div.find('.search').keyup(function () {
                        var keyWord = $(this).val();
                        tableToShow = [];
                        $.each(arr, function (k, v) {
                            if (v['entityName'].toLowerCase().indexOf(keyWord.toLowerCase()) > -1) {
                                tableToShow.push(v);
                            }
                        });
                        addTableItem(container);
                    });
                    return container

                }

                function addTableItem(container) {
                    container.empty();
                    function makeSpan(v) {
                        return '<span class="dbms-table-item"><input style="display: none;" value="' + v['dataSourceName'] + '::' + v['entityName'] + '" >' + v['entityName'] + '</span>';
                    }

                    if (showList) {
                        var table = $('<div class="table-wrapper"><table><thead><tr><th>表名</th><th>数据量（条）</th><th>描述</th></tr></thead></table></div>');

                        $.each(tableToShow, function (k, v) {
                            var tr = $('<tr>' +
                                    '<td style="width: 250px;text-align: left">' + makeSpan(v) + '</td>' +
                                    '<td>' + v.totalRow + '</td>' +
                                    '<td>' + v.description + '</td>' +
                                    '</tr>');
                            tr.click(function () {
                                table.find('.active').removeClass('active');
                                tr.addClass('active');
                                tr.find('span').addClass('active');
                            });
                            table.find('table').append(tr);
                        });
                        container.append(table);
                    } else {
                        $.each(tableToShow, function (k, v) {
                            var span = $(makeSpan(v));
                            span.click(function () {
                                table.find('.active').removeClass('active');
                                span.addClass('active');
                            });
                            container.append(span);
                        });
                    }
                }
            } else {
                context['chooseSaveDialog'].show();
            }

        })
    }

</script>