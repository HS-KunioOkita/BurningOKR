import { OkrUnitId } from '../../id-types';
import { OkrTopicDescriptionDto } from './okr-topic-description.dto';
import { status } from '../../ui/OrganizationalUnit/okr-topic-draft/okr-topic-draft-status-enum';

export class OkrTopicDraftDto extends OkrTopicDescriptionDto {
  parentUnitId: OkrUnitId;
  currentStatus: status;
}
