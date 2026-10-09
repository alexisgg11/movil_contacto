package com.example.contactoproveedores.ui
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.contactoproveedores.BuildConfig
import com.example.contactoproveedores.data.ApiProveedorRepository
import com.example.contactoproveedores.ui.screens.ProveedoresScreen
import com.example.contactoproveedores.ui.theme.ContactoProveedoresTheme
import com.example.contactoproveedores.viewmodel.ProveedoresViewModel
import com.example.contactoproveedores.ui.components.PermisoRedLocal

@Composable
fun ProveedoresApp() {
    ContactoProveedoresTheme { PermisoRedLocal { ProveedoresRoute() } }
}

@Composable
private fun ProveedoresRoute() {
    val model: ProveedoresViewModel = viewModel(factory = viewModelFactory {
        initializer { ProveedoresViewModel(ApiProveedorRepository(BuildConfig.API_BASE_URL)) }
    })
    val state by model.state.collectAsStateWithLifecycle()
    ProveedoresScreen(state, model::editar, model::buscar, model::agregar,
        model::confirmar, model::eliminar, model::cargar)
}
