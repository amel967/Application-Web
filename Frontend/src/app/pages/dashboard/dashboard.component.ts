import { Component, OnInit } from '@angular/core';
import { ApiService } from 'app/data.service';
import Chart from 'chart.js';


@Component({
    selector: 'dashboard-cmp',
    moduleId: module.id,
    templateUrl: 'dashboard.component.html'
})

export class DashboardComponent implements OnInit{

  public canvas : any;
  public ctx;
  public chartColor;
  public chartEmail;
  public chartHours;
  public lineChart: Chart;

  public articles = [];
  public articlesByYear = {};
  public articlesByAuthorsCount = {};
  public articlesByCitationCount: any; // or specify the type of data if possible
  public articlesThisYear: number = 0;
  public totalAuthors: number = 0;
  constructor(private dataService: ApiService) {}

  ngOnInit() {
    this.chartColor = "#FFFFFF";
    this.getDataArticles();

  }

  getDataArticles() {
    this.dataService.getArticles().subscribe((data: any) => {
      this.articles = data;
      this.processArticles();
      this.processArticlesData();
      this.processArticlesByAuythor();
      this.processArticlesByCitation();
      this.init_speed_dashboard();
      this.init_dashboard_Email();
      this.init_chart_hour();

    });
  }

  processArticlesData() {
    this.articles.forEach(article => {
      const year = article.year;
      if (!this.articlesByYear[year]) {
        this.articlesByYear[year] = 0;
      }
      this.articlesByYear[year]++;
      
    });
    

  }
  processArticlesByAuythor() {
    this.articles.forEach(article => {
      const authorsCount = article.author_Id.length;
      if (!this.articlesByAuthorsCount[authorsCount]) {
        this.articlesByAuthorsCount[authorsCount] = 0;
      }
      this.articlesByAuthorsCount[authorsCount]++;
    });
  }


  processArticlesByCitation() {
    // Initialize citation count ranges and their counts
    this.articlesByCitationCount = {
      '0-10 citations': 0,
      '11-20 citations': 0,
      '21-30 citations': 0,
      // Add more ranges as needed
    };

    // Iterate through articles and update counts in articlesByCitations
    this.articles.forEach(article => {
      const citationCount = article.citation; // Assuming citation count is in 'citation' property

      // Determine the range for this citation count
      let range = '';
      if (citationCount >= 0 && citationCount <= 10) {
        range = '0-10 citations';
      } else if (citationCount >= 11 && citationCount <= 20) {
        range = '11-20 citations';
      } else if (citationCount >= 21 && citationCount <= 30) {
        range = '21-30 citations';
      } else if (citationCount >= 31 && citationCount <= 40) {
        range = '31-40 citations';
      } else if (citationCount >= 41 && citationCount <= 50) {
        range = '41-50 citations';
      }
      else 
        {
          range = '51+ citations'
        }

      this.articlesByCitationCount[range]++;
    });
  }


  processArticles() {
    const currentYear = new Date().getFullYear();

    this.articlesThisYear = this.articles.filter(article => {
      return new Date(article.year).getFullYear() === currentYear;
    }).length;

    this.totalAuthors = new Set(
      this.articles.reduce((acc, article) => acc.concat(article.author_Id), [])
    ).size;
  }

  getTotalAuthors() {
    return this.totalAuthors;
  }

  getArticlesThisYear() {
    return this.articlesThisYear;
  }

  getAvgAuthorsPerArticle() {
    if (this.articles.length === 0) return 0;
    return (this.totalAuthors / this.articles.length).toFixed(2);
  }


  init_speed_dashboard()
  {
    const years = Object.keys(this.articlesByYear);
    const counter = Object.values(this.articlesByYear);
    var speedCanvas = document.getElementById("speedChart");

    var dataSecond = {
      data: counter,
      fill: false,
      borderColor: '#51CACF',
      backgroundColor: 'transparent',
      pointBorderColor: '#51CACF',
      pointRadius: 4,
      pointHoverRadius: 4,
      pointBorderWidth: 8
    };

    var speedData = {
      labels: years,
      datasets: [ dataSecond]
    };

    var chartOptions = {
      legend: {
        display: false,
        position: 'top'
      }
    };

    this.lineChart = new Chart(speedCanvas, {
      type: 'line',
      hover: false,
      data: speedData,
      options: chartOptions
    });
  }   





  init_chart_hour() {
    // Step 1: Calculate the number of articles by publication
    const publicationsCount = {};
  
    this.articles.forEach(article => {
      const publication = article.publication;
      if (!publicationsCount[publication]) {
        publicationsCount[publication] = 0;
      }
      publicationsCount[publication]++;
    });
  
    // Step 2: Prepare data for bar chart
    const publicationLabels = Object.keys(publicationsCount);
    const publicationData = Object.values(publicationsCount);
  
    // Step 3: Create the bar chart
    this.canvas = document.getElementById("chartHours");
    this.ctx = this.canvas.getContext("2d");
  
    this.chartHours = new Chart(this.ctx, {
      type: 'bar',
      data: {
        labels: publicationLabels,
        datasets: [{
          label: "Number of Articles by Publication",
          backgroundColor: '#36A2EB', // Blue color for bars
          data: publicationData
        }]
      },
      options: {
        legend: {
          display: false
        },
        scales: {
          yAxes: [{
            ticks: {
              beginAtZero: true
            }
          }]
        },
        tooltips: {
          enabled: true
        },
      }
    });
  }
  
  
  
  
  
     
   
    init_dashboard_Email() {
      const authorsCounts = Object.keys(this.articlesByAuthorsCount);
      const counts = Object.values(this.articlesByAuthorsCount);
    
      // Get the canvas and context
      this.canvas = document.getElementById("chartEmail");
      this.ctx = this.canvas.getContext("2d");
    
      // Initialize the pie chart
      this.chartEmail = new Chart(this.ctx, {
        type: 'pie',
        data: {
          labels: authorsCounts,
          datasets: [{
            label: "Articles",
            backgroundColor: [
              '#e3e3e3',
              '#4acccd',
              '#fcc468',
              '#ef8157',
              '#cf8227'
            ],
            data: counts
          }]
        },
        options: {
          legend: {
            display: true, // Display the legend
            position: 'right', // Position the legend to the right
          },
          pieceLabel: {
            render: 'percentage',
            fontColor: 'white',
            precision: 2
          },
          tooltips: {
            enabled: true // Enable tooltips to display data on hover
          },
        }
      });
    }
    




    }





  