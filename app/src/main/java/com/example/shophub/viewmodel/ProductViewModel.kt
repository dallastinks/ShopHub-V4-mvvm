package com.example.shophub.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.shophub.data.Product
import com.example.shophub.data.repository.CategoryRepository
import com.example.shophub.data.repository.ProductRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ProductViewModel(
    private val productRepository: ProductRepository,
    private val categoryRepository: CategoryRepository
) : ViewModel() {

    // privado: só o ViewModel altera | público: a tela só observa
    private val _uiState = MutableStateFlow(ProductUiState())
    val uiState: StateFlow<ProductUiState> = _uiState.asStateFlow()

    init {
        // Os Flows do Room avisam sozinhos a cada inserir/atualizar/deletar
        viewModelScope.launch {
            try {
                productRepository.buscarTodos().collect { lista ->
                    _uiState.value = _uiState.value.copy(produtos = lista)
                }
            } catch (e: Exception) {
                Log.e("ProductViewModel", "Erro ao observar produtos", e)
            }
        }
        viewModelScope.launch {
            try {
                categoryRepository.buscarTodos().collect { lista ->
                    _uiState.value = _uiState.value.copy(categorias = lista)
                }
            } catch (e: Exception) {
                Log.e("ProductViewModel", "Erro ao observar categorias", e)
            }
        }
    }

    // ---- campos do formulário ----
    fun alterarNome(valor: String) {
        _uiState.value = _uiState.value.copy(nome = valor)
    }

    fun alterarPreco(valor: String) {
        _uiState.value = _uiState.value.copy(preco = valor)
    }

    fun alterarCategoriaId(valor: String) {
        _uiState.value = _uiState.value.copy(categoriaIdTexto = valor)
    }

    fun alterarDescricao(valor: String) {
        _uiState.value = _uiState.value.copy(descricao = valor)
    }

    // ---- ações da lista ----
    fun alternarFavorito(produto: Product) {
        viewModelScope.launch {
            try {
                productRepository.atualizar(produto.copy(isFavorite = !produto.isFavorite))
            } catch (e: Exception) {
                Log.e("ProductViewModel", "Erro ao favoritar produto", e)
            }
        }
    }

    fun removerProduto(produto: Product) {
        viewModelScope.launch {
            try {
                productRepository.deletar(produto)
            } catch (e: Exception) {
                Log.e("ProductViewModel", "Erro ao remover produto", e)
            }
        }
    }

    // ---- detalhes / edição: busca o produto pelo id direto do banco ----
    fun carregarProduto(id: Int) {
        _uiState.value = _uiState.value.copy(editandoId = id, carregando = true)
        viewModelScope.launch {
            try {
                val produto = productRepository.buscarPorId(id)
                _uiState.value = _uiState.value.copy(
                    produtoSelecionado = produto,
                    nome = produto?.name ?: "",
                    preco = produto?.price ?: "",
                    categoriaIdTexto = produto?.categoryId?.toString() ?: "",
                    descricao = produto?.description ?: "",
                    carregando = false
                )
            } catch (e: Exception) {
                Log.e("ProductViewModel", "Erro ao buscar produto $id", e)
                _uiState.value = _uiState.value.copy(carregando = false)
            }
        }
    }

    // ---- salvar (inserir ou atualizar) ----
    fun salvarProduto(onSucesso: () -> Unit) {
        val estado = _uiState.value
        val categoryId = estado.categoriaIdTexto.toIntOrNull()

        if (estado.nome.isBlank() || estado.preco.isBlank() ||
            estado.descricao.isBlank() || categoryId == null
        ) {
            _uiState.value = estado.copy(erro = "Preencha todos os campos corretamente.")
            return
        }
        if (estado.categorias.none { it.id == categoryId }) {
            _uiState.value = estado.copy(erro = "Informe um ID de categoria válido.")
            return
        }
        if (estado.editandoId != null && estado.produtoSelecionado == null) {
            _uiState.value = estado.copy(erro = "Produto não encontrado.")
            return
        }

        _uiState.value = estado.copy(erro = "")
        viewModelScope.launch {
            try {
                val original = estado.produtoSelecionado
                if (original == null) {
                    // id = 0 -> o Room gera o id
                    productRepository.inserir(
                        Product(
                            name = estado.nome,
                            price = estado.preco,
                            categoryId = categoryId,
                            description = estado.descricao
                        )
                    )
                } else {
                    // mesmo id -> atualiza a linha existente
                    productRepository.atualizar(
                        original.copy(
                            name = estado.nome,
                            price = estado.preco,
                            categoryId = categoryId,
                            description = estado.descricao
                        )
                    )
                }
                onSucesso()
            } catch (e: Exception) {
                Log.e("ProductViewModel", "Erro ao salvar produto", e)
                _uiState.value = _uiState.value.copy(erro = "Erro ao salvar o produto.")
            }
        }
    }
}
