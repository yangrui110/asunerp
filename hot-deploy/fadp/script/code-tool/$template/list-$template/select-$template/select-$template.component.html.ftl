<ng-content></ng-content>
<span style="cursor: pointer;" *ngIf="showValue">{{showValue}}<i class="text-danger" (click)="deleteSelected()">&times;</i></span>
<span class="float-right"><i class="fa fa-search text-primary" (click)="openSelectView()"></i></span>
<ng-template #selectView>
  <div class="modal-header">
    选择${entityLabel}
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
              <input class="form-control form-control-sm" [(ngModel)]="queryData.${field.fieldName}_fld0" #fromDate="ngModel" [close-on-select]="false" ngui-datetime-picker >
              <span class="col-1">-</span>
              <input class="form-control form-control-sm" [(ngModel)]="queryData.${field.fieldName}_fld1" #fromDate="ngModel" [close-on-select]="false" ngui-datetime-picker >
              <#else>
              <input class="form-control form-control-sm" [(ngModel)]="queryData.${field.fieldName}" type="text">
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
        <div class="card-header">
          <button *ngIf="!selectOne" class="btn btn-outline-primary btn-xsm" [disabled]="checkedNum===0" (click)="selectDone()">
            <i class="fa fa-plus-circle"></i>选择多项
          </button>
        </div>
        <div class="card-body">
          <div class="table-responsive">
            <table class="table table-hover table-striped">
              <thead class="">
              <tr>
                <th *ngIf="!selectOne"><input type="checkbox" [checked]="isAllChecked" (click)="checkAll()"></th>
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
                <tr *ngIf="data.__show__!==false" style="cursor: pointer;">
                  <td *ngIf="!selectOne"><input type="checkbox" [(ngModel)]="data.checked"></td>
                <#list fieldList as field>
                    <#if field.showInTable=="Y">
                  <td>{{data.${field.fieldName}<#if field.viewValue>Value</#if>}}</td>
                    </#if>
                </#list>
                  <td>
                    <div class="btn-group ">
                        <button class="btn btn-xsm btn-outline-dark" (click)="selectOneDone(data)">选择</button>
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
        </div>
      </div>

  </div>
</ng-template>


