import { HttpErrorResponse } from '@angular/common/http';
import { Component, OnInit } from '@angular/core';
import { FormBuilder, Validators } from '@angular/forms';
import { MatDialogRef } from '@angular/material/dialog';
import { TranslateService } from '@ngx-translate/core';
import { NGXLogger } from 'ngx-logger';
import { Subscription } from 'rxjs';
import { switchMap, take } from 'rxjs/operators';
import { Controls, FormGroupTyped } from '../../../../../typings';
import { Consts } from '../../../../shared/consts';
import { User } from '../../../../shared/model/api/user';
import { NewPasswordForm } from '../../../../shared/model/forms/new-password-form';
import { wrongPasswordValidatorError } from '../../../../shared/validators/errors/wrong-password-validator-error';
import { PasswordsMatchValidator } from '../../../../shared/validators/password-match-validator/passwords-match-validator-function';
import { CurrentUserService } from '../../../services/current-user.service';
import { PasswordService } from '../password-service/password.service';

@Component({
  selector: 'app-change-password-dialog',
  templateUrl: './change-password-dialog.component.html',
  styleUrls: ['./change-password-dialog.component.css'],
})
export class ChangePasswordDialogComponent implements OnInit {

  subscriptions: Subscription[] = [];
  newPasswordForm: FormGroupTyped<NewPasswordForm>;
  title: string;

  constructor(
    private dialogRef: MatDialogRef<ChangePasswordDialogComponent>,
    private formBuilder: FormBuilder,
    private passwordService: PasswordService,
    private currentUserService: CurrentUserService,
    private logger: NGXLogger,
    private translate: TranslateService,
  ) {
  }

  ngOnInit(): void {
    this.generateNewPasswordForm();
    this.title = this.translate.instant('change-password-dialog.form.title');
  }

  generateNewPasswordForm(): void {
    const newPasswordForm: FormGroupTyped<NewPasswordForm> = this.formBuilder.group({
      previousPassword: ['', [Validators.required]],
      newPassword: ['', [Validators.required, Validators.minLength(7)]],
      newPasswordRepetition: ['', [Validators.required]],
    }) as FormGroupTyped<NewPasswordForm>;
    newPasswordForm.setValidators([PasswordsMatchValidator.Validate]);

    this.newPasswordForm = newPasswordForm;
  }

  saveAndClose(): void {
    const controls: Controls<NewPasswordForm> = this.newPasswordForm.controls;
    this.currentUserService.getCurrentUser$()
      .pipe(
        switchMap((user: User) => {
          return this.passwordService.setPasswordWhileUserIsLoggedin$({
            newPassword: controls.newPassword.value,
            oldPassword: controls.previousPassword.value,
            email: user.email,
          });
        }),
        take(1),
      )
      .subscribe(() => {
        this.dialogRef.close();
      }, (error: HttpErrorResponse) => {
        if (error.error.errorInformation === Consts.HTTP_ERROR_RESPONSE_WRONG_PASSWORD) {
          this.newPasswordForm.controls.previousPassword.setErrors(wrongPasswordValidatorError);
        }
      });
  }

}
