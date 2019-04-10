package com.eiip.sn

import com.hanlin.fadp.base.util.UtilDateTime
import com.hanlin.fadp.base.util.UtilHttp
import com.hanlin.fadp.common.AjaxUtil
import com.hanlin.fadp.entity.GenericDelegator
import com.hanlin.fadp.entity.GenericValue

import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

class SnEvent {
    static String saveMaterialSnTicket(HttpServletRequest request, HttpServletResponse response) {
        def parameters=UtilHttp.getCombinedMap(request)

        def returnObj = [:]
        parameters = parameters as Map
        def delegator = parameters.delegator as GenericDelegator
        def userLogin = parameters.userLogin as GenericValue


        def ticketItemObj = parameters.ticketItemObj as Map
        ticketItemObj.operator = userLogin.userLoginId
        ticketItemObj.mapOperator = userLogin.userLoginId
        ticketItemObj.operatorTime = UtilDateTime.nowDateString('yyyy-MM-dd HH:mm:ss')
        def orderId = ticketItemObj.orderId
        def ticketId = ticketItemObj.ticketId
        def ticketType = ticketItemObj.ticketType
        def ticketItemId = ticketItemObj.ticketItemId
        def materialId = ticketItemObj.materialId
        def snList = parameters.snList as List
        SnUtil.checkSnExist(delegator, returnObj, snList, materialId)
//        SnUtil.checkSnDuplicate(delegator, returnObj, snList, ticketId, ticketItemId, ticketType)
        switch (parameters.ticketType){
            case 'tt_reject':
                SnUtil.checkSnBelongThisDeliver(delegator,returnObj,snList,orderId)
                break
        }
        if (!returnObj.error) {
            SnUtil.saveTicketAndItem(delegator,ticketItemObj,snList,ticketId,ticketItemId)
        }
        AjaxUtil.writeJsonToResponse(returnObj, response)

        return "success"
    }

}
