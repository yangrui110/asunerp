
package com.hanlin.fadp.entity.transaction;

import javax.transaction.xa.Xid;
import javax.transaction.xa.XAException;

import com.hanlin.fadp.base.util.Debug;

public class DebugXaResource extends GenericXaResource {

    public static final String module = DebugXaResource.class.getName();
    public Exception ex = null;

    public DebugXaResource(String info) {
        this.ex = new Exception(info);
    }

    public DebugXaResource() {
        this.ex = new Exception();
    }

    @Override
    public void commit(Xid xid, boolean onePhase) throws XAException {
        TransactionUtil.debugResMap.remove(xid);
        if (Debug.verboseOn()) Debug.logVerbose("Xid : " + xid.toString() + " cleared [commit]", module);
    }

    @Override
    public void rollback(Xid xid) throws XAException {
        TransactionUtil.debugResMap.remove(xid);
        if (Debug.verboseOn()) Debug.logVerbose("Xid : " + xid.toString() + " cleared [rollback]", module);
    }

    @Override
    public void enlist() throws XAException {
        super.enlist();
        TransactionUtil.debugResMap.put(xid, this);
    }

    public void log() {
        Debug.logInfo("Xid : " + xid, module);
        Debug.logInfo(ex, module);
    }
}
