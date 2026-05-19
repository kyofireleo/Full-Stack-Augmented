import { AfterViewInit, ChangeDetectorRef, Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ReactiveFormsModule, FormBuilder, FormGroup } from '@angular/forms';
import { Router } from '@angular/router';
import { MatTableModule } from '@angular/material/table';
import { MatPaginatorModule, PageEvent } from '@angular/material/paginator';
import { MatButtonModule } from '@angular/material/button';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatIconModule } from '@angular/material/icon';
import { MatTooltipModule } from '@angular/material/tooltip';
import { MatDialog, MatDialogModule } from '@angular/material/dialog';
import { ProductoService } from '../../services/producto.service';
import { Producto } from '../../models/producto.model';
import { ConfirmDialogComponent } from '../confirm-dialog/confirm-dialog.component';

@Component({
  selector: 'app-producto-list',
  standalone: true,
  imports: [
    CommonModule,
    ReactiveFormsModule,
    MatTableModule,
    MatPaginatorModule,
    MatButtonModule,
    MatFormFieldModule,
    MatInputModule,
    MatIconModule,
    MatTooltipModule,
    MatDialogModule
  ],
  templateUrl: './producto-list.component.html',
  styleUrls: ['./producto-list.component.css']
})
export class ProductoListComponent implements OnInit, AfterViewInit {
  dataSource: Producto[] = [];
  displayedColumns: string[] = ['codigo', 'nombre', 'marca', 'precio', 'acciones'];
  filterForm: FormGroup;
  pageSize = 0;
  pageSizeOptions: number[] = [];
  currentPage = 1;
  totalItems = 0;

  constructor(
    private productoService: ProductoService,
    private fb: FormBuilder,
    private router: Router,
    private dialog: MatDialog,
    private cd: ChangeDetectorRef
  ) {
    this.filterForm = this.fb.group({
      codigo: [''],
      nombre: [''],
      marca: [''],
      precio: [null]
    });
  }

  ngOnInit(): void {
  }

  ngAfterViewInit(): void {
    this.cd.detach();
    setTimeout(() => this.loadProductos());
  }

  loadProductos(page: number = 1): void {
    this.currentPage = page;
    this.productoService.getAll(page).subscribe(
      (response) => {
        setTimeout(() => {
          this.dataSource = response.data;
          this.totalItems = response.data.length;
          this.pageSize = response.pageSize ?? this.pageSize;
          this.pageSizeOptions = [this.pageSize];
          this.cd.detectChanges();
        });
      },
      (error) => {
        console.error('Error al cargar productos:', error);
      }
    );
  }

  buscar(page: number = 1): void {
    const filtro = this.filterForm.value;
    this.currentPage = page;
    this.productoService.getByFilter(filtro, page).subscribe(
      (response) => {
        setTimeout(() => {
          this.dataSource = response.data;
          this.totalItems = response.data.length;
          this.pageSize = response.pageSize ?? this.pageSize;
          this.pageSizeOptions = [this.pageSize];
          this.cd.detectChanges();
        });
      },
      (error) => {
        console.error('Error al buscar productos:', error);
      }
    );
  }

  onPageChange(event: PageEvent): void {
    const page = event.pageIndex + 1;
    const filtro = this.filterForm.value;
    const hasFilter =
      !!filtro.codigo || !!filtro.nombre || !!filtro.marca || filtro.precio != null;

    if (hasFilter) {
      this.buscar(page);
    } else {
      this.loadProductos(page);
    }
  }

  editar(codigo: number): void {
    this.router.navigate(['/productos/editar', codigo]);
  }

  eliminar(codigo: number): void {
    const dialogRef = this.dialog.open(ConfirmDialogComponent, {
      width: '400px',
      data: { message: '¿Está seguro de que desea eliminar este producto?' }
    });

    dialogRef.afterClosed().subscribe((result) => {
      if (result) {
        this.productoService.eliminar(codigo).subscribe(
          () => {
            this.loadProductos();
          },
          (error) => {
            console.error('Error al eliminar producto:', error);
          }
        );
      }
    });
  }

  agregarProducto(): void {
    this.router.navigate(['/productos/agregar']);
  }
}
