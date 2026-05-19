export interface ProductoInventario {
  codigo: number;
  nombre: string;
  existenciaActual: number;
}

export interface Inventario {
  id: number;
  fecha: Date;
  responsable: string;
  productos: ProductoInventario[];
}
