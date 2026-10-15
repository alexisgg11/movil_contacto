package com.example.contactoproveedores.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.contactoproveedores.domain.model.*
import com.example.contactoproveedores.domain.repository.ErrorProveedor
import com.example.contactoproveedores.domain.repository.ProveedorRepository
import com.example.contactoproveedores.domain.validation.NormalizarEmpresa
import com.example.contactoproveedores.domain.validation.ValidarProveedor
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ProveedoresViewModel(private val repository: ProveedorRepository) : ViewModel() {
    private val mutable = MutableStateFlow(ProveedoresState())
    val state = mutable.asStateFlow()

    init { cargar() }

    fun nuevo() { if (!state.value.ocupado) mutable.update {
        it.copy(formulario = Proveedor(), formularioAbierto = true, errores = emptyMap())
    } }
    fun abrirEdicion(p: Proveedor) { if (!state.value.ocupado) mutable.update {
        it.copy(formulario = p, formularioAbierto = true, errores = emptyMap())
    } }
    fun cerrarFormulario() { if (!state.value.ocupado) mutable.update {
        it.copy(formularioAbierto = false, errores = emptyMap())
    } }
    fun editar(campo: Campo, texto: String) { mutable.update {
        it.copy(formulario = it.formulario.actualizar(campo, texto), errores = it.errores - campo)
    } }
    fun buscar(texto: String) { mutable.update { it.copy(busqueda = texto) } }
    fun filtrar(filtro: FiltroEstado) { mutable.update { it.copy(filtro = filtro) } }
    fun cerrarModal() { mutable.update { it.copy(modal = null) } }
    fun confirmarEstado(p: Proveedor?) { if (!state.value.ocupado) mutable.update { it.copy(cambioEstado = p) } }

    fun buscarConfirmado() {
        val actual = state.value
        if (actual.ocupado) return
        val mensaje = when {
            !actual.cargado -> MensajeModal("Directorio no disponible", "Conecta el servidor y actualiza el panel.")
            actual.busqueda.isBlank() -> MensajeModal("Escribe qué buscas", "Busca por empresa, contacto, teléfono, correo, dirección o rubro.")
            actual.resultados.isEmpty() -> MensajeModal("Proveedor no encontrado", "No hay coincidencias. Revisa el texto y el filtro de estado.")
            else -> null
        }
        mutable.update { it.copy(modal = mensaje) }
    }
    fun cargar() = operar {
        val lista = repository.listar()
        mutable.update { it.copy(proveedores = lista, cargado = true) }
    }
    fun guardar() {
        if (!state.value.cargado || state.value.ocupado) return
        val p = state.value.formulario.normalizado()
        val errores = ValidarProveedor.errores(p)
        if (errores.isNotEmpty()) {
            mutable.update { it.copy(errores = errores, modal = MensajeModal("Revisa el formulario",
                errores.entries.joinToString("\n") { (campo, mensaje) -> "${campo.etiqueta}: $mensaje" })) }
            return
        }
        if (state.value.proveedores.any { it.id != p.id &&
                NormalizarEmpresa.clave(it.empresa) == NormalizarEmpresa.clave(p.empresa) }) {
            mutable.update { it.copy(errores = mapOf(Campo.EMPRESA to "Esta empresa ya está registrada"),
                modal = MensajeModal("Proveedor duplicado", "La empresa ya existe. También se comprueban los proveedores inactivos.")) }
            return
        }
        operar {
            val guardado = if (p.id == 0L) repository.agregar(p) else repository.actualizar(p)
            mutable.update { it.copy(
                proveedores = if (p.id == 0L) listOf(guardado) + it.proveedores
                    else it.proveedores.map { actual -> if (actual.id == guardado.id) guardado else actual },
                formulario = Proveedor(), formularioAbierto = false, errores = emptyMap(),
                aviso = if (p.id == 0L) "Proveedor registrado" else "Proveedor actualizado",
                modal = MensajeModal(if (p.id == 0L) "Registro exitoso" else "Cambios guardados",
                    "${guardado.empresa} se guardó correctamente.")) }
        }
    }
    fun cambiarEstado() {
        val p = state.value.cambioEstado ?: return
        operar {
            val actualizado = repository.cambiarEstado(p.id, !p.activo)
            mutable.update { it.copy(
                proveedores = it.proveedores.map { actual -> if (actual.id == p.id) actualizado else actual },
                cambioEstado = null, aviso = if (actualizado.activo) "Proveedor reactivado" else "Proveedor desactivado",
                modal = MensajeModal("Estado actualizado",
                    if (actualizado.activo) "El proveedor vuelve a estar activo." else "El proveedor está inactivo. Sus datos se conservan.")) }
        }
    }
    private fun operar(accion: suspend () -> Unit) {
        if (state.value.ocupado) return
        mutable.update { it.copy(ocupado = true, error = null, aviso = null, modal = null) }
        viewModelScope.launch {
            try { accion() }
            catch (e: CancellationException) { throw e }
            catch (e: Exception) {
                val mensaje = if (e is ErrorProveedor) e.message ?: "No se pudo completar la operación."
                    else "No se pudo conectar. Comprueba que el servidor esté encendido y vuelve a intentar."
                mutable.update { it.copy(error = mensaje, cambioEstado = null,
                    modal = MensajeModal(if (e is ErrorProveedor && e.codigo == 409)
                        "Proveedor duplicado" else "No se pudo completar", mensaje)) }
            } finally { mutable.update { it.copy(ocupado = false) } }
        }
    }
}
