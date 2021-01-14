import { Component, Input, OnInit } from '@angular/core';
import { OkrDepartment } from '../../../../shared/model/ui/OrganizationalUnit/okr-department';
import { ContextRole } from '../../../../shared/model/ui/context-role';
import { TopicDescriptionMapper } from '../../../../shared/services/mapper/topic-description-mapper.service';
import { OkrTopicDescription } from '../../../../shared/model/ui/OrganizationalUnit/okr-topic-description';
import { Observable } from 'rxjs';

@Component({
  selector: 'app-department-tab-description',
  templateUrl: './department-tab-description.component.html',
  styleUrls: ['./department-tab-description.component.scss']
})
export class DepartmentTabDescriptionComponent implements OnInit {
  @Input() department: OkrDepartment;
  @Input() currentUserRole: ContextRole;

  description$: Observable<OkrTopicDescription>;

  constructor(
    private topicDescriptionMapperService: TopicDescriptionMapper
  ) {
  }

  ngOnInit(): void {
    this.description$ = this.topicDescriptionMapperService.getTopicDescriptionById$(this.department.id);
  }

}
