import { Component, OnInit } from '@angular/core';
import { ApiService } from 'app/data.service';

@Component({
  selector: 'app-article',
  templateUrl: './article.component.html',
  styleUrls: ['./article.component.scss']
})
export class ArticleComponent implements OnInit {

  title = 'frontend';
  articles: any;


  constructor(private apiService: ApiService) { }

  ngOnInit(): void {
     this.fetchArticles();
  }

 

  fetchArticles() {
    this.apiService.getArticles().subscribe((data: any) => {
      this.articles = data;
      console.log("articles " , data)
    });
  }

}
