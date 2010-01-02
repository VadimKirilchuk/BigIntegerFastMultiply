/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.amse.vadim.calc;

import java.text.ParseException;
import javax.swing.event.ListSelectionEvent;
import org.amse.vadim.interpretator.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.Map;
import java.util.HashMap;
import javax.swing.event.ListSelectionListener;
import org.amse.vadim.bignumberslibrary.BigNumber;
import org.amse.vadim.interpretator.Variable;

/**
 * @author chibis
 */
public class View extends JFrame {

    private int width = 800;
    private int heigth = 400;
    
    int i = 0;
    public  JTextField input;    
    private Map<Variable, Constant> map = new HashMap<Variable, Constant>();
    
    private DefaultListModel model = new DefaultListModel();    
    private JList history;
    private JButton clearHistory;
    public  JButton numpad;
    private NumpadView numView;

    public View() {
        super("CALCULATOR");
        setSize(this.width, this.heigth);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        input = new JTextField();
        input.addActionListener(new Entered());
        input.addKeyListener(new KeyAction());
	
	numView = new NumpadView(this);
	
        history = new JList(model); 
	history.addListSelectionListener(new histSelect());
        JScrollPane scrollPane = new JScrollPane(history);
	scrollPane.setAutoscrolls(true);	
        
	JPanel buttonsPanel = new JPanel(new FlowLayout());
        
	clearHistory = new JButton("Clear hist");
	clearHistory.addActionListener(new Clear());
	
	numpad = new JButton("Open Numpad");
	numpad.addActionListener(new Numpad());
	
	buttonsPanel.add(clearHistory);
	buttonsPanel.add(numpad);
	
	this.setLayout(new BorderLayout());
	this.add(scrollPane, BorderLayout.CENTER);
	this.add(input, BorderLayout.SOUTH);
        this.add(buttonsPanel,BorderLayout.NORTH);
	
        setVisible(true);
    }

    public class Entered implements ActionListener {
    
	public void actionPerformed(ActionEvent e) {	    
	    String text = input.getText();
	    String idName = null;
	    
	    StringBuilder res=new StringBuilder();
	    
	    int eqPos =  text.indexOf('=');
	    String buff;
	    try {
		
		if (eqPos != -1) {
		    String in = text.substring(0, eqPos);
		    //получили имя идентификатора
		    //или Parse Exception
		    idName = getIdName(in);
		    //после = должно быть выражение
		    text = text.substring(eqPos + 1, text.length());
		}
		//обрабатываем выражение
		Expression expression = ExprBuilder.generate(text);
		//вычисляем выражение в заданном контексте
		BigNumber value = expression.evaluate(map);
		buff = value.toStr();
		//если это было выражение типа Identificator = Expression
		//запоминаем значение переменной в словаре!
		if (idName!=null){
		    map.put(new Variable(idName), new Constant(value));
		    //добавляем имя переменной в начало результата
		    res.append(idName+" := ");
		} else {//иначе добавляем к результату выражение
		    res.append(text + " = ");
		}
	        //добавляем к результату значение выраения	
		res.append(buff);
		//записываем в историю
		buff=res.toString();
		model.add(model.size(), buff);
		i = model.size();
		history.ensureIndexIsVisible(model.size() - 1);
		input.setText("");	
	    }catch(Exception ex){
		buff = input.getText();		
		model.add(model.size(),"! "+ buff);
		i = model.size();
		history.ensureIndexIsVisible(model.size() - 1);
		input.setText(ex.getMessage());
	    }	    	    	    	    	    	    	    			
	        
	}
    }
    
    public class Clear implements ActionListener{

	public void actionPerformed(ActionEvent e) {
	    
	    model.clear();	    
	    i = 0;
	}
	
    }

    public class Numpad implements ActionListener {

	public void actionPerformed(ActionEvent e) {
	    if(numView.isVisible()){
		numView.setVisible(false);
		numpad.setText("Open Numpad");
	    }else{
		numView.setVisible(true);
		numpad.setText("Close Numpad");
	    }
	}
    }

    public class KeyAction implements KeyListener {

        public void keyTyped(KeyEvent e) {
        }

        public void keyPressed(KeyEvent e) {

            if (e.getKeyCode() == KeyEvent.VK_DOWN) {
                //if (hist.size() == 0) {
                if (model.size() == 0) {
		    return;
                }
                if (i == 0) {
                    //i = hist.size();
		    i = model.size();
                }
                i -= 1;
		input.setText((String)model.get(i));

            }

            if (e.getKeyCode() == KeyEvent.VK_UP) {
		if (model.size() == 0) {
                    return;
                }
		if (i == model.size() - 1 || i == model.size()) {
                    i = -1;
                }
                i += 1;
                input.setText((String)model.get(i));
            }

        }

        public void keyReleased(KeyEvent e) {
        }
    }
    
    public class histSelect implements ListSelectionListener{

	public void valueChanged(ListSelectionEvent e) {
	    int selectedIndex = history.getSelectedIndex();
	    if (selectedIndex != -1) {
		//Рассмотрим три типа выражений
		//1) ! -------
		//2) Expression = value
		//3) Identificator := value 		
		String str = (String) model.getElementAt(selectedIndex);
	        String res="";
		if(str.charAt(0)=='!'){
		    res = str.substring(1,str.length());
		}else{
		    int eqPos = str.indexOf('=');		  		    
		   
		    if (str.charAt(eqPos - 1) == ':') {
			res = str.substring(0,eqPos-1);
		    }else{
			res = str.substring(eqPos+1,str.length());
		    }
		}
		input.setText(res);
		history.clearSelection();	    
	    }
	}
	
    }
    //функция получающая имя идентификатора без пробелов
    //на вход - подстрока до символа :
    private static String getIdName(String in) throws ParseException{
	LexAnalyzer la = new LexAnalyzer(in);
	//Должно быть две лексемы 
	//1)Identificator 2)EOTEXT
	Lexema lex = la.nextLex();
	if (lex.type!=Lexema.Type.IDENT){
	    throw new ParseException("Wrong Identificator in left side",0);
	} else {
	    String name = ((IdLexema)lex).name;
	    lex = la.nextLex();
	    if(lex!=Lexema.EOTEXT){
		throw new ParseException("Wrong Identificator in left side",0);
	    }else{
		return name;
	    }
	}
	
    }
}
