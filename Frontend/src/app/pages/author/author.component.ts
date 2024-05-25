import { Component, OnInit } from '@angular/core';
import { ApiService } from 'app/data.service';

@Component({
  selector: 'app-author',
  templateUrl: './author.component.html',
  styleUrls: ['./author.component.scss']
})
export class AuthorComponent implements OnInit {

  title = 'frontend';
  authors: any;

  constructor(private apiService: ApiService) { }

  ngOnInit(): void {
    this.fetchAuthors();
  }

  fetchAuthors() {
    this.apiService.getAuthors().subscribe((data: any) => {
      this.authors = data;
     
    });
  }


}
