package com.example.shophub.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface CategoryDao {

    @Insert
    suspend fun inserir(category: Category)

    // Flow: o Room avisa sozinho sempre que a tabela muda (não precisa ser suspend)
    @Query("SELECT * FROM categorias ORDER BY id")
    fun buscarTodos(): Flow<List<Category>>

    @Query("SELECT * FROM categorias WHERE id = :id")
    suspend fun buscarPorId(id: Int): Category?

    @Update
    suspend fun atualizar(category: Category)

    @Delete
    suspend fun deletar(category: Category)
}
