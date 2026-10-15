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
    @Test fun combinaRubroEstadoYBusquedaAntesDePaginar() {
        val proveedores = registros.map { it.copy(rubro = if (it.id % 2L == 0L) "Tecnología" else "SERVICIOS") }
        val estado = ProveedoresState(proveedores = proveedores, filtroRubro = " Servicios ", pagina = 2)
        assertEquals(listOf(1L, 3L, 5L, 7L, 9L, 11L), estado.resultados.map { it.id })
        assertEquals(2, estado.totalPaginas)
        assertEquals(listOf(11L), estado.proveedoresPagina.map { it.id })
        val inactivos = estado.copy(filtro = FiltroEstado.INACTIVOS, busqueda = "Empresa 9")
        assertEquals(listOf(9L), inactivos.proveedoresPagina.map { it.id })
        assertEquals(0, inactivos.paginaActual)
        assertTrue(estado.copy(filtroRubro = "Salud").resultados.isEmpty())
        assertEquals(11, estado.copy(filtroRubro = null).resultados.size)
    }
}
