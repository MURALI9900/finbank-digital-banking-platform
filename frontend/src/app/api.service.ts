import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

export interface Account { accountNumber:string; customerNumber:string; accountType:string; status:string; currency:string; balance:number; availableBalance:number; createdAt:string; updatedAt:string; }
export interface Transaction { transactionReference:string; idempotencyKey:string; customerNumber:string; sourceAccountNumber:string; destinationAccountNumber:string; type:string; status:string; amount:number; currency:string; description:string; createdAt:string; updatedAt:string; }
export interface Beneficiary { beneficiaryReference:string; customerNumber:string; beneficiaryName:string; beneficiaryAccountNumber:string; bankName:string; bankCode?:string; nickname?:string; status:string; createdAt:string; updatedAt:string; }
export interface Loan { applicationReference:string; customerNumber:string; loanType:string; status:string; requestedAmount:number; tenureMonths:number; annualInterestRate:number; purpose?:string; reviewedBy?:string; rejectionReason?:string; submittedAt:string; reviewedAt?:string; disbursedAt?:string; }
export interface Kyc { applicationReference:string; customerNumber:string; status:string; submittedAt?:string; reviewedAt?:string; reviewedBy?:string; rejectionReason?:string; }
export interface KycDocument { documentReference:string; applicationReference:string; documentType:string; documentNumber:string; fileName?:string; }
export interface Ticket { ticketReference:string; customerNumber:string; requestType:string; status:string; priority:string; subject:string; description:string; assignedOfficer?:string; resolution?:string; createdAt:string; updatedAt:string; }

@Injectable({providedIn:'root'})
export class ApiService {
 private http=inject(HttpClient); private base='http://localhost:8080/api/v1';
 accounts(c:string){return this.http.get<Account[]>(this.base+'/accounts/customer/'+encodeURIComponent(c));}
 transactions(c:string){return this.http.get<Transaction[]>(this.base+'/transactions/customer/'+encodeURIComponent(c));}
 createTransaction(b:any){return this.http.post<Transaction>(this.base+'/transactions',b);}
 beneficiaries(c:string){return this.http.get<Beneficiary[]>(this.base+'/beneficiaries/customer/'+encodeURIComponent(c));}
 createBeneficiary(b:any){return this.http.post<Beneficiary>(this.base+'/beneficiaries',b);}
 loans(c:string){return this.http.get<Loan[]>(this.base+'/loans/customer/'+encodeURIComponent(c));}
 applyLoan(b:any){return this.http.post<Loan>(this.base+'/loans',b);}
 kyc(c:string){return this.http.get<Kyc>(this.base+'/kyc/customers/'+encodeURIComponent(c));}
 createKyc(b:any){return this.http.post<Kyc>(this.base+'/kyc/applications',b);}
 addKycDocument(ref:string,b:any){return this.http.post<KycDocument>(this.base+'/kyc/applications/'+encodeURIComponent(ref)+'/documents',b);}
 submitKyc(ref:string){return this.http.post<Kyc>(this.base+'/kyc/applications/'+encodeURIComponent(ref)+'/submit',{});}
 tickets(c:string){return this.http.get<Ticket[]>(this.base+'/support/customers/'+encodeURIComponent(c)+'/tickets');}
 createTicket(b:any){return this.http.post<Ticket>(this.base+'/support/tickets',b);}
}