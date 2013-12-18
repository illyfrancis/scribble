package week5

object morelists {;import org.scalaide.worksheet.runtime.library.WorksheetSupport._; def main(args: Array[String])=$execute{;$skip(59); 
	val list = List(1, 2, 4);System.out.println("""list  : List[Int] = """ + $show(list ));$skip(13); val res$0 = 
	list.length;System.out.println("""res0: Int = """ + $show(res$0));$skip(13); val res$1 = 
	
	list.head;System.out.println("""res1: Int = """ + $show(res$1));$skip(11); val res$2 = 
	list.tail;System.out.println("""res2: List[Int] = """ + $show(res$2));$skip(14); val res$3 = 
	
	list.init;System.out.println("""res3: List[Int] = """ + $show(res$3));$skip(11); val res$4 = 
	list.last;System.out.println("""res4: Int = """ + $show(res$4));$skip(15); val res$5 = 
	
	list take 2;System.out.println("""res5: List[Int] = """ + $show(res$5));$skip(13); val res$6 = 
	list drop 2;System.out.println("""res6: List[Int] = """ + $show(res$6));$skip(11); val res$7 = 
	
	list(0);System.out.println("""res7: Int = """ + $show(res$7));$skip(23); 
	
	val xs = List(1, 3);System.out.println("""xs  : List[Int] = """ + $show(xs ));$skip(21); 
	val ys = List(4, 8);System.out.println("""ys  : List[Int] = """ + $show(ys ));$skip(12); val res$8 = 
	
	xs ++ ys;System.out.println("""res8: List[Int] = """ + $show(res$8));$skip(14); val res$9 = 
	
	xs.reverse;System.out.println("""res9: List[Int] = """ + $show(res$9));$skip(23); val res$10 = 
	
	xs updated (0, 101);System.out.println("""res10: List[Int] = """ + $show(res$10));$skip(16); val res$11 = 
	
	xs indexOf 3;System.out.println("""res11: Int = """ + $show(res$11));$skip(15); val res$12 = 
	xs contains 1;System.out.println("""res12: Boolean = """ + $show(res$12))}
}

object foo {
	def last[T](xs: List[T]): T = xs match {
		case List() => throw new Error("last of empty list")
		case List(x) => x
		case y :: ys => last(ys)
	}
	
	def init[T](xs: List[T]): List[T] = xs match {
	  case List() => throw new Error("init of empty list")
	  case List(x) => ???
	  case y :: ys => ???
	}
}
