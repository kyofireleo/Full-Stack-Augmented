import { AfterViewInit, ChangeDetectorRef, Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ReactiveFormsModule, FormBuilder, FormGroup } from '@angular/forms';
import { Router } from '@angular/router';
import { MatTableModule } from '@angular/material/table';
import { MatButtonModule } from '@angular/material/button';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatDatepickerModule } from '@angular/material/datepicker';
import { MatNativeDateModule } from '@angular/material/core';
import { MatIconModule } from '@angular/material/icon';
import { MatTooltipModule } from '@angular/material/tooltip';
import { InventarioService } from '../../services/inventario.service';
import { Inventario } from '../../models/inventario.model';

@Component({
  selector: 'app-inventario-list',
  standalone: true,
  imports: [
    CommonModule,
    ReactiveFormsModule,
    MatTableModule,
    MatButtonModule,
    MatFormFieldModule,
    MatInputModule,
    MatDatepickerModule,
    MatNativeDateModule,
    MatIconModule,
    MatTooltipModule
  ],
  templateUrl: './inventario-list.component.html',
  styleUrl: './inventario-list.component.css'
})
export class InventarioListComponent implements OnInit, AfterViewInit {
  inventarios: Inventario[] = [];
  displayedColumns: string[] = ['id', 'fecha', 'responsable', 'acciones'];
  filterForm: FormGroup;

  constructor(
    private inventarioService: InventarioService,
    private fb: FormBuilder,
    private router: Router,
    private cd: ChangeDetectorRef
  ) {
    this.filterForm = this.fb.group({
      id: [null],
      fecha: [null],
      responsable: ['']
    });
  }

  ngOnInit(): void {
    
  }

  ngAfterViewInit(): void {
    setTimeout(() => {
      this.loadInventarios();
    });
  }

  loadInventarios(): void {
    setTimeout(() => {
    this.inventarioService.getAll().subscribe(
      (response) => {
        this.inventarios = response.data;
        this.cd.detectChanges();
      },
      (error) => {
        console.error('Error al cargar inventarios:', error);
      }
    );
  });
  }

  buscar(): void {
    const filtro = this.filterForm.value;
    setTimeout(() => {
      this.inventarioService.getByFilter(filtro).subscribe(
        (response) => {
          this.inventarios = response.data;
          this.cd.detectChanges();
        },
        (error) => {
          console.error('Error al buscar inventarios:', error);
        }
      );
    });
  }

  abrir(id: number): void {
    this.router.navigate(['/inventarios/editar', id]);
  }

  agregarInventario(): void {
    this.router.navigate(['/inventarios/agregar']);
  }
}
