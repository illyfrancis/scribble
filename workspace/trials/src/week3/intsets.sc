package week3

object intsets {
  val e = Empty                                   //> e  : week3.Empty.type = .
  val t1 = new NonEmpty(3, Empty, Empty)          //> t1  : week3.NonEmpty = {.3.}
  val t2 = t1 incl 4                              //> t2  : week3.IntSet = {.3{.4.}}

  val t3 = t2 incl 2                              //> t3  : week3.IntSet = {{.2.}3{.4.}}
  val t4 = new NonEmpty(7, Empty, Empty)          //> t4  : week3.NonEmpty = {.7.}
  
  val u1 = t3 union t4                            //> u1  : week3.IntSet = {{.2{{.3.}4.}}7.}
}

abstract class IntSet {
  def incl(x: Int): IntSet
  def contains(x: Int): Boolean
  def union(other: IntSet): IntSet
}

object Empty extends IntSet {
  def contains(x: Int): Boolean = false
  def incl(x: Int): IntSet = new NonEmpty(x, Empty, Empty)
  override def toString = "."
  def union(other: IntSet): IntSet = other
}

class NonEmpty(elem: Int, left: IntSet, right: IntSet) extends IntSet {
  def contains(x: Int): Boolean = {
    if (x < elem) left contains x
    else if (elem < x) right.contains(x)
    else true
  }
  def incl(x: Int): IntSet = {
    if (x < elem) new NonEmpty(elem, left.incl(x), right)
    else if (elem < x) new NonEmpty(elem, left, right.incl(x))
    else this
  }
  override def toString = "{" + left + elem + right + "}"
  def union(other: IntSet): IntSet =
  	((left union right) union other) incl elem
}