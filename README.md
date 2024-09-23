# Botão de Ação para Envio de Email com Relatório

Este projeto exemplifica a implementação de um botão de ação na aplicação Sankhya que dispara o envio de um email com e sem anexos. O código está estruturado para:

- Gerar um relatório em PDF.
- Enviar o email sem anexo.
- Enviar o email com o relatório gerado como anexo.

## Estrutura do Projeto

O código contém as seguintes funcionalidades:

1. **Botão de Ação**: Implementado na classe `BotaoDeAcao`, que dispara o envio do email ao ser acionado dentro do contexto da rotina Java.
   
2. **Gerar Relatório**: A função `gerarRelatorio` utiliza o `AgendamentoRelatorioHelper` para gerar o relatório em formato PDF a partir de parâmetros específicos.
   
3. **Envio de Email sem Anexo**: O método `enviarEmailSemAnexo` cria uma entrada na fila de mensagens (`MSDFilaMensagem`) no banco de dados da Sankhya para envio do email sem anexos.
   
4. **Envio de Email com Anexo**: O método `enviarEmailComRelatorio` realiza o envio do email, associando o relatório PDF gerado como anexo à mensagem na fila de mensagens.

## Dependências

O projeto depende de algumas bibliotecas e APIs da própria aplicação Sankhya, como:

- `br.com.sankhya.extensions.actionbutton.AcaoRotinaJava`
- `br.com.sankhya.jape.EntityFacade`
- `br.com.sankhya.modelcore.util.AgendamentoRelatorioHelper`

Além disso, ele utiliza a biblioteca `Apache Commons Lang` para o tratamento de exceções.

## Como Utilizar

1. Implemente o botão de ação dentro da aplicação Sankhya conforme a documentação oficial.
2. Ajuste os parâmetros do método `gerarRelatorio` para atender aos requisitos do relatório específico que deseja gerar.
3. Configure o SMTP no Sankhya caso deseje enviar os emails para destinatários reais.
4. O código pode ser adaptado para outros tipos de relatórios e diferentes ações de rotina Java na Sankhya.

