import { Component, OnInit } from '@angular/core';
import { ApiService } from '../../data.service'; 

@Component({
  selector: 'table-cmp',
  moduleId: module.id,
  templateUrl: 'table.component.html'
})
export class TableComponent implements OnInit {
  title = 'frontend';
  authors: any;
  articles: any;
  loadingArticles = false;
  errorFetchingArticles = false;

  constructor(private apiService: ApiService) { }

  ngOnInit(): void {
    this.fetchAuthors();
     this.fetchArticles();
  }

  fetchAuthors() {
    this.apiService.getAuthors().subscribe((data: any) => {
      this.authors = data;
     
    });
  }

  fetchArticles() {
    this.apiService.getArticles().subscribe((data: any) => {
      this.articles = data;
      console.log("articles " , data)
    });
  }
}
