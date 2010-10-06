package org.furb.arbuilder;

import java.awt.Point;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import org.furb.arbuilder.bd.DBUtil;
import org.furb.arbuilder.elementos.Operador;
import org.furb.arbuilder.elementos.Vertice;
import org.furb.arbuilder.elementos.tabela.Tabela;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.input.SAXBuilder;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;
import org.jgraph.graph.DefaultEdge;
import org.jgraph.graph.DefaultGraphCell;
import org.jgraph.graph.GraphConstants;

/**
 *
 * @author Jonathan Hess, André R. Sousa
 */
public class XML {
    
    private Document arquivoXML = null;
    
    /**
     * Abrir arquivo XML com as configurações
     * salvar, entre elas, configurações de banco
     * e configurações das arvores
     * @param pathArquivoXML
     * @return
     */
    public final boolean abrirXML(String pathArquivoXML)
    {
    	SAXBuilder builder = null;
    	
    	try {
    	
    		builder = new SAXBuilder(); 
    		arquivoXML = builder.build( new File(pathArquivoXML) );
    		
    	} catch ( Exception e ) {
			e.printStackTrace();
		}
    	
        return true;
    }
    
    /**
     * Salva as configurações no arquivo XML
     * @param pathArquivo
     * @return
     * @throws ParserConfigurationException
     * @throws TransformerException
     * @throws FileNotFoundException
     */
    public final boolean salvarXML(String pathArquivo) throws ParserConfigurationException, TransformerException, FileNotFoundException 
    {
    	XMLOutputter xout = new XMLOutputter();
    	Format fr = xout.getFormat();
    	fr.setEncoding("ISO-8859-1");
    	fr.setIndent("\t");
    	OutputStream arquivo = null;
    	
    	try {
    		
    		if( !pathArquivo.contains(".xml") ) {
    			arquivo = new FileOutputStream( new File( pathArquivo + ".xml" ) );
    		} else {
    			arquivo = new FileOutputStream( new File( pathArquivo ) );
    		}
    		
    		xout.setFormat(fr);
    		xout.output( arquivoXML , arquivo );
    		
    	} catch ( Exception e ) {
			e.printStackTrace();
		}
        
        return true;
    }
    
    @SuppressWarnings("unchecked")
	public final void getCabecalhoConfiguracao()
    {
        String driver	= "";
        String url		= "";
        String usuario  = "";
        String senha    = "";

        Element elementoPai	= arquivoXML.getRootElement();
        List<Element> elementos = elementoPai.getChildren("configs");
        
    	driver	= elementos.get(0).getChildText("driver");
    	url		= elementos.get(0).getChildText("url");
    	usuario	= elementos.get(0).getChildText("usuario");
    	senha	= elementos.get(0).getChildText("senha");
        
		String[] params = new String[]{ driver , url , usuario , senha };
		String[] valida = new String[]{ driver , url , usuario };
		
		boolean emptyFields = false;
		for( int i = 0; i < valida.length; i++ ) {
			if( valida[i].isEmpty() ) {
				emptyFields = true;
				break;
			}
		}
		
		if( !emptyFields ) {
			DBUtil.getInstance(params);
		}
    }
    
    public final void setCabecalhoConfiguracao() throws Exception 
    {
        String driver	= DBUtil.getInstance().getDbDriver();
        String url		= DBUtil.getInstance().getDbUrl();
        String usuario  = DBUtil.getInstance().getDbUser();
        String senha    = DBUtil.getInstance().getDbPass();
        
        Element root = new Element("arBuilder");
        arquivoXML = new Document(root);

        Element configs		= new Element("configs");
        Element elDriver	= new Element("driver");
        Element elUrl		= new Element("url");
        Element elUsuario	= new Element("usuario");
        Element elSenha		= new Element("senha");

        elDriver.setText(driver);
        elUrl.setText(url);
        elUsuario.setText(usuario);
        elSenha.setText(senha);
        
        configs.addContent(elDriver);
        configs.addContent(elUrl);
        configs.addContent(elUsuario);
        configs.addContent(elSenha);
        
        arquivoXML.getRootElement().addContent(configs);
    }
    
