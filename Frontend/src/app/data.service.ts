import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class ApiService {
  private baseUrl = 'http://localhost:8080';

  constructor(private http: HttpClient) { }

  getAuthors(): Observable<any> {
   
    return this.http.get(`${this.baseUrl}/authors`);
    
  }
  
  getArticles(): Observable<any> {
    return this.http.get(`${this.baseUrl}/articles`);
  }
}
