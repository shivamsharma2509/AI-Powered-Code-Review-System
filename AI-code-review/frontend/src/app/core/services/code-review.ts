import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

export interface CodeReviewRequest {
  code: string;
  language: string;
}

export interface CodeReviewResponse {
  success: boolean;
  message: string;
  data: {
    id: number;
    codeSnippet: string;
    reviewResult: string;
    createdAt: string;
  };
  timestamp: string;
}

@Injectable({
  providedIn: 'root'
})
export class CodeReviewService {

  private baseUrl = 'http://localhost:8080/api';

  constructor(private http: HttpClient) {}

  reviewCode(payload: CodeReviewRequest): Observable<CodeReviewResponse> {
    return this.http.post<CodeReviewResponse>(
      `${this.baseUrl}/review`,
      payload
    );
  }
}
