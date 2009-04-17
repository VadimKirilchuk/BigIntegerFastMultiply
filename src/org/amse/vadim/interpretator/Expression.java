package org.amse.vadim.interpretator;

import java.util.Map;

/**
 * В этом файле описывается интерфейс для представления и обработки выражений,
 * представленных синтаксическим деревом.
 *
 * Автор: Александр Кубенский
 * Дата: 6 октября 2008
 */

public interface Expression extends Iterable<Variable> {
  // Вычисление выражения, являющегося "производной" исходного по заданной переменной.
  Expression dash(Variable v);

  // Вычисление значения выражения в заданном контексте значений переменных.
  int evaluate(Map<Variable, Constant> context);
}