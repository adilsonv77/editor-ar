package org.furb.arbuilder;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import org.furb.arbuilder.elementos.Agrupamento;
import org.furb.arbuilder.elementos.Diferenca;
import org.furb.arbuilder.elementos.Distinct;
import org.furb.arbuilder.elementos.JuncaoExternaEsquerda;
import org.furb.arbuilder.elementos.JuncaoTeta;
import org.furb.arbuilder.elementos.Operador;
import org.furb.arbuilder.elementos.Ordenacao;
import org.furb.arbuilder.elementos.ProdutoCartesiano;
import org.furb.arbuilder.elementos.Projecao;
import org.furb.arbuilder.elementos.Selecao;
import org.furb.arbuilder.elementos.SelecaoProjecao;
import org.furb.arbuilder.elementos.Uniao;
import org.furb.arbuilder.elementos.UniaoProdutoCartesiano;
import org.furb.arbuilder.elementos.Vertice;
import org.furb.arbuilder.elementos.tabela.Tabela;
import org.furb.arbuilder.ui.Interface;
import org.jgraph.JGraph;
import org.jgraph.graph.DefaultCellViewFactory;
import org.jgraph.graph.DefaultEdge;
import org.jgraph.graph.DefaultGraphCell;
import org.jgraph.graph.DefaultGraphModel;
import org.jgraph.graph.DefaultPort;
import org.jgraph.graph.GraphConstants;
import org.jgraph.graph.GraphLayoutCache;
import org.jgraph.graph.GraphModel;

/**
 * 
 * @author Jonathan Hess, André R. Sousa
 */
public class PainelGrafico {

	private JFrame parent = null;
	private JList listaTabela = null;
	private JGraph painelGrafico = null;
	private Controle controle = null;
	private GraficoOperador operador = null;
	private GraficoLigacao ligacao = null;
	private DefaultGraphCell primeiraCelula = null;
	private boolean exigeParametro = false;
	private boolean isAgrupamento = false;
	private Map<DefaultEdge, List<DefaultGraphCell>> ligacaoHash = new HashMap<DefaultEdge, List<DefaultGraphCell>>();

	public PainelGrafico(Controle controle, JFrame parent,
			JPanel painelGraficoPai, JList listaTabela) {
		// Chama metodos de inicializacao
		this.parent = parent;
		this.listaTabela = listaTabela;
		this.controle = controle;
		this.criarNovoPainelGrafico(painelGraficoPai);
	}

