import { Inject, Injectable, LOCALE_ID } from '@angular/core';
import { TranslateService } from '@ngx-translate/core';
import { DateAdapter } from '@angular/material/core';
import { CookieHelperService } from './cookie-helper.service';
import { getLocaleId } from '@angular/common';
import { Observable, of } from 'rxjs';
import { map, startWith } from 'rxjs/operators';

@Injectable({
  providedIn: 'root',
})
export class OkrTranslationHelperService {

  private currentLanguage$: Observable<string> = of('');

  constructor(
    @Inject(LOCALE_ID) private locale: string,
    private translateService: TranslateService,
    private dateAdapter: DateAdapter<Date>,
    private cookieHelper: CookieHelperService,
  ) {
  }

  initializeTranslationOnStartup(): void {
    // this language will be used as a fallback when a translation isn't found in the current language
    this.translateService.setDefaultLang('en');
    this.translateService.addLangs(['en', 'de', 'ja']);
    // the lang to use, if the lang isn't available, it will use the current loader to get them
    this.changeToLanguage(this.getInitialLanguage());
    this.currentLanguage$ = this.translateService.onLangChange.pipe(map(lang => lang.lang));
  }

  changeCurrentLanguageTo(language: string): void {
    if (this.translateService.getLangs().includes(language)) {
      this.changeToLanguage(language);
    }
  }

  getInitialLanguage(): string {
    if (this.cookieHelper.isCookieSet('language')) {
      return this.cookieHelper.getCookieValue('language');
    } else if (this.locale !== undefined) {
      return getLocaleId(this.locale);
    } else {
      return 'en';
    }
  }

  getCurrentLanguage$(): Observable<string> {
    return this.currentLanguage$.pipe(startWith(this.translateService.currentLang));
  }

  private changeToLanguage(language: string): void {
    this.translateService.use(language);
    this.dateAdapter.setLocale(language);
    this.cookieHelper.setCookieValue('language', language, 30, '/');
  }
}
