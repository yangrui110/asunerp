//格式化时间
Date.prototype.Format = function (fmt) { //author: meizz
    var o = {
        "M+": this.getMonth() + 1, //月份
        "d+": this.getDate(), //日
        "h+": this.getHours(), //小时
        "m+": this.getMinutes(), //分
        "s+": this.getSeconds(), //秒
        "q+": Math.floor((this.getMonth() + 3) / 3), //季度
        "S": this.getMilliseconds() //毫秒
    };
    if (/(y+)/.test(fmt)) fmt = fmt.replace(RegExp.$1, (this.getFullYear() + "").substr(4 - RegExp.$1.length));
    for (var k in o)
        if (new RegExp("(" + k + ")").test(fmt)) fmt = fmt.replace(RegExp.$1, (RegExp.$1.length == 1) ? (o[k]) : (("00" + o[k]).substr(("" + o[k]).length)));
    return fmt;
};

jQuery.base64 = ( function( $ ) {

    var _PADCHAR = "=",
        _ALPHA = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/",
        _VERSION = "1.0";


    function _getbyte64( s, i ) {
        // This is oddly fast, except on Chrome/V8.
        // Minimal or no improvement in performance by using a
        // object with properties mapping chars to value (eg. 'A': 0)

        var idx = _ALPHA.indexOf( s.charAt( i ) );

        if ( idx === -1 ) {
            throw "Cannot decode base64";
        }

        return idx;
    }


    function _decode( s ) {
        var pads = 0,
            i,
            b10,
            imax = s.length,
            x = [];

        s = String( s );

        if ( imax === 0 ) {
            return s;
        }

        if ( imax % 4 !== 0 ) {
            throw "Cannot decode base64";
        }

        if ( s.charAt( imax - 1 ) === _PADCHAR ) {
            pads = 1;

            if ( s.charAt( imax - 2 ) === _PADCHAR ) {
                pads = 2;
            }

            // either way, we want to ignore this last block
            imax -= 4;
        }

        for ( i = 0; i < imax; i += 4 ) {
            b10 = ( _getbyte64( s, i ) << 18 ) | ( _getbyte64( s, i + 1 ) << 12 ) | ( _getbyte64( s, i + 2 ) << 6 ) | _getbyte64( s, i + 3 );
            x.push( String.fromCharCode( b10 >> 16, ( b10 >> 8 ) & 0xff, b10 & 0xff ) );
        }

        switch ( pads ) {
            case 1:
                b10 = ( _getbyte64( s, i ) << 18 ) | ( _getbyte64( s, i + 1 ) << 12 ) | ( _getbyte64( s, i + 2 ) << 6 );
                x.push( String.fromCharCode( b10 >> 16, ( b10 >> 8 ) & 0xff ) );
                break;

            case 2:
                b10 = ( _getbyte64( s, i ) << 18) | ( _getbyte64( s, i + 1 ) << 12 );
                x.push( String.fromCharCode( b10 >> 16 ) );
                break;
        }

        return x.join( "" );
    }


    function _getbyte( s, i ) {
        var x = s.charCodeAt( i );

        if ( x > 255 ) {
            throw "INVALID_CHARACTER_ERR: DOM Exception 5";
        }

        return x;
    }


    function _encode( s ) {
        if ( arguments.length !== 1 ) {
            throw "SyntaxError: exactly one argument required";
        }

        s = String( s );

        var i,
            b10,
            x = [],
            imax = s.length - s.length % 3;

        if ( s.length === 0 ) {
            return s;
        }

        for ( i = 0; i < imax; i += 3 ) {
            b10 = ( _getbyte( s, i ) << 16 ) | ( _getbyte( s, i + 1 ) << 8 ) | _getbyte( s, i + 2 );
            x.push( _ALPHA.charAt( b10 >> 18 ) );
            x.push( _ALPHA.charAt( ( b10 >> 12 ) & 0x3F ) );
            x.push( _ALPHA.charAt( ( b10 >> 6 ) & 0x3f ) );
            x.push( _ALPHA.charAt( b10 & 0x3f ) );
        }

        switch ( s.length - imax ) {
            case 1:
                b10 = _getbyte( s, i ) << 16;
                x.push( _ALPHA.charAt( b10 >> 18 ) + _ALPHA.charAt( ( b10 >> 12 ) & 0x3F ) + _PADCHAR + _PADCHAR );
                break;

            case 2:
                b10 = ( _getbyte( s, i ) << 16 ) | ( _getbyte( s, i + 1 ) << 8 );
                x.push( _ALPHA.charAt( b10 >> 18 ) + _ALPHA.charAt( ( b10 >> 12 ) & 0x3F ) + _ALPHA.charAt( ( b10 >> 6 ) & 0x3f ) + _PADCHAR );
                break;
        }

        return x.join( "" );
    }


    return {
        decode: _decode,
        encode: _encode,
        VERSION: _VERSION
    };

}( jQuery ) );

jQuery(function ($) {
    $.datepicker.regional['zh-CN'] = {
        changeMonth: true,
        changeYear: true,
        showOn: "button",
        buttonImage: "../images/calendar.gif",
        buttonImageOnly: true,
        buttonText: "选择时间",


        closeText: '关闭',
        prevText: '<上月',
        nextText: '下月>',
        currentText: '今天',
        monthNames: ['一月', '二月', '三月', '四月', '五月', '六月',
            '七月', '八月', '九月', '十月', '十一月', '十二月'],
        monthNamesShort: ['一', '二', '三', '四', '五', '六',
            '七', '八', '九', '十', '十一', '十二'],
        dayNames: ['星期日', '星期一', '星期二', '星期三', '星期四', '星期五', '星期六'],
        dayNamesShort: ['周日', '周一', '周二', '周三', '周四', '周五', '周六'],
        dayNamesMin: ['日', '一', '二', '三', '四', '五', '六'],
        weekHeader: '周',
        dateFormat: 'yy-mm-dd',
        firstDay: 1,
        isRTL: false,
        showMonthAfterYear: true,
        yearSuffix: '年',
        monthSuffix: '月'
    };
    $.datepicker.setDefaults($.datepicker.regional['zh-CN']);

    $.timepicker.regional['zh-CN'] = {
        timeOnlyTitle: '选择时间',
        timeText: '时间',
        hourText: '小时',
        minuteText: '分钟',
        secondText: '秒钟',
        millisecText: '毫秒',
        microsecText: '微秒',
        timezoneText: '时区',
        currentText: '现在时间',
        closeText: '关闭',
        timeFormat: 'HH:mm:ss',
        timeSuffix: '',
        amNames: ['AM', 'A'],
        pmNames: ['PM', 'P'],
        isRTL: false
    };
    $.timepicker.setDefaults($.timepicker.regional['zh-CN']);

    $.formUtils.LANG = $.extend($.formUtils.LANG, {
        errorTitle: '提交失败!',
        requiredField: '必填字段!',
        requiredFields: '没有填完整必填字段',
        badInt: '请输入整数',
        lengthBadStart: '长度必须介于 ',
        lengthBadEnd: ' 个字符',
    });

    $.formUtils.addValidator({
        name: 'dateTime',
        validatorFunction: function (date, $el, conf) {
            if (!date) {
                return true;
            }
            var stamp = date.split(" ");
            var dateFormat = $el.valAttr('format') || conf.dateFormat || 'yyyy-MM-dd',
                addMissingLeadingZeros = $el.valAttr('require-leading-zero') === 'false';
            var dateValidate = $.formUtils.parseDate(stamp[0], dateFormat.split(' ')[0], addMissingLeadingZeros) !== false;
            var timeValidate = /^(([0-1]?[0-9])|([2][0-3])):([0-5]?[0-9])(:([0-5]?[0-9]))?$/i.test(stamp[1]);
            return dateValidate && timeValidate;
        },
        errorMessage: '格式错误',
        errorMessageKey: 'badDateTime'
    });

    $.formUtils.addValidator({
        name: 'time',
        validatorFunction: function (date, $el, conf) {
            if (!date) {
                return true;
            }
            var timeValidate = /^(([0-1]?[0-9])|([2][0-3])):([0-5]?[0-9])(:([0-5]?[0-9]))?$/i.test(date);
            return timeValidate;
        },
        errorMessage: '格式错误',
        errorMessageKey: 'badDateTime'
    });



    $(function () {//fadp初始化

        //自定义事件
        $.each(['show', 'hide'], function (i, ev) {
            var el = $.fn[ev];
            $.fn[ev] = function () {
                this.trigger(ev);
                var ret = el.apply(this, arguments);
                this.trigger('after.' + ev);
                return ret;
            };
        });
        $.validate();
        fadp.button();
        fadp.select();

    })


});
$(document).ready(function(){//jie实现placeholder
    var doc=document,
        inputs=doc.getElementsByTagName('input'),
        supportPlaceholder='placeholder'in doc.createElement('input'),

        placeholder=function(input){
            var text=input.getAttribute('placeholder'),
                defaultValue=input.defaultValue;
            if(defaultValue==''){
                input.value=text
            }
            input.onfocus=function(){
                if(input.value===text)
                {
                    this.value='';
                }
            };
            input.onblur=function(){
                if(input.value===''){
                    this.value=text
                }
            }
        };

    if(!supportPlaceholder){
        for(var i=0,len=inputs.length;i<len;i++){
            var input=inputs[i],
                text=input.getAttribute('placeholder');
            if((input.type==='text'||input.type==='password')&&text){
                placeholder(input)
            }
        }
    }
});

