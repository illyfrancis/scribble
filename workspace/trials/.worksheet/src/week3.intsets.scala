package week3

object intsets {;import org.scalaide.worksheet.runtime.library.WorksheetSupport._; def main(args: Array[String])=$execute{;$skip(48); 
  val e = Empty;System.out.println("""e  : week3.Empty.type = """ + $show(e ));$skip(41); 
  val t1 = new NonEmpty(3, Empty, Empty);System.out.println("""t1  : week3.NonEmpty = """ + $show(t1 ));$skip(21); 
  val t2 = t1 incl 4;System.out.println("""t2  : week3.IntSet = """ + $show(t2 ));$skip(23); 

  val t3 = t2 incl 2;System.out.println("""t3  : week3.IntSet = """ + $show(t3 ));$skip(41); 
  val t4 = new NonEmpty(7, Empty, Empty);System.out.println("""t4  : week3.NonEmpty = """ + $show(t4 ));$skip(27); 
  
  val u1 = t3 union t4;System.out.println("""u1  : week3.IntSet = """ + $show(u1 ))}
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
