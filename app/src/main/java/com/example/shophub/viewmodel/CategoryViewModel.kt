package com.example.shophub.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.shophub.data.Category
import com.example.shophub.data.repository.CategoryRepository
import com.example.shophub.data.repository.ProductRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class CategoryViewModel(
    private val categoryRepository: CategoryRepository,
    private val productRepository: ProductRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(CategoryUiState())
    val uiState: StateFlow<CategoryUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            try {
                categoryRepository.buscarTodos().collect { lista ->
                    _uiState.value = _uiState.value.copy(categorias = lista)
                }
            } catch (e: Exception) {
                Log.e("CategoryViewModel", "Erro ao observar categorias", e)
            }
        }
        viewModelScope.launch {
            try {
                productRepository.buscarTodos().collect { lista ->
                    _uiState.value = _uiState.value.copy(produtos = lista)
                }
            } catch (e: Exception) {
                Log.e("CategoryViewModel", "Erro ao observar produtos", e)
            }
        }
    }

    // ---- campos do formulário ----
    fun alterarNome(valor: String) {
        _uiState.value = _uiState.value.copy(nome = valor)
    }

    fun alterarDescricao(valor: String) {
        _uiState.value = _uiState.value.copy(descricao = valor)
    }

    // ---- ações da lista ----
    fun alternarDestaque(categoria: Category) {
        viewModelScope.launch {
            try {
                categoryRepository.atualizar(categoria.copy(featured = !categoria.featured))
            } catch (e: Exception) {
                Log.e("CategoryViewModel", "Erro ao atualizar destaque", e)
            }
        }
    }

    fun removerCategoria(categoria: Category) {
        viewModelScope.launch {
            try {
                categoryRepository.deletar(categoria)
            } catch (e: Exception) {
                Log.e("CategoryViewModel", "Erro ao remover categoria", e)
            }
        }
    }

    // ---- detalhes / edição: busca a categoria pelo id direto do banco ----
    fun carregarCategoria(id: Int) {
        _uiState.value = _uiState.value.copy(editandoId = id, carregando = true)
        viewModelScope.launch {
            try {
                val categoria = categoryRepository.buscarPorId(id)
                _uiState.value = _uiState.value.copy(
                    categoriaSelecionada = categoria,
                    nome = categoria?.name ?: "",
                    descricao = categoria?.description ?: "",
                    carregando = false
                )
            } catch (e: Exception) {
                Log.e("CategoryViewModel", "Erro ao buscar categoria $id", e)
                _uiState.value = _uiState.value.copy(carregando = false)
            }
        }
    }

    // ---- salvar (inserir ou atualizar) ----
    fun salvarCategoria(onSucesso: () -> Unit) {
        val estado = _uiState.value

        if (estado.nome.isBlank() || estado.descricao.isBlank()) {
            _uiState.value = estado.copy(erro = "Preencha todos os campos.")
            return
        }
        if (estado.editandoId != null && estado.categoriaSelecionada == null) {
            _uiState.value = estado.copy(erro = "Categoria não encontrada.")
            return
        }

        _uiState.value = estado.copy(erro = "")
        viewModelScope.launch {
            try {
                val original = estado.categoriaSelecionada
                if (original == null) {
                    categoryRepository.inserir(
                        Category(name = estado.nome, description = estado.descricao)
                    )
                } else {
                    categoryRepository.atualizar(
                        original.copy(name = estado.nome, description = estado.descricao)
                    )
                }
                onSucesso()
            } catch (e: Exception) {
                Log.e("CategoryViewModel", "Erro ao salvar categoria", e)
                _uiState.value = _uiState.value.copy(erro = "Erro ao salvar a categoria.")
            }
        }
    }
}
