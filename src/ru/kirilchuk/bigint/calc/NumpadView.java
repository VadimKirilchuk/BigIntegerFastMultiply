/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.kirilchuk.bigint.calc;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import java.util.Random;

/**
 *
 * @author chibis
 */
public class NumpadView extends JFrame {

    private int width = 300;
    private int heigth = 250;
    private JPanel p;
    private View calc;
    private JSpinner spinner;

    public NumpadView(final View calc) {
	super("NUMPAD");
	this.calc=calc;
	this.addComponentListener(new ComponentListener() {

	    public void componentResized(ComponentEvent e) {
		//throw new UnsupportedOperationException("Not supported yet.");
	    }

	    public void componentMoved(ComponentEvent e) {
		//throw new UnsupportedOperationException("Not supported yet.");
	    }

	    public void componentShown(ComponentEvent e) {
		calc.numpad.setText("Close Numpad");
		//throw new UnsupportedOperationException("Not supported yet.");
	    }

	    public void componentHidden(ComponentEvent e) {
		calc.numpad.setText("Open Numpad");
		//throw new UnsupportedOperationException("Not supported yet.");
	    }
	});
	setMinimumSize(new Dimension(285, 200));
	setSize(this.width, this.heigth);
	setDefaultCloseOperation(HIDE_ON_CLOSE);
	
	setLayout(new BorderLayout(10, 5));

	p = new JPanel(new GridLayout(5, 4, 5, 5));

	JButton[] buttons = new JButton[19];
	buttons[0] = new JButton("0");
	buttons[1] = new JButton("1");
	buttons[2] = new JButton("2");
	buttons[3] = new JButton("3");
	buttons[4] = new JButton("4");
	buttons[5] = new JButton("5");
	buttons[6] = new JButton("6");
	buttons[7] = new JButton("7");
	buttons[8] = new JButton("8");
	buttons[9] = new JButton("9");
	
	buttons[10] = new JButton("<-");
	buttons[11] = new JButton("/");
	buttons[12] = new JButton("*");
	buttons[13] = new JButton("+");
	buttons[14] = new JButton("-");
	buttons[15] = new JButton("cl");
	
	buttons[16] = new JButton("(");
	buttons[17] = new JButton(")");
	buttons[18] = new JButton("=");

	for (int i = 0; i < buttons.length; i++) {
	    buttons[i].addActionListener(new NumpadPressed());
	}
	
	p.add(buttons[7]);
	p.add(buttons[8]);
	p.add(buttons[9]);
	p.add(buttons[11]);
	p.add(buttons[4]);
	p.add(buttons[5]);
	p.add(buttons[6]);
	p.add(buttons[12]);
	p.add(buttons[1]);
	p.add(buttons[2]);
	p.add(buttons[3]);
	p.add(buttons[14]);
	p.add(buttons[0]);
	p.add(buttons[10]);
	p.add(buttons[15]);
	p.add(buttons[13]);
	
	p.add(buttons[16]);
	p.add(buttons[17]);
	p.add(buttons[18]);

	add(p, BorderLayout.CENTER);

	Integer value = new Integer(50);
	Integer min = new Integer(1);
	Integer max = new Integer(Integer.MAX_VALUE);
	Integer step = new Integer(1);
	SpinnerNumberModel model = new SpinnerNumberModel(value, min, max, step);
	this.spinner = new JSpinner(model);
	
	JButton gen = new JButton("generate");
	gen.addActionListener(new GenerateBtnPressed());
	
	JPanel southPanel = new JPanel(new FlowLayout() );
	southPanel.add(spinner);
	southPanel.add(gen);
	
	add(southPanel, BorderLayout.SOUTH);
    }
    
    public class GenerateBtnPressed implements ActionListener{

	public void actionPerformed(ActionEvent e) {
	    Random rnd = new Random();
	    
	    Integer end = (Integer)spinner.getValue();
	    StringBuilder str= new StringBuilder();
	    for (int i = 0; i < end; i++) {
		str.append(rnd.nextInt(10));
	    }
	    
	    calc.input.setText(calc.input.getText()+str.toString());
	}
	
    }
    
    public class NumpadPressed implements ActionListener{

	public void actionPerformed(ActionEvent e) {
	    JButton btn = (JButton)e.getSource();
	    
	    if ( (!btn.getText().equals("<-")) && !(btn.getText().equals("cl")) && !(btn.getText().equals("="))){
		calc.input.setText(calc.input.getText()+btn.getText());
	    }else if (btn.getText().equals("<-")){
		if(calc.input.getText().length()!=0){
		StringBuilder strb = new StringBuilder(calc.input.getText());
		strb.deleteCharAt(strb.length()-1);
		calc.input.setText(strb.toString());
		}
	    } else if (btn.getText().equals("=")) {
		calc.input.getActionListeners()[0].actionPerformed(new ActionEvent(calc.input, 0, ""));		
	    } else {
		calc.input.setText("");
	    }
	}
	
    }
}
