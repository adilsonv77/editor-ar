package org.furb.arbuilder;

import java.lang.reflect.Method;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import org.furb.arbuilder.elementos.Agrupamento;
import org.furb.arbuilder.elementos.JuncaoExternaEsquerda;
import org.furb.arbuilder.elementos.JuncaoTeta;
import org.furb.arbuilder.elementos.Ordenacao;
import org.furb.arbuilder.elementos.Projecao;
import org.furb.arbuilder.elementos.Selecao;
import org.furb.arbuilder.elementos.Vertice;
import org.furb.arbuilder.elementos.tabela.Coluna;
import org.furb.arbuilder.elementos.tabela.Tabela;
import org.furb.arbuilder.util.AliasHelper;

/**
 * Classe responsavel por toda a logica na construcao do sql
 */
public class Digrafo {

	private List<Vertice> vertices;
	private List<Aresta> arestas;

	/**
	 * Cria um Digrafo vazio
	 */
	public Digrafo() {
		vertices = new ArrayList<Vertice>();
		arestas = new ArrayList<Aresta>();
	}

	/**
	 * Cria um Digrafo populado
	 * 
	 * @param vertices
	 * @param arestas
	 */
	public Digrafo(ArrayList<Vertice> vertices, ArrayList<Aresta> arestas) {
		this.vertices = vertices;
		this.arestas = arestas;
	}

	/**
	 * Adiciona um Vertice e retorna o Vertice recem-criado
	 * 
	 * @param v
	 */
	public final void addVertice(Vertice v) {
		vertices.add(v);
	}

	/**
	 * Retorna um ArrayList contendo todos os Vertices adjacentes a v
	 * 
	 * @param v
	 * @return
	 */
	public final List<Vertice> getAdjacencias(Vertice v) {
		List<Vertice> adjacencias = new ArrayList<Vertice>();
		for (int i = 0; i < arestas.size(); i++) {
			if (arestas.get(i).getVertice1() == v) {
				adjacencias.add(arestas.get(i).getVertice2());
			}
		}
		return adjacencias;
	}

	/**
	 * Apaga O Vertice com o nome passado
	 * 
	 * @param v
	 */
	public final void removeVertice(Vertice v) {
		removeArestas(v);
		vertices.remove(v);
	}

	/**
	 * Apaga todas as arestas que v possui (pode ser implementado no evendo do
	 * botao direito um 'Remove ligacoes')
	 * 
	 * @param v
	 */
	public final void removeArestas(Vertice v) {
		for (int i = 0; i < arestas.size(); i++) {
			if (arestas.get(i).contemVertice(v)) {
				arestas.remove(i);
				removeArestas(v);
			}
		}
	}

	/**
	 * Adiciona uma aresta e retorna ela
	 * 
	 * @param a
	 */
	public final void addAresta(Aresta a) {
		arestas.add(a);
	}

	/**
	 * Adiciona uma aresta entre os vertices v1 e v2
	 * 
	 * @param v1
	 * @param v2
	 * @return
	 */
	public final Aresta addAresta(Vertice v1, Vertice v2) {
		if (this.getArestaEntreVertices(v1, v2) == null) {
			Aresta aresta = new Aresta(v1, v2);
			arestas.add(aresta);
			return aresta;
		}
		return null;
	}

	/**
	 * Remove aresta entre os vertices especificados
	 * 
	 * @param v1
	 * @param v2
	 */
	public final void removeAresta(Vertice v1, Vertice v2) {
		for (int i = 0; i < arestas.size(); i++) {
			if (arestas.get(i).contemVertice(v1)
					&& arestas.get(i).contemVertice(v2))
				arestas.remove(i);
		}
	}

	/**
	 * Remove aresta passada por parametro
	 * 
	 * @param a
	 */
	public final void removeAresta(Aresta a) {
		arestas.remove(a);
	}

	/**
	 * Retorna a Aresta entre os vertices especificados
	 * 
	 * @param v1
	 * @param v2
	 * @return
	 */
	public final Aresta getArestaEntreVertices(Vertice v1, Vertice v2) {
		for (int i = 0; i < arestas.size(); i++) {
			if (arestas.get(i).contemVertice(v1)
					&& arestas.get(i).contemVertice(v2))
				return arestas.get(i);
		}
		return null;
	}

	/**
	 * Provavel q nao vo usa pra nada
	 * 
	 * @return
	 */
	public final int getQtdVertices() {
		return vertices.size();
	}

	/**
	 * Outra funcao pra c usada no evendo do botao direito (remove todas as
	 * ligacoes)
	 */
	public final void removeArestas() {
		arestas.clear();
	}

	/**
	 * Tem q pensa melhor nesse metodo se nao der certo
	 * 
	 * @param v
	 */
	public final void removeHierarquia(Vertice v) {
		for (int i = 0; i < arestas.size(); i++) {
			if (arestas.get(i).getVertice2().equals(v))
				arestas.remove(i);
		}
		if (getAdjacencias(v).size() == 2) {
			removeHierarquia(getAdjacencias(v).get(0));
			removeHierarquia(getAdjacencias(v).get(1));
		} else if (getAdjacencias(v).size() == 1) {
			removeHierarquia(getAdjacencias(v).get(0));
		}
		vertices.remove(v);
	}

	/**
	 * Metodo principal que retorna a expressao
	 * 
	 * @param v
	 * @param parent
	 * @return
	 */
	public final String montaAlgebraRelacional(Vertice v, Vertice parent) {
		String ret = null;

		try {
			// Invoka o metodo da expressao
			Method m = this.getClass().getMethod("logic" + v.getNome(),
					Vertice.class, Vertice.class);
			ret = String.valueOf(m.invoke(this, new Object[] { v, parent }));
		} catch (NoSuchMethodException e) {
			// Caso não existe um metodo, é uma tabela
			if (v instanceof Tabela) {
				Tabela tab = (Tabela) v;
				AliasHelper.getInstance().put(tab);

				return "SELECT " + tab.getColumnNames() + " FROM "
						+ v.toString() + " " + tab.getUniqueId();
			}

		} catch (Exception e) {
			AliasHelper.getInstance().clearAlias();
		}

		return ret;
	}

