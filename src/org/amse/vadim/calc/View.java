/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.amse.vadim.calc;

import java.text.ParseException;
import org.amse.vadim.interpretator.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;

/**
 * @author chibis
 */
public class View extends JFrame {

    private int width = 400;
    private int heigth = 300;
    ArrayList<String> hist = new ArrayList<String>();
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
        input.addActionListener(new Entered());
        input.addKeyListener(new KeyAction());

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

    public class Entered implements ActionListener {
    
        public void actionPerformed(ActionEvent e) {
            String text = input.getText();
	    history.append(text + newline);
            hist.add(text);
            i = hist.size();
            input.setText("");
	    
	    		//////////////////interpretator////////////
	    try {
		Expression expression = ExprBuilder.generate(text);
		Integer res = expression.evaluate(null);
		input.setText(res.toString());
		
	    } catch (ParseException ex) {
		input.setText("PARSE ERROR");
	    }
	    
        }
    }

    public class KeyAction implements KeyListener {

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
