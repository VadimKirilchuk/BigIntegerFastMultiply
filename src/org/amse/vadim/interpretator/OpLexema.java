package org.amse.vadim.interpretator;

/**
 * ������� - ���� ��������.
 * 
 * �����: ��������� ���������.
 * ���:   2008
 */
public class OpLexema extends Lexema {
  // ���� ��������.
  public final String operator;
  // ��������� ��������
  public final int prio;

  // �����������
  public OpLexema(String op, int prio) {
    super(Lexema.Type.OPERATOR);
    operator = op;
    this.prio = prio;
  }
}
