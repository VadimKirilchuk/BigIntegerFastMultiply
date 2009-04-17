package org.amse.vadim.interpretator;

/**
 * ������� - �������������.
 * 
 * �����: ��������� ���������.
 * ���:   2008
 */
public class IdLexema extends Lexema {
  // ��� �������������
  public final String name;
  
  // ����������� �������
  public IdLexema(String name) {
    super(Lexema.Type.IDENT);
    this.name = name;
  }
}
