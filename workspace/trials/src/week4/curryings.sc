package week4

object curryings {
  def sum(f: Int => Int): (Int, Int) => Int = {
    def sumF(a: Int, b: Int): Int =
      if (a > b) 0 else f(a) + sumF(a + 1, b)
    sumF
  }                                               //> sum: (f: Int => Int)(Int, Int) => Int

  def sum2(f: Int => Int)(a: Int, b: Int): Int =
    if (a > b) 0 else f(a) + sum2(f)(a + 1, b)    //> sum2: (f: Int => Int)(a: Int, b: Int)Int

  sum(x => x * x)(2, 3)                           //> res0: Int = 13
  sum2(x => x * x)(2, 3)                          //> res1: Int = 13

  val s1 = sum(x => x * x)                        //> s1  : (Int, Int) => Int = <function2>
  s1(2, 3)                                        //> res2: Int = 13
  val s2 = sum2(x => x * x)_                      //> s2  : (Int, Int) => Int = <function2>
  s2(2, 3)                                        //> res3: Int = 13

  abstract class CodeTree
  case class Fork(left: CodeTree, right: CodeTree, chars: List[Char], weight: Int) extends CodeTree
  case class Leaf(char: Char, weight: Int) extends CodeTree

  def weight(tree: CodeTree): Int = tree match {
    case Leaf(_, w) => w
    case Fork(_, _, _, w) => w
  }                                               //> weight: (tree: week4.curryings.CodeTree)Int

  def chars(tree: CodeTree): List[Char] = tree match {
    case Leaf(c, _) => List(c)
    case Fork(_, _, c, _) => c
  }                                               //> chars: (tree: week4.curryings.CodeTree)List[Char]

  type Bit = Int
  type CodeTable = List[(Char, List[Bit])]

  def convert(tree: CodeTree): CodeTable = {
    def visit(tree: CodeTree, bits: List[Bit]): CodeTable = tree match {
      case Leaf(c, _) => List((c, bits))
      case Fork(left, right, _, _) => visit(left, bits ::: 0 :: Nil) ::: visit(right, bits ::: 1 :: Nil)
    }
    visit(tree, List())
  }                                               //> convert: (tree: week4.curryings.CodeTree)week4.curryings.CodeTable

  def encode(tree: CodeTree)(text: List[Char]): List[Bit] = text match {
    case List() => Nil
    case x :: xs => encodeChar(tree)(x) ::: encode(tree)(xs)
  }                                               //> encode: (tree: week4.curryings.CodeTree)(text: List[Char])List[week4.curryi
                                                  //| ngs.Bit]

  def encodeChar(subTree: CodeTree)(char: Char): List[Bit] = subTree match {
    case Leaf(_, _) => Nil
    case Fork(left, right, _, _) => {
      if (chars(left).exists(c => c == char)) 0 :: encodeChar(left)(char)
      else 1 :: encodeChar(right)(char)
    }
  }                                               //> encodeChar: (subTree: week4.curryings.CodeTree)(char: Char)List[week4.curry
                                                  //| ings.Bit]

	// is there a better way?
  def encode2(tree: CodeTree)(text: List[Char]): List[Bit] = ???
                                                  //> encode2: (tree: week4.curryings.CodeTree)(text: List[Char])List[week4.curry
                                                  //| ings.Bit]

}