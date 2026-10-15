package com.example.contactoproveedores

import com.example.contactoproveedores.domain.model.FiltroEstado
import com.example.contactoproveedores.domain.model.Proveedor
import com.example.contactoproveedores.viewmodel.ProveedoresState
import org.junit.Assert.*
import org.junit.Test

class PaginacionStateTest {
    private val registros = (1L..11L).map { Proveedor(id = it, empresa = "Empresa $it", activo = it <= 8) }

    @Test fun divideOnceRegistrosEnTresPaginasSinRepetir() {
        val estado = ProveedoresState(proveedores = registros)
        assertEquals(3, estado.totalPaginas)
        assertEquals(1L, estado.copy(proveedores = registros.reversed()).proveedoresPagina.first().id)
        assertEquals(listOf(1L, 2L, 3L, 4L, 5L), estado.proveedoresPagina.map { it.id })
        assertEquals(listOf(6L, 7L, 8L, 9L, 10L), estado.copy(pagina = 1).proveedoresPagina.map { it.id })
        assertEquals(listOf(11L), estado.copy(pagina = 2).proveedoresPagina.map { it.id })
    }
    @Test fun filtraAntesDePaginarYAjustaPaginaFueraDeRango() {
        val estado = ProveedoresState(proveedores = registros, pagina = 2, filtro = FiltroEstado.INACTIVOS)
        assertEquals(0, estado.paginaActual)
        assertEquals(listOf(9L, 10L, 11L), estado.proveedoresPagina.map { it.id })
        assertEquals(listOf(11L), estado.copy(busqueda = "Empresa 11").proveedoresPagina.map { it.id })
    }
    @Test fun sinResultadosNoProducePaginasNegativas() {
        val estado = ProveedoresState(proveedores = registros, busqueda = "No existe", pagina = 2)
        assertTrue(estado.proveedoresPagina.isEmpty())
        assertEquals(0, estado.paginaActual)
        assertEquals(1, estado.totalPaginas)
    }
}
