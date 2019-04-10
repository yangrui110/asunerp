import {Component, ElementRef, Input} from '@angular/core';
import {CommonEdit} from '@shared';
import {List${entityName}Service} from '../list-${minusEntityName}-service';

@Component({
  selector: '[edit${entityName}]',
  templateUrl: './edit-${minusEntityName}.component.html',
  styleUrls: ['./edit-${minusEntityName}.component.scss'],
})
export class Edit${entityName}Component extends CommonEdit {

  @Input() edit${entityName};
  get editData() {
    return this.edit${entityName};
  }
  constructor(public el: ElementRef, public list${entityName}Service: List${entityName}Service) {
    super(el, list${entityName}Service);
  }
}
