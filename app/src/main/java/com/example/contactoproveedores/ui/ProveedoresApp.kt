package com.example.contactoproveedores.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.contactoproveedores.di.AppContainer
import com.example.contactoproveedores.ui.components.PermisoRedLocal
import com.example.contactoproveedores.ui.screens.ProveedoresScreen
import com.example.contactoproveedores.ui.theme.ContactoProveedoresTheme
import com.example.contactoproveedores.viewmodel.*

@Composable
fun ProveedoresApp() {
    ContactoProveedoresTheme {
        PermisoRedLocal {
            val model: ProveedoresViewModel = viewModel(factory = viewModelFactory {
                initializer { ProveedoresViewModel(AppContainer.proveedores) }
            })
            val state by model.state.collectAsStateWithLifecycle()
            ProveedoresScreen(state, AccionesProveedores(
                model::nuevo, model::abrirEdicion, model::cerrarFormulario, model::editar, model::guardar,
                model::buscar, model::buscarConfirmado, model::filtrar, model::confirmarEstado,
                model::cambiarEstado, model::cargar, model::cerrarModal, model::cambiarPagina))
        }
    }
}
