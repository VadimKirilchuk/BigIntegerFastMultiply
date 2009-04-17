package org.amse.vadim.interpretator;

/**
 * В этом файле описывается класс для представления переменной,
 * являющейся частным случаем выражения.
 *
 * Автор: Александр Кубенский
 * Дата: 6 октября 2008
 */

import java.util.Iterator;
import java.util.Map;

public class Variable implements Expression {
  // Имя переменной
  private final String name;

  // Конструктор
  public Variable(String name) { this.name = name; }
  // Функция доступа к имени переменной
  public String getName() { return name; }

  // Реализация итератора:
  // в случае переменной выражение содержит только одну эту переменную
  private static class VarIterator implements Iterator<Variable> {
    private Variable v;

    // Конструктор запоминает итерируемую переменную
    public VarIterator(Variable v) { this.v = v; }

    // Реализация методов итератора
    public boolean hasNext() { return v != null; }
    public Variable next() {
      Variable saveV = v;
      v = null;
      return saveV;
    }
    // Метод remove не поддерживается в дереве.
    public void remove() { throw new UnsupportedOperationException(); }
  }


  // Переопределение методов класса Object
  public boolean equals(Object o) {
    return (o instanceof Variable) && name.equals(((Variable)o).name);
  }
    @Override
  public int hashCode() { return name.hashCode(); }
    @Override
  public String toString() { return name; }

  // Реализация метода iterator интерфейса Iterable
  public Iterator<Variable> iterator() { return new VarIterator(this); }

  // Реализация методов интерфейса Expression
  public Expression dash(Variable v) {
    return v.equals(this) ? Constant.UNIT : Constant.ZERO;
  }

  public int evaluate(Map<Variable, Constant> context) {
    return context.get(this).getValue();
  }
}