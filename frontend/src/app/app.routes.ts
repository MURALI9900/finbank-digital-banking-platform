import { Routes } from '@angular/router';
import { LoginComponent } from './login.component';import { RegisterComponent } from './register.component';import { DashboardComponent } from './dashboard.component';import { AccountsComponent } from './accounts.component';import { TransactionsComponent } from './transactions.component';import { BeneficiariesComponent } from './beneficiaries.component';import { LoansComponent } from './loans.component';import { KycComponent } from './kyc.component';import { SupportComponent } from './support.component';import { authGuard } from './auth.guard';
export const routes: Routes = [
{path:'',pathMatch:'full',redirectTo:'dashboard'},
{path:'login',component:LoginComponent},{path:'register',component:RegisterComponent},
{path:'dashboard',component:DashboardComponent,canActivate:[authGuard]},{path:'accounts',component:AccountsComponent,canActivate:[authGuard]},{path:'transactions',component:TransactionsComponent,canActivate:[authGuard]},{path:'beneficiaries',component:BeneficiariesComponent,canActivate:[authGuard]},{path:'loans',component:LoansComponent,canActivate:[authGuard]},{path:'kyc',component:KycComponent,canActivate:[authGuard]},{path:'support',component:SupportComponent,canActivate:[authGuard]}
];