	/**
	 * Método responsável pela contrução logica do operador de selecao
	 * 
	 * @param v
	 * @param parent
	 * @return
	 * @throws Exception
	 */
	public final String logicSelecao(Vertice v, Vertice parent)
			throws Exception {
		Selecao ps = (Selecao) v;

		// Recursivamente chama os operadores superiores, motando suas querys.
		String parentLeft = montaAlgebraRelacional(getAdjacencias(v).get(0), v);
		this.validaQuery(parentLeft);

		// Seta qual operação está sendo executada
		AliasHelper.getInstance().setCurrentOperator(
				Operadores.SELECAO.getOperador());

		// Recupera a ultima operacao
		String op = AliasHelper.getInstance().getLastOperator();
		String cp = AliasHelper.getInstance().getCurrentOperator();

		// Caso houver where no resultado do operador
		// anterior, entao pega os resultados da penultima tabela
		// e não da ultima que foi um where
		Tabela t1 = null;
		if (op.equals(Operadores.SELECAO.getOperador())
				&& !cp.equals(Operadores.SELECAO.getOperador())) {
			t1 = AliasHelper.getInstance().getPenultimate();
		} else {
			t1 = AliasHelper.getInstance().getLast();
		}

		Tabela nT1 = AliasHelper.getInstance().getNewTableFrom(t1);
		nT1.setAutoExclude(true);
		AliasHelper.getInstance().put(nT1);

		// Substitui os parametros, pelos uniqueId's
		String params = ps.getParametro();
		boolean useHaving = false;

		if (params.indexOf("(") != -1 && params.indexOf(")") != 1) {
			useHaving = true;
		}

		for (Coluna c : t1.getColunas()) {
			String fullColumn = String.valueOf(c.getNmTabela() + "."
					+ c.getNmRealColuna());
			if (params.indexOf(fullColumn) != -1) {
				String newFullColumn = String.valueOf(c.getUniqueTable() + "."
						+ c.getNmColuna());
				params = params.replaceAll(fullColumn, newFullColumn);
			}
		}

		String operParams = ps.toString().replaceAll("\\^", "AND");
		params = params.replaceAll("\\^", "AND");

		operParams = params; // operParams.replaceAll( operParams , params );

		// TODO ticket 5

		/*
		 * if (op.equals(Operadores.PROJECAO.getOperador())) { String[] split =
		 * parentLeft.split("\\("); split[0].replace("FROM", "");
		 * split[0].replace("SELECT", ""); split = split[0].split(","); String s
		 * = "SELECT * FROM ("; for (int i = 0; i < split.length; i++) { if (i
		 * == split.length-1) s += split[i] + " "; else s += split[i] + ", "; }
		 * 
		 * s += t1.getNome() + " )"; parentLeft = s; }
		 */

		// Adiciona restricao no select
		if (op.equals(Operadores.SELECAO.getOperador())) { // Aninhamento de
			// restricoes

			if (parentLeft.contains("HAVING")) {
				return parentLeft + " AND " + operParams;
			}

			if (parentLeft.contains("WHERE")) {
				return parentLeft + " AND " + operParams;
			}

			if (parentLeft.contains("GROUP BY")) { // Adiciona antes do group by
				// caso exista
				return parentLeft.split("GROUP BY")[0].trim() + " WHERE "
						+ operParams + " GROUP BY"
						+ parentLeft.split("GROUP BY")[1];
			}

			if (parentLeft.contains("ORDER BY")) { // Adiciona antes do group by
				// caso exista
				return parentLeft.split("ORDER BY")[0].trim() + " AND "
						+ operParams + " ORDER BY"
						+ parentLeft.split("ORDER BY")[1];
			}

			return parentLeft + " WHERE " + operParams;
		} else { // Nova restricao

			if (parentLeft.contains("GROUP BY")) { // Adiciona antes do group by
				// caso exista

				if (useHaving) {
					return parentLeft.split("GROUP BY")[0].trim()
							+ " GROUP BY " + parentLeft.split("GROUP BY")[1]
							+ " HAVING " + operParams;
				}

				return parentLeft.split("GROUP BY")[0].trim() + " WHERE "
						+ operParams + " GROUP BY"
						+ parentLeft.split("GROUP BY")[1];
			}

			if (parentLeft.contains("ORDER BY")) { // Adiciona antes do group by
				// caso exista
				return parentLeft.split("ORDER BY")[0].trim() + " WHERE "
						+ operParams + " ORDER BY"
						+ parentLeft.split("ORDER BY")[1];
			}

			return parentLeft + " " + "WHERE " + operParams;
		}
	}

