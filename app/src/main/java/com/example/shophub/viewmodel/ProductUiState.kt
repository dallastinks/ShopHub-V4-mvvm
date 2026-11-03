package com.example.shophub.viewmodel

import com.example.shophub.data.Category
import com.example.shophub.data.Product

// Tudo que as telas de produto precisam para se desenhar, num lugar só.
data class ProductUiState(
    // lista (atualizada automaticamente pelo Flow do Room)
    val produtos: List<Product> = emptyList(),
    val categorias: List<Category> = emptyList(),

    // detalhes / edição
    val produtoSelecionado: Product? = null,
    val editandoId: Int? = null,
    val carregando: Boolean = true,

    // campos do formulário
    val nome: String = "",
    val preco: String = "",
    val categoriaIdTexto: String = "",
    val descricao: String = "",

    val erro: String = ""
) {
    fun nomeDaCategoria(categoryId: Int): String =
        categorias.find { it.id == categoryId }?.name ?: "Sem categoria"
}
