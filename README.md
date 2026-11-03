# ShopHub — Trabalho 4 (Arquitetura MVVM)

App Android (Kotlin + Jetpack Compose) para gerenciar **produtos** e **categorias**.
Evolução do Trabalho 3: o app faz **exatamente o mesmo** (adicionar, listar, editar, remover, persistir com Room),
mas agora o código está organizado em **MVVM** (Model — View — ViewModel) com Repository, StateFlow/UiState e ViewModelFactory.

**Integrantes do trio:** _(preencher nomes)_
**Slides (opcional):** _(link do Google Slides)_

## Como rodar

1. Android Studio recente (compatível com AGP 9.x).
2. `File > Open` na pasta do projeto e aguardar o **Gradle Sync**.
3. Emulador (API 24+) ou celular com depuração USB → **Run ▶** (módulo `app`).
4. Para zerar o banco: `Settings > Apps > ShopHub > Storage > Clear data`.

---

## Arquitetura

```
 View (ui)  ──►  ViewModel  ──►  Repository  ──►  DAO / Room (data)
 telas Compose   estado (UiState)  única fonte        @Entity, @Dao,
 só desenham e   + ações           de dados           @Database
 chamam métodos
        ◄── StateFlow<UiState> ◄── Flow<List<...>> do Room ◄──
```

| Pacote | O que tem | Camada |
|---|---|---|
| `ui` (`ui.screens`, `ui.components`, `ui.theme`) | Telas `@Composable`, TopBar, BottomBar, tema. **Sem** DAO, CoroutineScope ou Dispatchers | View |
| `data` | `Category`, `Product` (`@Entity`), `CategoryDao`, `ProductDao` (`@Dao`), `AppDatabase` (`@Database`, Singleton) | Model |
| `data.repository` | `CategoryRepository`, `ProductRepository` | Repository |
| `viewmodel` | `ProductViewModel`, `CategoryViewModel`, `ProductUiState`, `CategoryUiState`, `ProductViewModelFactory`, `CategoryViewModelFactory` | ViewModel |
| `navigation` | `AppRoutes`, `AppNavHost` (monta Database → Repository → Factory e entrega às telas) | — |

### Checklist do enunciado

| Requisito | Onde |
|---|---|
| 4 pacotes (ui, data, data.repository, viewmodel) | tabela acima |
| 1 Repository por Entity; só ele fala com o DAO | `data/repository/*` |
| `buscarTodos()` retornando `Flow<List<...>>` (sem `suspend`) | `CategoryDao`, `ProductDao` |
| 2 ViewModel, cada um com seu UiState | `ProductViewModel`/`ProductUiState`, `CategoryViewModel`/`CategoryUiState` |
| `_uiState` privado + `uiState` público (`StateFlow`) | início dos dois ViewModels |
| ViewModelFactory injetando o Repository | `ProductViewModelFactory`, `CategoryViewModelFactory` |
| Telas só leem `uiState` e chamam métodos do ViewModel | `ui/screens/*` (`collectAsState()` + `viewModel(factory = ...)`) |

---

# Documentação do processo e das decisões

## 1. Como estava o projeto no Trabalho 3 e o que mudou?

No Trabalho 3, cada tela pegava o `AppDatabase`, chamava o DAO diretamente, abria `CoroutineScope(Dispatchers.IO).launch`,
guardava o estado em várias variáveis `remember` e precisava **buscar a lista de novo** depois de cada inserir/remover/atualizar.

No Trabalho 4 nenhuma tela, campo ou funcionalidade nova foi criada. O que mudou:
- `buscarTodos()` dos DAOs virou `Flow` → a lista se atualiza sozinha quando a tabela muda.
- Foram criados 2 Repositories, 2 ViewModels (com UiState) e 2 Factories.
- As telas perderam toda a lógica de banco e de validação: só observam o `uiState` e chamam métodos do ViewModel.
- As validações dos formulários (campos vazios, categoria inexistente) foram para dentro do ViewModel.

## 2. Como dividimos os pacotes

- **`ui`**: tudo que desenha na tela (telas, componentes, tema).
- **`data`**: o que é do Room — as Entities (movidas da antiga pasta `model`), os DAOs e o `AppDatabase`, sem mudanças de comportamento.
- **`data.repository`**: um Repository por Entity.
- **`viewmodel`**: ViewModels, UiStates e Factories.
- Mantivemos também `navigation` (rotas e NavHost) por já existir desde o Trabalho 2.

## 3. Por que o Repository existe?

Antes, a tela conhecia o DAO, ou seja, sabia que os dados vinham do Room. Agora só o Repository conhece o DAO.
Se um dia os dados vierem de uma API, só o Repository muda; ViewModel e telas continuam iguais.
Hoje ele simplesmente repassa as chamadas para o DAO.

## 4. O que tem em cada UiState e por quê

**`ProductUiState`**: `produtos` e `categorias` (listas vindas dos Flows), `produtoSelecionado` (detalhes/edição),
`editandoId`, `carregando`, os 4 campos do formulário (`nome`, `preco`, `categoriaIdTexto`, `descricao`) e `erro`.
Também tem `nomeDaCategoria(id)` para as telas mostrarem o nome da categoria.

**`CategoryUiState`**: `categorias`, `produtos` (para contar/listar os produtos de cada categoria), `categoriaSelecionada`,
`editandoId`, `carregando`, os campos `nome` e `descricao` e `erro`. Tem `quantidadeDeProdutos(id)` e `produtosRelacionados(id)`.

Escolhemos esses campos porque são exatamente as variáveis `remember` que as telas tinham no Trabalho 3, agora agrupadas em um objeto só.

## 5. Alguma funcionalidade quebrou? Como corrigimos?

- Tela de detalhes mostrando "não encontrado" por um instante antes de carregar → resolvido com o campo `carregando` no UiState.
- Lista não atualizando depois de salvar → era o Flow; confirmar que o `buscarTodos()` está sem `suspend` e retorna `Flow`.
- Erro "Cannot create an instance of ViewModel" → faltava passar a Factory em `viewModel(factory = ...)`.
