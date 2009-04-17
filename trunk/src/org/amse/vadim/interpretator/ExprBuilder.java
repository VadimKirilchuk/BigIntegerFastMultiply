package org.amse.vadim.interpretator;


import java.text.ParseException;
import java.util.Stack;

/**
 * ����� ��� ���������� ��������� � ������� ��������
 * ������������ �����������.
 * 
 * @author ��������� ���������
 *
 */
public abstract class ExprBuilder {

  /**
   * enumerator ��� ������� ����� �������� ��������/���������.
   * 
   * @author Akoub
   */
  private static class Waiting {
    private Waiting() {}
    public static final Waiting WAIT_OPERAND = new Waiting();
    public static final Waiting WAIT_OPERATOR = new Waiting();
  
    // ��� ������� �������� �������� ��������� ������ ��������
    public void testOperand() throws ParseException {
      if (this != WAIT_OPERAND) {
        throw new ParseException("�������� �������", 0);
      }
    }
  
    public void testOperator() throws ParseException {
      if (this != WAIT_OPERATOR) {
        throw new ParseException("�������� ��������", 0);
      }
    }
  }

  // ��������� ������ ��������� �� �������� ������ ��������.
  public static Expression generate(String s) throws ParseException {
    // ����������� ����������, ������������ �������.
    LexAnalyzer la = new LexAnalyzer(s);
    // ���� ��������� - ��� ����������� ������ ���������.
    Stack<Expression> operands = new Stack<Expression>();
    // ���� ���������� - ������ �������� � ���������� ������.
    Stack<OpLexema> operators = new Stack<OpLexema>();
    // ���� �������� ��������/���������
    Waiting wait = Waiting.WAIT_OPERAND;

    // ��������� �������.
    for (Lexema lex = la.nextLex(); lex != Lexema.EOTEXT; lex = la.nextLex()) {
      if (lex == Lexema.LEFTPAR) {
        wait.testOperand();
        // ����� ������ ���������� � ����
        operators.push((OpLexema)lex);
      } else if (lex == Lexema.RIGHTPAR) {
        wait.testOperator();
        // ������ ������ �������� "����������" �������� ��
        // ����� �������� �� ����������� ������
        doExpressions(operands, operators, 1);
        if (operators.empty() || operators.peek() != Lexema.LEFTPAR) {
          throw new ParseException("������� ������ ������", 0);
        } else {
          operators.pop();
        }
      } else if (lex.type == Lexema.Type.IDENT) {
        wait.testOperand();
        // ������������� ������������ � ���� ���������
        operands.push(new Variable(((IdLexema)lex).name));
        wait = Waiting.WAIT_OPERATOR;
      } else if (lex.type == Lexema.Type.NUMBER) {
        wait.testOperand();
        // ��������� ������������ � ���� ���������
        operands.push(new Constant(((NumLexema)lex).value));
        wait = Waiting.WAIT_OPERATOR;
      } else if (lex.type == Lexema.Type.OPERATOR) {
        wait.testOperator();
        // ��������� ��� �������� � ����� �� ��� ����� ������� �����������
        doExpressions(operands, operators, ((OpLexema)lex).prio);
        // ���������� �������� � ���� �������� 
        operators.push((OpLexema)lex);
        wait = Waiting.WAIT_OPERAND;
      } else if (lex == Lexema.UNKNOWN) {
        throw new ParseException("����������� �������", 0);
      }
    }

    // ��������� ���������
    wait.testOperator();
    // ��������� ��� ������������� ��������
    doExpressions(operands, operators, 1);
    // ���������, ��� � ����� ��� ���������� ������
    if (!operators.empty()) {
      throw new ParseException("����������� ����� ���������", 0);
    }
    // ���������, ��� � ����� ���� �������������� �������
    if (operands.empty()) {
      throw new ParseException("������������ ���������", 0);
    }
    Expression result = operands.pop();
    // ���������, ��� ��������� - ������������
    if (!operands.empty()) {
      throw new ParseException("������������� ���������", 0);
    }
    return result;
  }

  private static void doExpressions(
      Stack<Expression> operands, 
      Stack<OpLexema> operators, int prio) throws ParseException {
    while (!operators.empty() && operators.peek().prio >= prio) {
      OpLexema nextOp = operators.pop();
      if (operands.empty()) {
        throw new ParseException("������������ ���������", 0);
      }
      Expression op2 = operands.pop();
      if (operands.empty()) {
        throw new ParseException("������������ ���������", 0);
      }
      Expression op1 = operands.pop();
      operands.push(new Binary(op1, nextOp.operator, op2));
    }
  }
}
