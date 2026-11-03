package com.example.shophub.data.repository

import com.example.shophub.data.Category
import com.example.shophub.data.CategoryDao
import kotlinx.coroutines.flow.Flow

// Única porta de entrada para os dados de categoria.
// Só o Repository conversa com o DAO; só o ViewModel conversa com o Repository.
class CategoryRepository(private val categoryDao: CategoryDao) {

    fun buscarTodos(): Flow<List<Category>> = categoryDao.buscarTodos()

    suspend fun buscarPorId(id: Int): Category? = categoryDao.buscarPorId(id)

    suspend fun inserir(category: Category) = categoryDao.inserir(category)

    suspend fun atualizar(category: Category) = categoryDao.atualizar(category)

    suspend fun deletar(category: Category) = categoryDao.deletar(category)
}
