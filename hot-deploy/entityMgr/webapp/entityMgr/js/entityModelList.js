/**
 * Created by 陈林 on 2016/6/9.
 */

var currentDatabase = 'default';


var tableToShow, showList = false;
var entityMap = {};

function showTables(arr) {//显示所有表
    $.each(arr, function (i, v) {
        entityMap[v['dataSourceName'] + '::' + v['entityName']] = v;
    })
    $('.ui-layout-center').empty();

    var div = $('<div class="entity-list-main" >' +
        '<div class="button-bar" id="button_bar"><label class="button-bar-info"></label><input style="float: right;" placeholder="搜索" class="search"></div>' +
        '<div class="tableList" ></div>' +
        '<div  class="bottom"><div class="right"><a class="item button-small button-icon-table-item" title="列表"></a><a title="详细信息" class="list button-small button-icon-table-list-info"></a></div></div>' +
        '</div>');
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
    var info = div.find('.button-bar-info');
    info.append("数据库：" + currentDatabase + " 当前" + arr.length + "个表");
    var container = div.find('.tableList');
    tableToShow = arr;
    addTableItem(container);
    $('.ui-layout-center').append(div);
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

function makeDataSourceMenu() {
    return [
        {
            label: "新建表", icon: '../../entityMgr/images/dataTable.bmp', click: function (e, e2) {
                var dataSourceName = tree.jstree().get_node(e2.target).id;
                getScreen('entityModel?dataSourceName=' + encodeURIComponent(dataSourceName));
            }
        },

        {
            label: "导入数据", icon: '../../entityMgr/images/entity/import-data.png', click: function (e, e2, li) {
                var data = tree.jstree('get_node', e2.target).original;

                var dataSourceName = data.id;
                var dialog = fadp.dialog("<div><button id='importXmlData'>上传文件</button></div>" +
                    "<div style='margin-top: 10px;'><textarea style='width: 800px;' rows='20' id='xmlTxt'></textarea><div><button id='importXmlTxtData'>导入文本</button></div></div>", {showReset: false, buttons: []});
                dialog.show();
                $('#xmlTxt').val('<entity-engine-xml>\n</entity-engine-xml>');
                $('#importXmlTxtData').click(function () {
                    fadp.ajax('importXmlTxtData?dataSourceName=' + encodeURIComponent(dataSourceName), {xmlTxt: $('#xmlTxt').val()},function () {
                        dialog.remove()
                    })
                });
                fadp.simpleUpload('../importXmlData?dataSourceName=' + encodeURIComponent(dataSourceName), 'importXmlData', function (uploaded) {
                    dialog.remove()
                });
            }
        }
    ];
}

function makeTableMenu() {
    function getId(e2) {
        var v = $(e2.target).find('input').val();
        if (!v) {
            v = tree.jstree().get_node(e2.target).id;
        }
        return v;
    }

    function getParams(id) {
        id = id.split("::");
        return '?dataSourceName=' + encodeURIComponent(id[0]) + '&entityName=' + id[1];
    }

    return [
        {
            label: "打开表", icon: '../../entityMgr/images/entity/open-table.png', click: function (e, e2) {
                openTable(entityMap[getId(e2)]);
            }
        },
        {
            label: "设计表", icon: '../../entityMgr/images/entity/edit-table.png', click: function (e, e2) {
                getScreen('entityModel' + getParams(getId(e2)));
            }
        },
        {
            label: "删除表", icon: '../../entityMgr/images/entity/delete-table.png', click: function (e, e2) {
                fadp.confirmInfo('确定删除该表吗？', function () {
                    $.get('deleteEntityModel' + getParams(getId(e2)), function () {
                        window.location.href = window.location.href;
                    })
                });

            }
        },

        {
            label: "导出数据", icon: '../../entityMgr/images/entity/export-data.png', click: function (e, e2, li) {
                var tableStr = $(e2.target).find('input').val();
                var arr = tableStr.split("::");
                var dataSourceName = arr[0];
                var tableNames = arr[1];
                var myRound = "data" + new Date().getTime();
                window.location.href = "XMLDownLoad?store=true&dataSourceName=" + encodeURIComponent(dataSourceName) + "&tableNames=" + tableNames;
                // $.post("XMLDownLoad?store=true&dataSourceName=" + dataSourceName + "&tableNames=" + tableNames + "&myRound=" + myRound, function () {
                //     window.location.href = 'download?myRound=' + myRound + "&store=false";
                // });
            }
        }
    ];
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
            tr.find('.dbms-table-item').dblclick(function () {
                openTable(v);
            });
            table.find('table').append(tr);
        });
        container.append(table);
    } else {
        $.each(tableToShow, function (k, v) {
            var span = $(makeSpan(v));
            span.dblclick(function () {
                openTable(v);
            });
            container.append(span);
        });
    }
    fadp.contextMenu(container, {
        menus: makeTableMenu(),
        //showOnFocusOn:true,
        //position:'bottom',
        delegate: ".dbms-table-item"
    });
}

function openTable(v) {
    // var ele = $('<div></div>');
    // $('.ui-layout-center').empty();
    // $('.ui-layout-center').append(ele);
    // fadp.dataTable(ele, {dataSourceName: v['dataSourceName'], entityName: v['entityName']});

    window.open("genericCURD?dataSourceName=" + encodeURIComponent(v.dataSourceName) + "&entityName=" + v.entityName);
}


function getScreen(url) {
    fadp.loading.show();
    $.get(url, function (html) {
        var bodyStart = html.indexOf('<body>');
        if (bodyStart != -1) {
            var bodyEnd = html.indexOf('</body>');
            html = html.substr(bodyStart + 6, bodyEnd);
        }
        $('.ui-layout-center').empty();
        $('.ui-layout-center').append(html);
        fadp.loading.hide();

    })
}

function splitTableNodeId(id) {
    var arr = id.split("::");
    return {"dataSourceName": arr[0], "entityName": arr[1]};
}

$(function () {

    /*    if(dataSourceName){
     tree.bind('loaded.jstree',function(){
     tree.jstree('open_node','${parameters.dataSourceName}');
     });
     }*/


})

