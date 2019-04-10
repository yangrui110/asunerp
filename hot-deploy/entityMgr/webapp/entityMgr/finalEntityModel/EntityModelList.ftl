<div style="width: 200px;" class="ui-layout-west">
    <div id="tree"></div>
</div>
<style>
    .ui-menu-item .open-table{  background-image: url(../../entityMgr/images/entity/open-table.png);  }
    .ui-menu-item .edit-table{  background-image: url(../../entityMgr/images/entity/edit-table.png);  }
    .ui-menu-item .add-table{  background-image: url(../../entityMgr/images/entity/add-table.png);  }
    .ui-menu-item .delete-table{  background-image: url(../../entityMgr/images/entity/delete-table.png);  }
    .ui-menu-item .import-data{  background-image: url(../../entityMgr/images/entity/import-data.png);  }
    .ui-menu-item .export-data{  background-image: url(../../entityMgr/images/entity/export-data.png);  }
</style>
<script>
    //构造树
    var tree = $('#tree').jstree({
        plugins: ["sort", "types", "wholerow", 'dnd',"contextmenu"],
        "contextmenu" : {
            "items" : customMenu
        },
        "types": {"file": {"icon": "../../entityMgr/images/dataTable.bmp"},"root": {"icon": "../../entityMgr/images/dataBase.bmp"}},
        core: {
//            data:${json}
            data: {
                'url': function (node) {
                    return node.id === '#' ? 'getTreeJson' : "getTreeJson";
                },
                'data' : function (node) {
                    return { 'id' : node.id };
                }
            }
        }
    });
    var currentTableArray;
    var currentDatabase;
    var content;
    //点击树
    $('#tree').bind("select_node.jstree", function (e, data) {
        if(window.event.which==1){
            if(data.node.a_attr.href=="#"){//点击数据库
                currentDatabase=data.node.id;
                if(data.node.children.length==0){
                    tree.jstree('load_node',data.node.id,function(){
                        currentTableArray=arguments[0].children;
                        showTables(currentTableArray);
                    });
                }else{
                    currentTableArray=data.node.children;
                    showTables(currentTableArray);
                }

            }else{//点击表
                toUrl("viewTableData",data.node.id);
            }
        }
    });

    function showTables(arr){//显示所有表
        $('.ui-layout-center').empty();

        var div=$('<div><div class="button-bar" id="button_bar"><label class="button-bar-info"></label><input style="float: right;" placeholder="搜索" class="search"></div><div class="tableList" ></div></div>');
        var info= div.find('.button-bar-info');
        info.append("数据库："+  currentDatabase+" 当前"+arr.length+"个表");
        var container=div.find('.tableList');
        addTableItem(container,arr);
        $('.ui-layout-center').append(div);
        div.find('.search').keyup(function(){
            var keyWord=$(this).val();
            var newArr=[];
            $.each(currentTableArray,function(k,v){
                if(v.split("::")[1].toLowerCase().indexOf(keyWord.toLowerCase())>-1){
                    newArr.push(v);
                }
            });
            addTableItem(container,newArr);
        })

    }
    function addTableItem(container,arr){
        container.empty();
        $.each(arr,function(k,v){
            var span=$('<span class="dbms-table-item"><input value="'+v+'" hidden>'+ v.split("::")[1]+'</span>');
            span.dblclick(function(){
                toUrl('viewTableData',span.find('input').val());
            });
            container.append(span);
        });
    }
    function customMenu(node){
        if(node.a_attr.href=="#"){
            return {
                '新建': makeMenuItemObject('新建','entityModel','../../entityMgr/images/dataTable.bmp',node)
            }
        }else{
            return {
                '打开表': makeMenuItemObject('打开表','viewTableData','../../entityMgr/images/entity/open-table.png',node),
                '编辑表': makeMenuItemObject('编辑表','entityModel','../../entityMgr/images/entity/edit-table.png',node),
                '删除': makeMenuItemObject('删除','deleteEntityModel','../../entityMgr/images/entity/delete-table.png',node),
                '导入数据': makeMenuItemObject('导入数据','importData','../../entityMgr/images/entity/import-data.png',node),
                '导出数据':     makeMenuItemObject('导出数据','exportData','../../entityMgr/images/entity/export-data.png',node)
            }
        }
    }
    function makeMenuItemObject(label,url,icon,node){
        if(node){//在jstree上。
            return {label:label,action:function(obj){
                toUrl(url,node.id);
            },icon:icon};
        }else{
            return {title:label,action:function(event, ui){
                toUrl(url,$(ui.target).find('input').val());
            },uiIcon:icon};
        }
    }
    function toUrl(url,id){

        var obj=id.split('::');
        var src=url+"?dataSourceName="+obj[0]  ;
        if(obj.length==2){
            src+="&entityName="+obj[1];
        }
        if(url.indexOf("delete")>-1){
            confirmInfo('确定删除吗？',function(){
                window.location.href=src;
            })
        }else{
            $('.ui-layout-center').empty();
            $('.ui-layout-center').append('<div  style="height:100%"><iframe src="'+src+'" style="width:100%;height:100%"  frameborder="0" ></iframe></div>');
        }

    }
    function splitTableNodeId(id){
        var arr=id.split("::");
        return {"dataSourceName":arr[0],"entityName":arr[1]};
    }
    $(function(){

        <#if parameters.dataSourceName??>
        tree.bind('loaded.jstree',function(){
            tree.jstree('open_node','${parameters.dataSourceName}');
        });
        </#if>


        $(document).contextmenu({
            delegate: ".dbms-table-item",
            show:null,
            autoFocus: true,
            preventContextMenuForPopup: true,
            preventSelect: true,
            taphold: true,
            menu: [
                makeMenuItemObject('打开表','viewTableData','open-table'),
                makeMenuItemObject('设计表','entityModel','edit-table'),
                makeMenuItemObject('删除表','deleteEntityModel','delete-table'),
                {title: "----"},
                makeMenuItemObject('导入数据','importData','import-data'),
                makeMenuItemObject('导出数据','exportData','export-data'),
//                makeMenuItemObject('打开表','viewTableData','ui-icon-scissors')
            ]

        });
    })



</script>
<style>
    .button-bar{
        background-color: #f6f6f6;height: 35px;
        padding-top: 6px;;
    }
</style>
<div class="my-hidden" style="display: none;"><div class="button-bar" id="button_bar"><label class="button-bar-info"></label><input style="float: right;" placeholder="搜索" class="search"></div><div class="tableList" ></div></div>