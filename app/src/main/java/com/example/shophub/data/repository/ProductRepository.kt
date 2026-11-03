package com.example.shophub.data.repository

import com.example.shophub.data.Product
import com.example.shophub.data.ProductDao
import kotlinx.coroutines.flow.Flow

// Única porta de entrada para os dados de produto.
class ProductRepository(private val productDao: ProductDao) {

    fun buscarTodos(): Flow<List<Product>> = productDao.buscarTodos()

    suspend fun buscarPorId(id: Int): Product? = productDao.buscarPorId(id)

    suspend fun inserir(product: Product) = productDao.inserir(product)

    suspend fun atualizar(product: Product) = productDao.atualizar(product)

    suspend fun deletar(product: Product) = productDao.deletar(product)
}
