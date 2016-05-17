# Anteros Android Examples  #

Esta aplicação foi construída para demonstrar a utilização dos frameworks Anteros e também soluções para resolver os principais problemas comuns a este tipo de aplicação usando Android. Vejamos as principais funcionalidades implementadas:

* Uso do Anteros Persistence Android para persistir objetos usando SqLite;
* Uso de login social Facebook, Google e Instagram usando Anteros Social Android;
* Tela exemplo com validação de login que já salva os dados nas preferências;
* Utilização de ToolBar e MenuItem na tela do menu principal. Implementação de menu responsivo;
* Apresentação da foto do perfil da rede social no menu usando CircularImageView;
* Tela de clientes com responsividade e busca de cep usando PostmonWebService do Anteros Android;
* Validação dos dados cliente usando Anteros Bean Validation;
* Tela para adicionar anexos no cliente podendo ser capturados da câmera ou selecionados dos arquivos;
* Redimensionamento da imagens capturadas da câmera;
* Manutenção do banco de dados usando classes do Anteros Persistence Android para exportar, importar, recriar e compartilhar o backup facilitando a manutenção remota;
* Visualização das tabelas do banco de dados e dos respectivos registros de cada tabela;
* Visualização dos Blob's das tabelas do banco de dados;
* Tela de consulta dos produtos usando checkbox na ListView para selecionar um ou mais itens;
* Tela de pedido usando páginas(tabs) para separação dos dados do pedido e dos itens;
* Uso de DatePickerDialog nos campos de data para seleção de data usando calendário;
* Tela de consulta de clientes e produtos usando DialogFragment;
* Uso de combobox com Enum's para edição nos cadastros;
* Uso de CurrencyEditText do Anteros para edição e apresentação de campos com valores monetários;
* Uso de AnterosArrayAdapterWithViewHolder na edição dos itens do pedido para permitir a edição dos campos dentro da ListView. Esta classe facilita o uso do padrão de projeto View Holder para Android;
* Recalculo do total do item e do pedido durante a digitação da quantidade no EditView dentro da lista;
* Uso de property da aplicação no manifesto para habilitar o uso de multidex: ```android:name="android.support.multidex.MultiDexApplication"``` e multiDexEnabled true no build.gradle da app;
* Adiciona metadata para uso do backup service no manifesto:  ```<meta-data android:name="br.com.anteros.persistence.DatabaseName" android:value="vendas.db" />```
* Adicionado BackupService e BackupReceiver para execução do backup do banco de dados via serviço inicializado após o boot do android;
* Uso de AsyncTask para download de várias imagens na mesma tarefa para adicionar produtos de demonstração;
* Tela para apresentação de violações(erros) gerados pelo validador do Anteros Bean Validation permitindo apresentar vários erros na mesma mensagem com possibilidade de navegação para visualização de cada erro;
* Adicionado logo na toolbar;
* Personalização do tema do android para a aplicação. styles.xml no resources/values;
* Adicionado icone para a aplicação nos resources/mipmap;
* Implementado tratamento de permissões para ficar compatível com Android 6 acima;
* Resolvido problema quando o usuário muda a orientação da tela. Usando ```android:configChanges="orientation"``` no manifesto.


#### Dependências do projeto ####
```gradle
    compile 'br.com.anteros:Anteros-Core:1.0.0'
    compile 'br.com.anteros:Anteros-Persistence-Core:1.0.3'
    compile 'br.com.anteros:Anteros-Bean-Validation:1.0.1'
    compile 'br.com.anteros:Anteros-Bean-Validation-Api:1.0.1'
    compile 'br.com.anteros:anteros-core-android:1.0.3'
    compile 'br.com.anteros:anteros-ui-controls-android:1.0.3'
    compile 'br.com.anteros:anteros-persistence-android:1.0.13'
    compile 'br.com.anteros.social:anteros-social-android-core:1.0.3@aar'
    compile 'br.com.anteros.social:anteros-social-android-facebook:1.0.+@aar'
    compile 'br.com.anteros.social:anteros-social-android-google:1.0.+@aar'
    compile 'br.com.anteros.social:anteros-social-android-instagram:1.0.+@aar'
    compile 'com.google.android.gms:play-services-plus:8.4.0'
    compile 'com.google.android.gms:play-services-auth:8.4.0'
    compile 'com.facebook.android:facebook-android-sdk:4.0.0'
    compile 'com.tonicartos:stickygridheaders:1.0.1'
    compile 'com.google.android.gms:play-services:8.4.0'
    compile 'com.google.android.gms:play-services-ads:8.4.0'
    compile 'com.google.android.gms:play-services-gcm:8.4.0'
    compile 'com.google.code.gson:gson:2.2.4'
    compile 'com.squareup.picasso:picasso:2.5.2'
    compile 'commons-io:commons-io:2.5'
```    


### Telas ###

Veja abaixo algumas telas da aplicação exemplo:

![alt text](https://raw.githubusercontent.com/anterostecnologia/anterosandroidexamples/master/AnterosVendas/app/images/screenshot-1.png) ![alt text](https://raw.githubusercontent.com/anterostecnologia/anterosandroidexamples/master/AnterosVendas/app/images/screenshot-2.png) ![alt text](https://raw.githubusercontent.com/anterostecnologia/anterosandroidexamples/master/AnterosVendas/app/images/screenshot-3.png) ![alt text](https://raw.githubusercontent.com/anterostecnologia/anterosandroidexamples/master/AnterosVendas/app/images/screenshot-4.png)

![alt text](https://raw.githubusercontent.com/anterostecnologia/anterosandroidexamples/master/AnterosVendas/app/images/screenshot-5.png) ![alt text](https://raw.githubusercontent.com/anterostecnologia/anterosandroidexamples/master/AnterosVendas/app/images/screenshot-6.png) ![alt text](https://raw.githubusercontent.com/anterostecnologia/anterosandroidexamples/master/AnterosVendas/app/images/screenshot-7.png) ![alt text](https://raw.githubusercontent.com/anterostecnologia/anterosandroidexamples/master/AnterosVendas/app/images/screenshot-8.png)

![alt text](https://raw.githubusercontent.com/anterostecnologia/anterosandroidexamples/master/AnterosVendas/app/images/screenshot-9.png) ![alt text](https://raw.githubusercontent.com/anterostecnologia/anterosandroidexamples/master/AnterosVendas/app/images/screenshot-10.png)


## Licença ##

Apache 2.0

http://www.apache.org/licenses/LICENSE-2.0


<center>
![alt text](https://avatars0.githubusercontent.com/u/16067889?v=3&u=ab2eb482a16fd90a17d7ce711885f0bdc0640997&s=64)  
Anteros Tecnologia