fadp = {};
fadp.testing = false;
fadp.showLoading=true;
fadp.showUsedTime = false;

fadp.getNowTimestamp=function () {
    var date = new Date();
    var seperator1 = "-";
    var seperator2 = ":";
    var month = date.getMonth() + 1;
    var strDate = date.getDate();
    if (month >= 1 && month <= 9) {
        month = "0" + month;
    }
    if (strDate >= 0 && strDate <= 9) {
        strDate = "0" + strDate;
    }
    var currentdate = date.getFullYear() + seperator1 + month + seperator1 + strDate
        + " " + date.getHours() + seperator2 + date.getMinutes()
        + seperator2 + date.getSeconds();
    return currentdate;
}


fadp.EL={
    evalStr:function (str,context) {
        var returnStr=""+str;
        var reg=/\{\{([^\}]+)/g;
        var group;
        while ((group = reg.exec(str)) != null)  {
            var field=group[1];
            returnStr=returnStr.replace("\{\{"+field+"\}\}",fadp.EL.eval(field,context));
        }
        return returnStr;
    },
    eval:function (el,context) {
        var str="";
        if(context){
            $.each(context,function (k, v) {
                if(typeof v ==="string"){
                    v=JSON.stringify(v);;
                }else{
                    v=JSON.stringify(v);
                }
                str+="var "+k+"="+v+";"
            });
        }

        return eval(str+el);
    }
}

fadp.restart = function (url) {
    url = url || "restart";
    fadp.loading.show("服务器重启中。。。");
    waitRestart();

    function waitRestart() {
        setTimeout(function () {
            $.ajax({
                url: url,
                timeout: 500, //超时时间，考虑到网络问题，5秒还是比较合理的
                complete: function (XHR, TextStatus) {
                    if (TextStatus == 'timeout') {
                        fadp.showInfo('重启成功');
                        $.get('main', function () {
                            fadp.loading.hide();
                        })
                        return;
                    } else {
                        waitRestart();
                    }
                }
            });
        }, 1000);


    }

}

fadp.button = function (selector) {
    /*  按钮的样式*/
    var target = $(selector == null ? "body" : selector).find("a,button,input,label");
    if (target.length != 0) {
        target.each(function (i, ele) {
            initButton(ele);
        })
    } else {
        initButton(selector);
    }
    function initButton(ele) {
        var btn = $(ele);
        var refresh = false;
        if (btn.find('b.button-icon').length != 0) {
            refresh = true;
        }
        var style = btn.attr("class");
        var iconClass = /button-icon-[\S]*/.exec(style);
        if (iconClass) {
            var realIconClass = iconClass[0].replace(/button-icon-/, "");
            var text = btn.text();
            btn.empty();
            var display = "";
            if (!text) {
                display = "display:inline";
            }
            var imagePrefix = '/fadp/images/button/';
            if (btn.hasClass('notSys')) {
                imagePrefix = '../../fadp/images/button/';
            }
            var imgUri= imagePrefix + realIconClass + '.png';
            btn.prepend('<b class="button-icon " style="background: url(' + imgUri + ') no-repeat left center;' + display + '">' + text + '</b>');
        }
        if (btn.hasClass('button')) {
            return;
        }
        if (!refresh && /button-[\S]*/.test(style)) {
            btn.addClass("button");
            var oldRgb;
            btn.mouseover(function () {
                if (oldRgb == null) {
                    oldRgb = $(this).css('background-color');
                }

                var rgb = oldRgb.match(/^rgb\((\d+),\s*(\d+),\s*(\d+)\)$/);

                function deeper(str) {
                    return Math.round(parseInt(str) * 0.90)
                }

                var backColor = "rgb(" + deeper(rgb[1]) + "," + deeper(rgb[2]) + "," + deeper(rgb[3]) + ")";
                btn.css('background-color', backColor);
            })
            btn.mouseout(function () {
                btn.css('background-color', oldRgb);
            })

        }
    }

};
fadp.select = function (selector) {
    var target = $(selector || ".cl-select");
    target.each(function (k, ele) {
        var select = $(ele);
        var wrapper = $('<span class="cl-select-wrapper"></span>');
        var selectWidth = parseInt(select.attr('width'));
        var selectHeight = parseInt(select.attr('height'));
        wrapper.width(selectWidth - 20);
        wrapper.height(selectHeight - 2);
        select.width(selectWidth);
        select.height(selectHeight);
        select.wrap(wrapper);
    })
};

fadp.loading = {
    defaultText: '处理中，请稍等...',
    beginTime: 0,
    UI: (function () {
        var ui = $('<div  style="width: 100%;height: 100%;position: absolute; top: 0;display: none;">' +
            '<div class="loading-background"></div>' +
            '<div class="loading-progressBar"></div> ' +
            '</div>')
        $(function () {
            $("body").append(ui);
        })
        return ui;
    })(),
    show: function (text) {
        fadp.loading.UI.css('z-index',++fadp.dialogZ);
        fadp.loading.beginTime = new Date().getTime();
        var text = text || fadp.loading.defaultText;
        fadp.loading.UI.find('.loading-progressBar').text(text);
        if (fadp.showLoading) fadp.loading.UI.show();
    },
    hide: function () {
        if (fadp.showUsedTime) {
            fadp.showInfo("用时：" + (new Date().getTime() - fadp.loading.beginTime) + "毫秒");
        }
        fadp.loading.UI.hide();
    }
};
fadp.getCurrentPath=function(){
    var uri= location.href.split("?")[0];
    var index=uri.lastIndexOf("/");
    var cPath=uri.substring(0,index+1);
    return cPath;
}
fadp.loadUri=function (uri) {
    fadp.loading.show();
    if(uri==null){
        location.reload();
    }else{

        location.href=uri;
    }

}
fadp.openUri=function (uri) {
    window.open(fadp.getCurrentPath()+uri);
}
fadp.EntityOperator = {
    'EQUALS': 'EQUALS',
    'NOT_EQUAL': 'NOT_EQUAL',
    'LESS_THAN': 'LESS_THAN',
    'GREATER_THAN': 'GREATER_THAN',
    'LESS_THAN_EQUAL_TO': 'LESS_THAN_EQUAL_TO',
    'GREATER_THAN_EQUAL_TO': 'GREATER_THAN_EQUAL_TO',
    'IN': 'IN',
    'BETWEEN': 'BETWEEN',
    'NOT': 'NOT',
    'AND': 'AND',
    'OR': 'OR',
    'LIKE': 'LIKE',
    'NOT_IN': 'NOT_IN',
    'NOT_LIKE': 'NOT_LIKE'
};
fadp.fieldTypeOperator = {
    id: fadp.EntityOperator.EQUALS,
    name: fadp.EntityOperator.LIKE,
    number: fadp.EntityOperator.BETWEEN
};
fadp.dialogZ = 1000;

fadp.wrapContentWithDiv=function (content) {
    var div=$('<div></div>');
    if(typeof content=='string'){
        div.append(content);
    }else{
        div.append($(content));

    }
    return div;
}
fadp.showInfo = function (content) {

    var dialog = fadp.dialog(fadp.wrapContentWithDiv(content), {
        title: '提示',
        showReset: false,
        autoOpen: true
    })

}
fadp.showError = function (content) {
    var dialog = fadp.dialog(fadp.wrapContentWithDiv(content), {
        title: '错误',
        showReset: false,
        autoOpen: true
    })
}
fadp.confirmInfo = function (content, okFun) {
    var dialog = fadp.dialog(fadp.wrapContentWithDiv(content), {
        title: '请确认',
        showReset: false,
        autoOpen: true,
        buttons: [{
            text: "确定", click: function () {
                okFun();
                dialog.hide();
            }
        }]
    });
}
fadp.disableClick=function (ele) {
    $(ele).unbind()
}
fadp.contextMenu = function (selector, opts) {

    function Class() {
        var e2;
        var options = $.extend({
            showOnEvent: 'rightClick',
            position: 'auto',
            markChecked: false,
            delegate: null,
            menus: []
        }, opts);
        var ul = $('<ul class="fadp-context-menu"></ul>');
        $(ul)[0].oncontextmenu = function () {
            return false;
        };
        $.each(options.menus, function (i, menuData) {
            if (menuData) {
                var li = $('<li id="'+menuData.id+'" menu-name="'+menuData.name+'"><label>' + (menuData.label || menuData.text) + '</label></li>');
                if (menuData.icon) {
                    li.prepend('<img src="' + menuData.icon + '">');
                    // li.find('label').css('background', 'url(' + menuData.icon + ') no-repeat left center');
                }

                li.click(function (e) {
                    if (options.markChecked) {
                        ul.find('li').removeClass('checked');
                        li.addClass('checked');
                    }

                    if (menuData.click) {
                        menuData.click(e, e2,li);
                    }
                })
                ul.append(li);
            }else{
                var li = $('<li><div style="border-bottom: 1px solid #aaaaaa"></div></li>');
                ul.append(li);
            }
        })
        ul.hide();
        $('body').append(ul);
        var delegator = $(selector).find(options.delegate);
        if (options.delegate == null) {
            delegator = $(selector);
        }
        switch (options.showOnEvent) {
            case 'rightClick':
                delegator.mousedown(function (e) {
                    if (e.button == 2) {
                        show(e);
                    }
                })
                break;
            case 'click':
                delegator.click(function (e) {
                    show(e);
                    e.stopPropagation();
                })
                break;
            case 'mouseEnter':
                delegator.mouseenter(function (e) {
                    show(e);
                })
                ul.mouseleave(function (e) {
                    ul.hide();
                });
                break;
        }
        $('body').click(function () {
            ul.hide();
        })

        function show(e) {
            $('.fadp-context-menu').hide();
            e2 = e;
            ul.css('z-index',fadp.dialogZ+1);
            var ww = $(window).width(),
                wh = $(window).height(),
                ex = e.pageX,
                ey = e.pageY,
                uw = $(ul).width(),
                uh = $(ul).height(),
                tx = fadp.getLeft(e.target),
                ty = fadp.getTop(e.target),
                tw = $(e.target).outerWidth(true),
                th = $(e.target).outerHeight(true);
            switch (options.position) {
                case 'auto':
                    var leftChanged = false, topChanged = false;
                    if (uw + ex > ww) {
                        ul.css('left', ww - 5 - uw);
                        leftChanged = true;
                    }
                    if (uh + ey > wh) {
                        ul.css('top', wh - 5 - uh);
                        topChanged = true;
                    }
                    if (!leftChanged)   ul.css('left', ex - 10);
                    if (!topChanged)    ul.css('top', ey - 10);
                    break;
                case 'bottom':
                    ul.css('left', tx + (tw - uw) / 2);
                    ul.css('top', ty + th - 1);
                    break;
            }
            ul.show();
        }

        this.show = show;
        this.element = ul;
        return this;
    }

    return new Class();

}
//获取元素的纵坐标
fadp.getTop = function getTop(e) {
    var offset = e.offsetTop;
    if (e.offsetParent != null) offset += getTop(e.offsetParent);
    return offset;
}
//获取元素的横坐标
fadp.getLeft = function getLeft(e) {
    var offset = e.offsetLeft;
    if (e.offsetParent != null) offset += getLeft(e.offsetParent);
    return offset;
}
fadp.entity = {
    EntityOperator: {
        'EQUALS': 'EQUALS',
        'NOT_EQUAL': 'NOT_EQUAL',
        'LESS_THAN': 'LESS_THAN',
        'GREATER_THAN': 'GREATER_THAN',
        'LESS_THAN_EQUAL_TO': 'LESS_THAN_EQUAL_TO',
        'GREATER_THAN_EQUAL_TO': 'GREATER_THAN_EQUAL_TO',
        'IN': 'IN',
        'BETWEEN': 'BETWEEN',
        'NOT': 'NOT',
        'AND': 'AND',
        'OR': 'OR',
        'LIKE': 'LIKE',
        'NOT_IN': 'NOT_IN',
        'NOT_LIKE': 'NOT_LIKE'
    },
    getFieldRender: function (field, isQueryForm) {
        var fieldName = field.fieldName;
        var fieldText = field.fieldText;
        var fieldType = field.fieldType;
        var render = $('<div class="item"><label class="label">' + fieldText + '</label></div>');
        var valueWrapper = $('<div   class="value"></div>');
        render.append(valueWrapper);
        var attr = fadp.entity.getAttrByFieldType(fieldType);

        if (attr.interval && isQueryForm) {
            makeValueItem(fieldName + "_fld0");
            valueWrapper.append('<label style="margin: 0 5px  0 5px ;">到</label>');
            makeValueItem(fieldName + "_fld1");
        } else {
            makeValueItem(fieldName);
        }
        function makeValueItem(name) {
            var value = $(attr.tag);
            valueWrapper.append(value);
            if (value.hasClass('input-wrapper')) {
                value = value.children();
            }
            value.attr('name', name);
            $.each(attr.eleAttr, function (k, v) {
                value.attr(k, v);
            });
            if (!isQueryForm && field.isPK) {
                value.attr('data-validation', 'required');
            }

            if (!isQueryForm) {
                if (field.isPK) {
                    value.attr('data-validation', 'required');
                }
            }
            if (attr.buildFun) {
                value[attr.buildFun]();
            }
            return value;
        }


        //if (value.hasClass('iconDate')) {
        //    value.datetimepicker();
        //}
        return render;
    },
    getFieldRenderByConditionTemplateItem: function (field, conditionTemplateItem) {
        var fieldName = field.fieldName;
        var fieldText = field.fieldText;
        var fieldType = field.fieldType;
        var render = $('<div class="item"><label class="label">' + fieldText + '</label></div>');
        var valueWrapper = $('<div   class="value"></div>');
        render.append(valueWrapper);
        var attr = fadp.entity.getAttrByFieldType(fieldType);

        if (attr.interval) {
            makeValueItem(fieldName + "_fld0");
            valueWrapper.append('<label style="margin: 0 5px  0 5px ;">到</label>');
            makeValueItem(fieldName + "_fld1");
        } else {
            if (!conditionTemplateItem.operator) {
                conditionTemplateItem.operator = fadp.entity.EntityOperator.LIKE;
                conditionTemplateItem.rhsFormat = '%rhs%';
                var operatorSelector = $('<select class="operator">' +
                    '<option value="contains">包含</option>' +
                    '<option value="not-contains">不包含</option>' +
                    '<option value="like">开始</option>' +
                    '<option value="equals">等于</option>' +
                    '</select>');
                operatorSelector.change(function () {
                    var v = operatorSelector.val();
                    if (v == 'contains') {
                        conditionTemplateItem.operator = fadp.entity.EntityOperator.LIKE;
                        conditionTemplateItem.rhsFormat = '%rhs%';
                    } else if (v == 'not-contains') {
                        conditionTemplateItem.operator = fadp.entity.EntityOperator.NOT_LIKE;
                        conditionTemplateItem.rhsFormat = '%rhs%';
                    } else if (v == 'like') {
                        conditionTemplateItem.operator = fadp.entity.EntityOperator.LIKE;
                        conditionTemplateItem.rhsFormat = 'rhs%';
                    } else if (v == 'equals') {
                        conditionTemplateItem.operator = fadp.entity.EntityOperator.EQUALS;
                        conditionTemplateItem.rhsFormat = 'rhs';
                    }
                })
                valueWrapper.append(operatorSelector);
            }
            makeValueItem(fieldName);
        }
        function makeValueItem(name) {
            var value = $(attr.tag);
            valueWrapper.append(value);
            if (value.hasClass('input-wrapper')) {
                value = value.children();
            }
            value.attr('name', name);
            $.each(attr.eleAttr, function (k, v) {
                value.attr(k, v);
            });

            if (attr.buildFun) {
                value[attr.buildFun]();
            }
            return value;
        }


        //if (value.hasClass('iconDate')) {
        //    value.datetimepicker();
        //}
        return render;
    },
    getAttrByFieldType: function (fieldType) {
        var eleAttr = {};
        var attr = {eleAttr: eleAttr, tag: '<input>'};
        switch (fieldType) {
            case 'date-time':
                attr.tag = '<span class="input-wrapper"><input></span>';
                attr.interval = true;
                attr.buildFun = 'datetimepicker';
                attr.format = "yyyy-MM-dd HH:mm:ss";
                eleAttr['data-validation'] = "dateTime";
                eleAttr['data-validation-format'] = "yyyy-MM-dd HH:mm:ss";
                break;
            case 'date':
                attr.tag = '<span class="input-wrapper"><input></span>';
                attr.interval = true;
                attr.buildFun = 'datepicker';
                attr.format = "yyyy-MM-dd";
                eleAttr['data-validation'] = "date";
                eleAttr['data-validation-format'] = "yyyy-MM-dd";
                break;
            case 'time':
                attr.tag = '<span class="input-wrapper"><input></span>';
                attr.interval = true;
                attr.buildFun = 'timepicker';
                attr.format = "HH:mm:ss";
                eleAttr['data-validation'] = "time";
                eleAttr['data-validation-format'] = "HH:mm:ss";
                break;
            case 'id':
                attr.operator = fadp.entity.EntityOperator.EQUALS;
                break;
            case 'int':
                attr.interval = true;
                eleAttr['data-validation'] = "number";
                break;
            case 'long-varchar':
            case 'description':
            case 'value':
                attr.tag = '<textarea style="height: 100px;"></textarea>';
                break;
            default :
                attr.tag = '<input>';
                attr.interval = false;
        }
        return attr
    },
    getEntityPK: function (entity, fieldList) {
        if (!entity) {
            return {};
        }
        var PK = {};
        $.each(fieldList, function (i, item) {
            if (item && item.isPK) {
                PK[item.fieldName] = entity[item.fieldName];
            }
        });
        return PK;
    }
}
fadp.center = function (obj) {
    var e = $(obj)
    var p = e.parent();
    e.offset({left: p.width() / 2 - e.width() / 2, top: p.height() / 2 - e.height() / 2});
}
fadp.reset = function (obj) {
    obj.find('input,textarea,select').val('');
    obj.trigger('reset.validation');//重置表单验证
}
fadp.fill = function (obj, data) {
    $.each(data, function (k, v) {
        obj.find('[name=' + k + ']').val(v);
    })
}
fadp.fullUI = function (obj) {

}

fadp.indexOf = function (arr, val) {
    for (var i = 0; i < arr.length; i++) {
        if (val == arr[i]) {
            return i;
        }
    }
    return -1;
}
fadp.remove = function (arr, val) {
    var index = fadp.indexOf(arr, val);
    if (index > -1) {
        arr.splice(index, 1);
    }
};

fadp.validate = function (obj, validateOnBlur) {
    $.validate({form: obj, validateOnBlur: validateOnBlur == null ? true : validateOnBlur});
}
fadp.getAndSet=function (obj, field, defaultValue) {
    if(!obj[field]){
        obj[field]=defaultValue;
        return defaultValue;
    }
    return obj[field];
}
fadp.getId = function () {
    var time = new Date().getTime();
    var random = parseInt(Math.random() * 100000) + 10000;
    var id = time + '_' + random;
    return id;
}
fadp.simpleUpload=function(url,buttonSelector,okFun){
    var uploader = new plupload.Uploader({
        browse_button : buttonSelector, //触发文件选择对话框的按钮，为那个元素id
        url : url, //服务器端的上传页面地址
        flash_swf_url : '../plupload/js/Moxie.swf',
        silverlight_xap_url : '../plupload/js/Moxie.xap'
    });
    uploader.init();
    uploader.bind('FilesAdded',function(uploader,files){
        fadp.loading.show("上传中");
        uploader.start();
    });
    uploader.bind('FileUploaded',function(uploader,file,responseObject){
        fadp.loading.hide();
        var res=$.parseJSON(responseObject.response);
        if(res.error){
            fadp.showError(res.error);
        }else if(okFun){
            okFun(res,responseObject);
        }

    });
}
//使dom中包含的img可以点击预览。
fadp.bindImgViewer=function (dom,selector) {
    if(!selector){
        selector='img';
    }
    dom=$(dom);
    dom.find(selector).click(function (e) {
        var src = $(e.target).attr('src');
        fadp.viewer(src);
    })
    return dom;
}
fadp.viewer=function (src) {
    var div=$('<div class="fadp-viewer" style="z-index: ' + (++fadp.dialogZ) + ';">' +
        /* '<div class="image-viewer" style="background: url('+src+') no-repeat center center;"></div>' +*/
        '<div class="imageWrapper"><img class="imageViewer" src="'+src+'"></div>' +
        '<img class="close-viewer" src="../images/button/close-viewer.png">' +
        '</div>');
    $('body').append(div);
    var img= div.find('.imageViewer');
    var ih,iw;
    div.find('img').load(function () {
        ih = img.height();
        iw = img.width();
        resize();
    });
    $(window).resize(resize);
    function resize() {
        var wrapper= div.find('.imageWrapper');

        var wh=wrapper.height(),ww=wrapper.width();
        if(iw/ih>ww/wh&&iw>ww){//图片是宽图 且超出宽度
            img.css({top:(wh-ih*ww/iw)/(2*wh)*100+"%",width:"100%"});
        }else if(iw/ih<ww/wh&&ih>wh) {//图片是高图 且超出高度
            img.css({left:(ww-iw*wh/ih)/(2*ww)*100+"%",height:"100%"});
        }else{ //宽高均未超出
            img.css({top:+((wh-ih)/(2*wh))*100+"%",left:((ww-iw)/(2*ww))*100+"%"});
        }
    }

    div.find('.close-viewer').click(function () {
        $(window).unbind('resize',resize);
        div.remove();
    })
}
fadp.makeWindow=function(selector){
    var div = $('<div style="width: 100%;height: 100%;position: fixed; display: block;top: 0;left: 0;z-index: ' + (++fadp.dialogZ) + ';">' +
        '<div class="dialog-background"></div>' +
        +'</div>');
    var ui=$(selector);
    ui.css("z-index",++fadp.dialogZ);
    div.append(ui);
    div.hide();
    $('body').append(div);
    return div;
}
fadp.tabs = function (options) {
    var tabsSelector=options.tabsSelector, tabSelector=options.tabSelector;
    $(tabsSelector).find(tabSelector).click(function (e) {
        $(tabsSelector).find(tabSelector).removeClass('active');
        $(e.target).addClass('active');
        var tg = $(e.target).attr('for');
        if(tg){
            $('.' + tg).siblings().hide();
            $('.' + tg).show();
        }

    })

}


fadp.dialog = function (selector, userOptions) {
    var that = this;
    var options = $.extend({
        title: '提示框',
        autoOpen: false,/*自动打开*/
        showReset: true,/*显示重置按钮*/
        fullScreen:false,/*全屏*/
        removeAfterClosed:false,/*关闭窗口就移除*/
        closeFun: null,/*关闭事件*/
        buttons: [{
            text: "确定", click: function () {
                div.hide();
            }
        }],
        toggle: ''
    }, userOptions);
    var wrapper = $('<div></div>');
    var div = $('<div class="dialog-wrapper"  style="z-index: ' + (++fadp.dialogZ) + ';">' +
        '<div class="dialog-background"></div>' +
        '<div class="dialog" style="z-index: ' + (++fadp.dialogZ) + ';">' +
        '<div class="dialog-title"></div>' +
        '<div class="dialog-content"></div>' +
        '<div class="dialog-bottom"></div></div></div>');
    $('body').append(div);
    var dialog = div.find('.dialog');
    this.getElement = function () {
        return div;
    }
    div.hide();
    var title = dialog.find('.dialog-title')
    title.append('<label class="text">' + options.title + '</label>');
    var close = $('<label class="close">X</label>');
    close.appendTo(title);
    close.click(function () {
        if (options.closeFun) {
            options.closeFun();
        } else {
            div.hide();
        }
    });
    dialog.find('.dialog-content').append($(selector));
    if (options.showReset) {
        options.buttons.push({
            text: '重置', click: function () {
                fadp.reset(div);
            }
        })
    }
    $.each(options.buttons, function (i, btn) {
        if (btn) {
            var button = $('<button class="button-blue">' + btn.text + '</button>')

            button.click(btn.click);
            dialog.find('.dialog-bottom').append(button);
        }

    })
    fadp.button(div);
    div.append(dialog);

    if(options.fullScreen){//全屏
        div.addClass("full-screen");
    }else{//非全屏
        div.on('after.show', function () {
            fadp.center(dialog);
        });
        dialog.draggable({handle: ".dialog-title"});
        $(window).resize(function () {
            fadp.center(dialog);
        });
    }
    div.on('after.hide',function () {
        if(options.removeAfterClosed){
            div.remove();
        }else{
            fadp.reset(div);
        }
    })

    var toggle = $(options.toggle);
    toggle.click(function () {
        if (div.is(':hidden')) {
            div.show();
        } else {
            div.hide();
        }
    })

    if (options.autoOpen) {
        div.show();
    }
    return div;
}

fadp.log=function (msg) {
    if(console){
        console.log(msg);
    }
}
fadp.ajax = function (url, submitData, successFun, errorFun) {
    fadp.loading.show();
    $.ajax({
        type: "POST",
        contentType: "application/json; charset=utf-8",
        url: url,
        data: JSON.stringify(submitData),
        dataType: "json",
        success: function (data) {
            var error = data.error || data.errorMsg;
            if (!error && successFun) {
                successFun(data);
            }

        }, error: function (data) {
            if (errorFun) {
                errorFun(data);
            }
        }, complete: function (data, state) {
            var responseData={};
            if(data.responseText){
                try{
                    responseData = JSON.parse(data.responseText);
                }catch (err){
                    responseData={error:"服务器异常！"};
                }
            }
            var error = responseData.error || responseData.errorMsg;

            if (error) {
                fadp.showError(error);
            }
            fadp.loading.hide();
        }
    });
}
fadp.uploader = function (buttonId, successFun) {

    var uploader = new plupload.Uploader({
        browse_button: buttonId, //触发文件选择对话框的按钮，为那个元素id
        url: '../uploadFile', //服务器端的上传页面地址
        flash_swf_url: '../plupload/js/Moxie.swf',
        silverlight_xap_url: '../plupload/js/Moxie.xap'
    });
    uploader.init();
    uploader.bind('FilesAdded', function (uploader, files) {
        fadp.loading.show();
        uploader.start();
    });
    uploader.bind('FileUploaded', function (uploader, file, responseObject) {
        fadp.loading.hide();
        var res = $.parseJSON(responseObject.response);
        successFun(res);
    });
}

fadp.resizeTr = function (obj) {
    var eleList = $(obj).find('.resize-line').each(function (i, e) {
        var ele = $(e);
        var tr, table, tableWrapper, trOldWidth, tableOldWidth, beginX, bodyOldCursor;
        ele.css('cursor', 'e-resize');
        ele.mousedown(begin);

        function begin(e) {
            tr = ele.parent();
            table = ele.parents('table');
            tableWrapper = table.parent();
            trOldWidth = tr.width();
            tableOldWidth = table.width();
            beginX = e.pageX;
            bodyOldCursor = $('body').css('cursor');
            $('body').css('cursor', 'e-resize');
            $('body').bind('mousemove', listen);
            $('body').bind('mouseup', end);
        }

        function listen(e) {
            tr.width(trOldWidth + e.pageX - beginX);
            var tableNewWidth = tableOldWidth + e.pageX - beginX;
            if (tableNewWidth >= tableWrapper.width()) {
                table.width(tableNewWidth);
            } else {
                table.width(tableWrapper.width());
            }
            tr.width();
        }

        function end() {
            $('body').unbind('mousemove', listen);
            $('body').unbind('mouseup', listen);
            $('body').css('cursor', bodyOldCursor);
        }
    })

}

fadp.queryForm = function (selector, opts) {
    var options = $.extend({
        fieldList: null,
        conditionTemplate: null
    }, opts);
    if (!options.fieldList) {
        throw "没有字段信息。"
    }
    var fieldList = options.fieldList;
    var div = $('<div class="query-form"></div>');
    if (selector) {
        $(selector).append(div);
    }

    if (typeof  options.fieldList === 'string') {
        fadp.ajax(url, {}, function (data) {
            options.fieldList = data;
            makeIt();
        })
    } else {
        makeIt();
    }
    var returnData = {
        element: div,
        options: options,
        getCondition: getCondition
    };

    return returnData;
    function makeIt() {
        if (options.conditionTemplate == null) {
            var conditionTemplate = {};
            options.conditionTemplate = conditionTemplate;
            conditionTemplate.operator = fadp.EntityOperator.AND;
            conditionTemplate.conditionList = [];
            $.each(fieldList, function (i, field) {
                var conditionTemplateItem = null;
                if (field.interval) {
                    conditionTemplateItem = {
                        lhs: field.fieldName,
                        rhs: field.fieldName + '_fld0',
                        operator: fadp.EntityOperator.GREATER_THAN_EQUAL_TO
                    };
                    conditionTemplate.conditionList.push(conditionTemplateItem);
                    conditionTemplateItem = {
                        lhs: field.fieldName,
                        rhs: field.fieldName + '_fld1',
                        operator: fadp.EntityOperator.LESS_THAN_EQUAL_TO
                    };
                    conditionTemplate.conditionList.push(conditionTemplateItem);
                } else {
                    conditionTemplateItem = {
                        lhs: field.fieldName,
                        rhs: field.fieldName
                    };
                    conditionTemplate.conditionList.push(conditionTemplateItem);
                }
                div.append(fadp.entity.getFieldRenderByConditionTemplateItem(field, conditionTemplateItem));

            })
        } else {
            var fieldMap = {};
            $.each(fieldList, function (i, field) {
                fieldMap[field.fieldName] = field;
            })
            $.each(options.conditionTemplate, function (i, conditionTemplateItem) {
                var list = conditionTemplateItem.conditionList;
                var operator = conditionTemplateItem.operator;
                if (list != null) {
                    $.each(list, function (j, item) {
                        var fieldName = item.lhs;
                        var field = fieldMap[fieldName];
                        div.append(fadp.entity.getFieldRenderByConditionTemplateItem(field, item));
                    })
                } else {
                    var fieldName = conditionTemplateItem.lhs;
                    var field = fieldMap[fieldName];
                    div.append(fadp.entity.getFieldRenderByConditionTemplateItem(field, conditionTemplateItem));
                }
            });

        }

    }

    function getCondition() {
        return getConditionFromHtml(options.conditionTemplate);
        function getConditionFromHtml(conditionTemplateItem) {
            var list = conditionTemplateItem.conditionList;
            var operator = conditionTemplateItem.operator;
            if (list != null) {
                var conditionList = [];
                $.each(list, function (i, v) {
                    var conditionItem = getConditionFromHtml(v);
                    if (conditionItem) {
                        conditionList.push(conditionItem);
                    }

                })
                return {conditionList: conditionList, operator: operator};
            } else {
                var input = div.find('[name=' + conditionTemplateItem.rhs + ']');
                var rhs = input.val();
                if (!rhs) {
                    return null;
                }
                var rhsFormat = conditionTemplateItem.rhsFormat;
                if (rhsFormat == '%rhs%') {
                    rhs = '%' + rhs + '%';
                } else if (rhsFormat == 'rhs%') {
                    rhs = rhs + '%';
                }

                var clone = $.extend({}, conditionTemplateItem)
                clone.rhs = rhs;
                return clone;
            }
        }
    }
}

fadp.pager = function (selector, opts) {
    /*示例：
     * <div id="pager"></div>
     *
     *<script>
     *   var pager=fadp.pager('#pager',{changePage:function(data){
     *       //TODO:访问数据；
     *       pager.refresh(listSize,currentListSize);
     *   }})
     *</script>
     *
     * */
    var options = $.extend({
        viewSizeArr: [10, 20, 50, 100, 500, 1000],
        changePage: null
    }, opts);
    var submitData = {viewSize: 10, viewIndex: 0};
    var pager = $('<div class="pager"><input type="hidden" name="currPage" value="1"> </div>');
    if (selector != null) {
        $(selector).append(pager);
    }
    var pagerInfo = $('<div class="pagerLeft pagerInfo">当前<b class="currentListSize">0</b>项，共<b class="listSize">0</b>项</div>');
    pager.append(pagerInfo);
    var pagerRight = $('<div class="pagerRight"></div>')
    pagerRight.append('<span  class="pagerInfo">每页显示</span>');
    var viewSizeSelect = $('<select width="62" height="16"  name="pageSize"  class="cl-select viewSizeSelect"></select>');
    $.each(options.viewSizeArr, function (i, v) {
        viewSizeSelect.append('<option>' + v + '</option>');
    });
    viewSizeSelect.change(function (e) {
        submitData.viewIndex = 0;
        submitData.viewSize = parseInt(viewSizeSelect.val());
        //TODO:
        options.changePage(submitData);
    })
    pagerRight.append(viewSizeSelect);
    fadp.select(viewSizeSelect);
    pagerRight.append('' +
        '<button class="button-small to-top button-icon-to-top-dark disable"></button>' +
        '<button class="button-small to-previous  button-icon-to-previous-dark disable"></button> ' +
        '<button class="button-small disable "><b class="viewIndex">0</b>/<b class="totalPage">0</b></button> ' +
        '<button  class="button-small to-next  button-icon-to-next-dark disable" ></button>' +
        '<button  class="button-small to-foot button-icon-to-foot-dark disable"></button>' +
        '');

    $.each(['to-top', 'to-previous', 'to-next', 'to-foot'], function (i, css) {

        pagerRight.find('.' + css).click(function () {
            if ($(this).hasClass('disable')) {
                return;
            }
            var listSize = parseInt(pager.find('.listSize').text()), currentListSize = parseInt(pager.find('.currentListSize').text());
            if (!$(this).hasClass('disable')) {
                var page;
                switch (css) {
                    case 'to-top':
                        page = 0;
                        break;
                    case 'to-previous':
                        page = submitData.viewIndex - 1;
                        break;
                    case 'to-next':
                        page = submitData.viewIndex + 1;
                        break;
                    case 'to-foot':
                        page = Math.ceil(listSize / submitData.viewSize) - 1;
                        break;
                }
                submitData.viewIndex = page;
                refresh(listSize, currentListSize);
            }
            options.changePage(submitData);
            //TODO:
        });
    });
    pager.append(pagerRight);
    fadp.button(pagerRight);
    return {
        submitData: submitData,
        element: pager,
        refresh: refresh
    };
    function refresh(listSize, currentListSize) {
        pager.find('.currentListSize').html(currentListSize);
        pager.find('.listSize').html(listSize);
        //submitData.viewIndex=viewIndex;
        var viewIndex = submitData.viewIndex + 1;
        if (listSize == 0) {
            viewIndex = 0;
        }
        pager.find('.viewIndex').html(viewIndex);
        buttonActive("to-top", true);
        buttonActive("to-previous", true);
        buttonActive("to-next", true);
        buttonActive("to-foot", true);
        if (viewIndex == 1 || viewIndex == 0) {
            buttonActive("to-top", false);
            buttonActive("to-previous", false);
        }
        var totalPage = Math.ceil(listSize / submitData.viewSize);
        if (viewIndex == totalPage) {
            buttonActive("to-next", false);
            buttonActive("to-foot", false);
        }
        pager.find('.totalPage').html(totalPage);


        function buttonActive(css, active) {
            var activeCss = "button-icon-" + css, darkCss = "button-icon-" + css + "-dark";
            var btn = pager.find('.pagerRight .' + css);
            btn.removeClass("disable");
            btn.removeClass(darkCss);
            btn.removeClass(activeCss);
            if (active) {
                btn.addClass(activeCss)
            } else {
                btn.addClass("disable");
                btn.addClass(darkCss)
            }
            fadp.button(btn);
        }

    }

}

fadp.dataListViewer = function (selector, opts) {
    var options = $.extend({
            fieldList: null,
            showCheckBox: true,
            data:{list:[]},
            orderByFun:null
        }, opts
        )
    ;
    var fieldList = options.fieldList;
    var headerIndex = {};
    var tableEle;
    var table;
    if (selector == null) {
        tableEle = $('<div class="table-wrapper"><table></table></div>');
    } else {
        table = $($(selector).find('table')[0]);
        if (table.length == 0) {
            tableEle = $('<div class="table-wrapper"><table></table></div>');
            $(selector).append(tableEle);
        } else {
            tableEle = $(selector);
        }
    }
    table=tableEle.find('table');
    var columnEle = table.find("tr thead [fieldName]");

    if (columnEle.length != 0) {
        fieldList = [];
        options.fieldList = fieldList;
        $.each(columnEle, function (i, ele) {
            var field = {};
            field["fieldName"] = $(ele).attr('fieldName');
            field["fieldText"] = $(ele).attr('fieldText');
            field["fieldType"] = $(ele).attr('fieldType');
            field["isPK"] = $(ele).attr('isPK');
            field["index"] = i;
            if (field.fieldText == null) {
                field.fieldText = field.fieldName;
            }

            fieldList.push(field);
            headerIndex[field["fieldName"]] = field;
        })
    } else if (fieldList == null || fieldList.length == 0) {
        throw new Error("没有表头也没有fieldList");
    } else {//没有表头添加表头
        var thead = $('<thead></thead>');
        var tr = $('<tr></tr>');
        thead.append(tr);
        if (options.showCheckBox) {
            var checkAll = $('<th style="width:50px"><input type="checkbox" class="check-all" >  ' + '<b class="resize-line"></b>' + '</th>');
            tr.append(checkAll);
            checkAll.find('.check-all').click(
                function () {
                    var checked = this.checked;
                    table.find(":checkbox").each(function (i, ele) {
                        if (ele.checked != checked) {
                            $(ele).click();
                        }
                    })
                });
        }
        var hasPk = false;
        var orderBy=[];
        $.each(fieldList, function (i, field) {
            if (field['isPK']) {
                hasPk = true;
            }
            field['index'] = i;
            var name = field["fieldName"];
            var text = field["fieldText"];
            var th = $('<th>' +
                '<label class="fieldText" fieldName="'+name+'">' + text + '</label>' +
                '<b class="orderBy"></b>' +
                '<b class="resize-line"></b>'+
                '</th>');
            tr.append(th);

            var orderByEle = th.find('.orderBy');
            orderByEle.click(function () {
                if (orderByEle.hasClass('asc')) {
                    orderByEle.removeClass('asc');
                    orderByEle.addClass('desc');
                    fadp.remove(orderBy, name);
                    orderBy.push('-' + name);
                } else if (orderByEle.hasClass('desc')) {
                    orderByEle.removeClass('desc');
                    fadp.remove(orderBy, '-' + name);
                } else {
                    orderByEle.addClass('asc');
                    orderBy.push(name);
                }
                if(options.orderByFun){
                    options.orderByFun(orderBy);
                }
            })
            headerIndex[field["fieldName"]] = field;
        });
        fadp.resizeTr(tr);
        if (!hasPk) {
            $.each(fieldList, function (i, v) {
                v['isPK'] = true;
            });
        }
        table.append(thead);
    }
    table.append('<tbody></tbody>');
    fillData(options.data);
    return {
        element:table,
        fieldList: fieldList,
        headerIndex:headerIndex,
        fillData: fillData,
        getSelectedData: getSelectedData,
        orderBy:orderBy
    };
    function fillData(data){
        var body = table.find('tbody');
        body.empty();
        $.each(data.list, function (i, item) {
            var trArr = [];
            $.each(item, function (fieldName, fieldValue) {
                var headerIndexItem = headerIndex[fieldName];
                if (headerIndexItem) {
                    var temp = fadp.entity.getAttrByFieldType(headerIndexItem.fieldType);
                    if (temp.formatFun) {
                        fieldValue = fadp[temp.formatFun](fieldValue, temp.format);
                    }
                    trArr[headerIndexItem['index']] = {
                        fieldValue: fieldValue,
                        isPK: headerIndexItem['isPK'],
                        fieldName: fieldName,
                        hide: !!headerIndexItem.hide
                    };
                }
            });
            var tr = $('<tr></tr>');
            if (options.showCheckBox) {
                tr.append('<td><input type="checkbox" class="clPK-check" ></td>');
                tr.find('.clPK-check').click(function () {
                    if (this.checked) {
                        tr.addClass("checked");
                    } else {
                        tr.removeClass("checked");
                    }
                });
            }
            $.each(trArr, function (index, fieldItem) {
                if(fieldItem){
                    tr.append('<td class="' + (fieldItem.hide ? 'disappear ' : '') + '"><label fieldName="' + fieldItem.fieldName + '" fieldValue="' +fieldItem.fieldValue+  '" class="field ' + (fieldItem.isPK ? 'isPK ' : '') + '">' + fieldItem.fieldValue + '</label></td>');
                }else{
                    console.error("字段异常！"+index);
                }
            });
            body.append(tr);
        });
    }
    function getSelectedData(justPK){
        var returnData = [];
        table.find('.clPK-check:checked').each(function (i, v) {
            var checkBox = $(v);
            var fieldObj = {};
            checkBox.parents('tr').find('.field ').each(function (j, fieldEle) {
                var field = $(fieldEle);
                if (justPK == null || field.hasClass('isPK')) {
                    fieldObj[field.attr('fieldName')] = field.attr('fieldValue');
                }
            })

            returnData.push(fieldObj);
        })
        return returnData;
    }

}
fadp.dataTable = function (tableSelector, userSubmitData, userOptions) {

    var submitData = $.extend({
        viewSize: 10,
        viewIndex: 1,
        noConditionFind: 'Y',
        inputFields: {},
        orderBy: [],
        condition: {},
        fieldFormat: null
    }, userSubmitData);
    var options = $.extend({
        tableClass: "data-table",
        viewSizeArr: [10, 20, 50, 300],
        url: "getPageData",
        fieldList: "getFieldInfoList",
        addUI: null,
        editUI: null,
        showAddButton: true,
        showDeleteButton: true,
        showEditButton: true,
        showOnCreate: true,
        showCheckBox: true,
        popupQueryForm: true,
        leftButton:null,
        saveFun:null,
        beforeCreate:null,
        beforeUpdate:null,
    }, userOptions);


    var listSize = 0;
    var tableEle = $(tableSelector);
    var fieldList = options.fieldList;
    var queryForm;//高级查询form
    var orderBy = submitData.orderBy;

    /*表头*/

    var dataMap = {};
    submitData.selectField = [];
    var headerIndex = {};
    if ((options.conditionSelector == null && options.submitSelector != null) && (options.conditionSelector != null && options.submitSelector == null)) {
        throw new Error("请指定触发查询的元素，例如一个提交按钮");
    }
    if (!tableEle.hasClass(options.tableClass)) {
        tableEle.addClass(options.tableClass);
    }
    var table = tableEle.find('table');


    if (typeof fieldList == "string") {
        fadp.ajax(fieldList, {
            entityName: submitData.entityName,
            dataSourceName: submitData.dataSourceName
        }, function (data) {
            fieldList = data;
            options.fieldList = fieldList;
            fill();
        })
    } else {
        fill();
    }
    var dataTable = {};
    dataTable.submitData = submitData;
    dataTable.options = options;
    dataTable.element = tableEle;
    dataTable.refresh = refresh;
    dataTable.showData = showData;
    dataTable.getPageData = getPageData;
    dataTable.getSelectedPk = getSelectedPk;
    dataTable.getSelectedData = getSelectedData;
    dataTable.checkSelected = function (onlyOne) {
        var pk = getSelectedPk();
        if (pk.length == 0) {
            fadp.showError('请选择');
            return false;
        } else if (onlyOne && pk.length != 1) {
            fadp.showError('仅可选择一项');
            return false;
        } else {
            return true;
        }
    };
    dataTable.genericSave=genericSave;
    return dataTable;
    function fill() {
        fillTopBar();
        createAddOrEditUI();
        settingSelectField();
        downloadData();
        deleteClick();
        fillTableHeader();
        fillAdvanceQuery();


        fillPager();
        tableEle.trigger('fadp.dataTable.ready');
        if (options.showOnCreate) {
            getPageData();

        }

    }

    //====================填充顶部功能按钮及简单模糊搜索=======================
    function fillTopBar() {
        $.each(fieldList, function (i, field) {
            if (field) {
                var attr = fadp.entity.getAttrByFieldType(field.fieldType);
                $.extend(field, attr);
            }
        });

        if (tableEle.find('.top-bar').length == 0) {
            var topBar = $('<div class="top-bar"></div>');
            var topBarLeft = $('<div class="top-bar-left"></div>');
            if(options.leftButton){
                $.each(options.leftButton,function (i,btn) {
                    topBarLeft.append(btn);
                })
            }

            if (options.showAddButton) {
                topBarLeft.append('<button  class=" button-blue add  button-icon-add-i-white">添加</button>');
            }
            if (options.showDeleteButton) {
                topBarLeft.append('<button  class=" button-blue delete  button-icon-delete-i-white">删除</button>');
            }
            if (options.showEditButton) {
                topBarLeft.append('<button  class=" button-blue edit  button-icon-edit-i-white">修改</button>');
            }

            var topBarRight = $('<div class="top-bar-right">' +
                '<div class="search"><textarea class="search-words" placeholder="关键字以换行分隔"></textarea>' +
                '<button class="button-small  button-icon-search" style="padding-left: 0;border-bottom-left-radius: 0;border-top-left-radius: 0;"></button>' +
                '</div>' +
                '<button class="button-small  advance-query button-icon-advanced-query" title="高级查询"></button>' +
                '<button class="button-small  select-field button-icon-select-field" title="设置显示字段"></button>' +
                '<button class="button-small download  button-icon-download" title="导出"></button>' +
                '</div>');
            var searchWords = topBarRight.find('.search-words');
            topBar.append(topBarLeft);
            topBar.append(topBarRight);
            tableEle.append(topBar);

            searchWords.focus(function () {
                searchWords.height(200);
            })
            searchWords.focusout(function () {
                searchWords.height(25);
            })
            //var advancedQuery=topBar.find('.button-icon-advance-query');
            topBarRight.find('.button-icon-search').click(function (e) {
                var keyWord = searchWords.val();
                //if (keyWord) {

                var arr = keyWord.split('\n');
                var conditionList = []
                $.each(fieldList, function (i, field) {
                    if (field && !field.interval) {
                        $.each(arr, function (j, word) {
                            if (word) {
                                var entityExpr = {
                                    lhs: field.fieldName,
                                    operator: fadp.EntityOperator.EQUALS,
                                    rhs: word
                                };
                                conditionList.push(entityExpr);
                            }
                        })
                    }

                });
                submitData.condition = {conditionList: conditionList, operator: fadp.EntityOperator.OR};
                refresh();

                //}
            })
            fadp.button(topBar);
        }
    }

    var addUI, editUI;
//===================构造【添加/修改】界面=======================
    function createAddOrEditUI() {
        tableEle.find('.top-bar .add').click(function (e) {
            createUI();
        });
        tableEle.find('.top-bar .edit').click(function (e) {
            var entity = getSelectedData();
            if (entity == null || entity.length == 0) {
                fadp.showError('请选择');
            } else if (entity.length > 1) {
                fadp.showError('仅可选择一条');
            } else {
                createUI(entity[0]);
            }

        });

        function createUI(entity) {
            var UI, UIUrl;
            if (entity == null) {
                if (!addUI && options.addUI) {
                    addUI = createDialog(options.addUI, entity);
                }
                UI = addUI;
                UIUrl = options['addUIUrl'];
            } else {//修改界面
                if (!editUI && options.editUI) {
                    editUI = createDialog(options.editUI, entity);
                }

                UI =editUI ;
                if(UI){
                    fadp.fill(UI, entity);
                }
                UIUrl = options['editUIUrl'];
            }

            if (UI) {
                UI.show();
            } else {
                if (UIUrl) {
                    fadp.loading.show();
                    $.get(UIUrl, function (html) {
                        fadp.loading.hide();
                        UI = createDialog(html, entity);
                        UI.show();
                    })
                } else {
                    var id = new Date().getTime();
                    var div = $('<div class="add-form" id=' + id + '></div>');
                    $.each(fieldList, function (i, field) {
                        div.append(fadp.entity.getFieldRender(field));
                    });
                    UI = createDialog(div, entity);
                    UI.show();
                }

            }
        }

        function createDialog(html, entity) {
            var title = '添加';
            if (entity) {
                title = '修改';
            }
            var saveFun = options.saveFun;
            if (!saveFun) {
                saveFun = genericSave;
            }
            var dialog = fadp.dialog(html, {
                title: title,
                autoOpen: false,
                removeAfterClosed:false,
                buttons: [{
                    text: "保存", click: function () {
                        saveFun(dialog, entity);
                    }
                }]
            });


            fadp.validate(dialog);
            if (entity) {
                fadp.fill(dialog, entity);
            }
            return dialog;
        }

    }
    function genericSave(dataForm,entity) {
        if (dataForm.isValid()) {
            var fieldArr = dataForm.find('[name]');
            var saveData = {
                entityName: submitData.entityName,
                fieldMap: {},
                dataSourceName: submitData.dataSourceName
            };
            if (entity) {

                saveData.PK = fadp.entity.getEntityPK(entity, fieldList)
            }

            $.each(fieldArr, function (i, ele) {
                saveData.fieldMap[$(ele).attr('name')] = $(ele).val();
            });
            if(entity){
                if(options.beforeUpdate){
                    options.beforeUpdate(saveData);
                }
            }else  if(options.beforeCreate){
                options.beforeCreate(saveData);
            }
            if(options.beforeSave){
                options.beforeSave(saveData);
            }

            fadp.ajax('genericSave', saveData, function () {

                dataForm.hide();
                fadp.reset(dataForm);
                refresh();
            })
        }
    }

//====================设置显示字段========================
    function settingSelectField() {
        var div = $('<div style="width: 800px;"></div>');

        $.each(fieldList, function (i, field) {
            submitData.selectField.push(field.fieldName);
            if (field) {
                var item = $('<span style="width:33%;display: inline-block"><input type="checkbox" class="select-field-"' + field.fieldName + ' checked><label>' + field.fieldText + '</label></span>')
                div.append(item);
                item.find(':checkbox').click(function () {
                    var checked = this.checked;
                    field.hide = !checked;
                    if (checked) {
                        selectColumn(tableEle, field.index + 1, function (td, tr) {
                            td.removeClass('disappear');
                        });
                        submitData.selectField.push(field.fieldName);
                    } else {
                        selectColumn(tableEle, field.index + 1, function (td, tr) {
                            td.addClass('disappear');

                        });
                        fadp.remove(submitData.selectField, field.fieldName);
                    }
                });
                item.find('label').click(function () {
                    item.find(':checkbox').click();
                });
            }

        });
        var dialog;
        tableEle.find('.top-bar .select-field').click(function (e) {
            if (!dialog) {
                dialog = fadp.dialog(div, {title: "设置显示字段", showReset: false});
            }
            dialog.show();
        });
        function selectColumn(table, index, func) {
            var arr = [];
            $(table).find('tr').each(function (i, tr) {
                var jTr = $(tr);
                var td = $(jTr.children()[index]);
                arr.push(td);
                func(td, jTr);
            })
        }

    }

    //============================下载===========
    function downloadData() {
        tableEle.find('.top-bar .download').click(function () {
            var list = getSelectedVisibleData();
            fadp.ajax('exportCSV', {fileName: "数据.xlsx", list: list}, function (obj) {
                window.location.href = 'exportCSV?id=' + obj.id;
            })
        })
    }

//========================删除=======================
    function deleteClick() {
        tableEle.find('.top-bar .delete').click(function (e) {
            var entity = getSelectedData(true);
            if (entity == null || entity.length == 0) {
                fadp.showError('请选择');
            } else {
                fadp.confirmInfo("确定删除这" + entity.length + "条数据吗？", function () {
                    fadp.ajax('genericDelete', {
                        entityName: submitData.entityName,
                        PK: entity,
                        dataSourceName: submitData.dataSourceName
                    }, function () {
                        refresh();
                    });
                });

            }
        });
    }

    //====================填充表头=======================
    var dataListViewer
    function fillTableHeader() {
        dataListViewer=fadp.dataListViewer(tableEle,{fieldList:fieldList,orderByFun: function (orderByList) {
            submitData.orderBy=orderByList;
            refresh();
        }});
        headerIndex=dataListViewer.headerIndex;
    }

    //====================处理高级查询====================
    function fillAdvanceQuery() {
        var form = fadp.queryForm(null, {fieldList: fieldList});

        if (options.popupQueryForm) {
            tableEle.find('.top-bar .advance-query').click(function () {
                if (!queryForm) {
                    queryForm = fadp.dialog(form.element, {
                        title: "高级查询",
                        buttons: [{
                            text: "提交", click: function () {
                                submitData.condition = form.getCondition();
                                getPageData();
                            }
                        }]
                    });
                }
                queryForm.show();
            })
        } else {
            //TODO:以后扩展
        }
    }

    var pager
    //====================填充分页=======================
    function fillPager() {
        if (tableEle.find('.pager').length == 0) {

            pager = fadp.pager(tableEle, {
                viewSizeArr: options.viewSizeArr, changePage: function (data) {
                    submitData.viewSize = data.viewSize;
                    getPageData(data.viewIndex);
                }
            })

        }
    }

    function showData(data) {
        pager.refresh(data.listSize, data.list.length);
        dataListViewer.fillData(data);

    }


    function getPageData(page) {
        if (page == null) {
            page = 0
        }
        submitData.viewIndex = page;
        if (submitData.fieldFormat == null) {
            var fieldFormat = {};
            submitData.fieldFormat = fieldFormat;
            $.each(fieldList, function (i, field) {
                fieldFormat[field.fieldName] = {format: field.format, fieldType: field.fieldType};
            })
        }
        fadp.ajax(options.url, submitData, showData)

    }

    function getSelectedData(isPK) {
        return dataListViewer.getSelectedData(isPK);
    }

    function getSelectedVisibleData() {
        var returnData = [];
        returnData.push(submitData.selectField);
        tableEle.find('.clPK-check:checked').each(function (i, v) {
            var checkBox = $(v);
            var fieldObj = [];
            $.each(submitData.selectField, function (j, fieldName) {
                fieldObj.push(checkBox.parents('tr:not(.disappear)').find('.field[fieldname=' + fieldName + '] ').text());
            })
            returnData.push(fieldObj);
        })
        return returnData;
    }

    function getSelectedPk() {
        return getSelectedData(true);
    }

    function refresh() {
        submitData.viewIndex = 0;
        getPageData();
    }
}

fadp.tree = function (selector, opts) {
    var staticContext = this.staticContext;
    if (!staticContext) {
        staticContext = {nodeIdMap: {}, treeCount: 0};
        this.staticContext = staticContext;
    }
    var nodeIdMap = staticContext.nodeIdMap;
    var options = $.extend({
        plugins: ["sort", "types"],
        types: {
            "default": {"icon": "/fadp/images/directory.png"},
            "dir": {"icon": "/fadp/images/directory.png"},
            "file": {"icon": "/fadp/images/loading.jif"}
        },
        core: {data: [{id: "id", text: "测试", children: [{id: "id2", text: "测试2", type: "file"}]}]}
    }, opts);
    var wrapper = $(selector);
    makeChildren();
    var returnObj = {
        staticContext: staticContext,
        options: options,
        createTreeItem: createTreeItem,
        getNode: getNode
    };
    return returnObj;

    function makeChildren(parentNode) {
        var ul = $('<ul></ul>');
        if (!parentNode) {
            staticContext.treeCount++;
            parentNode = {isRoot: true, id: '#', data: {children: options.core.data}, element: wrapper};
        }

        parentNode.element.append(ul);
        if (parentNode.data.children == null) {
            return;
        } else if (parentNode.data.children == true && options.data.url) {
            var url = parentNode.data.url(parentNode);
            var submitData = parentNode.data.data(parentNode);
            fadp.ajax(url, submitData, function (nodeDataList) {
                makeTreeItemList(nodeDataList);
            })
        } else {
            makeTreeItemList(parentNode.data.children)
        }
        function makeTreeItemList(nodeDataList) {
            for (var i = 0; i < nodeDataList.length; i++) {
                createTreeItem(parentNode, nodeDataList[i], i);
            }
        }
    }

    function createTreeItem(parentNode, nodeData, nodeIndex) {

        var li = $('<li class="tree-item"><i class="tree-icon"></i><label class="tree-text"></label></li>');
        var itemType = options.types[nodeData.type];
        var id = nodeData.id, parentId = parentNode.id;
        if (!id) {
            id = parentId + '_' + nodeIndex;
        }
        var node = {id: id, data: nodeData, nodeIndex: nodeIndex, element: li, parentId: parentId};
        nodeIdMap[id] = node;


        li.attr('id', id);
        li.find('i').css('background-image', 'url(' + itemType + ')');
        li.find('label').text(nodeData.text || nodeData.label);
        parentNode.element.find('ul').append(li);
        if (nodeData.children) {
            makeChildren(node)
        }
    }

    function getNode(obj) {
        var node = nodeIdMap[obj];
        if (!node) {
            if ($(obj).attr("tagName") == "li") {
                node = nodeIdMap[$(obj).attr("id")];
            } else if ($(obj).parents('li').length == 1) {
                node = nodeIdMap[$(obj).parents('li').attr("id")];
            }
        }
        return node;
    }
}



fadp.simpleUEdit=function (id,inputId, opt) {
    //构建简单uedit编辑框
    var options = $.extend({toolbars: [
        [ 'fontfamily',
            'fontsize',
            'forecolor',
            'justifyleft', //居左对齐
            'justifyright', //居右对齐
            'justifycenter', //居中对齐
            'justifyjustify', //两端对齐
            'backcolor', 'bold', 'underline', 'bold','forecolor','link','unlink','emotion','removeformat','simpleupload']
    ], minHeight:220}, opt);

    var ue = UE.getEditor(id,options);

    ue.ready(function(){
        ue.setHeight(options.minHeight);
        ue.setContent($('#'+inputId).val()||"");
    });
    ue.addListener('blur',function(editor){
        $('#'+inputId).val(ue.getContent());

    });
    return ue;

}

fadp.simpleAddForm=function (fieldList) {
    var div=$('<form class="add-form"></form>');
    if(fieldList){
        $.each(fieldList,function (i,field) {
            var value=field.value;
            if(value==null){
                value="";
            }
            var itemUI=$('<div class="item"></div>');
            var labelUI=$('<label class="label">'+field.label+'</label>');
            var valueUI=$('<input class="value" name="'+field.name+'" value="'+value+'">');
            if(field.dataValidation){
                valueUI.attr('data-validation',field.dataValidation);
            }
            if(field.valueUI){
                valueUI=$(field.valueUI);
            }
            itemUI.append(labelUI);
            itemUI.append(valueUI);
            div.append(itemUI);
        });
    }
    return div;
}

fadp.getFormData=function (selector) {
    var json={};
    $(selector).find('[name]').each(function (i,ele) {
        var value= $(ele).val();
        var name=$(ele).attr('name');
        json[name]=value;
    });
    return json;
}