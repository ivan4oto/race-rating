import { Routes } from '@angular/router';
import {RacelistComponent} from "./racelist/racelist.component";
import {HomeComponent} from "./home/home.component";
import {AboutComponent} from "./about/about.component";
import {OAuth2RedirectHandlerComponent} from "./auth/oauth2-redirect-handler/oauth2-redirect-handler.component";
import {RacedetailComponent} from "./racedetail/racedetail.component";
import {CreateRaceComponent} from "./create-race/create-race.component";
import {AuthGuard} from "./auth/guard/auth.guard";
import {EditRaceComponent} from "./edit-race/edit-race.component";

export const routes: Routes = [
  {path: '', pathMatch: 'full', component: RacelistComponent},
  {path: 'oauth2/redirect', component: OAuth2RedirectHandlerComponent},
  {path: 'race/all', component: RacelistComponent},
  {path: 'race/:id', component: RacedetailComponent},
  {path: 'race/:id/edit', component: EditRaceComponent}, // canActivate: [AuthGuard]},
  {path: 'create', component: CreateRaceComponent},
  {path: 'about', component: AboutComponent}
];
