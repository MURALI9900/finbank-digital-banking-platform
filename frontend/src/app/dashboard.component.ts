import { Component, inject } from '@angular/core';
import { Router } from '@angular/router';
import { AuthService } from './auth.service';

@Component({standalone:true,template:`
<section class="hero"><div><p class="eyebrow">DIGITAL BANKING</p><h1>Welcome, {{session?.username}}</h1><p>Manage your FinBank account securely through the API Gateway.</p></div><button (click)="logout()">Sign out</button></section>
<div class="grid"><article><h3>Session</h3><p>Role: <b>{{session?.role}}</b></p><p>Token type: {{session?.tokenType}}</p></article><article><h3>Platform</h3><p>Spring Boot microservices</p><p>JWT + API Gateway + PostgreSQL + Kafka</p></article><article><h3>Next modules</h3><p>Accounts · Transactions · Beneficiaries · Loans · KYC · Support</p></article></div>`})
export class DashboardComponent {
 private auth=inject(AuthService);private router=inject(Router);session=this.auth.session();
 constructor(){if(!this.session)this.router.navigateByUrl('/login');}
 logout(){this.auth.logout();this.router.navigateByUrl('/login');}
}