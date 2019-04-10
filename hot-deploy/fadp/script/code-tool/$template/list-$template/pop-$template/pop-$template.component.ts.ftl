import {Component, ElementRef, Input} from '@angular/core';
import {routerTransition} from '@app';
import {CommonDataTable, CommonPopDataTable} from '@shared';
import {List${entityName}Service} from '../list-${minusEntityName}-service';

@Component({
  selector: '[appPop${entityName}]',
  templateUrl: './pop-${minusEntityName}.component.html',
  styleUrls: ['./pop-${minusEntityName}.component.scss']
})
export class Pop${entityName}Component extends CommonPopDataTable {
  @Input() appPop${entityName}: any = {};
  constructor(public el: ElementRef, public list${entityName}Service: List${entityName}Service) {
    super(el, list${entityName}Service);
  }

}
