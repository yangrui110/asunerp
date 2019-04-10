package com.hanlin.fadp.workflow;

import com.hanlin.fadp.entity.Delegator;

public class FlowLog {
	public static void log(Delegator delegator,String msg,FlowRun run ,FlowRunPrcs prcs) {
		System.err.println(msg);
	}
}
