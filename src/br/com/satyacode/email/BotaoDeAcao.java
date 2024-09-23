package br.com.satyacode.email;

import br.com.sankhya.extensions.actionbutton.AcaoRotinaJava;
import br.com.sankhya.extensions.actionbutton.ContextoAcao;
import br.com.sankhya.jape.EntityFacade;
import br.com.sankhya.jape.bmp.PersistentLocalEntity;
import br.com.sankhya.jape.core.JapeSession;
import br.com.sankhya.jape.vo.DynamicVO;
import br.com.sankhya.jape.vo.EntityVO;
import br.com.sankhya.modelcore.MGEModelException;
import br.com.sankhya.modelcore.util.AgendamentoRelatorioHelper;
import br.com.sankhya.modelcore.util.EntityFacadeFactory;
import com.sankhya.util.TimeUtils;
import org.apache.commons.lang.exception.ExceptionUtils;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

public class BotaoDeAcao implements AcaoRotinaJava {
    @Override
    public void doAction(ContextoAcao contextoAcao) throws Exception {
        System.out.println("BOTAO ENVIAR EMAIL");

        String seuEmail = "teste@teste.com";

        byte[] binarioRelatorio = gerarRelatorio(new BigDecimal(75), contextoAcao.getUsuarioLogado());

        // Enviar email sem anexo
        //enviarEmailSemAnexo("Email Teste", seuEmail, "Teste para entendimento");

        // Enviar o email com anexo
        enviarEmailComRelatorio(binarioRelatorio, "Teste para entendimento".toCharArray(), "Email Teste", seuEmail);

        System.out.println("FIM");
    }


    private byte[] gerarRelatorio(BigDecimal codRelatorio, BigDecimal codUsuarioLogado) throws MGEModelException {
        byte[] pdfBytes = null;
        try {
            EntityFacade dwfFacade = EntityFacadeFactory.getDWFFacade();
            List<Object> lstParam = new ArrayList<Object>();
            AgendamentoRelatorioHelper.ParametroRelatorio pk = new AgendamentoRelatorioHelper.ParametroRelatorio("REFERENCIA", Timestamp.class.getName(), TimeUtils.getNow());
            lstParam.add(pk);
            pdfBytes = AgendamentoRelatorioHelper.getPrintableReport(codRelatorio, lstParam, codUsuarioLogado, dwfFacade);
        } catch (Exception e) {
            e.printStackTrace();
            throw new MGEModelException("Gerar Relatório: " + e.getMessage());
        }
        return pdfBytes;
    }


    private void enviarEmailSemAnexo(String assunto, String email, String mensagem) throws MGEModelException {
        BigDecimal codigoFila = BigDecimal.ZERO;
        JapeSession.SessionHandle hnd = null;
        try {
            hnd = JapeSession.open();
            EntityFacade dwfFacade = EntityFacadeFactory.getDWFFacade();
            EntityVO entityVO = dwfFacade.getDefaultValueObjectInstance("MSDFilaMensagem");
            DynamicVO dynamicVO = (DynamicVO) entityVO;
            dynamicVO.setProperty("ASSUNTO", assunto);
            dynamicVO.setProperty("DTENTRADA", TimeUtils.getNow());
            dynamicVO.setProperty("STATUS", "Pendente");
            dynamicVO.setProperty("EMAIL", email);
            dynamicVO.setProperty("TENTENVIO", BigDecimal.ONE);
            dynamicVO.setProperty("MENSAGEM", mensagem.toCharArray());
            dynamicVO.setProperty("TIPOENVIO", "E");
            dynamicVO.setProperty("MAXTENTENVIO", new BigDecimal(3));
            dynamicVO.setProperty("CODSMTP", new BigDecimal(8));
            dynamicVO.setProperty("CODCON", new BigDecimal(0));
            PersistentLocalEntity createEntity = dwfFacade.createEntity("MSDFilaMensagem", entityVO);
            DynamicVO save = (DynamicVO) createEntity.getValueObject();
            codigoFila = save.asBigDecimal("CODFILA");
        } catch (Exception e) {
            throw new MGEModelException("Erro ao tentar incluir os dados dentro do e-mail!" + ExceptionUtils.getStackTrace(e));
        } finally {
            JapeSession.close(hnd);
        }
    }

    public static void enviarEmailComRelatorio(byte[] relatorio, char[] mensagem, String assunto, String email) throws Exception {
        BigDecimal codigoFila = BigDecimal.ZERO;
        BigDecimal nuAnexoRelatorio = BigDecimal.ZERO;
        JapeSession.SessionHandle hnd = null;
        try {
            hnd = JapeSession.open();
            EntityFacade dwfFacade = EntityFacadeFactory.getDWFFacade();

            // Email
            EntityVO entityVO = dwfFacade.getDefaultValueObjectInstance("MSDFilaMensagem");
            DynamicVO dynamicVO = (DynamicVO) entityVO;
            dynamicVO.setProperty("ASSUNTO", assunto);
            dynamicVO.setProperty("DTENTRADA", TimeUtils.getNow());
            dynamicVO.setProperty("STATUS", "Pendente");
            dynamicVO.setProperty("EMAIL", email);
            dynamicVO.setProperty("TENTENVIO", new BigDecimal(1));
            dynamicVO.setProperty("MENSAGEM", mensagem);
            dynamicVO.setProperty("TIPOENVIO", "E");
            dynamicVO.setProperty("MAXTENTENVIO", new BigDecimal(3));
            dynamicVO.setProperty("CODSMTP", new BigDecimal(8));
            dynamicVO.setProperty("CODCON", new BigDecimal(0));
            PersistentLocalEntity createEntity = dwfFacade.createEntity("MSDFilaMensagem", entityVO);
            DynamicVO save = (DynamicVO) createEntity.getValueObject();
            codigoFila = save.asBigDecimal("CODFILA");


            // Cria anexo do relatorio
            entityVO = dwfFacade.getDefaultValueObjectInstance("AnexoMensagem");
            dynamicVO = (DynamicVO) entityVO;
            dynamicVO.setProperty("NOMEARQUIVO", "relatorio.pdf");
            dynamicVO.setProperty("TIPO", "application/pdf");
            dynamicVO.setProperty("ANEXO", relatorio);
            createEntity = dwfFacade.createEntity("AnexoMensagem", entityVO);
            save = (DynamicVO) createEntity.getValueObject();
            nuAnexoRelatorio = save.asBigDecimal("NUANEXO");

            // Fila de mensagem
            entityVO = dwfFacade.getDefaultValueObjectInstance("AnexoPorMensagem");
            dynamicVO = (DynamicVO) entityVO;
            dynamicVO.setProperty("CODFILA", codigoFila);
            dynamicVO.setProperty("NUANEXO", nuAnexoRelatorio);
            createEntity = dwfFacade.createEntity("AnexoPorMensagem", entityVO);
            save = (DynamicVO) createEntity.getValueObject();
        } catch (Exception e) {
            throw new MGEModelException("Erro ao tentar incluir os dados dentro do e-mail!" + e.getMessage());
        } finally {
            JapeSession.close(hnd);
        }
    }
}
