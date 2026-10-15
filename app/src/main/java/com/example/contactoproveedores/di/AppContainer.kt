package com.example.contactoproveedores.di

import com.example.contactoproveedores.BuildConfig
import com.example.contactoproveedores.data.remote.ClienteHttp
import com.example.contactoproveedores.data.repository.ApiProveedorRepository
import com.example.contactoproveedores.domain.repository.ProveedorRepository

object AppContainer {
    val proveedores: ProveedorRepository by lazy {
        ApiProveedorRepository(ClienteHttp(BuildConfig.API_BASE_URL))
    }
}
