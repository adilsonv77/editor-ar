package org.furb.arbuilder.ui.customTable;

import java.awt.Color;
import java.awt.Component;

import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;

public class ArBuilderCellRenderer extends DefaultTableCellRenderer {

	private static final long serialVersionUID = -1340426525069628432L;
	
	private Color whiteColor		= new Color(229, 229, 229);
	private Color alternateColor	= new Color(213, 213, 213);

	@Override
	public final Component getTableCellRendererComponent( JTable table , Object value , boolean selected , boolean focused , int row , int column ) 
	{
		if( String.valueOf(value).equals("null") || String.valueOf(value).isEmpty() ) {
			super.getTableCellRendererComponent( table , "(NULL)" , selected , focused , row , column );
		} else {
			super.getTableCellRendererComponent( table , value , selected , focused , row , column );
		}

		Color bg = getBackgroundColor(table, row, column, selected);
		super.setBackground(bg);
		return this;
	}
	
	/**
	 * Faz a alteração da cor das colunas. Efeito zebrado
	 * @param table
	 * @param row
	 * @param column
	 * @param selected
	 * @return
	 */
	private Color getBackgroundColor( JTable table , int row, int column , boolean selected ) 
	{
		Color bg = null;
		
		if (!selected) {
			bg = (row % 2 == 0 ? alternateColor : whiteColor);
		} 
		else {
			bg = (row % 2 == 0 ? alternateColor : whiteColor);
		}

		if (row == table.getSelectedRow() && column == table.getSelectedColumn()) {
			bg = new Color(255,204,153);
		}
		
		return bg;
	}
}
