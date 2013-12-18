package week5

object packs {;import org.scalaide.worksheet.runtime.library.WorksheetSupport._; def main(args: Array[String])=$execute{;$skip(195); 
	def pack[T](xs: List[T]): List[List[T]] = xs match {
		case Nil => Nil
		case x :: xs1 =>
			val (first, second) = xs span (s => s == x)
			first :: pack(second)
	};System.out.println("""pack: [T](xs: List[T])List[List[T]]""");$skip(54); 
	
	val list = List('a', 'a', 'a', 'b', 'c', 'c', 'a');System.out.println("""list  : List[Char] = """ + $show(list ));$skip(14); val res$0 = 
	
	pack(list);System.out.println("""res0: List[List[Char]] = """ + $show(res$0));$skip(97); 
	
	def encode[T](xs: List[T]): List[(T, Int)] = {
		pack(xs) map (xs => (xs.head, xs.length))
	};System.out.println("""encode: [T](xs: List[T])List[(T, Int)]""");$skip(15); val res$1 = 

	encode(list);System.out.println("""res1: List[(Char, Int)] = """ + $show(res$1))}
}
