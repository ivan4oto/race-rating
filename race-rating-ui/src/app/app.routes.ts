import { Routes } from '@angular/router';
import {RacelistComponent} from "./racelist/racelist.component";
import {AboutComponent} from "./about/about.component";
import {OAuth2RedirectHandlerComponent} from "./auth/oauth2-redirect-handler/oauth2-redirect-handler.component";
import {RacedetailComponent} from "./racedetail/racedetail.component";
import {CreateRaceComponent} from "./create-race/create-race.component";
import {AuthGuard} from "./auth/guard/auth.guard";
import {EditRaceComponent} from "./edit-race/edit-race.component";
import {LoginComponent} from "./auth/login/login.component";
import {AdminPanelComponent} from "./admin-panel/admin-panel.component";
import {SignupComponent} from "./auth/signup/signup.component";
import {MyProfileComponent} from "./my-profile/my-profile.component";
import {ContactComponent} from "./contact/contact.component";
import {PrivacyPolicyComponent} from "./privacy-policy/privacy-policy.component";
import {ForgotPasswordComponent} from "./auth/forgot-password/forgot-password.component";
import {PasswordResetComponent} from "./auth/password-reset/password-reset.component";

export const routes: Routes = [
  {path: '', pathMatch: 'full', component: RacelistComponent},
  {path: 'oauth2/redirect', component: OAuth2RedirectHandlerComponent},
  {path: 'race/all', component: RacelistComponent},
  {path: 'race/:id', component: RacedetailComponent},
  {path: 'race/:id/edit', component: EditRaceComponent, canActivate: [AuthGuard]},
  {path: 'profile', component: MyProfileComponent},
  {path: 'create', component: CreateRaceComponent, canActivate: [AuthGuard]},
  {path: 'about', component: AboutComponent},
  {path: 'contact', component: ContactComponent},
  {path: 'privacy-policy', component: PrivacyPolicyComponent},
  {path: 'login', component: LoginComponent},
  {path: 'forgot-password', component: ForgotPasswordComponent},
  {path: 'reset-password', component: PasswordResetComponent},
  {path: 'signup', component: SignupComponent},
  {path: 'admin', component: AdminPanelComponent, canActivate: [AuthGuard]}
];
