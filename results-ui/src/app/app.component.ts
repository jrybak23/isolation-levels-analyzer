import {Component, OnInit} from '@angular/core';
import {ResultService} from "./services/result.service";
import {Observable} from "rxjs";
import {Result} from "./model/result";

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.scss']
})
export class AppComponent implements OnInit {
  title = 'results-ui';
  results: Observable<Array<Result>> | null = null;

  constructor(private resultService: ResultService) {
  }

  ngOnInit(): void {
    this.results = this.resultService.getResults();
  }
}
