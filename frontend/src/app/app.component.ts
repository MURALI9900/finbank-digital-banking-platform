import { Component,inject } from '@angular/core';
import { RouterLink, RouterOutlet } from '@angular/router';
import { AuthService } from './auth.service';
@Component({selector:'app-root',standalone:true,imports:[RouterOutlet,RouterLink],template:`
<nav><strong>FinBank</strong><span></span><a routerLink="/dashboard">Dashboard</a><a routerLink="/accounts">Accounts</a><a routerLink="/transactions">Transactions</a><a routerLink="/beneficiaries">Beneficiaries</a><a routerLink="/loans">Loans</a><a routerLink="/kyc">KYC</a><a routerLink="/support">Support</a><a routerLink="/login">Login</a><a routerLink="/register">Register</a><button *ngIf="auth.session()" (click)="logout()">Logout</button></nav><main><router-outlet /></main>`,styles:[`nav{display:flex;gap:14px;align-items:center;flex-wrap:wrap}nav span{flex:1}button{padding:6px 12px}`]})
export class AppComponent { auth=inject(AuthService); logout(){this.auth.logout();location.href='/login';} }
