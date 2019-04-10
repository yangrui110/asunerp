<div style="width: 200px;;">
    <input id="plugins4_q" type="text">
    <div id="tree"></div>
</div>

<script>
    var tree = $('#tree').jstree({
        plugins: ["sort", "types", "wholerow", 'dnd',"search"],
        "types": {"file": {"icon": "jstree-file"}},
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
    $('#tree').on("select_node.jstree", function (e, data) {
        if(data.node.a_attr.href!="#"){
            window.location.href=" viewTableData?dataSourceName="+data.node.a_attr.dataSourceName+"&entityName="+data.node.a_attr.entityName
        }
    });

    var to = false;
    $('#plugins4_q').keyup(function () {
        if(to) { clearTimeout(to); }
        to = setTimeout(function () {
            var v = $('#plugins4_q').val();
            $('#tree').jstree(true).search(v);
        }, 250);
    });
</script>