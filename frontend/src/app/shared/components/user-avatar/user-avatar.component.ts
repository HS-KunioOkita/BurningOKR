import { Component, EventEmitter, Input, OnChanges, Output, SimpleChanges } from '@angular/core';
import { User } from '../../model/api/user';

@Component({
  selector: 'app-user-avatar',
  templateUrl: './user-avatar.component.html',
  styleUrls: ['./user-avatar.component.css'],
})
export class UserAvatarComponent implements OnChanges {
  @Input() user: User;
  @Input() size = 64;
  @Input() bgColor = '';
  @Input() clickable = false;
  @Output() clickOnAvatar: EventEmitter<any> = new EventEmitter<any>();

  render: boolean = true;
  
  ngOnChanges(changes: SimpleChanges): void {
    setTimeout(() => this.render = false);
    setTimeout(() => this.render = true);
  }

  buildInitialsForUserAvatar(user: User): string {
    const trimmedFirstName: string = user.givenName?.trim();
    const trimmedLastName: string = user.surname?.trim();

    if (trimmedFirstName && trimmedLastName) {
      return `${trimmedFirstName[0]} ${trimmedLastName[0]}`;
    } else {
      return null;
    }
  }
}
