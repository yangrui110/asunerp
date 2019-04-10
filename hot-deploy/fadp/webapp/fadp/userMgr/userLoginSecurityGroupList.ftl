<div id="dataList"></div>
<div hidden>
    <div class="add-form">
        <div class="item"><label class="label">用户名</label>
            <div class="value has-error"><input name="userLoginId" data-validation="required" ></div>
        </div>
        <div class="item"><label class="label">权限组</label>
            <div class="value has-error">
                <select name="groupId">
                    <#list groupList as group>
                        <option>${group.groupId}</option>
                    </#list>
                </select>
            </div>
        </div>

    </div>


</div>


<script>
    var dataTable = $('#dataList');
    var resetPassBtn = $('<button  class=" button-blue resetPass  ">重置密码</button>');

    var table = fadp.dataTable(dataTable, {entityName: "UserLoginSecurityGroup"}, {
        fieldList: [
            {fieldName: 'userLoginId', fieldText: '用户名', fieldType: 'id', index: 0, isPK: true}
            , {fieldName: 'groupId', fieldText: '权限组', fieldType: 'id', index: 1, isPK: true}
            , {fieldName: 'fromDate', fieldText: '有效期起', fieldType: 'date-time', index: 2}
            , {fieldName: 'thruDate', fieldText: '有效期止', fieldType: 'date-time', index: 3}
        ]
        ,addUI:$(".add-form")
        , showEditButton: false
        , beforeSave: function (saveData) {
            saveData.fieldMap.fromDate=fadp.getNowTimestamp();
        }
    });


</script>