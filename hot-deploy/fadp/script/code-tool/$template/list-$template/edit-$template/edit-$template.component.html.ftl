<ng-content></ng-content>
<ng-template #editView>
  <div class="modal-header">
    <h4 class="modal-title pull-left">{{inEdit?'修改':'添加'}}${entityLabel}</h4>
    <button type="button" class="close pull-right" aria-label="Close" (click)="editRef.close()">
      <span aria-hidden="true">&times;</span>
    </button>
  </div>
  <div class="modal-body">
    <div class="card">
      <div class="card-header">
        基本信息
      </div>
      <div class="card-body">
        <div>
          <div class="form-inline">
<#list fieldList as field>
<#if field.showInEdit=="Y">
            <div class="form-group p-0 pt-1 <#if field.component=='text'>col-12<#else>col-6</#if>"<#if field.PK=='Y'> *ngIf="!inEdit"</#if>>
              <small class="<#if field.component=='text'>col-2<#else>col-4</#if> col-form-label text-right">
  <#if field.required=='Y'>
                <span class="text-danger">*</span>
  </#if>
                {{uiLabel('${field.fieldName}')}}:
              </small>
  <#if field.component=='select'> <#--使用选择组件-->
              <common-select class="col-4 p-0 ml-0" [cssStyle]="'form-control form-control-sm'" [(ngModel)]="editDataTemp.${field.fieldName}" [dataList]="list${entityName}Service.fieldInfoMap.${field.fieldName}.selectDataList"></common-select>
  <#elseif field.component=='complexSelect'> <#--使用复杂选择组件-->
              <span class="col-4 form-control form-control-sm" [select${field.relEntityName}]="" [showValue]="editDataTemp.${field.fieldName}Value" [(ngModel)]="editDataTemp.${field.fieldName}"></span>
  <#elseif field.component=='trueFalseSelect'> <#--使用[是/否]组件-->
              <true-false-select class="col-4" [(ngModel)]="editDataTemp.${field.fieldName}"></true-false-select>
  <#elseif field.component=='imageUpload'> <#--使用图片上传组件-->
              <app-upload-image [(ngModel)]="editDataTemp.${field.fieldName}"></app-upload-image>
  <#elseif field.component=='selectUser'> <#--选人组件-->
              <span class="col-4 form-control form-control-sm" [selectUser] [(ngModel)]="editDataTemp.${field.fieldName}" [userName]="editDataTemp.${field.fieldName}Value"></span>
  <#elseif field.component=='selectDept'> <#--选部门组件-->
              <span class="col-4 form-control form-control-sm" [selectDept] [(ngModel)]="editDataTemp.${field.fieldName}" [deptName]="editDataTemp.${field.fieldName}Value"></span>
  <#elseif field.fieldType=="date">
              <input class="col-4 form-control form-control-sm" placeholder="yyyy-mm-dd" [(ngModel)]="editDataTemp.${field.fieldName}" ngbDatepicker #${field.fieldName}="ngbDatepicker" (click)="${field.fieldName}.toggle()">
  <#elseif field.fieldType=="dateTime">
              <input class="form-control form-control-sm" [(ngModel)]="editDataTemp.${field.fieldName}" #fromDate="ngModel" [close-on-select]="false" ngui-datetime-picker>
  <#elseif field.component=="text">
              <textarea class="col-8 form-control form-control-sm" rows="2" [(ngModel)]="editDataTemp.${field.fieldName}"></textarea>
  <#else>
              <input class="col-4 form-control form-control-sm" [(ngModel)]="editDataTemp.${field.fieldName}" type="text">
  </#if>
              <small class="<#if field.component=='text'>col-2<#else>col-4</#if>">
                <#if field.required=='Y'>
                <div>
                  <small class="text-left text-muted">必填</small>
                </div>
                </#if>
                <#if field.PK=='Y'>
                <div>
                  <small class="text-left text-muted">请保证唯一</small>
                </div>
                </#if>
              </small>
            </div>
  <#if field.PK=='Y'>
            <div class="form-group p-0 pt-1 col-6" *ngIf="inEdit">
              <small class="col-4 col-form-label text-right">
              <#if field.required=='Y'>
                <span class="text-danger">*</span>
              </#if>
                {{uiLabel('${field.fieldName}')}}
              </small>
              <span class="col-4">{{editDataTemp.${field.fieldName}}}</span>
              <small class="col-4">
                <div>
                  <small class="text-left text-muted">不可修改</small>
                </div>
              </small>
            </div>
  </#if>
</#if>
</#list>
          </div>
        </div>
      </div>
    </div>

    <div class="mt-2" *ngIf="hasAttachment">
      <app-upload-file [(ngModel)]="editDataTemp.attachList"></app-upload-file>
    </div>

  </div>
  <div class="modal-footer">
    <button type="submit" class="btn btn-xsm btn-primary" (click)="saveData()">保存</button>
    <button type="submit" class="btn btn-xsm btn-primary" *ngIf="list${entityName}Service.canCommitApprove(approveAction)" (click)="saveDataAndCommit()">提交审批</button>
    <button type="submit" class="btn btn-xsm btn-primary" (click)="editRef.close()">关闭</button>
  </div>
</ng-template>
