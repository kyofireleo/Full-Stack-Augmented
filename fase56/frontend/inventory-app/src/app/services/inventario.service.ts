import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Inventario } from '../models/inventario.model';

@Injectable({
  providedIn: 'root'
})
export class InventarioService {
  private apiUrl = 'http://localhost:3000/api/inventario';

  constructor(private http: HttpClient) { }

  getAll(): Observable<{ data: Inventario[] }> {
    return this.http.get<{ data: Inventario[] }>(`${this.apiUrl}/getAll`);
  }

  getByFilter(filter: { id?: number | null; fecha?: string | null; responsable?: string }): Observable<{ data: Inventario[] }> {
    return this.http.post<{ data: Inventario[] }>(`${this.apiUrl}/getByFilter`, filter);
  }

  getById(id: number): Observable<{ data: Inventario }> {
    return this.http.get<{ data: Inventario }>(`${this.apiUrl}/getById/${id}`);
  }

  nuevo(inventario: Inventario): Observable<any> {
    return this.http.post(`${this.apiUrl}/nuevo`, inventario);
  }

  guardar(id: number, inventario: Inventario): Observable<any> {
    return this.http.post(`${this.apiUrl}/guardar/${id}`, inventario);
  }
}
