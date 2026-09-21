import { Component, inject } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { AuthService } from './auth.service';

@Component({standalone:true,imports:[FormsModule,RouterLink],template:`
<section class="card"><h1>Create account</h1><p>Register a FinBank user.</p>
<form (ngSubmit)="register()"><label>Username<input [(ngModel)]="username" name="username" minlength="4" required></label><label>Password<input type="password" [(ngModel)]="password" name="password" minlength="8" required></label><label>Customer number<input [(ngModel)]="customerNumber" name="customerNumber"></label><button>Create account</button></form>
@if (error) { <p class="error">{{error}}</p> } @if (created) { <p>Account created. <a routerLink="/login">Sign in</a></p> }</section>`})
export class RegisterComponent {
 private auth=inject(AuthService); private router=inject(Router); username='';password='';customerNumber='';error='';created=false;
 register(){this.error='';this.auth.register(this.username,this.password,this.customerNumber).subscribe({next:()=>this.created=true,error:e=>this.error=e?.error?.message||'Registration failed.'});}
}