<script src="../js/sha1.js"></script>
<div id="user_login_List"></div>
<div hidden>
    <div  class="add-form">
        <div class="item"><label class="label">用户名</label>
            <div class="value"><input name="userLoginId" data-validation="required" autocomplete="off"></div>
        </div>
        <div class="item"><label class="label">密码</label>
            <div class="value"><input name="currentPassword"  data-validation="length"  data-validation-length="5-20"  type="password" autocomplete="off"></div>
        </div>
        <div class="item"><label class="label">有效</label>
            <div class="value">
                <select name="enabled">
                    <option>Y</option>
                    <option>N</option>
                </select>
            </div>
        </div>
        <div class="item"><label class="label">下线</label>
            <div class="value">
                <select name="hasLoggedOut">
                    <option>Y</option>
                    <option>N</option>
                </select>
            </div>
        </div>
    </div>


    <div id="resetPass">新密码：<input style="width: 200px;" name="currentPassword"  data-validation="length"  data-validation-length="5-20" ></div>
</div>



<script>
    var dataTable = $('#user_login_List');
    var resetPassBtn = $('<button  class=" button-blue resetPass  ">重置密码</button>');

    var table = fadp.dataTable(dataTable, {entityName: "UserLogin"}, {
        fieldList: [
            {fieldName: 'userLoginId', fieldText: '用户名', fieldType: 'id', index: 0, isPK: true}
            , {fieldName: 'enabled', fieldText: '有效', fieldType: 'indicator', index: 1}
            , {fieldName: 'hasLoggedOut', fieldText: '下线', fieldType: 'indicator', index: 2}
        ]
        , addUI: $(".add-form")
        , editUI: (function () {
           var editUI= $(".add-form").clone();
           editUI.find("[name=currentPassword]").parent().parent().remove();
           return editUI;
        })()
        , leftButton: [resetPassBtn]
        ,beforeSave:function(saveData) {
            var pass=saveData.fieldMap.currentPassword;
            if(pass==null||pass.length<5){
               delete saveData.fieldMap.currentPassword;
                return ;
            }else{
                saveData.fieldMap.currentPassword="{SHA}"+hex_sha1(saveData.fieldMap.currentPassword);
            }
        }
    });
    resetPassBtn.click(function () {

        if(table.checkSelected()){
            var form=$('#resetPass');
            fadp.validate(form);
            var dialog = fadp.dialog(form, {
                title: '重置密码',
                autoOpen: false,
                removeAfterClosed:false,
                buttons: [{
                    text: "保存", click: function () {
                        if(form.isValid()){
                            fadp.confirmInfo("确定重置所选账户的密码吗？",function () {
                                var pass="{SHA}"+hex_sha1(form.find("[name=currentPassword]").val());
                                fadp.ajax('resetPassword',{pkList:table.getSelectedPk(),password:pass},function () {
                                    fadp.showInfo("重置成功。");
                                },function () {
                                    fadp.showInfo("重置失败。");
                                });
                            });

                        }
                    }
                }]
            });
            dialog.show();
        }

    })


</script>