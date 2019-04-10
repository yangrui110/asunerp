<style>
    #armyOrg select {
        /*width: 600px;*/
        font-size: 16px;


    }
    #armyOrg .item {
        display: block;
        word-break: keep-all;
        /*position:relative;*/
        /*width:500px;*/

    }

    #armyOrg .item .label {
        width: 70px;
        text-align: right;
        display: block;margin-right: 5px;
        /*position: absolute;*/
    }
    #armyOrg .item .value{
        /*position: absolute;*/
        /*display:flow;*/
        display: inline-block;
    }
    .armyDiv{
        /*width: 120px;*/
        float: left;
        margin: 5px;
        padding: 2px;
        background-color: rgb(0, 113, 206);
        border-color: rgb(0, 100, 182);
        color: rgb(255, 255, 255);
        outline-color: rgb(255, 255, 255);
    }
    .armyDiv:hover{
        background: #000;
        cursor: pointer;
    }
    .armyDiv.active{
        background: #000;
    }

</style>

<div style="width: 200px;" class="ui-layout-west">
    <div id="tree"></div>
</div>

<div class="ui-layout-center">
    <h2>按照组织结构进行数据切分</h2>
    <div  id="armyOrg">
        <div class="item">
            <label class="label">第一级:</label>
            <div class="value" name="armyJun"></div>
        </div>
        <div class="item">
            <label class="label">第二级:</label>
            <div class="value" name="armyNv"></div>
        </div>
        <div class="item">
            <label class="label">第三级:</label>
            <div class="value" name="armyYing"></div>
        </div>
        <div class="item">
            <label class="label">第四级:</label>
            <div class="value" name="armyJar"></div>
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

    //清除下级下拉框
    function loopReset(selectName) {

        var select=$("[name=" + selectName + "]");
        select.empty();
        if (selectName != "armyJar") {
            var nextSelect = findNextClassify(select);
            var nextName = nextSelect.attr("name");
            //不是最后一级
            loopReset(nextName)
        }
    }

    // {dataSourceName:dataSourceName,armyJun:armyJun,armyNv:armyNv,armyJar:armyJar}
    function loadOrg(selectName, data) {
        var nextSelect = $("[name=" + selectName + "]");
        fadp.ajax("loadOrg", data, function (json) {
            // nextSelect.append("<option>请选择</option>");

            $.each(json, function (i, v) {
                var armyDiv=$("<label class='armyDiv' armyId='" + v.armyId + "'>" + v.armyName + "</label>");
                armyDiv.click(function (e) {
                    $(e.target).parent().find('.armyDiv').removeClass('active');
                    $(e.target).addClass("active");
                    var armyId = $(e.target).attr("armyId");
                    currentArmyId=armyId;
                    if($(e.target).parents(".value").attr("name")!="armyJar"){
                        var nextSelectName=findNextClassify($(e.target)).attr("name");
                        if(armyId){
                            loadOrg(nextSelectName, {
                                dataSourceName: dataSourceName,
                                armyId: armyId
                            });
                        }
                        loopReset(nextSelectName);
                    }
                });
                nextSelect.append(armyDiv);
            });
        });
    }
    function exportArmyData() {
        if(dataSourceName==null){
            return
        }
        window.location.href="exportArmyData?dataSourceName="+dataSourceName+"&armyId="+currentArmyId
    }
    function findNextClassify(armyDiv) {
        return armyDiv.parents(".item").next().find(".value");
    }
</script>