	public final void criarNovoPainelGrafico(JPanel painelGraficoPai) {

		// Cria novo modelo de grafico
		GraphModel model = new DefaultGraphModel();

		// Cria nova view de grafico
		GraphLayoutCache view = new GraphLayoutCache(model,
				new DefaultCellViewFactory());

		// Enfim cria uma novo grafico com o model e o view
		JGraph grafico = new JGraph(model, view);
		grafico.setBackground(new Color(200, 200, 200));
		// Guarda a instacia do grafo
		this.painelGrafico = grafico;

		// Cria novo componente painel
		JScrollPane painelGrafico = new JScrollPane(grafico);

		// Altera propriedades do painel
		painelGrafico.setSize(new Dimension(
				painelGraficoPai.getSize().width - 40, painelGraficoPai
						.getSize().height - 40));
		painelGrafico.setLocation(new Point(20, 20));

		// Adiciona handle ao componente grafico para quando clicado o mouse
		grafico.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent evt) {
				manipularCliqueDoMouse(evt);
			}
		});

		// Adiciona painel e grafico na tela pai
		painelGraficoPai.add(painelGrafico);
	}

	public final void inserirTabelaNoGrafico(String nomeTabela, int x, int y) {
		if (this.painelGrafico == null) { // Se nao existe grafico cai fora
			return;
		}

		Vertice v = new Tabela(nomeTabela);

		// Cria nova celula grafica que representa uma tabela
		DefaultGraphCell novaTabela = new DefaultGraphCell(v);

		// Cria um port para mostrar que e ligavel
		DefaultPort porta = new DefaultPort();

		// Adiciona o componente porta
		novaTabela.add(porta);

		// E o contrario tambem
		porta.setParent(novaTabela);

		// Altera propriedas da celula
		GraphConstants.setAutoSize(novaTabela.getAttributes(), true);
		GraphConstants.setInset(novaTabela.getAttributes(), 5);
		GraphConstants
				.setBounds(novaTabela.getAttributes(), new Rectangle2D.Double(
						x, y, nomeTabela.length() * 10 + 10, 30));
		GraphConstants.setOpaque(novaTabela.getAttributes(), true);
		GraphConstants.setBorder(novaTabela.getAttributes(), BorderFactory
				.createLineBorder(new Color(0, 102, 153), 2));
		GraphConstants.setEditable(novaTabela.getAttributes(), false);

		// Adiciona nova celula no grafico
		this.painelGrafico.getGraphLayoutCache().insert(novaTabela);
		this.controle.getEstruturaDigrafo().addVertice(v);
	}

	public void inserirLigacaoNoGrafico(int x1, int y1, int x2, int y2) {
		// Se operador for nulo cai fora
		if (this.ligacao == null && x2 < 0 && y2 < 0) {
			return;
		}

		if (this.primeiraCelula == null) { // Achou primeira celula
			Object[] cells = DefaultGraphModel
					.getAll((DefaultGraphModel) this.painelGrafico.getModel());

			this.painelGrafico.setSelectionCells(cells);

			this.primeiraCelula = (DefaultGraphCell) this.painelGrafico
					.getSelectionCellAt(new Point(x1, y1));
			if (this.primeiraCelula == null) {
				return;
			}
			if (this.primeiraCelula.getUserObject() instanceof Tabela) {
				JOptionPane
						.showMessageDialog(
								null,
								"Para realizar um relacao escolha primeiro um operador!",
								"Erro!", JOptionPane.ERROR_MESSAGE);
				this.limparAtributo();
				return;
			}
			if (x2 < 0 && y2 < 0) {
				return;
			}
		}

		Vertice v1 = (Vertice) this.primeiraCelula.getUserObject();

		// Cria uma nova ligacao
		DefaultEdge ligacao = new DefaultEdge();

		// Constante do tipo da ligacao
		int arrowTipo = GraphConstants.ARROW_CLASSIC;

		DefaultGraphCell segundaCelula = null;

		// Acha segunda celula
		if (x2 < 0 && y2 < 0) {
			segundaCelula = (DefaultGraphCell) this.painelGrafico
					.getSelectionCellAt(new Point(x1, y1));
		} else {
			segundaCelula = (DefaultGraphCell) this.painelGrafico
					.getSelectionCellAt(new Point(x2, y2));
		}

		if (segundaCelula == null) { // Se nao existir ainda segunda celula cai
			// fora
			return;
		}

		Vertice v2 = (Vertice) segundaCelula.getUserObject();

		// Evita arestas em si mesmo
		if (v1 == v2) {
			JOptionPane
					.showMessageDialog(
							null,
							"Nao e possivel realizar uma relacao para o mesmo operador!",
							"Erro!", JOptionPane.ERROR_MESSAGE);
			this.limparAtributo();
			return;
		}

		if (primeiraCelula.getUserObject() instanceof Ordenacao
				&& this.controle.getEstruturaDigrafo().getAdjacencias(
						(Vertice) primeiraCelula.getUserObject()).size() > 0) {
			this.limparAtributo();
			return;
		}
		if (primeiraCelula.getUserObject() instanceof SelecaoProjecao
				&& this.controle.getEstruturaDigrafo().getAdjacencias(
						(Vertice) primeiraCelula.getUserObject()).size() > 0) {
			this.limparAtributo();
			return;
		}
		if (primeiraCelula.getUserObject() instanceof UniaoProdutoCartesiano
				&& this.controle.getEstruturaDigrafo().getAdjacencias(
						(Vertice) primeiraCelula.getUserObject()).size() > 1) {
			this.limparAtributo();
			return;
		}
		if (primeiraCelula.getUserObject() instanceof Diferenca
				&& this.controle.getEstruturaDigrafo().getAdjacencias(
						(Vertice) primeiraCelula.getUserObject()).size() > 1) {
			this.limparAtributo();
			return;
		}
		if (primeiraCelula.getUserObject() instanceof Agrupamento
				&& this.controle.getEstruturaDigrafo().getAdjacencias(
						(Vertice) primeiraCelula.getUserObject()).size() > 0) {
			this.limparAtributo();
			return;
		}
		if (primeiraCelula.getUserObject() instanceof JuncaoExternaEsquerda
				&& this.controle.getEstruturaDigrafo().getAdjacencias(
						(Vertice) primeiraCelula.getUserObject()).size() > 1) {
			this.limparAtributo();
			return;
		}
		if (primeiraCelula.getUserObject() instanceof JuncaoTeta
				&& this.controle.getEstruturaDigrafo().getAdjacencias(
						(Vertice) primeiraCelula.getUserObject()).size() > 1) {
			this.limparAtributo();
			return;
		}

		ligacao.setSource(this.primeiraCelula.getChildAt(0));
		ligacao.setTarget(segundaCelula.getChildAt(0));

		ArrayList<DefaultGraphCell> listaLigacao = new ArrayList<DefaultGraphCell>();

		listaLigacao.add(this.primeiraCelula);
		listaLigacao.add(segundaCelula);

		this.ligacaoHash.put(ligacao, listaLigacao);

		GraphConstants.setLineEnd(ligacao.getAttributes(), arrowTipo);
		GraphConstants.setEndFill(ligacao.getAttributes(), true);

		// Enfim insere no grafico
		this.painelGrafico.getGraphLayoutCache().insert(ligacao);

		this.controle.getEstruturaDigrafo().addAresta(v1, v2);
		ligacao.setUserObject(this.controle.getEstruturaDigrafo()
				.getArestaEntreVertices(v1, v2));

		this.painelGrafico.setSelectionCells(new Object[] {}); // Limpa selecao

		// Limpa campo ligacao
		this.limparAtributo();
	}

	public void inserirOperadorNoGrafico(String parametro1, String parametro2,
			int x, int y) {

		// Se operador for nulo cai fora
		if (this.operador == null) {
			return;
		}
		String args = null;
		String a1 = null;
		String a2 = null;

		if (parametro1 == null && parametro2 == null) {

			if (this.isAgrupamento) {

				this.isAgrupamento = false;
				a1 = JOptionPane
						.showInputDialog("Digite a(s) coluna(s) agrupadoras:");
				if (a1 == null) {
					this.limparAtributo();
					return;
				}
				a2 = JOptionPane
						.showInputDialog("Digite a(s) coluna(s) projetadas:");
				if (a2 == null) {
					this.limparAtributo();
					return;
				}
				if (a1.isEmpty() && a2.isEmpty()) {
					JOptionPane
							.showMessageDialog(
									null,
									"Nao e possivel adicionar operador sem parametro(s)!",
									"Erro!", JOptionPane.ERROR_MESSAGE);
					this.limparAtributo();
					return;
				}
			} else {
				if (this.exigeParametro) {
					this.exigeParametro = false;
					args = JOptionPane
							.showInputDialog("Digite o(s) parametro(s) para o operador "
									+ this.operador.toString() + ":");
					if (args == null) {
						this.limparAtributo();
						return;
					}
					if (args.isEmpty()) {
						JOptionPane
								.showMessageDialog(
										null,
										"Nao e possível adicionar operador sem parametro(s)!",
										"Erro!", JOptionPane.ERROR_MESSAGE);
						this.limparAtributo();
						return;
					}
				}
			}
		} else {
			a1 = parametro1;
			a2 = parametro2;
			args = parametro1;
		}

		if (args != null
				&& (this.operador.getOperador().getClass() == Selecao.class || this.operador
						.getOperador().getClass() == Projecao.class)) {
			((SelecaoProjecao) this.operador.getOperador()).setParametro(args);
			((SelecaoProjecao) this.operador.getOperador())
					.setEntrada(new Vertice(""));
		}

		if (args != null
				&& this.operador.getOperador().getClass() == JuncaoExternaEsquerda.class) {
			((JuncaoExternaEsquerda) this.operador.getOperador())
					.setParametro(args);
			((JuncaoExternaEsquerda) this.operador.getOperador())
					.setEntrada(new Vertice(""));
		}

		if (args != null
				&& this.operador.getOperador().getClass() == JuncaoTeta.class) {
			((JuncaoTeta) this.operador.getOperador())
					.setParametro(args);
			/*((JuncaoTeta) this.operador.getOperador())
					.setEntrada(new Vertice(""));*/
		}

		if (args != null
				&& (this.operador.getOperador().getClass() == Ordenacao.class)) {
			((Ordenacao) this.operador.getOperador()).setParametro(args);
			/*((Ordenacao) this.operador.getOperador())
					.setEntrada(new Vertice(""));*/
		}

		if (this.operador.getOperador().getClass() == Agrupamento.class) {
			if (a1 != null) {
				((Agrupamento) this.operador.getOperador())
						.setColunasAgrupadoras(a1);
			}
			if (a2 != null) {
				((Agrupamento) this.operador.getOperador())
						.setColunasProjetadas(a2);
			}
		}

		Vertice v = buildVertice(args, a1, a2);
		// Cria nova celula grafica que representa um operador
		DefaultGraphCell novoOperador = new DefaultGraphCell(v);

		// Cria um port para mostrar que e ligavel
		DefaultPort porta = new DefaultPort();

		// Adiciona o componente porta
		novoOperador.add(porta);

		// E o contrario tambem
		porta.setParent(novoOperador);

		// Altera propriedas da celula
		GraphConstants.setAutoSize(novoOperador.getAttributes(), true);
		GraphConstants.setInset(novoOperador.getAttributes(), 5);
		GraphConstants.setLineStyle(novoOperador.getAttributes(),
				GraphConstants.STYLE_ORTHOGONAL);
		GraphConstants.setBounds(novoOperador.getAttributes(),
				new Rectangle2D.Double(x, y, this.operador.getOperador()
						.toString().length() * 10 + 10, 30));
		GraphConstants.setOpaque(novoOperador.getAttributes(), true);
		GraphConstants.setBorder(novoOperador.getAttributes(), BorderFactory
				.createLineBorder(new Color(100, 100, 100), 1));
		GraphConstants.setEditable(novoOperador.getAttributes(), false);

		GraphConstants.setIcon(novoOperador.getAttributes(),
				new javax.swing.ImageIcon(getClass().getResource(
						Operadores.getImageForOparador(v.toString()))));

		// Adiciona nova celula no grafico
		this.painelGrafico.getGraphLayoutCache().insert(novoOperador);

		this.controle.getEstruturaDigrafo().addVertice(v);

		// Limpa campo operador
		this.limparAtributo();
	}

	private Vertice buildVertice(String args, String a1, String a2) {

		Vertice v = null;

		if (this.operador.getOperador() instanceof Selecao) {
			v = new Selecao(this.operador.getOperador().getNome(), args);
		}
		if (this.operador.getOperador() instanceof Projecao) {
			v = new Projecao(this.operador.getOperador().getNome(), args);
		}
		if (this.operador.getOperador() instanceof Ordenacao) {
			v = new Ordenacao(this.operador.getOperador().getNome(), args);
		}
		if (this.operador.getOperador() instanceof Uniao) {
			v = new Uniao(this.operador.getOperador().getNome());
		}
		if (this.operador.getOperador() instanceof Diferenca) {
			v = new Diferenca(this.operador.getOperador().getNome());
		}
		if (this.operador.getOperador() instanceof ProdutoCartesiano) {
			v = new ProdutoCartesiano(this.operador.getOperador().getNome());
		}
		if (this.operador.getOperador() instanceof JuncaoExternaEsquerda) {
			v = new JuncaoExternaEsquerda(
					this.operador.getOperador().getNome(), args);
		}
		if (this.operador.getOperador() instanceof Agrupamento) {
			v = new Agrupamento(this.operador.getOperador().getNome(), a1, a2);
		}
		if (this.operador.getOperador() instanceof Distinct) {
			v = new Distinct(this.operador.getOperador().getNome());
		}
		if (this.operador.getOperador() instanceof JuncaoTeta) {
			v = new JuncaoTeta(this.operador.getOperador().getNome(), args);
		}

		return v;
	}

	public final void limparAtributo() {
		this.operador = null;
		this.ligacao = null;
		this.isAgrupamento = false;
		this.exigeParametro = false;
		this.primeiraCelula = null;
	}

	public final void limparTela() {
		Object[] cells = DefaultGraphModel
				.getAll((DefaultGraphModel) this.painelGrafico.getModel());

		this.painelGrafico.setSelectionCells(cells);

		Object[] listaRemover = this.painelGrafico.getSelectionCells();

		for (int i = 0; i < listaRemover.length; i++) {
			if (((DefaultGraphCell) listaRemover[i]).getUserObject() instanceof Vertice) {
				this.controle.getEstruturaDigrafo().removeVertice(
						(Vertice) ((DefaultGraphCell) listaRemover[i])
								.getUserObject());
				this.controle.getEstruturaDigrafo().removeArestas(
						(Vertice) ((DefaultGraphCell) listaRemover[i])
								.getUserObject());
			} else if (((DefaultGraphCell) listaRemover[i]).getUserObject() instanceof Aresta) {
				this.controle.getEstruturaDigrafo().removeAresta(
						(Aresta) ((DefaultGraphCell) listaRemover[i])
								.getUserObject());
			}
		}
		this.painelGrafico.getGraphLayoutCache().remove(
				this.painelGrafico.getSelectionCells(), false, true);
	}

	public void manipularCliqueDoMouse(MouseEvent evt) {
		DefaultGraphCell cell = null;

		try {

			cell = (DefaultGraphCell) this.painelGrafico
					.getSelectionCellAt(new Point(evt.getX(), evt.getY()));

			// Limpa atributo operador e ligacao caso botao pressionado seja o
			// da direita
			if (evt.getButton() == MouseEvent.BUTTON3) {
				if (this.painelGrafico.getSelectionCells() != null) {
					// Remove celulas selecionadas caso existam
					Object[] listaRemover = this.painelGrafico
							.getSelectionCells();
					for (int i = 0; i < listaRemover.length; i++) {
						if (((DefaultGraphCell) listaRemover[i])
								.getUserObject() instanceof Vertice) {
							this.controle
									.getEstruturaDigrafo()
									.removeVertice(
											(Vertice) ((DefaultGraphCell) listaRemover[i])
													.getUserObject());
							this.controle
									.getEstruturaDigrafo()
									.removeArestas(
											(Vertice) ((DefaultGraphCell) listaRemover[i])
													.getUserObject());
						} else if (((DefaultGraphCell) listaRemover[i])
								.getUserObject() instanceof Aresta) {
							this.controle
									.getEstruturaDigrafo()
									.removeAresta(
											(Aresta) ((DefaultGraphCell) listaRemover[i])
													.getUserObject());
						}
					}
					this.painelGrafico.getGraphLayoutCache()
							.remove(this.painelGrafico.getSelectionCells(),
									false, true);
				}
				this.limparAtributo();
				return;
			}

			if (evt.getButton() == MouseEvent.BUTTON1) {

				if (painelGrafico.getSelectionCells().length == 1) {
					Object celula = ((DefaultGraphCell) this.painelGrafico
							.getSelectionCell()).getUserObject();

					if (celula instanceof Tabela) {
						((Interface) this.parent)
								.setEditarParametrosTabela(((Vertice) celula)
										.getNome());
					}

					else if (celula instanceof SelecaoProjecao) {
						((Interface) this.parent).setEditarParametrosOperador(
								((SelecaoProjecao) celula).getNome(),
								((SelecaoProjecao) celula).getParametro(), "",
								false);
					}

					else if (celula instanceof JuncaoExternaEsquerda) {
						((Interface) this.parent)
								.setEditarParametrosOperador(
										((JuncaoExternaEsquerda) celula)
												.getNome(),
										((JuncaoExternaEsquerda) celula)
												.getParametro(), "", false);
					}

					else if (celula instanceof Ordenacao) {
						((Interface) this.parent).setEditarParametrosOperador(
								((Ordenacao) celula).getNome(),
								((Ordenacao) celula).getParametro(), "", false);
					}

					else if (celula instanceof Agrupamento) {
						((Interface) this.parent).setEditarParametrosOperador(
								((Agrupamento) celula).getNome(),
								((Agrupamento) celula).getColunasAgrupadoras(),
								((Agrupamento) celula).getColunasProjetadas(),
								true);
					} else if (celula instanceof Distinct) {
						((Interface) this.parent).setEditarParametrosOperador(
								((Distinct) celula).getNome(), "", "", true);
					} else if (celula instanceof JuncaoTeta) {
						((Interface) this.parent).setEditarParametrosOperador(
								((JuncaoTeta) celula).getNome(),
								((JuncaoTeta) celula).getParametro(), "",
								false);
					} else {
						((Interface) this.parent)
								.setEditarParametrosTabela(((Operador) celula)
										.getNome());
					}
				} else {
					((Interface) this.parent).setEditarParametrosTabela("");
				}
			}

		} catch (Exception e) {

		}

		try {
			if (cell != null && cell.getUserObject() instanceof Vertice) {
				((Interface) this.parent).setSQL(this.controle.getDAO()
						.montarSQlDeAR(
								this.controle.getEstruturaDigrafo()
										.montaAlgebraRelacional(
												(Vertice) cell.getUserObject(),
												null)));
			}
		} catch (Exception e) {
			((Interface) this.parent)
					.setSQL("Erro, estrutura mal-formada neste operador!");
		}

		// Se o atributo for nulo ou nao tiver nada selecionado cai fora
		if (this.listaTabela == null) {
			return;
		}

		// Se atributo operador for nao nulo entao insere novo operador
		if (this.operador != null || this.ligacao != null) {
			if (this.operador != null) { // Caso tenha de inserir GraficoLigacao
				this.inserirOperadorNoGrafico(null, null, evt.getX(), evt
						.getY());
			} else if (this.ligacao != null) {
				this.inserirLigacaoNoGrafico(evt.getX(), evt.getY(), -1, -1); // Senao
				// insere
				// GraficoOperador
			}
		} else if (this.listaTabela.getSelectedValue() != null) {
			// Insere componente no grafico
			this.inserirTabelaNoGrafico(listaTabela.getSelectedValue()
					.toString(), evt.getX(), evt.getY());

			// E limpa selecao da lista para nao duplicar tabelas no grafico
			// quando clicar
			this.listaTabela.clearSelection();
		}
	}

	public final void setNovaLigacao() {
		this.ligacao = new GraficoLigacao();
	}

	public final void setNovoOperador(int tipoOperador) {

		Vertice v = new Operador("");

		switch (tipoOperador) {
		case 0:
			v = new Selecao(Operadores.SELECAO.getOperador());
			this.exigeParametro = true;
			break;
		case 1:
			v = new Projecao(Operadores.PROJECAO.getOperador());
			this.exigeParametro = true;
			break;
		case 2:
			v = new Uniao(Operadores.UNIAO.getOperador());
			this.exigeParametro = false;
			break;
		case 3:
			v = new ProdutoCartesiano(Operadores.PRODUTO_CARTESIANO
					.getOperador());
			this.exigeParametro = false;
			break;
		case 4:
			v = new Agrupamento(Operadores.AGRUPAMENTO.getOperador());
			this.isAgrupamento = true;
			break;
		case 5:
			v = new Diferenca(Operadores.DIFERENCA.getOperador());
			this.exigeParametro = false;
			break;
		case 6:
			v = new Ordenacao(Operadores.ORDENACAO.getOperador());
			this.exigeParametro = true;
			break;
		case 7:
			v = new JuncaoExternaEsquerda(Operadores.JUNCAO_EX_ESQUERDA
					.getOperador());
			this.exigeParametro = true;
			break;
		case 8:
			v = new Distinct(Operadores.DISTINCT.getOperador());
			this.exigeParametro = false;
			break;
		case 9:
			v = new JuncaoTeta(Operadores.JUNCAO_TETA.getOperador());
			this.exigeParametro = true;
			break;
		}

		this.operador = new GraficoOperador(v.toString(), v);
	}

	public final void refresh() {
		painelGrafico.refresh();
	}

	public final List<DefaultGraphCell> getModelo() {

		List<DefaultGraphCell> modelo = new ArrayList<DefaultGraphCell>();

		Object[] cells = DefaultGraphModel
				.getAll((DefaultGraphModel) this.painelGrafico.getModel());

		for (int i = 0; i < cells.length; i++) {
			modelo.add((DefaultGraphCell) cells[i]);
		}

		return modelo;
	}

	public final Map<DefaultEdge, List<DefaultGraphCell>> getLigacaoHash() {
		return this.ligacaoHash;
	}

	public final void alterarParametroOperador(String parm1Operador,
			String parm2Operador) {
		if (this.painelGrafico.getSelectionCells().length == 1) {
			DefaultGraphCell celula = (DefaultGraphCell) this.painelGrafico
					.getSelectionCell();

			Object objeto = celula.getUserObject();

			if (objeto instanceof SelecaoProjecao) {
				((SelecaoProjecao) objeto).setParametro(parm1Operador);
				celula.setUserObject(objeto);
			} else if (objeto instanceof Ordenacao) {
				((Ordenacao) objeto).setParametro(parm1Operador);
				celula.setUserObject(objeto);
			} else if (objeto instanceof JuncaoExternaEsquerda) {
				((JuncaoExternaEsquerda) objeto).setParametro(parm1Operador);
				celula.setUserObject(objeto);
			} else if (objeto instanceof Agrupamento) {
				((Agrupamento) objeto).setColunasAgrupadoras(parm1Operador);
				((Agrupamento) objeto).setColunasProjetadas(parm2Operador);
			} else if (objeto instanceof JuncaoTeta) {
				((JuncaoTeta) objeto).setParametro(parm1Operador);
				celula.setUserObject(objeto);
				// TODO: VERIFICAR
			}

			painelGrafico.refresh();
		}
	}
}
