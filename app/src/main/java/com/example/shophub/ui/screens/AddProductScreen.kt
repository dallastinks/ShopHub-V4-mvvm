package com.example.shophub.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.shophub.viewmodel.ProductViewModel
import com.example.shophub.viewmodel.ProductViewModelFactory

/**
 * Formulário de produto.
 *  - productId == null -> adicionar
 *  - productId != null -> editar (o ViewModel busca o produto pelo id)
 */
@Composable
fun AddProductScreen(
    innerPadding: PaddingValues,
    factory: ProductViewModelFactory,
    onSave: () -> Unit,
    productId: Int? = null
) {
    val viewModel: ProductViewModel = viewModel(factory = factory)
    val uiState by viewModel.uiState.collectAsState()
    val isEditing = productId != null

    LaunchedEffect(productId) {
        if (productId != null) {
            viewModel.carregarProduto(productId)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(innerPadding)
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            text = if (isEditing) "Editar produto" else "Adicionar produto",
            style = MaterialTheme.typography.headlineSmall
        )

        Card(modifier = Modifier.fillMaxWidth()) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text("Categorias disponíveis:")
                uiState.categorias.forEach { category ->
                    Text("${category.id} - ${category.name}")
                }
            }
        }

        OutlinedTextField(
            value = uiState.nome,
            onValueChange = { viewModel.alterarNome(it) },
            label = { Text("Nome do produto") },
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = uiState.preco,
            onValueChange = { viewModel.alterarPreco(it) },
            label = { Text("Preço") },
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = uiState.categoriaIdTexto,
            onValueChange = { viewModel.alterarCategoriaId(it) },
            label = { Text("ID da categoria") },
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = uiState.descricao,
            onValueChange = { viewModel.alterarDescricao(it) },
            label = { Text("Descrição") },
            modifier = Modifier.fillMaxWidth()
        )

        if (uiState.erro.isNotBlank()) {
            Text(
                text = uiState.erro,
                color = MaterialTheme.colorScheme.error
            )
        }

        Button(
            onClick = { viewModel.salvarProduto(onSucesso = onSave) },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(if (isEditing) "Salvar alterações" else "Salvar produto")
        }
    }
}
