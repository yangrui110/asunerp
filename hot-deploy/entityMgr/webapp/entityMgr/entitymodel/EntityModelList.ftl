<div style="width: 200px;" class="ui-layout-west">
    <div id="tree"></div>
</div>
<style>
    .ui-menu-item .open-table {
        background-image: url(../../entityMgr/images/entity/open-table.png);
    }

    .ui-menu-item .edit-table {
        background-image: url(../../entityMgr/images/entity/edit-table.png);
    }

    .ui-menu-item .add-table {
        background-image: url(../../entityMgr/images/entity/add-table.png);
    }

    .ui-menu-item .delete-table {
        background-image: url(../../entityMgr/images/entity/delete-table.png);
    }

    .ui-menu-item .import-data {
        background-image: url(../../entityMgr/images/entity/import-data.png);
    }

    .ui-menu-item .export-data {
        background-image: url(../../entityMgr/images/entity/export-data.png);
    }
</style>

<style>
    .button-bar {
        background-color: #f6f6f6;
        height: 25px;
        line-height: 25px;
    }
</style>


<script>
    //构造树
    var tree = $('#tree').jstree({
        plugins: ["sort", "types", "wholerow", 'dnd'/*, "contextmenu"*/],
        /*    "contextmenu": {
         "items": customMenu
         },*/
        "types": {"file": {"icon": "../../entityMgr/images/dataTable.bmp"}, "root": {"icon": "../../entityMgr/images/dataBase.bmp"}},
        core: {
//            data:${json}
            data: {
                'url': function (node) {
                    return node.id === '#' ? 'getTreeJson' : "getTreeJson";
                },
                'data': function (node) {
                    return {'id': node.id};
                }
            }
        }
    });
    var menu1 = new fadp.contextMenu(tree, {
        menus: makeDataSourceMenu(),
        delegate: 'fuck'
    });

    var menu2 = new fadp.contextMenu(tree, {
        menus: makeTableMenu(),
        delegate: 'fuck'
    });

    $('#tree').mousedown(function (e) {
        if (e.button == 2) {
            var id = $(e.target).parents('.jstree-node').attr('id');
            if (id.indexOf("::") != -1) {
                menu2.show(e);
            } else {
                menu1.show(e);
            }
        }
    })

    tree.bind("select_node.jstree", function (e, data) {
        if (data.event.type == "click") {
            if (data.node.a_attr.href == "#") {//点击数据库
                currentDatabase = data.node.id;
                if (data.node.children.length == 0) {
                    fadp.loading.show();
                    tree.jstree('load_node', data.node.id, function () {
                        var container = showTables(makeList(arguments[0].children));
                        fadp.loading.hide();
                    });
                } else {
                    showTables(makeList(data.node.children));
                }
                function makeList(nodeIds) {
                    var list = [];
                    $.each(nodeIds, function (i, v) {
                        list.push(tree.jstree().get_node(v).a_attr);
                    });
                    return list;
                }
            } else {//点击表
                openTable(splitTableNodeId(data.node.id));
                //toUrl("viewTableData", data.node.id);
            }
        }
    });
</script>