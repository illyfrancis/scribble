package week4

object curryings {;import org.scalaide.worksheet.runtime.library.WorksheetSupport._; def main(args: Array[String])=$execute{;$skip(177); 
  def sum(f: Int => Int): (Int, Int) => Int = {
    def sumF(a: Int, b: Int): Int =
      if (a > b) 0 else f(a) + sumF(a + 1, b)
    sumF
  };System.out.println("""sum: (f: Int => Int)(Int, Int) => Int""");$skip(98); 

  def sum2(f: Int => Int)(a: Int, b: Int): Int =
    if (a > b) 0 else f(a) + sum2(f)(a + 1, b);System.out.println("""sum2: (f: Int => Int)(a: Int, b: Int)Int""");$skip(26); val res$0 = 

  sum(x => x * x)(2, 3);System.out.println("""res0: Int = """ + $show(res$0));$skip(25); val res$1 = 
  sum2(x => x * x)(2, 3);System.out.println("""res1: Int = """ + $show(res$1));$skip(29); 

  val s1 = sum(x => x * x);System.out.println("""s1  : (Int, Int) => Int = """ + $show(s1 ));$skip(11); val res$2 = 
  s1(2, 3);System.out.println("""res2: Int = """ + $show(res$2));$skip(29); 
  val s2 = sum2(x => x * x)_;System.out.println("""s2  : (Int, Int) => Int = """ + $show(s2 ));$skip(11); val res$3 = 
  s2(2, 3)

  abstract class CodeTree
  case class Fork(left: CodeTree, right: CodeTree, chars: List[Char], weight: Int) extends CodeTree
  case class Leaf(char: Char, weight: Int) extends CodeTree;System.out.println("""res3: Int = """ + $show(res$3));$skip(299); 

  def weight(tree: CodeTree): Int = tree match {
    case Leaf(_, w) => w
    case Fork(_, _, _, w) => w
  };System.out.println("""weight: (tree: week4.curryings.CodeTree)Int""");$skip(123); 

  def chars(tree: CodeTree): List[Char] = tree match {
    case Leaf(c, _) => List(c)
    case Fork(_, _, c, _) => c
  }

  type Bit = Int
  type CodeTable = List[(Char, List[Bit])];System.out.println("""chars: (tree: week4.curryings.CodeTree)List[Char]""");$skip(362); 

  def convert(tree: CodeTree): CodeTable = {
    def visit(tree: CodeTree, bits: List[Bit]): CodeTable = tree match {
      case Leaf(c, _) => List((c, bits))
      case Fork(left, right, _, _) => visit(left, bits ::: 0 :: Nil) ::: visit(right, bits ::: 1 :: Nil)
    }
    visit(tree, List())
  };System.out.println("""convert: (tree: week4.curryings.CodeTree)week4.curryings.CodeTable""");$skip(163); 

  def encode(tree: CodeTree)(text: List[Char]): List[Bit] = text match {
    case List() => Nil
    case x :: xs => encodeChar(tree)(x) ::: encode(tree)(xs)
  };System.out.println("""encode: (tree: week4.curryings.CodeTree)(text: List[Char])List[week4.curryings.Bit]""");$skip(268); 

  def encodeChar(subTree: CodeTree)(char: Char): List[Bit] = subTree match {
    case Leaf(_, _) => Nil
    case Fork(left, right, _, _) => {
      if (chars(left).exists(c => c == char)) 0 :: encodeChar(left)(char)
      else 1 :: encodeChar(right)(char)
    }
  };System.out.println("""encodeChar: (subTree: week4.curryings.CodeTree)(char: Char)List[week4.curryings.Bit]""");$skip(93); 

	// is there a better way?
  def encode2(tree: CodeTree)(text: List[Char]): List[Bit] = ???;System.out.println("""encode2: (tree: week4.curryings.CodeTree)(text: List[Char])List[week4.curryings.Bit]""")}

}