	/**
	 * Método responsável pela contrução logica do operador de projecao
	 * 
	 * @param v
	 * @param parent
	 * @return
	 * @throws Exception
	 */
	public final String logicProjecao(Vertice v, Vertice parent)
			throws Exception {
		Projecao ps = (Projecao) v;

		if (ps.getParametro().trim().contains("*")) {
			throw new Exception();
		}

		// Recursivamente chama os operadores superiores, motando suas querys.
		String parentLeft = montaAlgebraRelacional(getAdjacencias(v).get(0), v);
		Tabela t1 = AliasHelper.getInstance().getLast();
		this.validaQuery(parentLeft);

		AliasHelper.getInstance().setCurrentOperator(
				Operadores.PROJECAO.getOperador());

		Tabela nT1 = new Tabela(t1.getNome());
		StringTokenizer st = new StringTokenizer(ps.getParametro(), ",");
		while (st.hasMoreTokens()) {
			String param = st.nextToken().trim();
			boolean find = false;

			// Tem alias
			if (param.indexOf(".") != -1) {
				for (Coluna nT1c : t1.getColunas()) {
					Coluna nT1c1 = nT1c.clone();
					String lastCmd = nT1c.getNmTabela() + "."
							+ nT1c.getNmRealColuna();
					if (param.equals(lastCmd)) {
						if (!nT1c1.getAliasColuna().trim().isEmpty()) {
							nT1c1.setNmColuna(nT1c.getAliasColuna());
							nT1c1.setAliasColuna(nT1.getUniqueId()
									+ nT1c.getAliasColuna());
						}
						nT1c1.setUniqueTable(String.valueOf(nT1.getUniqueId()));
						nT1.getColunas().add(nT1c1);
						find = true;
					}
					if (find) {
						find = false;
						break;
					}
				}
			} else {
				Coluna c = null;
				for (Coluna cols : t1.getColunas()) {
					if (cols.getNmRealColuna().equals(param)) {
						c = cols.clone();
						if (!c.getAliasColuna().trim().isEmpty()) {
							c.setNmColuna(cols.getAliasColuna());
							c.setAliasColuna(nT1.getUniqueId()
									+ cols.getAliasColuna());
						}
						c.setUniqueTable(String.valueOf(nT1.getUniqueId()));
						find = true;
					}
					if (find) {
						find = false;
						break;
					}
				}
				if (c != null) {
					nT1.getColunas().add(c);
				}
			}

		}

		AliasHelper.getInstance().put(nT1);

		String newQuery = "SELECT " + nT1.getParameters() + " FROM ( "
				+ parentLeft + " ) " + nT1.getUniqueId();
		return makeQuerySafe(newQuery);
	}

	/**
	 * Método responsável pela contrução logica do operador de ordenacao
	 * 
	 * @param v
	 * @param parent
	 * @return
	 */
	public final String logicOrdenacao(Vertice v, Vertice parent)
			throws Exception {
		Ordenacao od = (Ordenacao) v;

		// Recursivamente chama os operadores superiores, motando suas querys.
		String paramsLeft = montaAlgebraRelacional(getAdjacencias(v).get(0), v);
		Tabela t1 = AliasHelper.getInstance().getLast();
		this.validaQuery(paramsLeft);

		AliasHelper.getInstance().setCurrentOperator(
				Operadores.ORDENACAO.getOperador());

		// Substitui os parametros, pelos uniqueId's
		String params = od.getParametro();
		for (Coluna c : t1.getColunas()) {
			String fullColumn = String.valueOf(c.getNmTabela() + "."
					+ c.getNmRealColuna());
			if (params.indexOf(fullColumn) != -1) {
				String newFullColumn = String.valueOf(c.getUniqueTable() + "."
						+ c.getNmColuna());
				params = params.replaceAll(fullColumn, newFullColumn);
			}
		}

		if (paramsLeft.contains("ORDER BY")) {
			return paramsLeft.split("ORDER BY")[0].trim() + " ORDER BY"
					+ paramsLeft.split("ORDER BY")[1] + ", " + params;
		}

		String newQuery = paramsLeft + " ORDER BY " + params;
		return newQuery;
	}

	public final String logicDistinct(Vertice v, Vertice parent)
			throws Exception {
		// Recupera o SQL resultante dos vertices adjacentes
		String paramsLeft = montaAlgebraRelacional(getAdjacencias(v).get(0), v);
		this.validaQuery(paramsLeft);

		AliasHelper.getInstance().setCurrentOperator(
				Operadores.DISTINCT.getOperador());

		return "SELECT DISTINCT "
				+ paramsLeft.substring(9, paramsLeft.length());

	}

	public final String logicJuncaoTeta(Vertice v, Vertice parent)
			throws Exception {

		JuncaoTeta jt = (JuncaoTeta) v;

		// Recursivamente chama os operadores superiores, motando suas querys.
		String parentLeft = montaAlgebraRelacional(getAdjacencias(v).get(0), v)
				.trim();
		this.validaQuery(parentLeft);
		Tabela t1 = AliasHelper.getInstance().getLast();

		String parentRight = montaAlgebraRelacional(getAdjacencias(v).get(1), v)
				.trim();
		this.validaQuery(parentRight);
		Tabela t2 = AliasHelper.getInstance().getLast();

		AliasHelper.getInstance().setCurrentOperator(
				Operadores.JUNCAO_TETA.getOperador());

		// Recupera as colunas
		Tabela nT1 = AliasHelper.getInstance().getNewTableFrom(t1);
		Tabela nT2 = AliasHelper.getInstance().getNewTableFrom(t2);
		nT1.setAutoExclude(true);
		nT2.setAutoExclude(true);

		// Checa compatibilidade de colunas
		AliasHelper.getInstance().put(nT1);
		AliasHelper.getInstance().put(nT2);
		AliasHelper.getInstance().checkAlias(nT1, nT2);

		String onParams = jt.getParametro();
		// Colocar alias para as colunas
		for (Coluna c2 : nT1.getColunas()) {
			String fullColumn = String.valueOf(c2.getNmTabela() + "."
					+ c2.getNmRealColuna());
			if (onParams.indexOf(fullColumn) != -1) {
				String newFullColumn = String.valueOf(c2.getUniqueTable() + "."
						+ c2.getNmColuna());
				onParams = onParams.replaceAll(fullColumn, newFullColumn);
			}
		}

		for (Coluna c1 : nT2.getColunas()) {
			String fullColumn = String.valueOf(c1.getNmTabela() + "."
					+ c1.getNmRealColuna());

			if (onParams.indexOf(fullColumn) != -1) {
				String newFullColumn = String.valueOf(c1.getUniqueTable() + "."
						+ c1.getNmColuna());
				onParams = onParams.replaceAll(fullColumn, newFullColumn);

			}
		}
		Tabela nT3 = AliasHelper.getInstance().getNewTableFromGroup(nT1, nT2);

		AliasHelper.getInstance().put(nT3);
		StringBuilder query = new StringBuilder();

		query.append("SELECT ");
		query.append(nT3.getParameters());
		query.append(" FROM ( ");

		query.append("SELECT ");
		query.append(nT1.getParameters());
		query.append(", ");
		query.append(nT2.getParameters());
		query.append(" FROM ( ");
		query.append(parentLeft);
		query.append(") AS ");
		query.append(nT1.getUniqueId());
		query.append(" JOIN ( ");
		query.append(parentRight);
		query.append(") AS ");
		query.append(nT2.getUniqueId());
		query.append(" ON ( ");
		query.append(onParams);
		query.append(" ) ");

		query.append(" ) ");
		query.append(nT3.getUniqueId());

		return query.toString();
	}

