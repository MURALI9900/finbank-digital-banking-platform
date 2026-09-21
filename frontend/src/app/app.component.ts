import { Component } from '@angular/core';
import { RouterLink, RouterOutlet } from '@angular/router';

@Component({selector:'app-root',standalone:true,imports:[RouterOutlet,RouterLink],template:`
<nav><strong>FinBank</strong><span></span><a routerLink="/dashboard">Dashboard</a><a routerLink="/login">Login</a><a routerLink="/register">Register</a></nav>
<main><router-outlet /></main>`})
export class AppComponent {}