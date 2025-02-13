import { Component, OnInit, ViewChild } from '@angular/core';
import { MatCheckboxChange } from '@angular/material/checkbox';
import { MatDialog } from '@angular/material/dialog';
import { MatPaginator } from '@angular/material/paginator';
import { MatSort } from '@angular/material/sort';
import { MatTableDataSource } from '@angular/material/table';
import { Router } from '@angular/router';
import { TranslateService } from '@ngx-translate/core';
import { BehaviorSubject, combineLatest, forkJoin, Observable, of } from 'rxjs';
import { filter, map, switchMap, take } from 'rxjs/operators';
import { environment } from '../../../../../environments/environment';
import { ConfirmationDialogComponent } from '../../../../shared/components/confirmation-dialog/confirmation-dialog.component';
import { User } from '../../../../shared/model/api/user';
import { LocalUserService } from '../../../../shared/services/helper/local-user.service';
import { CurrentUserService } from '../../../services/current-user.service';
import { ImportCsvDialogComponent } from './forms/import-csv-dialog/import-csv-dialog.component';
import { UserDialogData } from './forms/user-dialog-data';
import { UserDialogComponent } from './forms/user-dialog/user-dialog.component';

export interface LocalUserManagementUser extends User {
  isAdmin: boolean;
}

@Component({
  selector: 'app-user-management',
  templateUrl: './user-management.component.html',
  styleUrls: ['./user-management.component.scss'],
})
export class UserManagementComponent implements OnInit {

  @ViewChild(MatSort, { static: true }) sort: MatSort;
  @ViewChild(MatPaginator, { static: true }) paginator: MatPaginator;

  currentUser$: BehaviorSubject<User> = new BehaviorSubject<User>(new User());
  columnsToDisplay = ['photo', 'active', 'email', 'givenName', 'department', 'jobTitle', 'isAdmin', 'actions'];

  rowData = new MatTableDataSource([] as User[]);

  showDeactivatedUsers: boolean = false;
  isPlayground: boolean = environment.playground;

  disabledInPlaygroundTranslation: string;
  addUserTranslation: string;
  importUsersFromCSVTranslation: string;
  deactivateUserTranslation: string;

  private users$: BehaviorSubject<LocalUserManagementUser[]> = new BehaviorSubject<LocalUserManagementUser[]>([]);
  private filteredUsers$: BehaviorSubject<LocalUserManagementUser[]> = new BehaviorSubject<LocalUserManagementUser[]>([]);

  constructor(
    private currentUserService: CurrentUserService,
    private dialog: MatDialog,
    private router: Router,
    private userService: LocalUserService,
    private translate: TranslateService,
  ) {
  }

  ngOnInit(): void {
    this.initializeUsers();
    this.initializeCurrentUser();
    this.rowData.sort = this.sort;
    this.rowData.paginator = this.paginator;
    this.users$.asObservable()
      .subscribe(users => {

        const filteredUsers: LocalUserManagementUser[] = this.filterUsers(users);
        this.filteredUsers$.next(filteredUsers);
      });

    this.filteredUsers$.subscribe(users => {
      this.rowData.data = users;
    });

    this.disabledInPlaygroundTranslation = this.translate.instant('disabled-in-demo-version');
    this.addUserTranslation = this.translate.instant('user-management.tooltip.add-new-user');
    this.importUsersFromCSVTranslation = this.translate.instant('user-management.tooltip.import-users-from-csv');
    this.deactivateUserTranslation = this.translate.instant('user-management.tooltip.deactivate-user');
  }

  handleEdit(userToEdit: LocalUserManagementUser): void {
    this.dialog.open(UserDialogComponent, this.getEditDialogData(userToEdit))
      .afterClosed()
      .pipe(
        filter(v => {
          return v;
        }),
        switchMap((editedUser: LocalUserManagementUser) => {
          return combineLatest([of(editedUser), this.userService.getAdminIds$()]);
        }),
        switchMap(([editedUser, adminIds]: [LocalUserManagementUser, string[]]) => {
          if (editedUser.isAdmin) {
            return this.updateUserAndSetToAdminIfChanged$(adminIds, editedUser);
          } else {
            return this.updateUserAndRemoveAdminIfChanged$(adminIds, editedUser);
          }
        }),
      )
      .subscribe((editedUser: LocalUserManagementUser) => {
        this.editUserInTable(userToEdit, editedUser);
      });
  }

