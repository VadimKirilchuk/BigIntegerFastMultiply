package org.amse.vadim.interpretator;

import java.util.Map;
import org.amse.vadim.bignumberslibrary.BigNumber;

/**
 * interface for representation and operations over Expressions
 * which are represented by syntax tree
 */
public interface Expression extends Iterable<Variable> {
    // Вычисление выражения, являющегося "производной" исходного по заданной переменной.
    Expression dash(Variable v);

    // Evaluation of expression in context(known meanings of variables)
    BigNumber evaluate(Map<Variable, Constant> context);
}