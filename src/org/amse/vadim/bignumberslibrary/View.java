/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.amse.vadim.bignumberslibrary;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import javax.swing.*;
/**
 *
 * @author chibis
 */
public class View extends JFrame{

    private int width=400;
    private int heigth=300;
    
    public View(){
	super("CALCULATOR");
        setSize(this.width, this.heigth);
	setDefaultCloseOperation(EXIT_ON_CLOSE);
	
	Container container = getContentPane();
	
	JTextArea input = new JTextArea(">>");
	input.setSize(this.width, this.heigth);
	input.addKeyListener(new inputKeyListener());
	
	Container c = new Container();
	c.setLayout(new GridLayout());
	
	c.add(input);
	container.add(c);
	
	
	setVisible(true);
    }
    
    public class inputKeyListener implements KeyListener{

	public void keyTyped(KeyEvent e) {
	    JTextArea input=(JTextArea)e.getComponent();
	    //input.setText();
	}

	public void keyPressed(KeyEvent e) {
	    throw new UnsupportedOperationException("Not supported yet.");
	}

	public void keyReleased(KeyEvent e) {
	    throw new UnsupportedOperationException("Not supported yet.");
	}
	
    }  
}
