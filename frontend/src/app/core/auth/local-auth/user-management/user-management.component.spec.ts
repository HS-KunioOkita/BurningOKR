import { ComponentFixture, TestBed, waitForAsync } from '@angular/core/testing';
import { UserManagementComponent } from './user-management.component';
import { CUSTOM_ELEMENTS_SCHEMA, NO_ERRORS_SCHEMA } from '@angular/core';
import { MatDialog } from '@angular/material/dialog';
import { RouterTestingModule } from '@angular/router/testing';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { OAuthService } from 'angular-oauth2-oidc';
import { MaterialTestingModule } from '../../../../testing/material-testing.module';
import { LoggerTestingModule } from 'ngx-logger/testing';
import { CurrentUserService } from '../../../services/current-user.service';
import { User } from '../../../../shared/model/api/user';
import { of } from 'rxjs';
import { NoopAnimationsModule } from '@angular/platform-browser/animations';
import { LocalUserService } from '../../../../shared/services/helper/local-user.service';

describe('UserManagementComponent', () => {

  let component: UserManagementComponent;
  let fixture: ComponentFixture<UserManagementComponent>;

  const oAuthServiceMock: any = {};

  const localUserServiceMock: any = {
    getUsers$: jest.fn(),
    getAdminIds$: jest.fn(),
  };

  const currentUserServiceMock: any = {
    getCurrentUser$: jest.fn(),
  };

  const currentUserMock: User = {
    id: 'testId',
    givenName: 'testGivenName',
    surname: 'testSurname',
    jobTitle: 'testJobTitle',
    department: 'testDepartment',
    email: 'testEmail',
    photo: 'testPhoto',
    active: true,
  };
  const anotherUserMock: User = {
    id: 'anotherTestId',
    givenName: 'anotherTestGivenName',
    surname: 'anotherTestSurname',
    jobTitle: 'anotherTestJobTitle',
    department: 'anotherTestDepartment',
    email: 'anotherTestEmail',
    photo: 'anotherTestPhoto',
    active: true,
  };

  beforeEach(waitForAsync(() => {
    localUserServiceMock.getUsers$.mockReset();
    localUserServiceMock.getAdminIds$.mockReset();
    currentUserServiceMock.getCurrentUser$.mockReset();

    localUserServiceMock.getUsers$.mockReturnValue(of([currentUserMock, anotherUserMock]));
    localUserServiceMock.getAdminIds$.mockReturnValue(of([anotherUserMock.id]));
    currentUserServiceMock.getCurrentUser$.mockReturnValue(of(currentUserMock));
    TestBed.configureTestingModule({
      declarations: [UserManagementComponent],
      imports: [
        MaterialTestingModule,
        RouterTestingModule,
        HttpClientTestingModule,
        LoggerTestingModule,
        NoopAnimationsModule,
      ],
      providers: [
        { provide: MatDialog, useValue: {} },
        { provide: LocalUserService, useValue: localUserServiceMock },
        { provide: OAuthService, useValue: oAuthServiceMock },
        { provide: CurrentUserService, useValue: currentUserServiceMock },
      ],
      schemas: [CUSTOM_ELEMENTS_SCHEMA, NO_ERRORS_SCHEMA],
    })
      .compileComponents();
    fixture = TestBed.createComponent(UserManagementComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  }));

  it('should create', () => {
    expect(component)
      .toBeTruthy();
  });
});
