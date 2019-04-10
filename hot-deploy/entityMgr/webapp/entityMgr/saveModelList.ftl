<script>
    var allTableList;currentDatabase = 'savemodel';
    fadp.ajax('getTreeJson?id='+currentDatabase+'', {}, function (data) {
        var list = [];
        $.each(data, function (i, v) {
            list.push(v.a_attr);
        })
        showTables(list);
        allTableList = list;
    })


    var tableToShow, showList = true;
    var entityMap = {};
    function showTables(arr) {//显示所有表
        $.each(arr, function (i, v) {
            entityMap[v['dataSourceName'] + '::' + v['entityName']] = v;
        })
        $('.ui-layout-center').empty();
        var addDiv=$('<div class="top-bar" ><button class="button-blue add-table">添加存储模型</button></div>');
        $('.ui-layout-center').append( addDiv);
        fadp.button(addDiv);
        addDiv.find('button').click(function () {
            getScreen('entityModel?dataSourceName=' + currentDatabase);
        })
        var div = $('<div class="entity-list-main" >' +
                '<div class="button-bar" id="button_bar"><label class="button-bar-info"></label><input style="float: right;" placeholder="搜索" class="search"></div>' +
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
        var info = div.find('.button-bar-info');
        info.append(" 当前" + arr.length + "个存储模型");
        var container = div.find('.tableList');
        tableToShow = arr;
        addTableItem(container);
        var wrapper=$('<div style="position: absolute;top: 50px;bottom: 5px;left: 10px;right: 10px;;"></div>');
        wrapper.append(div);
        $('.ui-layout-center').append(wrapper);
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
                getScreen('entityModel?dataSourceName=' + dataSourceName);
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
            return '?dataSourceName=' + id[0] + '&entityName=' + id[1];
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
            {label: "导入数据", icon: '../../entityMgr/images/entity/import-data.png', click: null},
            {label: "导出数据", icon: '../../entityMgr/images/entity/export-data.png', click: null},
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
        var ele = $('<div></div>');
        $('.ui-layout-center').empty();
        addBack();
        $('.ui-layout-center').append(ele);
        fadp.dataTable(ele, {dataSourceName: v['dataSourceName'], entityName: v['entityName']});
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
            addBack();
            $('.ui-layout-center').append(html);
            $('.ui-layout-center').find('#part1 tr:first').css('display','none');
            fadp.loading.hide();

        })
    }
    function addBack(){
        $('.ui-layout-center').append('<div><button class="button-weak back-to-list"><返回</button></div>');
        var back = $('.ui-layout-center').find('.back-to-list');
        fadp.button(back);
        back.click(function () {
            window.location.href = window.location.href;
        });
    }
    function splitTableNodeId(id) {
        var arr = id.split("::");
        return {"dataSourceName": arr[0], "entityName": arr[1]};
    }


</script>