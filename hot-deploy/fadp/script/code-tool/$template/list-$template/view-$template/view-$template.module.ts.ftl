import {CommonModule} from '@angular/common';
import {FormsModule} from '@angular/forms';
import {NgModule} from '@angular/core';
import {NgbModule} from '@ng-bootstrap/ng-bootstrap';
import {UploadFileModule} from '@shared';

import {View${entityName}Component} from './view-${minusEntityName}.component';
import {View${entityName}BaseInfoModule} from './view-${minusEntityName}-base-info/view-${minusEntityName}-base-info.module';

@NgModule({
  imports: [
    CommonModule,
    FormsModule,
    NgbModule,
    UploadFileModule,
    View${entityName}BaseInfoModule,
  ],
  declarations: [View${entityName}Component],
  exports: [View${entityName}Component]
})
export class View${entityName}Module {
}
