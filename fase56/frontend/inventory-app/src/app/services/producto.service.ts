import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Producto } from '../models/producto.model';

@Injectable({
  providedIn: 'root'
})
export class ProductoService {
  private apiUrl = 'http://localhost:3000/api/producto';

  constructor(private http: HttpClient) { }

  getAll(page: number = 1): Observable<{ data: Producto[]; pageSize: number; }> {
    const params = new HttpParams().set('page', page.toString());
    return this.http.get<{ data: Producto[]; pageSize: number; }>(`${this.apiUrl}/getAll`, { params });
    /*var response = {data: [] as Producto[]};
    const produ = {} as Producto;
    produ.codigo = 1;
    produ.nombre = "Producto 1";
    produ.descripcion = "Descripción del producto 1";
    produ.altura = 10;
    produ.ancho = 20;
    produ.largo = 30;
    produ.marca = "Marca A";
    produ.precio = 100.50;

    response.data.push(produ);
    return new Observable(observer => observer.next(response));*/
  }

  getByFilter(filter: { codigo?: string; nombre?: string; marca?: string; precio?: number | null }, page: number = 1): Observable<{ data: Producto[]; pageSize: number; }> {
    const params = new HttpParams().set('page', page.toString());
    return this.http.post<{ data: Producto[]; pageSize: number; }>(`${this.apiUrl}/getByFilter`, filter, { params });
  }

  getByCode(codigo: number): Observable<{data: Producto}> {
    return this.http.get<{data: Producto}>(`${this.apiUrl}/getByCode/${codigo}`);
  }

  nuevo(producto: Producto): Observable<any> {
    return this.http.post(`${this.apiUrl}/nuevo`, producto);
  }

  guardar(producto: Producto): Observable<any> {
    return this.http.post(`${this.apiUrl}/guardar`, producto);
  }

  eliminar(codigo: number): Observable<any> {
    return this.http.delete(`${this.apiUrl}/eliminar/${codigo}`);
  }
}
