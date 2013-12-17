package week5

object pairs {;import org.scalaide.worksheet.runtime.library.WorksheetSupport._; def main(args: Array[String])=$execute{;$skip(52); 
  val pair = ('b', 4);System.out.println("""pair  : (Char, Int) = """ + $show(pair ));$skip(29); 
  val list = ('a', 2) :: Nil;System.out.println("""list  : List[(Char, Int)] = """ + $show(list ));$skip(120); 

  def foo(c: Char, p: (Char, Int)): (Char, Int) = p match {
    case (a, b) => if (a == c) (a, b + 1) else (c, 0)
  };System.out.println("""foo: (c: Char, p: (Char, Int))(Char, Int)""");$skip(19); val res$0 = 

  foo('a', pair);System.out.println("""res0: (Char, Int) = """ + $show(res$0));$skip(17); val res$1 = 
  foo('b', pair);System.out.println("""res1: (Char, Int) = """ + $show(res$1));$skip(464); 


  def times(chars: List[Char]): List[(Char, Int)] = {
	  def map(ch: Char, list: List[(Char, Int)]): List[(Char, Int)] = list match {
	    case List() => List((ch, 1))
	    case (a, b) :: xs => if (a == ch) (a, b + 1) :: xs else (a, b) :: map(ch, xs)
	  }
	  def timesAcc(chars: List[Char], accum: List[(Char, Int)]): List[(Char, Int)] = chars match {
	    case List() => accum
	    case x :: xs => timesAcc(xs, map(x, accum))
	  }
  	timesAcc(chars, Nil)
  };System.out.println("""times: (chars: List[Char])List[(Char, Int)]""");$skip(36); val res$2 = 

  times(List('x', 'a', 'b', 'a'));System.out.println("""res2: List[(Char, Int)] = """ + $show(res$2));$skip(13); val res$3 = 
  times(Nil);System.out.println("""res3: List[(Char, Int)] = """ + $show(res$3));$skip(29); 
  
  val baz = List(1, 2, 3);System.out.println("""baz  : List[Int] = """ + $show(baz ));$skip(109); 
  baz match {
  	case x :: y :: z => println(x + " : " + y + " : " + z)
  	case _ => println("no match")
  }}

}