	@SuppressWarnings("unchecked")
	public final List<List<?>> getModeloGrafico() 
    {
        List<List<?>> elementoList = new ArrayList<List<?>>();
        List<Element> tabelaList   = new ArrayList<Element>();
        List<Element> operadorList = new ArrayList<Element>();
        List<Element> ligacaoList  = new ArrayList<Element>();
        
        Element elementoPai	= arquivoXML.getRootElement();
        List<Element> modeloGrafico = elementoPai.getChildren("modeloGrafico");
        List<Element> modeloGraficoChilds = modeloGrafico.get(0).getChildren();

        for( Element el : modeloGraficoChilds ) 
        {
            if( el.getName().equals("tabela")) {
                tabelaList.add( el );
            }
            
            if( el.getName().equals("operador") ) {
                operadorList.add( el );
            }
            
            if( el.getName().equals("ligacao") ) {
                ligacaoList.add( el );
            }
        }
        
        List vertices = new ArrayList();
        List arestas  = new ArrayList();
        
        for( Element el : tabelaList ) 
        {
        	List<Element> attrList = el.getChildren();
            Element nomeTabela    = null;
            Element posicaoTabela = null;
            
            for( Element ele : attrList ) 
            {
                if( ele.getName().equals("nomeTabela") ) {
                    nomeTabela = ele;
                }
                if( ele.getName().equals("posicaoTabela") ) {
                    posicaoTabela = ele;
                }
            }
            
            Tabela tabela = new Tabela( nomeTabela.getValue() );
            GraficoTabela grafTab = new GraficoTabela( nomeTabela.getValue() , tabela , Integer.parseInt( posicaoTabela.getValue().split(",")[0] ), Integer.parseInt( posicaoTabela.getValue().split(",")[1]));
            
            vertices.add(grafTab);
        }
        
        for( Element el : operadorList )
        {
            List<Element> attrList = el.getChildren();
            Element nomeOperador       = null;
            Element parametro1Operador = null;
            Element parametro2Operador = null;
            Element posicaoOperador    = null;
            
            for( Element ele : attrList ) 
            {
                if( ele.getName().equals("nomeOperador") ) {
                    nomeOperador = ele;
                }
                if( ele.getName().equals("parametro1Operador") ) {
                    parametro1Operador = ele;
                }
                if( ele.getName().equals("parametro2Operador") ) {
                    parametro2Operador = ele;
                }
                if( ele.getName().equals("posicaoOperador") ) {
                    posicaoOperador = ele;
                }
            }
            
            Vertice operador = new Operador( nomeOperador.getValue() );
            
            String parametro1 = "";
            String parametro2 = "";
            
            if(parametro1Operador != null) {
                try {
                    parametro1 = parametro1Operador.getValue();
                }
                catch(NullPointerException ex) {
                    parametro1 = "";
                }
            }
            
            if(parametro2Operador != null) {
                try {
                    parametro2 = parametro2Operador.getValue();
                }
                catch(NullPointerException ex) {
                    parametro2 = "";
                }
            }
            
            GraficoOperador grafOp = new GraficoOperador(nomeOperador.getValue(), operador, parametro1, parametro2, Integer.parseInt(posicaoOperador.getValue().split(",")[0]), Integer.parseInt(posicaoOperador.getValue().split(",")[1]));
            
            vertices.add(grafOp);
        }
        
        for( Element el : ligacaoList ) 
        {
            List<Element> posicaoList = el.getChildren();
            Element posicaoPai   = null;
            Element posicaoFilho = null;
            
            for( Element ele : posicaoList ) 
            {
                if( ele.getName().equals("posicaoPai")) {
                    posicaoPai = ele;
                }
                if( ele.getName().equals("posicaoFilho")) {
                    posicaoFilho = ele;
                }
            }
            
            Point posPai   = new Point(Integer.parseInt(posicaoPai.getValue().split(",")[0]), Integer.parseInt(posicaoPai.getValue().split(",")[1]));
            Point posFilho = new Point(Integer.parseInt(posicaoFilho.getValue().split(",")[0]), Integer.parseInt(posicaoFilho.getValue().split(",")[1]));
            
            arestas.add(new GraficoLigacao(posPai, posFilho));
        }
        
        elementoList.add(vertices);
        elementoList.add(arestas);
        
        return elementoList;
    }
    