	public final String logicJuncaoNatural(Vertice v, Vertice parent)
			throws Exception {

		// JuncaoNatural jn = (JuncaoNatural) v;

		// Recursivamente chama os operadores superiores, motando suas querys.
		String parentLeft = montaAlgebraRelacional(getAdjacencias(v).get(0), v)
				.trim();
		this.validaQuery(parentLeft);
		Tabela t1 = AliasHelper.getInstance().getLast();

		String parentRight = montaAlgebraRelacional(getAdjacencias(v).get(1), v)
				.trim();
		this.validaQuery(parentRight);
		Tabela t2 = AliasHelper.getInstance().getLast();

		AliasHelper.getInstance().setCurrentOperator(
				Operadores.JUNCAO_NATURAL.getOperador());

		// Recupera as colunas
		Tabela nT1 = AliasHelper.getInstance().getNewTableFrom(t1);
		Tabela nT2 = AliasHelper.getInstance().getNewTableFrom(t2);
		nT1.setAutoExclude(true);
		nT2.setAutoExclude(true);

		// Checa compatibilidade de colunas
		AliasHelper.getInstance().put(nT1);
		AliasHelper.getInstance().put(nT2);
		AliasHelper.getInstance().checkAlias(nT1, nT2);

		/*
		 * String onParams = jn.getParametro(); // Colocar alias para as colunas
		 * for (Coluna c2 : nT1.getColunas()) { String fullColumn =
		 * String.valueOf(c2.getNmTabela() + "." + c2.getNmRealColuna()); if
		 * (onParams.indexOf(fullColumn) != -1) { String newFullColumn =
		 * String.valueOf(c2.getUniqueTable() + "." + c2.getNmColuna());
		 * onParams = onParams.replaceAll(fullColumn, newFullColumn); } }
		 * 
		 * for (Coluna c1 : nT2.getColunas()) { String fullColumn =
		 * String.valueOf(c1.getNmTabela() + "." + c1.getNmRealColuna());
		 * 
		 * if (onParams.indexOf(fullColumn) != -1) { String newFullColumn =
		 * String.valueOf(c1.getUniqueTable() + "." + c1.getNmColuna());
		 * onParams = onParams.replaceAll(fullColumn, newFullColumn);
		 * 
		 * } }
		 */
		Tabela nT3 = AliasHelper.getInstance().getNewTableFromGroup(nT1, nT2);

		AliasHelper.getInstance().put(nT3);
		StringBuilder query = new StringBuilder();

		List<Coluna> listaTemp = nT2.getColunas();
		for (Coluna c1 : nT1.getColunas()) {
			for (Coluna c2 : listaTemp) {
				if (c1.getNmColuna().equals(c2.getNmColuna())) {
					nT2.getColunas().remove(c2);
				}
			}
		}

		query.append("SELECT ");
		for (Coluna c : nT1.getColunas()) {
			query.append(nT1.getUniqueId() + "." + c.getNmColuna() + ", ");
		}
		for (Coluna c : nT2.getColunas()) {
			query.append(nT2.getUniqueId() + "." + c.getNmColuna() + ", ");
		}
		query.deleteCharAt(query.lastIndexOf(", "));

		query.append(" FROM ");
		query.append(nT1.getNome());
		query.append(" AS ");
		query.append(nT1.getUniqueId());
		query.append(" NATURAL JOIN  ");
		query.append(nT2.getNome());
		query.append(" AS ");
		query.append(nT2.getUniqueId());

		System.out.println(query.toString());
		return query.toString();

	}

	public final String logicInterseccao(Vertice v, Vertice parent)
			throws Exception {

		String parentLeft = montaAlgebraRelacional(getAdjacencias(v).get(0), v)
				.trim();
		Tabela t1 = AliasHelper.getInstance().getLast();
		this.validaQuery(parentLeft);

		String parentRight = montaAlgebraRelacional(getAdjacencias(v).get(1), v)
				.trim();
		Tabela t2 = AliasHelper.getInstance().getLast();
		this.validaQuery(parentRight);

		// Valida numero de colunas
		if (t1.getColunas().size() != t2.getColunas().size()) {
			throw new Exception("Incompatibilidade de colunas (tamanho!)");
		}

		// Valida tipo das colunas
		for (int i = 0; i < t1.getColunas().size(); i++) {
			if (!t1.getColunas().get(i).getTpColuna().equals(
					t2.getColunas().get(i).getTpColuna())) {
				throw new Exception("Incompatibilidade de colunas (tipo!)");
			}
		}

		AliasHelper.getInstance().setCurrentOperator(
				Operadores.INTERSECCAO.getOperador());

		Tabela nT1 = AliasHelper.getInstance().getNewTableFrom(t1);
		AliasHelper.getInstance().put(nT1);
		Tabela nT2 = AliasHelper.getInstance().getNewTableFrom(t2);

		// query alternativa ao INTERSECT (Oracle):
		// SELECT a.idvendedor, a.dsvendedor
		// FROM vendedores a INNER JOIN clientes b
		// ON a.idvendedor=b.idcliente AND a.dsvendedor=b.dscliente
		StringBuilder query = new StringBuilder();
		query.append("SELECT ");
		query.append(nT1.getParameters());
		query.append(" FROM ( ");
		query.append(makeQuerySafe(parentLeft).trim());
		query.append(" ");
		query.append(v.toString().replace("Interseccao", "INNER JOIN").trim());
		query.append(" ");
		query.append(nT2.getNome());
		query.append(" ");
		query.append(t2.getUniqueId());
		query.append(" ON ");
		query.append(t1.getUniqueId());
		query.append(".");
		query.append(t1.getColunas().get(0).getNmColuna());
		query.append("=");
		query.append(t2.getUniqueId());
		query.append(".");
		query.append(t2.getColunas().get(0).getNmColuna());		
		for (int i = 1; i < t1.getColunas().size(); i++) {
			query.append(" AND ");
			query.append(t1.getUniqueId());
			query.append(".");
			query.append(t1.getColunas().get(i).getNmColuna());
			query.append("=");
			query.append(t2.getUniqueId());
			query.append(".");
			query.append(t2.getColunas().get(i).getNmColuna());
		}
		query.append(" ) ");
		query.append(nT1.getUniqueId());
		
		return makeQuerySafe(query.toString());

	}

