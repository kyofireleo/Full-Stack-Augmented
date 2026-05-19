export interface ProductoInventario {
  codigo: number;
  nombre: string;
  existenciaActual: number;
}

export interface Inventario {
  id: number;
  fecha: string;
  responsable: string;
  productos: ProductoInventario[];
}
