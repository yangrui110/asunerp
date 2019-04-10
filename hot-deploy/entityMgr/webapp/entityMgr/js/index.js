/**
 * Created by 陈林 on 2016/4/29.
 */
$(function () {
    $(".message").click(function(e){
        $(e.target).remove();
    })
    $('#check-all').click(function () {
        var checked = this.checked;
        $('#check-all').parents("table").find(":checkbox").each(function (i, ele) {
            if (ele.checked != checked) {
                $(ele).click();
            }
        })
    });
    $('#check-all').parents("table").find(":checkbox").click(function () {
        var tr = $(this).parents("tr");
        if (this.checked) {
            tr.addClass("checked");
        } else {
            tr.removeClass("checked");
        }
    });


    /*form验证*/
    $('.require').parents('li').find('.item-label').prepend('<b class="my-required">*</b>')
    $('form').submit(function(){
        return  checkForm();
    })
})
function checkForm(){
    var error="";
    $.each($('.require'),function(k,v){
        if(!$(v).val()){
            error+="\""+$(v).parents('li').find('.item-label').text().replace("\*","").replace(":","")+"\"不可为空\n"
        }
    });
    if(error!=""){
        showInfo(error)
        return false;
    }
   return true;
}
function getOnlyOneChecked(name) {
    var arr = $("[name = " + name + "]:checked");
    if (arr.length == 1) {
        return arr.val();
    }
    return null;
}
function checkChoose(name, chooseOne) {
    var arr = $("[name = " + name + "]:checked");
    if (arr.length == 0) {
        showInfo("请选择");
        return false;
    } else if (arr.length == 1) {
        return true;
    } else if (chooseOne) {
        showInfo("该操作只能作用于单个数据");
        return false;
    } else {
        return true;
    }
}

function commonDelete(actoin,name) {
    if (checkChoose(name, false)) {
        confirmInfo('确定吗？',function(){
            $("form").attr("action", actoin);
            $("form").submit();
        });
    }
    return false;
}
function commonEdit(url, name) {
    if (checkChoose(name, true)) {
        window.location = url + getOnlyOneChecked(name);
    }
    return false;
}


function showInfo(content) {
    fadp.showInfo(content);
}
function confirmInfo(content,okFun) {
    fadp.confirmInfo(content,okFun);
}