	/**
	 * Método responsável pela contrução logica do operador de juncao externa a
	 * esquerda
	 * 
	 * @param v
	 * @param parent
	 * @return
	 */
	public final String logicJuncaoExEsquerda(Vertice v, Vertice parent)
			throws Exception {
		JuncaoExternaEsquerda jes = (JuncaoExternaEsquerda) v;

		// Recursivamente chama os operadores superiores, motando suas querys.
		String parentLeft = montaAlgebraRelacional(getAdjacencias(v).get(0), v)
				.trim();
		this.validaQuery(parentLeft);
		Tabela t1 = AliasHelper.getInstance().getLast();

		String parentRight = montaAlgebraRelacional(getAdjacencias(v).get(1), v)
				.trim();
		this.validaQuery(parentRight);
		Tabela t2 = AliasHelper.getInstance().getLast();

		AliasHelper.getInstance().setCurrentOperator(
				Operadores.JUNCAO_EX_ESQUERDA.getOperador());

		// Recupera as colunas
		Tabela nT1 = AliasHelper.getInstance().getNewTableFrom(t1);
		Tabela nT2 = AliasHelper.getInstance().getNewTableFrom(t2);
		nT1.setAutoExclude(true);
		nT2.setAutoExclude(true);

		// Checa compatibilidade de colunas
		AliasHelper.getInstance().put(nT1);
		AliasHelper.getInstance().put(nT2);
		AliasHelper.getInstance().checkAlias(nT1, nT2);

		String onParams = jes.getParametro();

		// Colocar alias para as colunas
		for (Coluna c2 : nT1.getColunas()) {
			String fullColumn = String.valueOf(c2.getNmTabela() + "."
					+ c2.getNmRealColuna());
			if (onParams.indexOf(fullColumn) != -1) {
				String newFullColumn = String.valueOf(c2.getUniqueTable() + "."
						+ c2.getNmColuna());
				onParams = onParams.replaceAll(fullColumn, newFullColumn);
			}
		}

		for (Coluna c1 : nT2.getColunas()) {
			String fullColumn = String.valueOf(c1.getNmTabela() + "."
					+ c1.getNmRealColuna());
			if (onParams.indexOf(fullColumn) != -1) {
				String newFullColumn = String.valueOf(c1.getUniqueTable() + "."
						+ c1.getNmColuna());
				onParams = onParams.replaceAll(fullColumn, newFullColumn);
			}
		}

		Tabela nT3 = AliasHelper.getInstance().getNewTableFromGroup(nT1, nT2);

		AliasHelper.getInstance().put(nT3);
		StringBuilder query = new StringBuilder();

		query.append("SELECT ");
		query.append(nT3.getParameters());
		query.append(" FROM ( ");

		query.append("SELECT ");
		query.append(nT1.getParameters());
		query.append(", ");
		query.append(nT2.getParameters());
		query.append(" FROM ( ");
		query.append(parentLeft);
		query.append(") AS ");
		query.append(nT1.getUniqueId());
		query.append(" LEFT OUTER JOIN ( ");
		query.append(parentRight);
		query.append(") AS ");
		query.append(nT2.getUniqueId());
		query.append(" ON ( ");
		query.append(onParams);
		query.append(" ) ");

		query.append(" ) ");
		query.append(nT3.getUniqueId());

		return query.toString();
	}

	/**
	 * Método responsável pela contrução logica do operador de produto
	 * cartesiano
	 * 
	 * @param v
	 * @param parent
	 * @return
	 */
	public final String logicProdutoCartesiano(Vertice v, Vertice parent)
			throws Exception {
		// Recursivamente chama os operadores superiores, motando suas querys.
		String parentLeft = montaAlgebraRelacional(getAdjacencias(v).get(0), v)
				.trim();
		Tabela t1 = AliasHelper.getInstance().getLast();
		this.validaQuery(parentLeft);

		String parentRight = montaAlgebraRelacional(getAdjacencias(v).get(1), v)
				.trim();
		Tabela t2 = AliasHelper.getInstance().getLast();
		this.validaQuery(parentRight);

		AliasHelper.getInstance().setCurrentOperator(
				Operadores.PRODUTO_CARTESIANO.getOperador());

		// Cria a nova tabela utilizada na operacao
		Tabela nT1 = AliasHelper.getInstance().getNewTableFrom(t1);
		Tabela nT2 = AliasHelper.getInstance().getNewTableFrom(t2);
		nT1.setAutoExclude(true);
		nT2.setAutoExclude(true);

		// Checa compatibilidade de colunas
		AliasHelper.getInstance().put(nT1);
		AliasHelper.getInstance().put(nT2);
		AliasHelper.getInstance().checkAlias(nT1, nT2);

		// Cria a nova tabela para agrupar tudo
		Tabela nT3 = AliasHelper.getInstance().getNewTableFromGroup(nT1, nT2);
		AliasHelper.getInstance().put(nT3);

		// Recupera as colunas com os novos aliases e uniqueId's
		String paramsLeft = nT1.getParameters();
		String paramsRight = nT2.getParameters();

		parentLeft = " ( " + parentLeft.trim() + " ) " + nT1.getUniqueId();
		parentRight = " ( " + parentRight.trim() + " ) " + nT2.getUniqueId();

		StringBuilder retQuery = new StringBuilder();
		retQuery.append("SELECT ");
		retQuery.append(nT3.getParameters());
		retQuery.append(" FROM ( ");
		retQuery.append("SELECT ");
		retQuery.append(paramsLeft);
		retQuery.append(", ");
		retQuery.append(paramsRight);
		retQuery.append(" FROM ");
		retQuery.append(makeQuerySafe(parentLeft).trim());
		retQuery.append(v.toString().replace("ProdutoCartesiano", ", ").trim());
		retQuery.append(" ");
		retQuery.append(makeQuerySafe(parentRight).trim());
		retQuery.append(" ) ");
		retQuery.append(nT3.getUniqueId());

		return makeQuerySafe(retQuery.toString());
	}

