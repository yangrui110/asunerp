<ng-content></ng-content>
<ng-template #view>
  <div class="modal-header">
    <h4 class="modal-title pull-left">${appHeader}</h4>
    <button type="button" class="close pull-right" aria-label="Close" (click)="viewRef.close()">
      <span aria-hidden="true">&times;</span>
    </button>
  </div>
  <div class="modal-body">
    <div class="card">
      <div class="card-header"><span>查询</span></div>
      <div class="card-body">
        <div class="form-inline">
          <#list fieldList as field>
            <#if field.showInQuery=="Y"><#--参与查询的字段-->
            <div class="form-group col-6">
              <small class="col-3 col-form-label text-right">{{uiLabel('${field.fieldName}')}}:</small>
              <#if field.component=='select'> <#--使用选择组件-->
              <common-select class="col-4 p-0 ml-0" [cssStyle]="'form-control form-control-sm'" [(ngModel)]="queryData.${field.fieldName}" [dataList]="list${entityName}Service.fieldInfoMap.${field.fieldName}.selectDataList"></common-select>
              <#elseif field.component=='complexSelect'> <#--使用复杂选择组件-->
              <span class="col-4 form-control form-control-sm" [select${field.relEntityName}]="" [showValue]="queryData.${field.fieldName}Value" [(ngModel)]="queryData.${field.fieldName}"></span>
              <#elseif field.component=='trueFalseSelect'> <#--使用[是/否]组件-->
              <true-false-select class="col-4" [(ngModel)]="queryData.${field.fieldName}"></true-false-select>
              <#elseif field.component=='selectUser'> <#--选人组件-->
              <span class="col-4 form-control form-control-sm" [selectUser] [(ngModel)]="queryData.${field.fieldName}" [userName]="queryData.${field.fieldName}Value"></span>
              <#elseif field.component=='selectDept'> <#--选部门组件-->
              <span class="col-4 form-control form-control-sm" [selectDept] [(ngModel)]="queryData.${field.fieldName}" [deptName]="queryData.${field.fieldName}Value"></span>
              <#elseif field.fieldType=="numeric">
              <input class="col-4 form-control form-control-sm" [(ngModel)]="queryData.${field.fieldName}_fld0">
              <span class="col-1">-</span>
              <input class="col-4 form-control form-control-sm" [(ngModel)]="queryData.${field.fieldName}_fld1">
              <#elseif field.fieldType=="date">
              <input class="col-4 form-control form-control-sm" placeholder="yyyy-mm-dd" [(ngModel)]="queryData.${field.fieldName}_fld0" ngbDatepicker #${field.fieldName}_fld0="ngbDatepicker" (click)="${field.fieldName}_fld0.toggle()">
              <span class="col-1">-</span>
              <input class="col-4 form-control form-control-sm" placeholder="yyyy-mm-dd" [(ngModel)]="queryData.${field.fieldName}_fld1" ngbDatepicker #${field.fieldName}_fld1="ngbDatepicker" (click)="${field.fieldName}_fld1.toggle()">
              <#elseif field.fieldType=="dateTime">
              <input class="form-control form-control-sm" [(ngModel)]="queryData.${field.fieldName}_fld0" #fromDate="ngModel" [close-on-select]="false" ngui-datetime-picker>
              <span class="">-</span>
              <input class=" form-control form-control-sm" [(ngModel)]="queryData.${field.fieldName}_fld1" #fromDate="ngModel" [close-on-select]="false" ngui-datetime-picker>
              <#else>
              <input class="col-4 form-control form-control-sm" [(ngModel)]="queryData.${field.fieldName}" type="text">
              </#if>
            </div>
            </#if>
          </#list>
        </div>


        <div class="form-inline ">
          <div class="col-12">
            <button class="btn btn-sm btn-outline-info float-right" (click)="loadDataList()">搜索</button>
          </div>
        </div>
      </div>
    </div>

    <div class="card mt-2">
      <div class="card-header" *ngIf="list${entityName}Service.canAdd(approveAction)">
        <button class="btn btn-outline-primary btn-xsm" [edit${entityName}] [defaultAddValue]="defaultAddValue" [approveAction]="approveAction" (dataSavedEvent)="dataSaved($event)"><i class="fa fa-plus-circle"></i>新增
        </button>
        <button [disabled]="checkedNum==0" class="btn btn-xsm  btn-danger" [confirm]="'确定删除？'" (confirmClick)="deleteList()"><i class="fa fa-fw fa-remove"></i>批量删除
        </button>

      </div>
      <div class="card-body">
        <ng-container *ngTemplateOutlet="table"></ng-container>
      </div>
    </div>



  </div>
</ng-template>
<ng-container *ngIf="showDataTableOnly">
  <ng-container *ngTemplateOutlet="table"></ng-container>
</ng-container>

<ng-template #table>
  <div class="table-responsive">
    <table class="table table-hover table-striped">
      <thead class="">
      <tr>
        <th><input type="checkbox" [checked]="isAllChecked" (click)="checkAll()"></th>
              <#list fieldList as field>
                <#if field.showInTable=="Y">
                <th>{{uiLabel('${field.fieldName}')}}</th>
                </#if>
              </#list>
        <th>操作</th>
      </tr>
      </thead>
      <tbody>
      <ng-template ngFor let-data [ngForOf]="dataList">
        <tr>
          <td><input type="checkbox" [(ngModel)]="data.checked"></td>
                <#list fieldList as field>
                  <#if field.showInTable=="Y">
                  <td>{{data.${field.fieldName}<#if field.component=='trueFalseSelect'> | trueFalse</#if><#if field.viewValue>Value</#if>}}</td>
                  </#if>
                </#list>
          <td>
            <div class="btn-group ">
              <button class="btn btn-xsm btn-outline-dark" [view${entityName}]="data">查看</button>
              <ng-container *ngIf="!showDataTableOnly">
                <button class="btn btn-xsm btn-outline-dark" *ngIf="list${entityName}Service.canEdit(approveAction,data)" [edit${entityName}]="data"  (dataSavedEvent)="loadDataList()" [approveAction]="approveAction">编辑</button>
                <button class="btn btn-xsm btn-outline-dark" *ngIf="list${entityName}Service.canApprove(approveAction)" [view${entityName}]="data" (dataSavedEvent)="loadDataList()" [approveAction]="approveAction" >审核</button>
                <button class="btn btn-xsm btn-outline-dark" *ngIf="list${entityName}Service.canMonitor(approveAction)" [view${entityName}]="data" (dataSavedEvent)="loadDataList()" [approveAction]="approveAction" >监控</button>
                <button class="btn btn-xsm btn-outline-dark" *ngIf="list${entityName}Service.canAdjust(approveAction)" [view${entityName}]="data" (dataSavedEvent)="loadDataList()" [approveAction]="approveAction" >修正</button>
                <button class="btn btn-xsm btn-outline-dark">打印</button>
                <button class="btn btn-xsm btn-outline-danger" *ngIf="list${entityName}Service.canDelete(approveAction,data)" [confirm]="'确定删除吗？'" (confirmClick)="deleteOne(data)">删除</button>
              </ng-container>
            </div>
          </td>
        </tr>
      </ng-template>
      </tbody>

    </table>
    <small class="form-inline">
      <ngb-pagination [collectionSize]="pageData.listSize" [(page)]="pageData.page" (pageChange)="pageData.pageChange($event)" [maxSize]="5" [rotate]="true" [boundaryLinks]="true"></ngb-pagination>
      <label>共{{pageData.listSize}}条记录</label>
    </small>

  </div>
</ng-template>