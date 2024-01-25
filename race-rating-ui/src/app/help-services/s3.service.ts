import { Injectable } from '@angular/core';
import {HttpClient} from "@angular/common/http";
import {Observable} from "rxjs";
import {environment} from "../../environments/environment";
import {S3objectModel} from "../misc-models/s3object.model";


@Injectable({
  providedIn: 'root'
})
export class S3Service {
  private apiUrl = environment.apiUrl

  constructor(private httpClient: HttpClient) { }

  listImages(raceId: string): Observable<S3objectModel[]> {
    return this.httpClient.get<S3objectModel[]>(`${this.apiUrl}api/race/${raceId}/list-images`);
  }
}
