import {Component} from '@angular/core';
import {List${entityName}Service} from '../../list-${minusEntityName}-service';
import {CommonViewBaseInfo} from '@shared';

@Component({
  selector: 'app-view-${minusEntityName}-base-info',
  templateUrl: './view-${minusEntityName}-base-info.component.html',
})
export class View${entityName}BaseInfoComponent extends CommonViewBaseInfo {
  constructor(public list${entityName}Service: List${entityName}Service) {
    super( list${entityName}Service);
  }
}
