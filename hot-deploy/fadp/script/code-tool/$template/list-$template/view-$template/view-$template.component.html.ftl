<ng-content></ng-content>
<ng-template #viewView>
  <div class="modal-header">
    <h4 class="modal-title pull-left">查看${entityLabel}</h4>
    <button type="button" class="close pull-right" aria-label="Close" (click)="viewRef.close()">
      <span aria-hidden="true">&times;</span>
    </button>
  </div>
  <div class="modal-body">
    <div class="card">
      <div class="card-header">
        基本信息
      </div>
      <div class="card-body">
        <app-view-${minusEntityName}-base-info [viewData]="viewData"></app-view-${minusEntityName}-base-info>
      </div>
    </div>

    <div class="mt-2" *ngIf="hasAttachment">
      <app-upload-file [(ngModel)]="viewDataTemp.attachList" [inEdit]="false"></app-upload-file>
    </div>

  </div>
  <div class="modal-footer">
    <button class="btn btn-xsm btn-primary" *ngIf="list${entityName}Service.canApprove(approveAction)" (click)="approved(viewData)">审核通过</button>
    <button class="btn btn-xsm btn-danger" *ngIf="list${entityName}Service.canApprove(approveAction)" (click)="disapproved(viewData)">审核不通过</button>
    <button class="btn btn-xsm btn-primary" *ngIf="list${entityName}Service.canMonitor(approveAction)" (click)="finished(viewData)">整单结束</button>

  </div>

</ng-template>
