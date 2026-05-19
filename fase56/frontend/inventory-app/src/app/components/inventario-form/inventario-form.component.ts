import { ChangeDetectorRef, Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ReactiveFormsModule, FormBuilder, FormGroup, Validators } from '@angular/forms';
import { Router, ActivatedRoute } from '@angular/router';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatButtonModule } from '@angular/material/button';
import { MatSnackBar, MatSnackBarModule } from '@angular/material/snack-bar';
import { MatCardModule } from '@angular/material/card';
import { MatTableModule } from '@angular/material/table';
import { MatDialog } from '@angular/material/dialog';
import { InventarioService } from '../../services/inventario.service';
import { ProductoService } from '../../services/producto.service';
import { Inventario, ProductoInventario } from '../../models/inventario.model';
import { Producto } from '../../models/producto.model';
import { ConfirmDialogComponent } from '../confirm-dialog/confirm-dialog.component';
import { FormsModule } from '@angular/forms';

@Component({
  selector: 'app-inventario-form',
  standalone: true,
  imports: [
    CommonModule,
    ReactiveFormsModule,
    MatFormFieldModule,
    MatInputModule,
    MatButtonModule,
    MatSnackBarModule,
    MatCardModule,
    MatTableModule,
    FormsModule
  ],
  templateUrl: './inventario-form.component.html',
  styleUrl: './inventario-form.component.css'
})
export class InventarioFormComponent implements OnInit {
  inventarioForm: FormGroup;
  isEditing = false;
  id: number = 0;
  productos: Producto[] = [];
  productosInventario: ProductoInventario[] = [];
  displayedColumns: string[] = ['codigo', 'nombre', 'existencia'];
  productoInventarioList: ProductoInventario[] = [];
  loaded = false;

  constructor(
    private fb: FormBuilder,
    private inventarioService: InventarioService,
    private productoService: ProductoService,
    private router: Router,
    private route: ActivatedRoute,
    private snackBar: MatSnackBar,
    private dialog: MatDialog
  ) {
    this.inventarioForm = this.fb.group({
      responsable: ['', [Validators.required, Validators.maxLength(100)]]
    });
  }

  ngOnInit(): void {
    this.route.params.subscribe(params => {
      if (params['id']) {
        this.id = +params['id'];
        this.isEditing = true;
        this.cargarInventario();
      } else {
        this.isEditing = false;
        this.cargarProductos();
      }
    });
  }

  cargarProductos(): void {
    this.productoService.getAll().subscribe(
      (response) => {
        this.productos = response.data;
        this.productoInventarioList = response.data.map(p => ({
          codigo: p.codigo,
          nombre: p.nombre,
          existenciaActual: 0
        }));
        this.loaded = true;
      },
      (error) => {
        console.error('Error al cargar productos:', error);
      }
    );
  }

  cargarInventario(): void {
    if (!this.id) {
      return;
    }

    this.inventarioService.getById(this.id).subscribe(
      (response) => {
        console.log('Inventario cargado:', response.data);
        this.inventarioForm.patchValue({
          responsable: response.data.responsable
        });

        this.productoInventarioList = response.data.productos.map(p => ({
          codigo: p.codigo,
          nombre: p.nombre,
          existenciaActual: p.existenciaActual ?? 0
        }));
        this.loaded = true;
      },
      (error) => {
        console.error('Error al cargar inventario:', error);
        this.snackBar.open('Error al cargar el inventario', 'Cerrar', { duration: 5000 });
      }
    );
  }

  guardar(): void {
    if (this.inventarioForm.invalid) {
      this.snackBar.open('Por favor complete todos los campos', 'Cerrar', { duration: 5000 });
      return;
    }

    const dialogRef = this.dialog.open(ConfirmDialogComponent, {
      width: '400px',
      data: { message: '¿Está seguro de que desea continuar?' }
    });

    dialogRef.afterClosed().subscribe((result) => {
      if (result) {
        const inventario: Inventario = {
          id: this.id || 0,
          fecha: new Date().toISOString().replace('T', ' ').substring(0, 19), // Formato YYYY-MM-DD HH:MM:SS
          responsable: this.inventarioForm.get('responsable')?.value,
          productos: this.productoInventarioList.map(p => ({
            codigo: p.codigo,
            nombre: p.nombre,
            existenciaActual: p.existenciaActual || 0
          }))
        };

        if (this.isEditing && this.id) {
          this.inventarioService.guardar(this.id, inventario).subscribe(
            () => {
              this.snackBar.open('Inventario actualizado correctamente', 'Cerrar', { duration: 3000 });
              this.router.navigate(['/inventarios']);
            },
            (error) => {
              console.error('Error al guardar inventario:', error);
              this.snackBar.open('No se pudo guardar el inventario', 'Cerrar', { duration: 5000 });
            }
          );
        } else {
          this.inventarioService.nuevo(inventario).subscribe(
            () => {
              this.snackBar.open('Inventario creado correctamente', 'Cerrar', { duration: 3000 });
              this.router.navigate(['/inventarios']);
            },
            (error) => {
              console.error('Error al crear inventario:', error);
              this.snackBar.open('No se pudo crear el inventario', 'Cerrar', { duration: 5000 });
            }
          );
        }
      }
    });
  }

  cancelar(): void {
    this.router.navigate(['/inventarios']);
  }

  updateExistencia(producto: any, value: number): void {
    const index = this.productoInventarioList.findIndex(p => p.codigo === producto.codigo);
    if (index >= 0) {
      this.productoInventarioList[index].existenciaActual = value;
    }
  }
}
