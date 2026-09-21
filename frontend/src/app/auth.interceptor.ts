import { HttpInterceptorFn } from '@angular/common/http';

export const authInterceptor: HttpInterceptorFn = (req,next) => {
  const token=localStorage.getItem('finbank_auth');
  if(!token) return next(req);
  try {
    const accessToken=JSON.parse(token).accessToken;
    return next(req.clone({setHeaders:{Authorization:`Bearer ${accessToken}`}}));
  } catch { return next(req); }
};