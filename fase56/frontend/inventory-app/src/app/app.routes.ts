import { Routes } from '@angular/router';
import { ProductoListComponent } from './components/producto-list/producto-list.component';
import { ProductoFormComponent } from './components/producto-form/producto-form.component';
import { InventarioListComponent } from './components/inventario-list/inventario-list.component';
import { InventarioFormComponent } from './components/inventario-form/inventario-form.component';

export const routes: Routes = [
  { path: '', redirectTo: 'productos', pathMatch: 'full' },
  { path: 'productos', component: ProductoListComponent },
  { path: 'productos/agregar', component: ProductoFormComponent },
  { path: 'productos/editar/:codigo', component: ProductoFormComponent },
  { path: 'inventarios', component: InventarioListComponent },
  { path: 'inventarios/agregar', component: InventarioFormComponent },
  { path: 'inventarios/editar/:id', component: InventarioFormComponent }
];
