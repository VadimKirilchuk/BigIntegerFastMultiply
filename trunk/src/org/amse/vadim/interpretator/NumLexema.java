package org.amse.vadim.interpretator;

/**
 * ������� - ����� �����.
 * 
 * �����: ��������� ���������.
 * ���:   2008
 */
public class NumLexema extends Lexema {
  // �������� �����.
  public final int value;

  // �����������
  public NumLexema(int v) {
    super(Lexema.Type.NUMBER);
    value = v;
  }
}
