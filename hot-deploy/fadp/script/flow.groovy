import com.hanlin.fadp.entity.transaction.TransactionUtil;
import com.hanlin.fadp.workflow.*

def start(){
	def begin=TransactionUtil.begin();
	runFlow1();
	runFlow12();
	runFlow21();
	TransactionUtil.commit(begin);
	return "success"
}

def newFlow1(){
	/**
	 * 流程图
	 * 
	 *	     [--2--X---]
	 *1------|        |------结束
	 *	     [--3--4--]
	 * 
	 */
	def flow=new Flow(delegator, [userId:"admin"]);
	def flowId=flow.getFlowId();
	def prcs1=new FlowPrcs(delegator, [flowId:flowId]);
	def prcs2=new FlowPrcs(delegator,[flowId:flowId,parentPrcsId:prcs1.getPrcsId(),enterCondition:"deptId!='组织部'"]);
	def prcs3=new FlowPrcs(delegator,[flowId:flowId,parentPrcsId:prcs1.getPrcsId(),enterCondition:"deptId=='组织部'"]);
	def prcs4=new FlowPrcs(delegator,[flowId:flowId,parentPrcsId:prcs3.getPrcsId()]);
	return flow;
}
def newFlow2(){
	/**
	 * 流程图
	 *
	 *	     [--2-----]
	 *1------|        |------结束
	 *	     [--3--4--]
	 *
	 */
	def flow=new Flow(delegator, [userId:"admin"]);
	def flowId=flow.getFlowId();
	def prcs1=new FlowPrcs(delegator, [flowId:flowId]);
	def prcs2=new FlowPrcs(delegator,[flowId:flowId,parentPrcsId:prcs1.getPrcsId()]);
	def prcs3=new FlowPrcs(delegator,[flowId:flowId,parentPrcsId:prcs1.getPrcsId()]);
	def prcs4=new FlowPrcs(delegator,[flowId:flowId,parentPrcsId:prcs3.getPrcsId()]);
	return flow;
}
def runFlow1(){
	Flow flow=newFlow1();
	def run=new FlowRun(delegator, [flowId:flow.getFlowId(),userId:"admin"]);
	def runPrcs1=run.start();
	def nextPrcsList=runPrcs1.getNextAccessibleFlowPrcs([runId:run.getRunId(),deptId:"组织部"]);
	if(nextPrcsList.size()!=1){
		throw new Exception("判断进入条件出错了")
	}
	def nextPrcs=nextPrcsList[0];
	runPrcs1.forward(nextPrcs,["admin"],null);
	def runPrcs3=runPrcs1.getNextRunPrcs()[0];
	runPrcs3.forward(runPrcs3.getFlowPrcs().getNextFlowPrcs()[0],["admin"],null);
	def runPrcs4=runPrcs3.getNextRunPrcs()[0];
	runPrcs4.forward(null,null,null);
}
def runFlow12(){
	Flow flow =newFlow1();
	def run=new FlowRun(delegator, [flowId:flow.getFlowId(),userId:"admin"]);
	def runPrcs1=run.start();
	def nextPrcsList=runPrcs1.getNextAccessibleFlowPrcs([runId:run.getRunId(),deptId:"组织部"]);
	if(nextPrcsList.size()!=1){
		throw new Exception("判断进入条件出错了")
	}
	def nextPrcs=nextPrcsList[0];
	runPrcs1.forward(nextPrcs,["admin"],null);
	def runPrcs3=runPrcs1.getNextRunPrcs()[0];
	runPrcs3.forward(runPrcs3.getFlowPrcs().getNextFlowPrcs()[0],["admin"],null);
	def runPrcs4=runPrcs3.getNextRunPrcs()[0];
	runPrcs4.backward(runPrcs1);
	
	def runPrcs5=runPrcs4.getNextRunPrcs()[0];
	
//	def runPrcs6=runPrcs5.getNextRunPrcs()[0]
	def prcs6List=runPrcs5.getNextAccessibleFlowPrcs([runId:run.getRunId(),deptId:"组织部"]);
	if(prcs6List.size()!=1){
		throw new Exception("回退后，判断条件出错")
	}
	runPrcs5.forward(prcs6List[0],["admin"],null);
	def runPrcs6=runPrcs5.getNextRunPrcs()[0];
	runPrcs6.forward(runPrcs6.getFlowPrcs().getNextFlowPrcs()[0],["admin"],null);
	def runPrcs7=runPrcs6.getNextRunPrcs()[0];
	runPrcs7.forward(null,null,null);
	
}

def runFlow21(){
	Flow flow =newFlow2();
	def run=new FlowRun(delegator, [flowId:flow.getFlowId(),userId:"admin"]);
	def runPrcs1=run.start();
	def nextPrcsList=runPrcs1.getNextAccessibleFlowPrcs([runId:run.getRunId()]);
	runPrcs1.forward(nextPrcsList[0],["admin"],null);
	runPrcs1.forward(nextPrcsList[1],["admin"],null);
	def runPrcs3,runPrcs2;
	runPrcs1.getNextRunPrcs().each{prcs->
		if(prcs.getPrcsId()=="2"){
			 runPrcs2=prcs;
		}else{
			runPrcs3=prcs;
		}
		
	}
	runPrcs2.forward(null,null,null)
	runPrcs3.forward(runPrcs3.getFlowPrcs().getNextFlowPrcs()[0],["admin"],null);
	def runPrcs4=runPrcs3.getNextRunPrcs()[0];
	runPrcs4.backward(runPrcs1);
	
	def runPrcs5=runPrcs4.getNextRunPrcs()[0];
	
//	def runPrcs6=runPrcs5.getNextRunPrcs()[0]
	def prcs6List=runPrcs5.getNextAccessibleFlowPrcs([runId:run.getRunId(),deptId:"组织部"]);
	if(prcs6List.size()!=1){
		throw new Exception("回退后，判断条件出错")
	}
	runPrcs5.forward(prcs6List[0],["admin"],null);
	def runPrcs6=runPrcs5.getNextRunPrcs()[0];
	runPrcs6.forward(runPrcs6.getFlowPrcs().getNextFlowPrcs()[0],["admin"],null);
	def runPrcs7=runPrcs6.getNextRunPrcs()[0];
	runPrcs7.forward(null,null,null);
	
}

FlowRunPrcs getPrcs(runId,runPrcsId,nextPrcsId){
	def prcs=FlowRunPrcs.getRunPrcs(delegator,runId,runPrcsId);
	if(nextPrcsId){
		prcs.process(null);
		prcs.forward(FlowPrcs.getFlowPrcs(delegator,runId,nextPrcsId),["admin"],null);
	}
	return prcs;
}
