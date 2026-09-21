import { Component, inject } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { AuthService } from './auth.service';

@Component({standalone:true,imports:[FormsModule,RouterLink],template:`
<section class="card"><h1>Welcome back</h1><p>Sign in to FinBank Digital Banking.</p>
<form (ngSubmit)="login()"><label>Username<input [(ngModel)]="username" name="username" required></label><label>Password<input type="password" [(ngModel)]="password" name="password" required></label><button>Sign in</button></form>
@if (error) { <p class="error">{{error}}</p> }<a routerLink="/register">Create an account</a></section>`})
export class LoginComponent {
  private auth=inject(AuthService); private router=inject(Router); username=''; password=''; error='';
  login(){this.error='';this.auth.login(this.username,this.password).subscribe({next:()=>this.router.navigateByUrl('/dashboard'),error:e=>this.error=e?.error?.message||'Login failed. Check your credentials.'});}
}