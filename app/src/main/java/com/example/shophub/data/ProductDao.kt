package com.example.shophub.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface ProductDao {

    @Insert
    suspend fun inserir(product: Product)

    // Flow: o Room avisa sozinho sempre que a tabela muda (não precisa ser suspend)
    @Query("SELECT * FROM produtos ORDER BY id")
    fun buscarTodos(): Flow<List<Product>>

    @Query("SELECT * FROM produtos WHERE id = :id")
    suspend fun buscarPorId(id: Int): Product?

    @Update
    suspend fun atualizar(product: Product)

    @Delete
    suspend fun deletar(product: Product)
}
