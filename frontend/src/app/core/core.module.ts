import { NgModule, Optional, SkipSelf } from '@angular/core';
import { CommonModule } from '@angular/common';
import { TrackByPropertyPipe } from '../admin/pipes/track-by-property.pipe';
import { FeedbackFormComponent } from './feedback/feedback-form/feedback-form.component';
import { AdminSettingsFormComponent } from './settings/settings-form/admin-settings/admin-settings-form.component';
import { VersionFormComponent } from './version-form/version-form.component';
import { SharedModule } from '../shared/shared.module';
import { ReactiveFormsModule } from '@angular/forms';
import { MatCardModule } from '@angular/material/card';
import { MatCheckboxModule } from '@angular/material/checkbox';
import { MatRippleModule } from '@angular/material/core';
import { MatDialogModule } from '@angular/material/dialog';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatIconModule } from '@angular/material/icon';
import { MatInputModule } from '@angular/material/input';
import { MatSelectModule } from '@angular/material/select';
import { MatTabsModule } from '@angular/material/tabs';
import { MatTooltipModule } from '@angular/material/tooltip';
import { FeedbackButtonComponent } from './feedback/feedback-button/feedback-button.component';
import { FlexModule } from '@angular/flex-layout';
import { RouterModule } from '@angular/router';
import { LocalAuthModule } from './auth/local-auth/local-auth.module';
import { LandingPageNavigationComponent } from './landing-page-router/landing-page-navigation.component';
import { SettingsFormComponent } from './settings/settings-form/settings-form.component';
import { UserSettingsComponent } from './settings/settings-form/user-settings/user-settings.component';
import { TranslateModule } from '@ngx-translate/core';
import { PickLanguageComponent } from './settings/pick-language/pick-language.component';

@NgModule({
  declarations: [
    FeedbackButtonComponent,
    FeedbackFormComponent,
    AdminSettingsFormComponent,
    TrackByPropertyPipe,
    VersionFormComponent,
    LandingPageNavigationComponent,
    SettingsFormComponent,
    UserSettingsComponent,
    PickLanguageComponent,
  ],
  exports: [
    TrackByPropertyPipe,
    VersionFormComponent,
    FeedbackButtonComponent,
  ],
  imports: [
    MatInputModule,
    MatIconModule,
    MatTooltipModule,
    CommonModule,
    SharedModule,
    ReactiveFormsModule,
    MatFormFieldModule,
    MatDialogModule,
    MatTabsModule,
    MatSelectModule,
    MatCardModule,
    FlexModule,
    RouterModule,
    MatRippleModule,
    LocalAuthModule,
    MatCheckboxModule,
    TranslateModule,
  ],
})
export class CoreModule {
  constructor(@Optional() @SkipSelf() parentModule: CoreModule) {
    if (parentModule) {
      throw new Error('CoreModule has already been loaded. You should only import Core modules in the AppModule only.');
    }
  }
}