	/**
	 * Método responsável pela contrução logica do operador de uniao
	 * 
	 * @param v
	 * @param parent
	 * @return
	 * @throws SQLException
	 */
	public final String logicUniao(Vertice v, Vertice parent) throws Exception {
		// Recursivamente chama os operadores superiores, motando suas querys.
		String parentLeft = montaAlgebraRelacional(getAdjacencias(v).get(0), v)
				.trim();
		Tabela t1 = AliasHelper.getInstance().getLast();
		this.validaQuery(parentLeft);

		String parentRight = montaAlgebraRelacional(getAdjacencias(v).get(1), v)
				.trim();
		Tabela t2 = AliasHelper.getInstance().getLast();
		this.validaQuery(parentRight);

		// Valida numero de colunas
		if (t1.getColunas().size() != t2.getColunas().size()) {
			throw new Exception("Incompatibilidade de colunas");
		}

		AliasHelper.getInstance().setCurrentOperator(
				Operadores.UNIAO.getOperador());

		// Cria a nova tabela utilizada na operacao,
		// como é uniao só precisa de um dos resultados
		// nao mais das colunas das duas relacoes
		Tabela nT1 = AliasHelper.getInstance().getNewTableFrom(t1);

		// Cria a nova tabela para agrupar tudo
		Tabela nT3 = AliasHelper.getInstance().getNewTableFrom(nT1);

		AliasHelper.getInstance().put(nT3);

		// Se você quiser usar um ORDER BY para o resultado UNION final, você
		// deve utilizar parenteses
		if (parentLeft.contains("ORDER BY")) {
			parentLeft = "( " + parentLeft + " )";
		}

		if (parentRight.contains("ORDER BY")) {
			parentRight = "( " + parentRight + " )";
		}

		StringBuilder retQuery = new StringBuilder();
		retQuery.append("SELECT ");
		retQuery.append(nT3.getParameters());
		retQuery.append(" FROM ( ");
		retQuery.append(makeQuerySafe(parentLeft).trim());
		retQuery.append(" ");
		retQuery.append(v.toString().replace("Uniao", "UNION ALL").trim());
		retQuery.append(" ");
		retQuery.append(makeQuerySafe(parentRight).trim());
		retQuery.append(" ) ");
		retQuery.append(nT3.getUniqueId());

		return makeQuerySafe(retQuery.toString());

	}

