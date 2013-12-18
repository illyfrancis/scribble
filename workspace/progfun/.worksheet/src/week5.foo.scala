package week5

object foo {;import org.scalaide.worksheet.runtime.library.WorksheetSupport._; def main(args: Array[String])=$execute{;$skip(174); 
	def last[T](xs: List[T]): T = xs match {
		case List() => throw new Error("last of empty list")
		case List(x) => x
		case y :: ys => last(ys)
	};System.out.println("""last: [T](xs: List[T])T""");$skip(168); 
	
	def init[T](xs: List[T]): List[T] = xs match {
	  case List() => throw new Error("init of empty list")
	  case List(x) => List()
	  case y :: ys => y :: init(ys)
	};System.out.println("""init: [T](xs: List[T])List[T]""");$skip(125); 

	def concat[T](xs: List[T], ys: List[T]): List[T] = xs match {
		case List() => ys
		case z :: zs => z :: concat(zs, ys)
	};System.out.println("""concat: [T](xs: List[T], ys: List[T])List[T]""");$skip(124); 
	
	def reverse[T](xs: List[T]): List[T] = xs match {
	  case List() => List()
	  case y :: ys => reverse(ys) ::: List(y)
	};System.out.println("""reverse: [T](xs: List[T])List[T]""");$skip(154); 
	
	def removeAt[T](xs: List[T], n: Int): List[T] = xs match {
	  case List() => List()
	  case y :: ys => if (n == 0) ys else y :: removeAt(ys, n - 1)
	};System.out.println("""removeAt: [T](xs: List[T], n: Int)List[T]""");$skip(42); val res$0 = 
	
	removeAt(List('a', 'b', 'c', 'd'), 1);System.out.println("""res0: List[Char] = """ + $show(res$0));$skip(75); 
	
	def removeAt2[T](n: Int, xs: List[T]) = (xs take n) ::: (xs drop n + 1);System.out.println("""removeAt2: [T](n: Int, xs: List[T])List[T]""");$skip(42); val res$1 = 
	
	removeAt2(1, List('a', 'b', 'c', 'd'));System.out.println("""res1: List[Char] = """ + $show(res$1));$skip(33); 
	
		
	val list = List(1, 2, 4);System.out.println("""list  : List[Int] = """ + $show(list ));$skip(13); val res$2 = 
	list.length;System.out.println("""res2: Int = """ + $show(res$2));$skip(13); val res$3 = 
	
	list.head;System.out.println("""res3: Int = """ + $show(res$3));$skip(11); val res$4 = 
	list.tail;System.out.println("""res4: List[Int] = """ + $show(res$4));$skip(14); val res$5 = 
	
	list.init;System.out.println("""res5: List[Int] = """ + $show(res$5));$skip(11); val res$6 = 
	list.last;System.out.println("""res6: Int = """ + $show(res$6));$skip(15); val res$7 = 
	
	list take 2;System.out.println("""res7: List[Int] = """ + $show(res$7));$skip(13); val res$8 = 
	list drop 2;System.out.println("""res8: List[Int] = """ + $show(res$8));$skip(11); val res$9 = 
	
	list(0);System.out.println("""res9: Int = """ + $show(res$9));$skip(23); 
	
	val xs = List(1, 3);System.out.println("""xs  : List[Int] = """ + $show(xs ));$skip(21); 
	val ys = List(4, 8);System.out.println("""ys  : List[Int] = """ + $show(ys ));$skip(12); val res$10 = 
	
	xs ++ ys;System.out.println("""res10: List[Int] = """ + $show(res$10));$skip(13); val res$11 = 
	
	xs ::: ys;System.out.println("""res11: List[Int] = """ + $show(res$11));$skip(14); val res$12 = 
	
	xs.reverse;System.out.println("""res12: List[Int] = """ + $show(res$12));$skip(23); val res$13 = 
	
	xs updated (0, 101);System.out.println("""res13: List[Int] = """ + $show(res$13));$skip(16); val res$14 = 
	
	xs indexOf 3;System.out.println("""res14: Int = """ + $show(res$14));$skip(15); val res$15 = 
	xs contains 1;System.out.println("""res15: Boolean = """ + $show(res$15));$skip(16); val res$16 = 
	
	List(1).init;System.out.println("""res16: List[Int] = """ + $show(res$16));$skip(17); val res$17 = 
	
	List() == Nil;System.out.println("""res17: Boolean = """ + $show(res$17))}
}
