/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.amse.vadim.calc;

import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import javax.swing.*;

/**
 *
 * @author chibis
 */
public class View extends JFrame {

    private int width = 400;
    private int heigth = 300;
    java.util.ArrayList<String> hist = new ArrayList<String>();
    int i = 0;
    protected JTextField input;
    protected JTextArea history;
    private final static String newline = "\n";

    public View() {
	super("CALCULATOR");
	setSize(this.width, this.heigth);
	setDefaultCloseOperation(EXIT_ON_CLOSE);

	this.setLayout(new GridBagLayout());

	input = new JTextField();
	input.addActionListener(new entered());
	input.addKeyListener(new keyListener());

	history = new JTextArea();
	history.setEditable(false);
	JScrollPane scrollPane = new JScrollPane(history);

	//Add Components to this panel.
	GridBagConstraints c = new GridBagConstraints();
	c.gridwidth = GridBagConstraints.REMAINDER;

	double buffx = c.weightx;
	double buffy = c.weighty;



	c.fill = GridBagConstraints.BOTH;
	c.weightx = 1.0;
	c.weighty = 1.0;
	add(scrollPane, c);

	c.fill = GridBagConstraints.HORIZONTAL;
	c.weightx = buffx;
	c.weighty = buffy;
	add(input, c);

	setVisible(true);
    }

    public class entered implements ActionListener {

	public void actionPerformed(ActionEvent e) {
	    history.append(input.getText() + newline);
	    hist.add(input.getText());
	    i = hist.size();
	    input.setText("");
	}
    }

    public class keyListener implements KeyListener {

	public void keyTyped(KeyEvent e) {
	}

	public void keyPressed(KeyEvent e) {

	    if (e.getKeyCode() == KeyEvent.VK_DOWN) {
		if (hist.size() == 0) {
		    return;
		}
		if (i == 0) {
		    i = hist.size();
		}
		i -= 1;
		input.setText(hist.get(i));

	    }

	    if (e.getKeyCode() == KeyEvent.VK_UP) {
		if (hist.size() == 0) {
		    return;
		}
		if (i == hist.size() - 1 || i == hist.size()) {
		    i = -1;
		}
		i += 1;
		input.setText(hist.get(i));

	    }

	}

	public void keyReleased(KeyEvent e) {
	}
    }
}