  applyFilter(filterValue: string): void {
    this.rowData.filter = filterValue
      .trim()
      .toLowerCase();
  }

  handleDeactivate(userToDeactivate: LocalUserManagementUser): void {
    this.dialog.open(ConfirmationDialogComponent, this.getConfirmDeactivateDialogData(userToDeactivate))
      .afterClosed()
      .pipe(
        filter(v => v),
        switchMap(() => {
          return this.userService.deactivateUser$(userToDeactivate.id);
        }),
      )
      .subscribe(() => {
        const deactivatedUser: LocalUserManagementUser = userToDeactivate;
        deactivatedUser.active = false;
        this.editUserInTable(userToDeactivate, deactivatedUser);
      });
  }

  handleActivate(userToBeActivated: LocalUserManagementUser): void {
    this.dialog.open(ConfirmationDialogComponent, this.getConfirmActivateDialogData(userToBeActivated))
      .afterClosed()
      .pipe(
        filter(v => v),
        switchMap(() => {
          const userWithActiveFlagSet: LocalUserManagementUser = userToBeActivated;

          userWithActiveFlagSet.active = true;

          return this.userService.updateUser$(userWithActiveFlagSet);
        }),
      )
      .subscribe((activatedUser: LocalUserManagementUser) => {
        activatedUser.isAdmin = userToBeActivated.isAdmin;
        this.editUserInTable(userToBeActivated, activatedUser);
      });
  }

  handleCreate(): void {
    this.dialog.open(UserDialogComponent, this.getUserCreationDialogData())
      .afterClosed()
      .pipe(
        filter(v => v),
      )
      .subscribe((user: LocalUserManagementUser) => {
        this.createNewUser(user);
      });
  }

  getConfirmDeactivateDialogData(user: LocalUserManagementUser): { data: { title: string; message: string }; width: string } {
    return {
      width: '500px',
      data: {
        title: 'Benutzer deaktivieren',
        message: `Möchten Sie ${user.givenName} ${user.surname} wirklich deaktivieren?
         Der Benutzer kann die Anwendung danach nicht mehr verwenden
          und wird beim nächsten Zykluswechsel automatisch aus allen Teams entfernt.`,
      },
    };
  }

  getConfirmActivateDialogData(user: LocalUserManagementUser): { data: { title: string; message: string }; width: string } {
    return {
      width: '500px',
      data: {
        title: 'Benutzer reaktivieren',
        message: `Möchten Sie ${user.givenName} ${user.surname} reaktivieren?
         Der Benutzer kann die Anwendung danach wieder in vollem Umfang verwenden.`,
      },
    };
  }

  getEditDialogData(user: LocalUserManagementUser): { data: UserDialogData } {
    return {
      data: {
        title: 'Benutzer bearbeiten',
        user,
      },
    };
  }

  getResetPasswordDialogData(user: LocalUserManagementUser): { data: UserDialogData } {
    return {
      data: {
        title: 'Passwort zurücksetzen',
        user,
      },
    };
  }

  navigateToCompanies(): void {
    this.router.navigate(['/companies'])
      .catch();
  }

  onShowDeactivatedUserChange($event: MatCheckboxChange): void {
    this.showDeactivatedUsers = $event.checked;
    this.filteredUsers$.next(this.filterUsers(this.users$.getValue()));
  }

  handleCsvImport(): void {
    this.dialog.open(ImportCsvDialogComponent)
      .afterClosed()
      .pipe(
        filter(v => v),
        switchMap(users => {
          return this.userService.bulkCreateUsers$(users);
        }),
      )
      .subscribe(users => {
        const localUsers: LocalUserManagementUser[] = users.map(user => {
          return user as LocalUserManagementUser;
        });
        localUsers.forEach(user => this.addUserToTable(user));
      });
  }

  private getUserCreationDialogData(): { data: UserDialogData } {
    return {
      data: {
        title: 'Benutzer erstellen',
      },
    };
  }

