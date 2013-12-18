package week5

object maps {;import org.scalaide.worksheet.runtime.library.WorksheetSupport._; def main(args: Array[String])=$execute{;$skip(60); 
	val list = List(1, 2, 3, 4, 5);System.out.println("""list  : List[Int] = """ + $show(list ));$skip(122); 
	
	def squareList(xs: List[Int]): List[Int] = xs match {
		case Nil => Nil
		case y :: ys => (y * y) :: squareList(ys)
	};System.out.println("""squareList: (xs: List[Int])List[Int]""");$skip(21); val res$0 = 
	
	squareList(list);System.out.println("""res0: List[Int] = """ + $show(res$0));$skip(69); 
	
	def squareList2(xs: List[Int]): List[Int] =
		xs map (y => y * y);System.out.println("""squareList2: (xs: List[Int])List[Int]""");$skip(21); val res$1 = 

	squareList2(list);System.out.println("""res1: List[Int] = """ + $show(res$1));$skip(34); 
	
	def even(x: Int) = x % 2 == 0;System.out.println("""even: (x: Int)Boolean""");$skip(21); val res$2 = 
	
	list filter even;System.out.println("""res2: List[Int] = """ + $show(res$2));$skip(21); val res$3 = 
	list filterNot even;System.out.println("""res3: List[Int] = """ + $show(res$3));$skip(21); val res$4 = 
	list partition even;System.out.println("""res4: (List[Int], List[Int]) = """ + $show(res$4));$skip(23); val res$5 = 
	
	list takeWhile even;System.out.println("""res5: List[Int] = """ + $show(res$5));$skip(21); val res$6 = 
	list dropWhile even;System.out.println("""res6: List[Int] = """ + $show(res$6));$skip(16); val res$7 = 
	list span even;System.out.println("""res7: (List[Int], List[Int]) = """ + $show(res$7))}
}
