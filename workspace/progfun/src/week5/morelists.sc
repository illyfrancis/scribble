package week5

object foo {
	def last[T](xs: List[T]): T = xs match {
		case List() => throw new Error("last of empty list")
		case List(x) => x
		case y :: ys => last(ys)
	}                                         //> last: [T](xs: List[T])T
	
	def init[T](xs: List[T]): List[T] = xs match {
	  case List() => throw new Error("init of empty list")
	  case List(x) => List()
	  case y :: ys => y :: init(ys)
	}                                         //> init: [T](xs: List[T])List[T]

	def concat[T](xs: List[T], ys: List[T]): List[T] = xs match {
		case List() => ys
		case z :: zs => z :: concat(zs, ys)
	}                                         //> concat: [T](xs: List[T], ys: List[T])List[T]
	
	def reverse[T](xs: List[T]): List[T] = xs match {
	  case List() => List()
	  case y :: ys => reverse(ys) ::: List(y)
	}                                         //> reverse: [T](xs: List[T])List[T]
	
	def removeAt[T](xs: List[T], n: Int): List[T] = xs match {
	  case List() => List()
	  case y :: ys => if (n == 0) ys else y :: removeAt(ys, n - 1)
	}                                         //> removeAt: [T](xs: List[T], n: Int)List[T]
	
	removeAt(List('a', 'b', 'c', 'd'), 1)     //> res0: List[Char] = List(a, c, d)
	
	def removeAt2[T](n: Int, xs: List[T]) = (xs take n) ::: (xs drop n + 1)
                                                  //> removeAt2: [T](n: Int, xs: List[T])List[T]
	
	removeAt2(1, List('a', 'b', 'c', 'd'))    //> res1: List[Char] = List(a, c, d)
	
		
	val list = List(1, 2, 4)                  //> list  : List[Int] = List(1, 2, 4)
	list.length                               //> res2: Int = 3
	
	list.head                                 //> res3: Int = 1
	list.tail                                 //> res4: List[Int] = List(2, 4)
	
	list.init                                 //> res5: List[Int] = List(1, 2)
	list.last                                 //> res6: Int = 4
	
	list take 2                               //> res7: List[Int] = List(1, 2)
	list drop 2                               //> res8: List[Int] = List(4)
	
	list(0)                                   //> res9: Int = 1
	
	val xs = List(1, 3)                       //> xs  : List[Int] = List(1, 3)
	val ys = List(4, 8)                       //> ys  : List[Int] = List(4, 8)
	
	xs ++ ys                                  //> res10: List[Int] = List(1, 3, 4, 8)
	
	xs ::: ys                                 //> res11: List[Int] = List(1, 3, 4, 8)
	
	xs.reverse                                //> res12: List[Int] = List(3, 1)
	
	xs updated (0, 101)                       //> res13: List[Int] = List(101, 3)
	
	xs indexOf 3                              //> res14: Int = 1
	xs contains 1                             //> res15: Boolean = true
	
	List(1).init                              //> res16: List[Int] = List()
	
	List() == Nil                             //> res17: Boolean = true
}