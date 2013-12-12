package ideanlized.scala

abstract class Boolean {
  def ifThenElse[T](t: => T, e: => T): T
  
  def && (x: => Boolean): Boolean = ifThenElse(x, iFalse)
  def < (x: => Boolean): Boolean = ifThenElse(iFalse, x)
}

object iFalse extends Boolean {
  def ifThenElse[T](t: => T, e: => T): T = e
}