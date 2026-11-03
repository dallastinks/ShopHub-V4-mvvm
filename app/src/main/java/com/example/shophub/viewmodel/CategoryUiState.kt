package com.example.shophub.viewmodel

import com.example.shophub.data.Category
import com.example.shophub.data.Product

// Tudo que as telas de categoria precisam para se desenhar, num lugar só.
data class CategoryUiState(
    // lista (atualizada automaticamente pelo Flow do Room)
    val categorias: List<Category> = emptyList(),
    val produtos: List<Product> = emptyList(),

    // detalhes / edição
    val categoriaSelecionada: Category? = null,
    val editandoId: Int? = null,
    val carregando: Boolean = true,

    // campos do formulário
    val nome: String = "",
    val descricao: String = "",

    val erro: String = ""
) {
    fun quantidadeDeProdutos(categoryId: Int): Int =
        produtos.count { it.categoryId == categoryId }

    fun produtosRelacionados(categoryId: Int): List<Product> =
        produtos.filter { it.categoryId == categoryId }
}
