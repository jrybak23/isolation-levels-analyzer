import {Injectable} from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {Result} from "../model/result";
import {Observable} from "rxjs";

@Injectable({
  providedIn: 'root'
})
export class ResultService {

  constructor(private httpClient: HttpClient) { }

  public getResults(): Observable<Array<Result>> {
    return this.httpClient.get<Array<Result>>('/assets/results/result.json');
  }
}
