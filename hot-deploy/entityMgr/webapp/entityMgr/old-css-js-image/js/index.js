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

    /*  按钮的样式*/
    $("a,button,input").each(function (i, ele) {
        var style = $(ele).attr("class");
        var iconClass = /button-icon-[\S]*/.exec(style)
        if (iconClass) {
            var realIconClass = iconClass[0].replace(/button-icon-/, "");
            //$(ele).addClass("button-icon");
            //$(ele).css("background","url(\../../entityMgr/images/"+realIconClass+".png\) no-repeat left center ");
            var text=$(ele).text();
            $(ele).empty();
            $(ele).prepend('<b class="button-icon " style="background: url(\'../../entityMgr/images/'+realIconClass+'.png\') no-repeat left center;">'+text+'</b>');
            //$(ele).prepend('<img class="button-icon "src="../../entityMgr/images/' + realIconClass + '.png;">');
        }
        if (/button-[\S]*/.test(style)) {
            $(ele).addClass("button");
            var oldRgb = $(ele).css('background-color')
            $(ele).mouseover(function () {
                var rgb = oldRgb.match(/^rgb\((\d+),\s*(\d+),\s*(\d+)\)$/);
                function deeper(str) {
                    return Math.round(parseInt(str) * 0.90)
                }
                var backColor = "rgb(" + deeper(rgb[1]) + "," + deeper(rgb[2]) + "," + deeper(rgb[3]) + ")";
                $(ele).css('background-color', backColor);
            })
            $(ele).mouseout(function () {
                $(ele).css('background-color', oldRgb);
            })

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
    var dialog = art.dialog({
        title: '提示',
        fixed: false,
        width: 300,
        height: 50,
        content: content,
        ok: function () {
        }
    });
}
function confirmInfo(content,okFun) {
    var dialog = art.dialog({
        title: '提示',
        fixed: true,
        width: 300,
        height: 50,
        content: content,
        ok: function () {
            okFun()
        }
    });
}