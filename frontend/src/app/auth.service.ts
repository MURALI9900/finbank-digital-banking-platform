import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, tap } from 'rxjs';

export interface AuthResponse { accessToken:string; tokenType:string; expiresIn:number; username:string; role:string; customerNumber?:string; }

@Injectable({providedIn:'root'})
export class AuthService {
  private http=inject(HttpClient);
  private readonly baseUrl='http://localhost:8080/api/v1/auth';
  login(username:string,password:string):Observable<AuthResponse>{
    return this.http.post<AuthResponse>(`${this.baseUrl}/login`,{username,password}).pipe(tap(r=>localStorage.setItem('finbank_auth',JSON.stringify(r))));
  }
  register(username:string,password:string,customerNumber:string):Observable<void>{
    return this.http.post<void>(`${this.baseUrl}/register`,{username,password,customerNumber});
  }
  logout(){localStorage.removeItem('finbank_auth');}
  session():AuthResponse|null { const raw=localStorage.getItem('finbank_auth'); return raw?JSON.parse(raw):null; }
  token(){return this.session()?.accessToken ?? null;}
  customerNumber(){return this.session()?.customerNumber || '';}
}