package org.amse.vadim.interpretator;

/**
 * В этом файле описывается класс для представления константы,
 * являющейся частным случаем выражения. Считаем, что возможны только
 * целочисленные константы.
 *
 * Автор: Александр Кубенский
 * Дата: 6 октября 2008
 */

import java.util.Iterator;
import java.util.Map;

public class Constant implements Expression, Comparable<Constant> {
  private final int value;

  // Две часто встречающиеся константы.
  public final static Constant ZERO = new Constant(0);
  public final static Constant UNIT = new Constant(1);

  // Конструктор
  public Constant(int value) { this.value = value; }
  // Функция доступа к значению
  public int getValue() { return value; }

  // Реализация итератора константы - пустой итератор
  private static class ConstIterator implements Iterator<Variable> {
    public boolean hasNext() { return false; }
    public Variable next() { return null; }
    public void remove() {
      throw new UnsupportedOperationException();
    }
  }

  // Переопределение методов класса Object
  public boolean equals(Object o) {
    return (o instanceof Constant) && value == ((Constant)o).value;
  }
  public int hashCode() { return (value + 29) * 37; }
  public String toString() { return ((Integer)value).toString(); }

  // Реализация метода интерфейса Comparable
  public int compareTo(Constant c) { return value - c.value; }

  // Реализация метода интерфейса Iterable
  public Iterator<Variable> iterator() { return new ConstIterator(); }

  // Реализация методов интерфейса Expression
  public Expression dash(Variable v) {
    return ZERO;
  }

  public int evaluate(Map<Variable, Constant> context) {
    return value;
  }
}