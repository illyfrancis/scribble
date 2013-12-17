package week5

object pairs {
  val pair = ('b', 4)                             //> pair  : (Char, Int) = (b,4)
  val list = ('a', 2) :: Nil                      //> list  : List[(Char, Int)] = List((a,2))

  def foo(c: Char, p: (Char, Int)): (Char, Int) = p match {
    case (a, b) => if (a == c) (a, b + 1) else (c, 0)
  }                                               //> foo: (c: Char, p: (Char, Int))(Char, Int)

  foo('a', pair)                                  //> res0: (Char, Int) = (a,0)
  foo('b', pair)                                  //> res1: (Char, Int) = (b,5)


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
  }                                               //> times: (chars: List[Char])List[(Char, Int)]

  times(List('x', 'a', 'b', 'a'))                 //> res2: List[(Char, Int)] = List((x,1), (a,2), (b,1))
  times(Nil)                                      //> res3: List[(Char, Int)] = List()
  
  val baz = List(1, 2, 3)                         //> baz  : List[Int] = List(1, 2, 3)
  baz match {
  	case x :: y :: z => println(x + " : " + y + " : " + z)
  	case _ => println("no match")
  }                                               //> 1 : 2 : List(3)

}