    public final void setModeloGrafico(PainelGrafico modeloGrafico) 
    {
        List<DefaultGraphCell> modeloList = modeloGrafico.getModelo();
        Map<DefaultEdge, List<DefaultGraphCell>> ligacaoHash = modeloGrafico.getLigacaoHash();

        Element modelo = new Element("modeloGrafico");

        for( DefaultGraphCell dg : modeloList ) 
        {
            if( dg.getClass().toString().contains("GraphCell") ) 
            {
                if( dg.getUserObject().getClass().toString().contains("Tabela") ) 
                {
                    Element tabela		= new Element("tabela");
                    Element nomeTabela	= new Element("nomeTabela");
                    Element posTabela	= new Element("posicaoTabela");

                    nomeTabela.setText( dg.getUserObject().toString() );
                    
                    String posicao = String.valueOf( Math.round(GraphConstants.getBounds( dg.getAttributes()).getX() ) + "," + Math.round(GraphConstants.getBounds( dg.getAttributes() ).getY() ) );
                    posTabela.setText(posicao);
                    
                    tabela.addContent(nomeTabela);
                    tabela.addContent(posTabela);
                    modelo.addContent(tabela);
                }
                else {
                	
                    Element operador		= new Element("operador");
                    Element nomeOperador	= new Element("nomeOperador");
                    Element par1Operador	= new Element("parametro1Operador");
                    Element par2Operador	= new Element("parametro2Operador");
                    Element posOperador		= new Element("posicaoOperador");
                    
                    String nmOp = dg.getUserObject().toString();
                    
                    if( !nmOp.contains( Operadores.AGRUPAMENTO.getOperador() ) )
                    {
                        if( nmOp.contains( Operadores.PRODUTO_CARTESIANO.getOperador() ) ) 
                        {
                            nomeOperador.setText( Operadores.PRODUTO_CARTESIANO.getOperador() );
                        }
                        else {
                            nomeOperador.setText( nmOp.split(" ")[0] );
                        }
                        
                        if( !nmOp.contains( Operadores.UNIAO.getOperador() ) && 
                        	!nmOp.contains( Operadores.PRODUTO_CARTESIANO.getOperador() ) &&
                        	!nmOp.contains( Operadores.DIFERENCA.getOperador() ) ) 
                        {
                            par1Operador.setText( nmOp.split(" ", 2)[1] );
                        }
                        else {
                            par1Operador.setText("");
                        }
                        
                        par2Operador.setText("");
                    }
                    else {
                        nomeOperador.setText( Operadores.AGRUPAMENTO.getOperador() );
                        par1Operador.setText( nmOp.split( Operadores.AGRUPAMENTO.getOperador() )[0].trim() );
                        par2Operador.setText( nmOp.split( Operadores.AGRUPAMENTO.getOperador() )[1].trim() );
                    }
                    
                    String posicao = String.valueOf(Math.round(GraphConstants.getBounds( dg.getAttributes()).getX()) + "," + Math.round(GraphConstants.getBounds( dg.getAttributes()).getY() ) );
                    posOperador.setText(posicao);

                    operador.addContent(nomeOperador);
                    operador.addContent(par1Operador);
                    operador.addContent(par2Operador);
                    operador.addContent(posOperador);
                    
                    modelo.addContent(operador);
                }
            }
            
            if( dg.getClass().toString().contains("Edge") ) 
            {
                Element ligacao		= new Element("ligacao");
                Element posPai		= new Element("posicaoPai");
                Element posFilho	= new Element("posicaoFilho");

                String pPai   = "";
                String pFilho = "";
                
                DefaultGraphCell pai   = ligacaoHash.get(dg).get(0);
                DefaultGraphCell filho = ligacaoHash.get(dg).get(1);
                
                pPai   = String.valueOf(Math.round(GraphConstants.getBounds(pai.getAttributes()).getX()) + "," + Math.round(GraphConstants.getBounds(pai.getAttributes()).getY()));
                pFilho = String.valueOf(Math.round(GraphConstants.getBounds(filho.getAttributes()).getX()) + "," + Math.round(GraphConstants.getBounds(filho.getAttributes()).getY()));
                
                posPai.setText(pPai);
                posFilho.setText(pFilho);

                ligacao.addContent(posPai);
                ligacao.addContent(posFilho);
                modelo.addContent(ligacao);
            }
        }
        
        arquivoXML.getRootElement().addContent(modelo);
    }
}