package com.example.shophub.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
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
import com.example.shophub.viewmodel.CategoryViewModel
import com.example.shophub.viewmodel.CategoryViewModelFactory

/**
 * Formulário de categoria.
 *  - categoryId == null -> adicionar
 *  - categoryId != null -> editar (o ViewModel busca a categoria pelo id)
 */
@Composable
fun AddCategoryScreen(
    innerPadding: PaddingValues,
    factory: CategoryViewModelFactory,
    onSave: () -> Unit,
    categoryId: Int? = null
) {
    val viewModel: CategoryViewModel = viewModel(factory = factory)
    val uiState by viewModel.uiState.collectAsState()
    val isEditing = categoryId != null

    LaunchedEffect(categoryId) {
        if (categoryId != null) {
            viewModel.carregarCategoria(categoryId)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(innerPadding)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            text = if (isEditing) "Editar categoria" else "Adicionar categoria",
            style = MaterialTheme.typography.headlineSmall
        )

        OutlinedTextField(
            value = uiState.nome,
            onValueChange = { viewModel.alterarNome(it) },
            label = { Text("Nome da categoria") },
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = uiState.descricao,
            onValueChange = { viewModel.alterarDescricao(it) },
            label = { Text("Descrição da categoria") },
            modifier = Modifier.fillMaxWidth()
        )

        if (uiState.erro.isNotBlank()) {
            Text(
                text = uiState.erro,
                color = MaterialTheme.colorScheme.error
            )
        }

        Button(
            onClick = { viewModel.salvarCategoria(onSucesso = onSave) },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(if (isEditing) "Salvar alterações" else "Salvar categoria")
        }
    }
}
