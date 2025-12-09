# 📱 Catálogo Smart

![Kotlin](https://img.shields.io/badge/Kotlin-1.9.0-purple?style=flat&logo=kotlin)
![Jetpack Compose](https://img.shields.io/badge/Jetpack%20Compose-M3-green?style=flat&logo=android)
![Android Studio](https://img.shields.io/badge/Android%20Studio-Flamingo-orange?style=flat&logo=android-studio)

> **Aplicativo de Catálogo de Produtos** desenvolvido como projeto acadêmico sob orientação do Professor **Hélio Fernando Bentzen Pessoa Filho**.

---

## 👥 Integrantes da Equipe

| Nome | Função | E-mail |
| :--- | :--- | :--- |
| **Flavio Wallefy da Silva Oliveira** | UI/UX, Componentes Visuais e Jetpack Compose | fwso@discente.ifpe.edu.br |
| **Lucas Rafael da Silva** | Banco de Dados (Room) e Persistência Offline | lrs15@discente.ifpe.edu.br |
| **Marcos Vinicius Vitor de Moraes** | API, Networking, DTOs, Repository | mvvm@discente.ifpe.edu.br |
| **Nielson Florêncio da Silva Junior** | MVVM, Navegação, Lógica da App e Estados | nfsj1@discente.ifpe.edu.br |

---

## 📝 Descrição do Problema

Catálogos físicos ou aplicativos que dependem exclusivamente de internet dificultam o acesso rápido a informações de produtos. As principais limitações encontradas atualmente são:

* ❌ Atualizações lentas de catálogo.
* ❌ Falta de acessibilidade e usabilidade.
* ❌ Necessidade constante de conexão com a internet.
* ❌ Dificuldade para organizar categorias e encontrar produtos.

## 💡 Solução Proposta

O **Catálogo Smart** é um aplicativo Android moderno que resolve esses problemas através de uma arquitetura robusta e foco na experiência do usuário.

* ✔ **Integração API:** Lista produtos da *FakeStoreAPI*.
* ✔ **Detalhamento:** Permite visualizar detalhes ricos de cada item.
* ✔ **Organização:** Produtos separados por categorias.
* ✔ **Favoritos:** Armazenamento local de itens preferidos.
* ✔ **Modo Offline:** Funciona sem internet utilizando **Room Database**.
* ✔ **UX Moderna:** Interface fluida com **Jetpack Compose + Material Design 3**.
* ✔ **Feedback Visual:** Shimmer loading e telas de boas-vindas.

---

## 🛠️ Tecnologias e Arquitetura

O projeto foi desenvolvido seguindo os princípios de **Clean Architecture** e **MVVM**, garantindo um código desacoplado, testável e escalável.

### 🎨 Frontend / UI
* **Jetpack Compose:** UI declarativa moderna.
* **Material Design 3:** Padrões de design mais recentes do Google.
* **Compose Navigation:** Gerenciamento de rotas e navegação.
* **Coil:** Carregamento assíncrono de imagens.
* **Shimmer Effect:** Feedback visual durante carregamento de dados.

### 💾 Backend / Dados
* **Retrofit + Gson:** Consumo de API REST.
* **OkHttp:** Interceptores para monitoramento e controle de requisições.
* **Room Database:** Persistência de dados local (Cache/Offline).
* **Coroutines + StateFlow:** Gerenciamento de threads e estados reativos.

### 🏗️ Padrões Adotados
* **MVVM (Model–View–ViewModel)**
* **Repository Pattern**
* **RemoteResult:** Sealed class para gerenciamento de estados (Sucesso, Erro, Loading).

---

## 📂 Organização e Metodologia

Para garantir a organização e a qualidade do desenvolvimento em equipe, foram adotadas as seguintes práticas:

### 📁 Estrutura de Pastas
O projeto segue uma hierarquia clara de pacotes, separando responsabilidades:
* **`app/`**: Configurações gerais da aplicação.
* **`data/`**: Camada de dados (Implementação da API, Banco de Dados Room, DTOs e Mappers).
* **`domain/`**: Camada de domínio (Modelos de dados e Interfaces dos Repositórios).
* **`ui/`**: Camada de apresentação (Telas/Screens, ViewModels, Estados e Componentes Visuais).

### 🤝 Fluxo de Trabalho (Git Flow)
* **Branches por Feature:** O desenvolvimento foi segmentado utilizando branches específicas para cada nova funcionalidade (ex: `feat-api`, `feat-room`, `feat-ui-ux`).
* **Code Review:** A integração de código na branch principal (`main`) foi realizada através de **Pull Requests**, revisados pelos membros da equipe para garantir a qualidade e consistência do código.

---

## ⚙️ Instruções de Instalação e Execução

### 🔧 1. Pré-requisitos
Certifique-se de ter instalado:
* Android Studio (Versão Flamingo ou superior).
* JDK 11+.
* Emulador Android ou dispositivo físico configurado.

### ▶️ 2. Clonar o repositório

git clone [https://github.com/MoraesMarcos/catalogo_smart](https://github.com/MoraesMarcos/catalogo_smart)

### 📦 3. Abrir no Android Studio
1. Abra o **Android Studio**.
2. Clique em **Open an Existing Project**.
3. Selecione a pasta do projeto clonado.
4. Aguarde o Gradle finalizar a sincronização das dependências.

### 🚀 4. Executar o App
1. Conecte seu dispositivo via USB ou inicie um emulador (AVD).
2. Clique no botão **Run ▶️** (Shift + F10).
3. O app iniciará na tela de Boas-vindas.

---

## 📸 Prints do Aplicativo
<div style="display: flex; flex-direction: row; flex-wrap: wrap; justify-content: center; gap: 10px;">
  <img src="docs/boas_vindas.png" alt="Tela de Boas-vindas" width="200"/>
  <img src="docs/lista_produtos.png" alt="Lista de Produtos" width="200"/>
  <img src="docs/detalhes.png" alt="Detalhes do Produto" width="200"/>
  <img src="docs/favoritos.png" alt="Tela de Favoritos" width="200"/>
</div>

---

## 🎉 Conclusão
O **Catálogo Smart** atende 100% dos requisitos técnicos propostos, oferecendo uma experiência de navegação fluida e resiliente (Online/Offline). O código está limpo, modular e pronto para futuras expansões, como:

* 🔐 Login e Autenticação Real.
* 🛒 Carrinho de Compras.
* 💳 Integração com Pagamentos.
* 📊 Monitoramento de Estoque.
