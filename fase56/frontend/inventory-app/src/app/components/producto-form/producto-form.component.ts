import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ReactiveFormsModule, FormBuilder, FormGroup, Validators } from '@angular/forms';
import { Router, ActivatedRoute } from '@angular/router';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatButtonModule } from '@angular/material/button';
import { MatSnackBar, MatSnackBarModule } from '@angular/material/snack-bar';
import { MatCardModule } from '@angular/material/card';
import { ProductoService } from '../../services/producto.service';
import { Producto } from '../../models/producto.model';

@Component({
  selector: 'app-producto-form',
  standalone: true,
  imports: [
    CommonModule,
    ReactiveFormsModule,
    MatFormFieldModule,
    MatInputModule,
    MatButtonModule,
    MatSnackBarModule,
    MatCardModule
  ],
  templateUrl: './producto-form.component.html',
  styleUrl: './producto-form.component.css'
})
export class ProductoFormComponent implements OnInit {
  productoForm: FormGroup;
  isEditing = false;
  codigo: number | null = null;

  constructor(
    private fb: FormBuilder,
    private productoService: ProductoService,
    private router: Router,
    private route: ActivatedRoute,
    private snackBar: MatSnackBar
  ) {
    this.productoForm = this.fb.group({
      nombre: ['', [Validators.required, Validators.maxLength(100)]],
      descripcion: ['', [Validators.required, Validators.maxLength(500)]],
      altura: ['', [Validators.required, Validators.pattern(/^\d+(\.\d{1,2})?$/)]],
      ancho: ['', [Validators.required, Validators.pattern(/^\d+(\.\d{1,2})?$/)]],
      largo: ['', [Validators.required, Validators.pattern(/^\d+(\.\d{1,2})?$/)]],
      marca: ['', [Validators.required, Validators.maxLength(100)]],
      precio: ['', [Validators.required, Validators.pattern(/^\d+(\.\d{1,2})?$/)]]
    });
  }

  ngOnInit(): void {
    this.route.params.subscribe(params => {
      if (params['codigo']) {
        this.codigo = params['codigo'];
        this.isEditing = true;
        this.cargarProducto();
      } else {
        this.isEditing = false;
      }
    });
  }

  cargarProducto(): void {
    if (this.codigo) {
      this.productoService.getByCode(this.codigo).subscribe(
        (producto) => {
          console.log('Producto cargado:', producto.data);
          this.productoForm.patchValue(producto.data);
          this.productoForm.get('codigo')?.disable();
        },
        (error) => {
          console.error('Error al cargar producto:', error);
          this.snackBar.open('Error al cargar el producto', 'Cerrar', { duration: 5000 });
        }
      );
    }
  }

  guardar(): void {
    if (this.productoForm.invalid) {
      this.snackBar.open('Por favor complete todos los campos correctamente', 'Cerrar', { duration: 5000 });
      return;
    }

    const producto: Producto = this.productoForm.getRawValue();
    if (this.isEditing && this.codigo) {
      this.productoService.guardar(producto).subscribe(
        () => {
          this.snackBar.open('Producto guardado correctamente', 'Cerrar', { duration: 3000 });
          this.router.navigate(['/productos']);
        },
        (error) => {
          console.error('Error al guardar producto:', error);
          this.snackBar.open('No se pudo agregar el producto', 'Cerrar', { duration: 5000 });
        }
      );
    } else {
      producto.codigo = 0; // El backend asignará el código
      this.productoService.nuevo(producto).subscribe(
        () => {
          this.snackBar.open('Producto guardado correctamente', 'Cerrar', { duration: 3000 });
          this.router.navigate(['/productos']);
        },
        (error) => {
          console.error('Error al crear producto:', error);
          this.snackBar.open('No se pudo agregar el producto', 'Cerrar', { duration: 5000 });
        }
      );
    }
  }

  cancelar(): void {
    this.router.navigate(['/productos']);
  }
}
