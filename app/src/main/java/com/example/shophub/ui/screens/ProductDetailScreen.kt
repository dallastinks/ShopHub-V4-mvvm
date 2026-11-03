package com.example.shophub.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
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

@Composable
fun ProductDetailScreen(
    innerPadding: PaddingValues,
    factory: ProductViewModelFactory,
    productId: Int,
    onEdit: () -> Unit,
    onBackToList: () -> Unit
) {
    val viewModel: ProductViewModel = viewModel(factory = factory)
    val uiState by viewModel.uiState.collectAsState()

    // Recebe só o ID pela navegação; o ViewModel busca o produto no banco
    LaunchedEffect(productId) {
        viewModel.carregarProduto(productId)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(innerPadding)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        if (uiState.carregando) {
            Text("Carregando...")
            return
        }

        val product = uiState.produtoSelecionado
        if (product == null) {
            Text(
                text = "Produto não encontrado.",
                style = MaterialTheme.typography.headlineSmall
            )
            Button(onClick = onBackToList) {
                Text("Voltar para produtos")
            }
            return
        }

        Text(
            text = "Detalhes do produto",
            style = MaterialTheme.typography.headlineSmall
        )

        Card(modifier = Modifier.fillMaxWidth()) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = product.name,
                    style = MaterialTheme.typography.titleLarge
                )
                Text(text = "Preço: R$ ${product.price}")
                Text(text = "Categoria: ${uiState.nomeDaCategoria(product.categoryId)}")
                Text(text = "Descrição: ${product.description}")
                Text(
                    text = if (product.isFavorite) "Marcado como favorito" else "Ainda não favoritado"
                )
            }
        }

        Button(
            onClick = onEdit,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Editar produto")
        }

        OutlinedButton(
            onClick = onBackToList,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Voltar para a lista")
        }
    }
}
