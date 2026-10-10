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

data class MensajeModal(val titulo: String, val detalle: String)

data class ProveedoresState(
    val formulario: Proveedor = Proveedor(), val proveedores: List<Proveedor> = emptyList(),
    val errores: Map<Campo, String> = emptyMap(), val busqueda: String = "",
    val ocupado: Boolean = false, val cargado: Boolean = false, val error: String? = null,
    val aviso: String? = null, val eliminar: Proveedor? = null, val modal: MensajeModal? = null
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
    fun cerrarModal() { mutable.update { it.copy(modal = null) } }
    fun buscarConfirmado() {
        val actual = state.value
        if (actual.ocupado) return
        val mensaje = when {
            !actual.cargado -> MensajeModal("Directorio no disponible", "Conecta con el servidor y actualiza el directorio antes de buscar.")
            actual.busqueda.isBlank() -> MensajeModal("Escribe qué buscas", "Ingresa empresa, contacto, teléfono, correo, dirección o rubro.")
            actual.resultados.isEmpty() -> MensajeModal("Proveedor no encontrado", "No hay coincidencias para «${actual.busqueda.trim()}». Revisa el texto o actualiza el directorio.")
            else -> null
        }
        mutable.update { it.copy(modal = mensaje) }
    }
    fun confirmar(proveedor: Proveedor?) { mutable.update { it.copy(eliminar = proveedor) } }
    fun cargar() = operar {
        val lista = repository.listar()
        mutable.update { it.copy(proveedores = lista, cargado = true) }
    }
    fun agregar() {
        if (!state.value.cargado || state.value.ocupado) return
        val p = state.value.formulario.normalizado()
        val errores = ValidarProveedor.errores(p)
        if (errores.isNotEmpty()) {
            mutable.update { it.copy(errores = errores, modal = MensajeModal("Revisa el formulario",
                errores.entries.joinToString("\n") { (campo, mensaje) -> "${campo.etiqueta}: $mensaje" })) }; return
        }
        val empresaNormalizada = p.empresa.trim().replace(Regex("\\s+"), " ")
        if (state.value.proveedores.any {
                it.empresa.trim().replace(Regex("\\s+"), " ").equals(empresaNormalizada, ignoreCase = true)
            }) {
            mutable.update { it.copy(errores = mapOf(Campo.EMPRESA to "Esta empresa ya está registrada"),
                modal = MensajeModal("Proveedor duplicado", "Ya existe un proveedor con esa empresa. Busca el registro en el directorio.")) }
            return
        }
        operar {
            val guardado = repository.agregar(p)
            mutable.update { it.copy(proveedores = listOf(guardado) + it.proveedores,
                formulario = Proveedor(), errores = emptyMap(), busqueda = "", aviso = "Proveedor guardado",
                modal = MensajeModal("Registro exitoso", "${guardado.empresa} se guardó correctamente.")) }
        }
    }
    fun eliminar() {
        val p = state.value.eliminar ?: return
        operar {
            repository.eliminar(p.id)
            mutable.update { it.copy(proveedores = it.proveedores.filterNot { item -> item.id == p.id },
                eliminar = null, aviso = "Proveedor eliminado",
                modal = MensajeModal("Eliminación exitosa", "${p.empresa} se eliminó del directorio.")) }
        }
    }
    private fun operar(accion: suspend () -> Unit) {
        if (state.value.ocupado) return
        mutable.update { it.copy(ocupado = true, error = null, aviso = null, modal = null) }
        viewModelScope.launch {
            try { accion() }
            catch (e: CancellationException) { throw e }
            catch (e: Exception) {
                val mensaje = if (e is ApiException) e.message ?: "No se pudo completar la operación."
                    else "No se pudo conectar. Comprueba que el servidor esté encendido y vuelve a intentar."
                mutable.update { it.copy(error = mensaje, eliminar = null,
                    modal = MensajeModal(if (mensaje.contains("Ya existe")) "Proveedor duplicado" else "No se pudo completar", mensaje)) }
            }
            finally { mutable.update { it.copy(ocupado = false) } }
        }
    }
}