	/**
	 * Método responsável pela contrução logica do operador de agrupamento
	 * 
	 * @param v
	 * @param parent
	 * @return
	 */
	public final String logicAgrupamento(Vertice v, Vertice parent)
			throws Exception {
		Agrupamento a = (Agrupamento) v;

		// Recursivamente chama os operadores superiores, motando suas querys.
		String parentLeft = montaAlgebraRelacional(getAdjacencias(v).get(0), v);
		Tabela t1 = AliasHelper.getInstance().getLast();
		this.validaQuery(parentLeft);

		AliasHelper.getInstance().setCurrentOperator(
				Operadores.AGRUPAMENTO.getOperador());
		Tabela nT1 = AliasHelper.getInstance().getNewTableFrom(t1);
		nT1.getColunas().clear();
		AliasHelper.getInstance().checkSingleAlias(nT1);

		// Colunas projetadas
		StringTokenizer st = new StringTokenizer(a.getColunasProjetadas(), ",");
		while (st.hasMoreTokens()) {

			boolean find = false;
			String param = st.nextToken().trim();

			// é fuma funcao
			if (param.indexOf("(") != -1 && param.indexOf(")") != 1) {

				String nmReal = param.split("\\(")[1].trim().split("\\)")[0]
						.trim();
				String nmFunc = param.split("\\(")[0].trim();

				// Tem alias
				if (nmReal.indexOf(".") != -1) {
					for (Coluna nT1c : t1.getColunas()) {
						Coluna nT1c1 = nT1c.clone();
						String lastCmd = nT1c.getNmTabela() + "."
								+ nT1c.getNmRealColuna();
						if (nmReal.equals(lastCmd)) {
							if (!nT1c1.getAliasColuna().trim().isEmpty()) {
								nT1c1.setUniqueTable(String.valueOf(nT1
										.getUniqueId()));
								nT1c1.setNmColuna(nT1c.getAliasColuna());
								nT1c1.setAliasColuna(nT1.getUniqueId()
										+ nT1c.getAliasColuna());
							}

							nT1c1.setUniqueTable(String.valueOf(nT1
									.getUniqueId()));
							nT1c1.setFuncaoAgrupadora(nmFunc);
							nT1.getColunas().add(nT1c1);
							find = true;
						}
						if (find) {
							find = false;
							break;
						}
					}
				} else {
					Coluna c = null;
					for (Coluna cols : t1.getColunas()) {
						if (cols.getNmRealColuna().equals(nmReal)) {
							c = cols.clone();

							if (!c.getAliasColuna().trim().isEmpty()) {
								c.setUniqueTable(String.valueOf(nT1
										.getUniqueId()));
								c.setNmColuna(cols.getAliasColuna());
								c.setAliasColuna(nT1.getUniqueId()
										+ cols.getAliasColuna());
							}

							c.setUniqueTable(String.valueOf(nT1.getUniqueId()));
							c.setNmRealColuna(nmReal);
							c.setFuncaoAgrupadora(nmFunc);
							find = true;
						}
						if (find) {
							find = false;
							break;
						}
					}
					if (c != null) {
						nT1.getColunas().add(c);
					}
				}

			} else {

				String nmReal = param;

				// Tem alias
				if (nmReal.indexOf(".") != -1) {
					for (Coluna nT1c : t1.getColunas()) {
						Coluna nT1c1 = nT1c.clone();
						String lastCmd = nT1c.getNmTabela() + "."
								+ nT1c.getNmRealColuna();
						if (nmReal.equals(lastCmd)) {

							if (!nT1c1.getAliasColuna().trim().isEmpty()) {
								nT1c1.setUniqueTable(String.valueOf(nT1
										.getUniqueId()));
								nT1c1.setNmColuna(nT1c.getAliasColuna());
								nT1c1.setAliasColuna(nT1.getUniqueId()
										+ nT1c.getAliasColuna());
							}

							nT1c1.setUniqueTable(String.valueOf(nT1
									.getUniqueId()));
							nT1.getColunas().add(nT1c1);
							find = true;

						}
						if (find) {
							find = false;
							break;
						}
					}
				} else {
					Coluna c = null;
					for (Coluna cols : t1.getColunas()) {
						if (cols.getNmRealColuna().equals(param)) {
							c = cols.clone();

							if (!c.getAliasColuna().trim().isEmpty()) {
								c.setUniqueTable(String.valueOf(nT1
										.getUniqueId()));
								c.setNmColuna(cols.getAliasColuna());
								c.setAliasColuna(nT1.getUniqueId()
										+ cols.getAliasColuna());
							}

							c.setUniqueTable(String.valueOf(nT1.getUniqueId()));
							c.setNmRealColuna(param);
							c.setFuncaoAgrupadora("");
							find = true;
						}
						if (find) {
							find = false;
							break;
						}
					}
					if (c != null) {
						nT1.getColunas().add(c);
					}
				}
			}
		}

		// Colunas agrupadoras
		StringTokenizer str = new StringTokenizer(a.getColunasAgrupadoras(),
				",");
		while (str.hasMoreTokens()) {

			boolean find = false;
			String param = str.nextToken().trim();
			String nmReal = param;

			// Tem alias
			if (nmReal.indexOf(".") != -1) {
				for (Coluna nT1c : t1.getColunas()) {
					Coluna nT1c1 = nT1c.clone();
					String lastCmd = nT1c.getNmTabela() + "."
							+ nT1c.getNmRealColuna();
					if (nmReal.equals(lastCmd)) {
						nT1c1.setUniqueTable(String.valueOf(nT1.getUniqueId()));

						if (!nT1c1.getAliasColuna().trim().isEmpty()) {
							nT1c1.setUniqueTable(String.valueOf(nT1
									.getUniqueId()));
							nT1c1.setNmColuna(nT1c.getAliasColuna());
							nT1c1.setAliasColuna(nT1.getUniqueId()
									+ nT1c.getAliasColuna());
						}

						nT1c1.setUniqueTable(String.valueOf(nT1.getUniqueId()));
						nT1.getGroupBy().add(nT1c1);
						nT1.getColunas().add(nT1c1);
						find = true;
					}
					if (find) {
						find = false;
						break;
					}
				}
			} else {
				Coluna c = null;
				for (Coluna cols : t1.getColunas()) {
					if (cols.getNmRealColuna().equals(param)) {
						c = cols.clone();

						if (!c.getAliasColuna().trim().isEmpty()) {
							c.setUniqueTable(String.valueOf(nT1.getUniqueId()));
							c.setNmColuna(cols.getAliasColuna());
							c.setAliasColuna(nT1.getUniqueId()
									+ cols.getAliasColuna());
						}

						c.setUniqueTable(String.valueOf(nT1.getUniqueId()));
						c.setNmRealColuna(param);
						c.setFuncaoAgrupadora("");
						find = true;
					}
					if (find) {
						find = false;
						break;
					}
				}
				if (c != null) {
					nT1.getGroupBy().add(c);
					nT1.getColunas().add(c);
				}
			}
		}

		AliasHelper.getInstance().put(nT1);

		String colAg = a.getColunasAgrupadoras();
		String colPr = a.getColunasProjetadas();

		// Nao possui colunas agrupadoras
		if (colAg.trim().isEmpty()) {
			return "SELECT " + nT1.getParameters() + " FROM ( " + parentLeft
					+ " ) " + nT1.getUniqueId(); // concatena os campos
			// projetados
		}
		// Nao possui colunas projetadas (funcao)
		else if (colPr.trim().isEmpty()) {

			// Aninhamento de agrupamentos
			if (parentLeft.contains("GROUP BY")) {
				return "SELECT " + nT1.getParameters() + " FROM ( "
						+ parentLeft + " ) " + nT1.getUniqueId() + ", "
						+ nT1.getGroupByParameters();
			}

			return "SELECT " + nT1.getParameters() + " FROM ( " + parentLeft
					+ " ) " + nT1.getUniqueId() + " GROUP BY "
					+ nT1.getGroupByParameters();
		}

		return "SELECT " + nT1.getParameters() + " FROM ( " + parentLeft
				+ " ) " + nT1.getUniqueId() + " GROUP BY "
				+ nT1.getGroupByParameters();
	}

