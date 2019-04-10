<style>
    #armyOrg select {
        width: 600px;
        font-size: 16px;

    }
    #armyOrg .item{
        width: 100%;
    }
</style>

<div style="width: 200px;" class="ui-layout-west">
    <div id="tree"></div>
</div>

<div class="ui-layout-center">
    <div class="query-form" id="armyOrg">
        <div class="item">
            <label class="label">第一级</label>
            <select class="value" name="armyJun"></select>
        </div>
        <div class="item">
            <label class="label">第二级</label>
            <select class="value" name="armyNv"></select>
        </div>
        <div class="item">
            <label class="label">第三级</label>
            <select class="value" name="armyYing"></select>
        </div>
        <div class="item">
            <label class="label">第四级</label>
            <select class="value" name="armyJar"></select>
        </div>
    </div>
    <h1></h1>
    <button class="button-blue" style="float: right;" onclick="exportArmyData()">导出当前组织基础数据</button>
</div>


<style>

</style>




<script>
    var dataSourceName, armyJun, armyNv, armyYing, armyJar;
    var currentArmyId="";
    $(function () {
        //构造树
        var tree = $('#tree').jstree({
            plugins: ["sort", "types", "wholerow", 'dnd'],
            "types": {"file": {"icon": "../../entityMgr/images/dataTable.bmp"}, "root": {"icon": "../../entityMgr/images/dataBase.bmp"}},
            core: {data:${json!}}
        });

        tree.bind("select_node.jstree", function (e, data) {
            if (data.event.type == "click") {
                if (data.node.a_attr.href == "#") {//点击数据库
                    currentArmyId="";
                    dataSourceName = data.node.id;
                    loadOrg("armyJun", {dataSourceName: dataSourceName});
                    loopReset("armyJun");

                }
            }
        });
    });
    var form = $("#armyOrg");
    form.find("select").change(function (e) {
        var armyId = $(e.target).val();
        currentArmyId=armyId;
        if($(e.target).attr("name")!="armyJar"){
            var nextSelectName=$(e.target).parent().next().find("select").attr("name");
            if(armyId){
                loadOrg(nextSelectName, {
                    dataSourceName: dataSourceName,
                    armyId: armyId
                });
            }
            loopReset(nextSelectName);


        }



    });
    //清除下级下拉框
    function loopReset(selectName) {

        var select=$("[name=" + selectName + "]");
        select.empty();
        if (selectName != "armyJar") {
            var nextSelect = select.parent().next().find("select");
            var nextName = nextSelect.attr("name");
            //不是最后一级
            loopReset(nextName)
        }
    }

    // {dataSourceName:dataSourceName,armyJun:armyJun,armyNv:armyNv,armyJar:armyJar}
    function loadOrg(selectName, data) {
        var nextSelect = $("[name=" + selectName + "]");
        fadp.ajax("loadOrg", data, function (json) {
            nextSelect.append("<option>请选择</option>")

            $.each(json, function (i, v) {
                nextSelect.append("<option value='" + v.armyId + "'>" + v.armyName + "</option>")
            });
        });

    }
    function exportArmyData() {
        if(dataSourceName==null){
            return
        }
        window.location.href="exportArmyData?dataSourceName="+dataSourceName+"&armyId="+currentArmyId
    }
</script>