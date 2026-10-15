package com.example.contactoproveedores.domain.repository

class ErrorProveedor(message: String, val codigo: Int? = null) : Exception(message)
