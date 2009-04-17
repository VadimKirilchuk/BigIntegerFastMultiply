package org.amse.vadim.interpretator;

/**
 * ������� ��� ������� ���������.
 * 
 * �����: ��������� ���������.
 * ���:   2008
 */
public class Lexema {
  // ��� �������. ���������� ���� ������������ ����� ����� ������,
  // ������������� ����. ������ ����� ��������� enum.
  public static class Type {
    // �������� ����� ������.
    public static final Type IDENT = new Type();
    public static final Type NUMBER = new Type();
    public static final Type OPERATOR = new Type();
    public static final Type EOTEXT = new Type();
    public static final Type UNKNOWN = new Type();

    private Type() {}
  }

  // ��� �������
  public final Type type;

  // ��������� ����� ������������� �������.
  public static final Lexema LEFTPAR = new OpLexema("(", 0);
  public static final Lexema RIGHTPAR = new OpLexema(")", 0);
  public static final Lexema EOTEXT = new Lexema(Type.EOTEXT);
  public static final Lexema UNKNOWN = new Lexema(Type.EOTEXT);

  // ������� ������� � ���� �������.
  public Lexema(Type tp) {
    type = tp;
  }
}

