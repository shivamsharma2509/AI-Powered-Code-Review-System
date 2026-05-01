import { Component } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';

@Component({
  selector: 'app-code-review',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './code-review.component.html',
  styleUrls: ['./code-review.component.css']
})
export class CodeReviewComponent {

  code = '';
  language = 'Java';
  reviewResult = '';
  loading = false;
  error = '';

  constructor(private http: HttpClient) {}

  submitCode() {
    if (!this.code.trim()) {
      this.error = 'Please enter code';
      return;
    }

    this.loading = true;

    this.http.post<any>('http://localhost:8080/api/review', {
      code: this.code,
      language: this.language
    }).subscribe({
      next: (res) => {
        this.reviewResult = res?.data?.reviewResult;
        this.loading = false;
      },
      error: () => {
        this.error = 'Server error';
        this.loading = false;
      }
    });
  }

  clear() {
    this.code = '';
    this.reviewResult = '';
    this.error = '';
  }
}
