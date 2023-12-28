import { Routes } from '@angular/router';
import {RacelistComponent} from "./racelist/racelist.component";
import {HomeComponent} from "./home/home.component";
import {AboutComponent} from "./about/about.component";
import {OAuth2RedirectHandlerComponent} from "./auth/oauth2-redirect-handler/oauth2-redirect-handler.component";
import {RacedetailComponent} from "./racedetail/racedetail.component";

export const routes: Routes = [
  {path: '', pathMatch: 'full', component: HomeComponent},
  {path: 'oauth2/redirect', component: OAuth2RedirectHandlerComponent},
  {path: 'races/all', component: RacelistComponent},
  {path: 'race/:id', component: RacedetailComponent},
  {path: 'about', component: AboutComponent}
];
