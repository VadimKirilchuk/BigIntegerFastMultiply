package org.amse.vadim.interpretator;

/**
 * � ���� ����� ����������� ����� ��� ������������� �������� ��������,
 * ���������� ������� ������� ���������. �������, ��� �������� ������
 * ��� ��������: �������� (+), ��������� (*) � ��������� (-).
 *
 * �����: ��������� ���������
 * ����: 6 ������� 2008
 */

import java.util.Iterator;
import java.util.Map;

public class Binary implements Expression{
  private final Expression left, right;
  private final String operator;  // "+", "-" ��� "*"

  // �����������
  public Binary(Expression left, String operator, Expression right) {
    this.left = left; this.operator = operator; this.right = right;
  }
  // ������� �������
  public Expression getLeft() { return left; }
  public Expression getRight() { return right; }
  public String getOperator() { return operator; }

  // ��������������� ������� ������ Object
    @Override
  public boolean equals(Object o) {
    // ����� ������� ��������� ������������, ���� � ��� ���������
    // ��� �������� � ����� ��������.
    if (!(o instanceof Binary)) return false;
    Binary b = (Binary)o;
    return operator.equals(b.operator) &&
        left.equals(b.left) && right.equals(b.right);
  }
    @Override
  public int hashCode() {
    return 19*left.hashCode() + 23*right.hashCode() + 37*operator.hashCode();
  }
    @Override
  public String toString() {
    return "(" + left + operator + right + ")";
  }

  // ���������� ������ iterator ���������� Iterable
  public Iterator<Variable> iterator() {
    return new PairIterator<Variable>(left.iterator(), right.iterator());
  }

  // ���������� ������� ���������� Expression
  public Expression dash(Variable v) {
    // ����������� ������ � ������� ���������
    Expression leftDash = left.dash(v);
    Expression rightDash = right.dash(v);

    if (operator.equals("+") || operator.equals("-")) {
      return new Binary(leftDash, operator, rightDash);
    } else if (operator.equals("*")) {
      return new Binary(
          new Binary(leftDash, "*", right),
          "+",
          new Binary(left, "*", rightDash)
      );
    }
    // ������������ ������ �������� +, -, *.
    throw new IllegalArgumentException();
  }

  public int evaluate(Map<Variable, Constant> context) {
    // �������� ������ � ������� ���������
    int leftVal = left.evaluate(context);
    int rightVal = right.evaluate(context);

    if (operator.equals("+")) {
      return leftVal + rightVal;
    } else if (operator.equals("-")) {
      return leftVal - rightVal;
    } else if (operator.equals("*")) {
      return leftVal * rightVal;
    }
    // ������������ ������ �������� +, -, *.
    throw new IllegalArgumentException();
  }
}
