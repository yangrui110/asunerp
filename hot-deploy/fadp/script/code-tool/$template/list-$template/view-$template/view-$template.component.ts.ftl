import {Component, ElementRef, Input} from '@angular/core';
import {CommonView} from '@shared';
import {List${entityName}Service} from '../list-${minusEntityName}-service';

@Component({
  selector: '[view${entityName}]',
  templateUrl: './view-${minusEntityName}.component.html',
  styleUrls: ['./view-${minusEntityName}.component.scss'],
})
export class View${entityName}Component extends CommonView {
  @Input() view${entityName}: any;

  get viewData() {
    return this.view${entityName};
  }
  constructor(public el: ElementRef, public list${entityName}Service: List${entityName}Service) {
    super(el, list${entityName}Service);
  }
}
