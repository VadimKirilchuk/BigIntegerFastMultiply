package org.amse.vadim.interpretator;

import java.text.ParseException;
import java.util.Stack;

/**
 * Class for building expression
 * by simple Lexical analyzator
 *
 */
public abstract class ExprBuilder {

    /**
     * enumerator for making flag of what we are waiting
     */
    private static class Waiting {

	private Waiting() {
	}
	public static final Waiting WAIT_OPERAND = new Waiting();
	public static final Waiting WAIT_OPERATOR = new Waiting();

	public void testOperand() throws ParseException {
	    if (this != WAIT_OPERAND) {
		throw new ParseException("operand was waited", 0);
	    }
	}

	public void testOperator() throws ParseException {
	    if (this != WAIT_OPERATOR) {
		throw new ParseException("operator was waited", 0);
	    }
	}
    }

    // Generating Expression Tree from String
    public static Expression generate(String s) throws ParseException {
	// creating lexical analyzator
	LexAnalyzer la = new LexAnalyzer(s);
	// Stack of operands - ready parts of expression
	Stack<Expression> operands = new Stack<Expression>();
	// Stack of operators - operations and open brackets
	Stack<OpLexema> operators = new Stack<OpLexema>();
	// flag of waiting. First we are waiting operator!
	Waiting wait = Waiting.WAIT_OPERAND;

	// Getting next lexema
	for (Lexema lex = la.nextLex(); lex != Lexema.EOTEXT; lex = la.nextLex()) {
	    if (lex == Lexema.LEFTPAR) {
		//if we were waiting operand
		wait.testOperand();
		// putting bracket in stack
		operators.push((OpLexema) lex);
	    } else if (lex == Lexema.RIGHTPAR) {
		//if we were waiting operator
		wait.testOperator();
		// calling making of operations
		// until open bracket
		doExpressions(operands, operators, 1);

		if (operators.empty() || operators.peek() != Lexema.LEFTPAR) {
		    throw new ParseException("wrong balance of brackets", 0);
		} else {
		    operators.pop();
		}
	    } else if (lex.type == Lexema.Type.IDENT) {
		//if we were waiting operand
		wait.testOperand();
		// adding identificator(variable) to stack
		operands.push(new Variable(((IdLexema) lex).name));
		//changing wait state
		wait = Waiting.WAIT_OPERATOR;
	    } else if (lex.type == Lexema.Type.NUMBER) {
		//if we were waiting operand
		wait.testOperand();
		// adding number(constant) to stack
		operands.push(new Constant(((NumLexema) lex).value));
		//changing wait state
		wait = Waiting.WAIT_OPERATOR;
	    } else if (lex.type == Lexema.Type.OPERATOR) {
		//if we were waiting operator
		wait.testOperator();
		// doing all previous operations with same or higher peiority
		doExpressions(operands, operators, ((OpLexema) lex).prio);
		// adding operation to stack 
		operators.push((OpLexema) lex);
		//changing wait state
		wait = Waiting.WAIT_OPERAND;
	    } else if (lex == Lexema.UNKNOWN) {
		throw new ParseException("Unknown lexema", 0);
	    }
	}

	// Expression is ended
	wait.testOperator();
	// doing all unddid operations 
	doExpressions(operands, operators, 1);
	// checking brackets
	if (!operators.empty()) {
	    throw new ParseException("wrong balance of brackets or wrong end of expression", 0);
	}
	// in stack must be operand which we have made
	if (operands.empty()) {
	    throw new ParseException("Error. Not enough operands", 0);
	}
	//result of our expression
	Expression result = operands.pop();
	// the stack must be empty now
	if (!operands.empty()) {
	    throw new ParseException("Expression not correct", 0);
	}
	return result;
    }

    private static void doExpressions(
	    Stack<Expression> operands,
	    Stack<OpLexema> operators, int prio) throws ParseException {
	while (!operators.empty() && operators.peek().prio >= prio) {
	    OpLexema nextOp = operators.pop();
	    if (operands.empty()) {
		throw new ParseException("Not enough operands", 0);
	    }
	    Expression op2 = operands.pop();
	    if (operands.empty()) {
		throw new ParseException("Not enough operands", 0);
	    }
	    //???? what if operands is empty???
	    Expression op1 = operands.pop();
	    operands.push(new Binary(op1, nextOp.operator, op2));
	}
    }
}
