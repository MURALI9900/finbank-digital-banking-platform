import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
export interface Account {accountNumber:string;customerNumber:string;accountType:string;status:string;currency:string;balance:number;availableBalance:number;}
export interface Transaction {transactionReference:string;customerNumber:string;sourceAccountNumber:string;destinationAccountNumber:string;type:string;status:string;amount:number;currency:string;description:string;createdAt:string;}
@Injectable({providedIn:'root'}) export class ApiService {
 private http=inject(HttpClient); private base='http://localhost:8080/api/v1';
 accounts(customerNumber:string):Observable<Account[]>{return this.http.get<Account[]>(`${this.base}/accounts/customer/${encodeURIComponent(customerNumber)}`);}
 transactions(customerNumber:string):Observable<Transaction[]>{return this.http.get<Transaction[]>(`${this.base}/transactions/customer/${encodeURIComponent(customerNumber)}`);}
 createTransaction(body:unknown){return this.http.post<Transaction>(`${this.base}/transactions`,body);}
}