  private updateUserAndRemoveAdminIfChanged$(
    adminIds: string[],
    editedUser: LocalUserManagementUser,
  ): Observable<LocalUserManagementUser> {
    if (adminIds.indexOf(editedUser.id) !== -1) {
      return forkJoin([
        this.userService.updateUser$(editedUser)
          .pipe(map((user: LocalUserManagementUser) => {
            user.isAdmin = false;

            return user;
          })),
        this.userService.deleteAdmin$(editedUser.id)],
      )
        .pipe(map(([user, _]: [LocalUserManagementUser, boolean]) => user));
    } else {
      return this.userService.updateUser$(editedUser)
        .pipe(map((user: LocalUserManagementUser) => {
          user.isAdmin = adminIds.indexOf(user.id) !== -1;

          return user;
        }));
    }
  }

  private updateUserAndSetToAdminIfChanged$(
    adminIds: string[],
    editedUser: LocalUserManagementUser,
  ): Observable<LocalUserManagementUser> {
    if (adminIds.indexOf(editedUser.id) !== -1) {
      return forkJoin([
        this.userService.updateUser$(editedUser)
          .pipe(map((user: LocalUserManagementUser) => {
            user.isAdmin = true;

            return user;
          })),
        this.userService.addAdmin$(editedUser)],
      )
        .pipe(map(([user, _]: [LocalUserManagementUser, User]) => user));
    } else {
      return this.userService.updateUser$(editedUser)
        .pipe(map((user: LocalUserManagementUser) => {
          user.isAdmin = adminIds.indexOf(user.id) !== -1;

          return user;
        }));
    }
  }

  private editUserInTable(userToEdit: LocalUserManagementUser, editedUser: LocalUserManagementUser): void {
    const indexOfUserToEdit: number = this.users$.getValue()
      .indexOf(userToEdit);
    const newList: LocalUserManagementUser[] = this.users$.getValue();
    newList[indexOfUserToEdit] = editedUser;
    this.users$.next(newList);
  }

  private initializeUsers(): void {
    combineLatest([this.userService.getUsers$(), this.userService.getAdminIds$()])
      .pipe(
        take(1),
        map(([users, adminIds]: [User[], string[]]) => {
          return users.map((user: LocalUserManagementUser) => {
            if (adminIds.indexOf(user.id) !== -1) {
              user.isAdmin = true;
            }

            return user;
          });
        }),
      )
      .subscribe(users => {
        this.users$.next(users);
      });
  }

  private initializeCurrentUser(): void {
    this.currentUserService.getCurrentUser$()
      .pipe(take(1))
      .subscribe((reveived: User) => {
        this.currentUser$.next(reveived);
      });
  }

  private createNewUser(user: LocalUserManagementUser): void {
    this.createNewUserOnServer$(user)
      .subscribe((newUser: LocalUserManagementUser) => {
          this.addUserToTable(newUser);
        },
      );
  }

  private addUserToTable(user: LocalUserManagementUser): void {
    const userListToUpdate: LocalUserManagementUser[] = this.users$.getValue();
    userListToUpdate.push(user);
    this.users$.next(userListToUpdate);
  }

  private createNewUserOnServer$(user: LocalUserManagementUser): Observable<LocalUserManagementUser> {
    return this.userService.createUser$(user)
      .pipe(
        take(1),
        switchMap((newUser: User) => {
          if (user.isAdmin) {
            return this.userService.addAdmin$(newUser)
              .pipe(switchMap(_ => of(newUser)));
          } else {
            return of(newUser);
          }
        }),
        map((newUser: User) => {
          const newViewUser: LocalUserManagementUser = newUser as LocalUserManagementUser;
          newViewUser.isAdmin = user.isAdmin;

          return newViewUser;
        }),
      );
  }

  private filterUsers(users: LocalUserManagementUser[]): LocalUserManagementUser[] {
    let filteredUsers: LocalUserManagementUser[] = users;

    if (!this.showDeactivatedUsers) {
      filteredUsers = filteredUsers.filter(user => user.active);
    }

    return filteredUsers;
  }
}
