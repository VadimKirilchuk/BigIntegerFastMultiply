package org.amse.vadim.interpretator;

import java.io.Reader;
import java.io.StringReader;
import java.io.IOException;

/**
 * ����������� ���������� ��� ������� ���������.
 * 
 * �����: ��������� ���������.
 * ���:   2008
 */
public class LexAnalyzer {
  // ������� ����� ��������.
  private Reader is;
  // ��������� ������.
  private char nextChar;

  // ����������� �� ���� ��� ������������� ������ ��������.
  public LexAnalyzer(Reader is) {
    this.is = is;
    getNext();
  }

  // ����������� �� ������ ��������.
  public LexAnalyzer(String s) {
    this(new StringReader(s));
  }

  // ������ ���������� ������� �� ������
  private void getNext() {
    try {
      int nextChar = is.read();
      if (nextChar == -1) {
        // ����� �������� ������.
        this.nextChar = 0;
      } else {
        this.nextChar = (char)nextChar;
      }
    } catch (IOException x) {
      // ������ ��� ������ �� ������.
      this.nextChar = 0;
    }
  }

  // ���������� ������ �������.
  private void skip() {
    while (Character.isWhitespace(nextChar)) getNext();
  }

  /**
   * ������ � ������ ��������� �������.
   * ������� ������������, ��� ������ ������ ������� ���
   * ������ �� �������� ������.
   * ������� ������������ �� ������, ��������� ���������
   * ������� � ���������������, �������� ������ �������,
   * �� ����������� � ������.
   * 
   * @return ��������� ������� �� �������� ������ ��� EOTEXT,
   *     ���� ��������� ������� �����������. 
   */
  public Lexema nextLex() {
    // ������� �������� ������.
    skip();
    switch (nextChar) {
      case 0:  // ����� ������ ��� ������ ������.
        return Lexema.EOTEXT;
      case '(':
        getNext();
        return Lexema.LEFTPAR;
      case ')':
        getNext();
        return Lexema.RIGHTPAR;
      case '+':
      case '-':
        Lexema l = new OpLexema(Character.toString(nextChar), 1);
        getNext();
        return l;
      case '*':
        getNext();
        return new OpLexema(Character.toString('*'), 2);
      default:
        if (Character.isDigit(nextChar)) {
          // ������ �����
          int n = 0;
          do {
            n *= 10;
            n += (nextChar - '0');
            getNext();
          } while (Character.isDigit(nextChar));
          return new NumLexema(n);
        } else if (Character.isLetter(nextChar)) {
          // ������ ��������������
          StringBuffer bs = new StringBuffer();
          do {
            bs.append(Character.toString(nextChar));
            getNext();
          } while (Character.isLetterOrDigit(nextChar));
          return new IdLexema(bs.toString());
        } else {
          return Lexema.UNKNOWN;
        }
    }
  }
}
