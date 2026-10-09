package com.example.contactoproveedores.viewmodel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.contactoproveedores.data.ApiException
import com.example.contactoproveedores.domain.*
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class ProveedoresState(
    val formulario: Proveedor = Proveedor(), val proveedores: List<Proveedor> = emptyList(),
    val errores: Map<Campo, String> = emptyMap(), val busqueda: String = "",
    val ocupado: Boolean = false, val cargado: Boolean = false, val error: String? = null,
    val aviso: String? = null, val eliminar: Proveedor? = null
) {
    val resultados get() = proveedores.filter { p ->
        listOf(p.empresa, p.contacto, p.telefono, p.correo, p.direccion, p.rubro)
            .any { it.contains(busqueda.trim(), ignoreCase = true) }
    }
}
class ProveedoresViewModel(private val repository: ProveedorRepository) : ViewModel() {
    private val mutable = MutableStateFlow(ProveedoresState())
    val state = mutable.asStateFlow()
    init { cargar() }
    fun editar(campo: Campo, texto: String) { mutable.update {
        it.copy(formulario = it.formulario.actualizar(campo, texto), errores = it.errores - campo, aviso = null)
    } }
    fun buscar(texto: String) { mutable.update { it.copy(busqueda = texto) } }
    fun confirmar(proveedor: Proveedor?) { mutable.update { it.copy(eliminar = proveedor) } }
    fun cargar() = operar {
        val lista = repository.listar()
        mutable.update { it.copy(proveedores = lista, cargado = true) }
    }
    fun agregar() {
        if (!state.value.cargado || state.value.ocupado) return
        val p = state.value.formulario.normalizado()
        val errores = ValidarProveedor.errores(p)
        if (errores.isNotEmpty()) { mutable.update { it.copy(errores = errores) }; return }
        operar {
            val guardado = repository.agregar(p)
            mutable.update { it.copy(proveedores = listOf(guardado) + it.proveedores,
                formulario = Proveedor(), errores = emptyMap(), busqueda = "", aviso = "Proveedor guardado") }
        }
    }
    fun eliminar() {
        val p = state.value.eliminar ?: return
        operar {
            repository.eliminar(p.id)
            mutable.update { it.copy(proveedores = it.proveedores.filterNot { item -> item.id == p.id },
                eliminar = null, aviso = "Proveedor eliminado") }
        }
    }
    private fun operar(accion: suspend () -> Unit) {
        if (state.value.ocupado) return
        mutable.update { it.copy(ocupado = true, error = null, aviso = null) }
        viewModelScope.launch {
            try { accion() }
            catch (e: CancellationException) { throw e }
            catch (e: Exception) { mutable.update { it.copy(error = if (e is ApiException) e.message
                else "No se pudo conectar. Comprueba que el servidor esté encendido y vuelve a intentar.") } }
            finally { mutable.update { it.copy(ocupado = false) } }
        }
    }
}
