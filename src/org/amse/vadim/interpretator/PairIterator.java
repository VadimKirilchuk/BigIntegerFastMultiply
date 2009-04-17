package org.amse.vadim.interpretator;

/**
 * В этом файле описывается класс для реализации итератора,
 * основанного на последовательном соединении двух итераторов.
 *
 * Автор: Александр Кубенский
 * Дата: 6 октября 2008
 */

import java.util.Iterator;

public class PairIterator<E> implements Iterator<E> {
  private Iterator<E> it1, it2;

  public PairIterator(Iterator<E> it1, Iterator<E> it2) {
    this.it1 = it1; this.it2 = it2;
  }
  public boolean hasNext() { return it1.hasNext() || it2.hasNext(); }
  public E next() {
    if (it1.hasNext()) return it1.next(); else return it2.next();
  }
  // Метод remove не поддерживается в нашей реализации
  public void remove() { throw new UnsupportedOperationException(); } 
}
