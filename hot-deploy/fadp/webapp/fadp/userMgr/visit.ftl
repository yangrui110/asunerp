<script src="../js/sha1.js"></script>
<div id="visit"></div>

<script>

    var fieldList = [ {
        "fieldName": "webappName",
        "isPK": false,
        "fieldText": "webappName",
        "fieldType": "short-varchar"
    }, {
        "fieldName": "initialLocale",
        "isPK": false,
        "fieldText": "initialLocale",
        "fieldType": "short-varchar"
    }, {
        "fieldName": "initialRequest",
        "isPK": false,
        "fieldText": "initialRequest",
        "fieldType": "url"
    }, {
        "fieldName": "initialReferrer",
        "isPK": false,
        "fieldText": "initialReferrer",
        "fieldType": "url"
    }, {
        "fieldName": "initialUserAgent",
        "isPK": false,
        "fieldText": "initialUserAgent",
        "fieldType": "long-varchar"
    }, {
        "fieldName": "userAgentId",
        "isPK": false,
        "fieldText": "userAgentId",
        "fieldType": "id"
    }, {
        "fieldName": "clientIpAddress",
        "isPK": false,
        "fieldText": "clientIpAddress",
        "fieldType": "short-varchar"
    }, {
        "fieldName": "clientHostName",
        "isPK": false,
        "fieldText": "clientHostName",
        "fieldType": "long-varchar"
    }, {
        "fieldName": "clientUser",
        "isPK": false,
        "fieldText": "clientUser",
        "fieldType": "short-varchar"
    }, {
        "fieldName": "clientIpIspName",
        "isPK": false,
        "fieldText": "clientIpIspName",
        "fieldType": "short-varchar"
    }, {
        "fieldName": "clientIpPostalCode",
        "isPK": false,
        "fieldText": "clientIpPostalCode",
        "fieldType": "short-varchar"
    }, {
        "fieldName": "cookie",
        "isPK": false,
        "fieldText": "cookie",
        "fieldType": "short-varchar"
    }, {
        "fieldName": "fromDate",
        "isPK": false,
        "fieldText": "fromDate",
        "fieldType": "date-time"
    }, {
        "fieldName": "thruDate",
        "isPK": false,
        "fieldText": "thruDate",
        "fieldType": "date-time"
    }, {
        "fieldName": "clientIpStateProvGeoId",
        "isPK": false,
        "fieldText": "clientIpStateProvGeoId",
        "fieldType": "id"
    }, {"fieldName": "clientIpCountryGeoId", "isPK": false, "fieldText": "clientIpCountryGeoId", "fieldType": "id"}]
    var dataTable = $('#visit');

    var table = fadp.dataTable(dataTable, {entityName: "Visit"}, {
          fieldList: [
              {fieldName: 'visitId', fieldText: 'id', fieldType: 'id', index: 0, isPK: true}
              , {fieldName: 'userLoginId', fieldText: '用户名', fieldType: 'id', index: 1}
              , {fieldName: 'serverIpAddress', fieldText: '服务器ip', fieldType: 'id', index: 2}
              , {fieldName: 'serverHostName', fieldText: '服务器名', fieldType: 'long-varchar', index: 3}
              , {fieldName: 'webappName', fieldText: '应用名', fieldType: 'short-varchar', index: 4}
              , {fieldName: 'initialRequest', fieldText: '初次访问地址', fieldType: 'url', index: 5}
              , {fieldName: 'clientIpAddress', fieldText: '客户端口ip', fieldType: 'url', index: 6}
              , {fieldName: 'fromDate', fieldText: '开始时间', fieldType: 'date-time', index: 7}
              , {fieldName: 'thruDate', fieldText: '结束时间', fieldType: 'date-time', index: 8}
          ]

          , showAddButton: false
        , showEditButton: false
        , showDeleteButton: false
    });


</script>