	/**
	 * Método responsável pela contrução logica do operador de diferenca
	 * 
	 * @param v
	 * @param parent
	 * @return
	 * @throws Exception
	 */
	public final String logicDiferenca(Vertice v, Vertice parent)
			throws Exception {
		// Recupera o SQL resultante dos vertices adjacentes
		String parentLeft = montaAlgebraRelacional(getAdjacencias(v).get(0), v);
		Tabela t1 = AliasHelper.getInstance().getLast();
		this.validaQuery(parentLeft);

		String parentRight = montaAlgebraRelacional(getAdjacencias(v).get(1), v);
		Tabela t2 = AliasHelper.getInstance().getLast();
		this.validaQuery(parentRight);

		// Valida compatibilidade de colunas
		if (t1.getColunas().size() != t2.getColunas().size()) {
			throw new Exception("Incompatibilidade de colunas");
		}

		AliasHelper.getInstance().setCurrentOperator(
				Operadores.DIFERENCA.getOperador());
		boolean first = false;

		// Cria uma nova tabela para novo ID
		Tabela nT1 = AliasHelper.getInstance().getNewTableFrom(t1);
		// Sera desconsiderado pelo metodo getLast() do AliasHelper
		nT1.setAutoExclude(true);

		Tabela nT11 = AliasHelper.getInstance().getNewTableFrom(nT1);

		StringBuilder query = new StringBuilder();
		query.append("SELECT ");
		query.append(nT11.getParameters());
		query.append(" FROM ( ");

		// Busca todos os bairros que já receberam um pedido
		query.append("SELECT ");
		query.append(nT1.getParameters());
		query.append(" , @num := if( ");

		for (Coluna c : nT1.getColunas()) {

			if (!first) {
				first = !first;
			} else {
				query.append(" AND ");
			}

			query.append("@");
			query.append(c.getNmRealColuna());
			query.append(" = ");
			query.append(c.getNmColuna());

		}

		query.append(" , @num + 1, 1) as rowNumber ");

		for (Coluna c : nT1.getColunas()) {
			query.append(" , @");
			query.append(c.getNmRealColuna());
			query.append(" := ");
			query.append(c.getNmColuna());
		}

		query.append(" FROM ( ");
		query.append(parentLeft);
		query.append(" ) AS ");
		query.append(nT1.getUniqueId());

		query.append(",(SELECT @num := '' ) AS _num ");
		for (Coluna c : nT1.getColunas()) {

			query.append(",(SELECT @");
			query.append(c.getNmRealColuna());
			query.append(" := '' ) AS _");
			query.append(c.getNmColuna());
		}

		query.append(" ORDER BY ");
		query.append(nT1.getParametersWithoutAs());
		query.append(" ) AS ");
		query.append(nT11.getUniqueId());
		query.append(" WHERE NOT EXISTS ( ");

		// Cria uma nova tabela para novo ID
		Tabela nT2 = AliasHelper.getInstance().getNewTableFrom(t2);
		// Sera desconsiderado pelo metodo getLast() do AliasHelper
		nT2.setAutoExclude(true);
		first = false;

		// Cria uma nova tabela para novo ID
		Tabela nT21 = AliasHelper.getInstance().getNewTableFrom(nT2);
		// Sera desconsiderado pelo metodo getLast() do AliasHelper
		nT21.setAutoExclude(true);

		query.append("SELECT ");
		query.append(nT21.getParameters());
		query.append(", rowNumber FROM ( ");

		// Busca todos os bairros que já receberam um pedido
		query.append("SELECT ");
		query.append(nT2.getParameters());
		query.append(" , @num := if( ");

		for (Coluna c : nT2.getColunas()) {

			if (!first) {
				first = !first;
			} else {
				query.append(" AND ");
			}

			query.append("@");
			query.append(c.getNmRealColuna());
			query.append(" = ");
			query.append(c.getNmColuna());

		}

		query.append(" , @num + 1, 1) as rowNumber ");

		for (Coluna c : nT2.getColunas()) {
			query.append(" , @");
			query.append(c.getNmRealColuna());
			query.append(" := ");
			query.append(c.getNmColuna());
		}

		query.append(" FROM ( ");
		query.append(parentRight);
		query.append(" ) AS ");
		query.append(nT2.getUniqueId());

		query.append(",(SELECT @num := '' ) AS _num ");
		for (Coluna c : nT2.getColunas()) {

			query.append(",(SELECT @");
			query.append(c.getNmRealColuna());
			query.append(" := '' ) AS _");
			query.append(c.getNmColuna());
		}

		query.append(" ORDER BY ");
		query.append(nT2.getParametersWithoutAs());
		query.append(" ) AS ");
		query.append(nT21.getUniqueId());

		// Adiciona as tabelas no AliasHelper.
		// A ordem de insert influencia na sequencia dos aliases
		AliasHelper.getInstance().put(nT2);
		AliasHelper.getInstance().put(nT21);
		AliasHelper.getInstance().put(nT1);
		AliasHelper.getInstance().put(nT11);

		query.append(" WHERE ");
		query.append(nT11.getUniqueId());
		query.append(".rowNumber");
		query.append(" = ");
		query.append(nT21.getUniqueId());
		query.append(".rowNumber ");

		for (int i = 0; i < nT11.getColunas().size(); i++) {
			query.append(" AND ");
			query.append(nT11.getColunas().get(i).getUniqueTable() + "."
					+ nT11.getColunas().get(i).getNmColuna());
			query.append(" = ");
			query.append(nT21.getColunas().get(i).getUniqueTable() + "."
					+ nT21.getColunas().get(i).getNmColuna());
			query.append(" ");
		}

		query.append(" ) ");

		return makeQuerySafe(query.toString());
	}

	/**
	 * Altera as clausulas de SELECT, WHERE e GROUP BY das querys para que
	 * outros metodos não as altere atraves das verificações da funcao
	 * "contains"
	 * 
	 * @param query
	 * @return
	 */
	private String makeQuerySafe(String query) {
		return query.replace("SELECT", "@S#ELECT@")
				.replace("WHERE", "@W#HERE@")
				.replace("GROUP BY", "@G#ROUP BY@").replace("ORDER BY",
						"@O#RDER BY@");
	}

	/**
	 * Valida a query retornada do elemento superior
	 * 
	 * @param parentLeft
	 * @throws Exception
	 */
	private void validaQuery(String parentLeft) throws Exception {
		if (parentLeft == null || String.valueOf(parentLeft).equals("null")
				|| String.valueOf(parentLeft).trim().isEmpty()) {
			throw new Exception();
		}
	}
}