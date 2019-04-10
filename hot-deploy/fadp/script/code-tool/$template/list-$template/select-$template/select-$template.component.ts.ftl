import {Component, Input} from '@angular/core';
import {routerTransition} from '@app';
import {CommonSelectOne} from '@shared';
import {List${entityName}Service} from '../list-${minusEntityName}-service';
import {NG_VALUE_ACCESSOR} from '@angular/forms';

@Component({
  selector: '[select${entityName}]',
  templateUrl: './select-${minusEntityName}.component.html',
  styleUrls: ['./select-${minusEntityName}.component.scss'],
  animations: [routerTransition()],
  providers: [
    {provide: NG_VALUE_ACCESSOR, useExisting: Select${entityName}Component, multi: true}
  ],
})
export class Select${entityName}Component extends CommonSelectOne {
  @Input() select${entityName}; // 原始选中值
  constructor(public list${entityName}Service: List${entityName}Service) {
    super(list${entityName}Service);
  }
}
