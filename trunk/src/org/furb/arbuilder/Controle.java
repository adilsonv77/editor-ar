package org.furb.arbuilder;

import javax.swing.UIManager;

import org.furb.arbuilder.ui.Interface;

/**
 *
 * @author Jonathan Hess, André R. Sousa
 */
public class Controle {
    
    private static Controle controle = new Controle();      
    private DAO dao = new DAO();
    private Digrafo estruturaDigrafo = new Digrafo();
    
    public final void init() {
    	
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ex) {
        	ex.printStackTrace();
        } finally {
        	Controle.rodar();
        }
    	
    }
    
    public final Digrafo getEstruturaDigrafo() {
        return this.estruturaDigrafo;
    }
    
    public static void rodar() {
        new Interface(controle).setVisible(true);
    }
    
    public final DAO getDAO() {
        return this.dao;
    }
}
