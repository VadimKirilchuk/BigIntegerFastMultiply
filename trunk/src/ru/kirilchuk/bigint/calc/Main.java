package ru.kirilchuk.bigint.calc;

import javax.swing.SwingUtilities;

/**
 * Main class.
 *
 * @author Kirilchuk V.E.
 */
public class Main {
    public static void main(String[] args) {
	SwingUtilities.invokeLater(new Runnable() {

            public void run() {
                new CalcGUI();
            }
        });
    }
}
