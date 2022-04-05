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
  private readonly SOURCE_CODE_LINK_PREFIX = 'https://github.com/jrybak23/isolation-levels-analyzer/blob/master/analyzer/src/main/java/com/example/isolationlevelsdemo/analises/';
  title = 'results-ui';
  results: Observable<Array<Result>> | null = null;

  constructor(private resultService: ResultService) {
  }

  ngOnInit(): void {
    this.results = this.resultService.getResults();
  }

  getSourceLink(columnHeader: string): string {
    const className = columnHeader.replace(/\s/g, '') + 'Analysis.java';
    return this.SOURCE_CODE_LINK_PREFIX + className;
  }
}
