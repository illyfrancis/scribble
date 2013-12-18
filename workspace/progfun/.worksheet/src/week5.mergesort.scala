package week5

object mergesort {;import org.scalaide.worksheet.runtime.library.WorksheetSupport._; def main(args: Array[String])=$execute{;$skip(428); 
	def msort(xs: List[Int]): List[Int] = {
		val n = xs.length / 2
		if (n == 0) xs
		else {
			def merge(xs: List[Int], ys: List[Int]): List[Int] = (xs, ys) match {
				case (xs, Nil) => xs
				case (Nil, ys) => ys
				case (x :: xs1, y :: ys1) =>
					if (x < y) x :: merge(xs1, ys)
					else y :: merge(xs, ys1)
			}
			
			val (fst, snd) = xs splitAt n
			merge(msort(fst), msort(snd))
		}
	};System.out.println("""msort: (xs: List[Int])List[Int]""");$skip(35); 
	
	val nums = List(2, -4, 5, 7, 1);System.out.println("""nums  : List[Int] = """ + $show(nums ));$skip(13); val res$0 = 
	msort(nums);System.out.println("""res0: List[Int] = """ + $show(res$0));$skip(427); 
	
	def msort2[T](xs: List[T])(lt: (T, T) => Boolean): List[T] = {
		val n = xs.length / 2
		if (n == 0) xs
		else {
			def merge(xs: List[T], ys: List[T]): List[T] = (xs, ys) match {
				case (xs, Nil) => xs
				case (Nil, ys) => ys
				case (x :: xs1, y :: ys1) =>
					if (lt(x, y)) x :: merge(xs1, ys)
					else y :: merge(xs, ys1)
			}
			
			val (fst, snd) = xs splitAt n
			merge(msort2(fst)(lt), msort2(snd)(lt))
		}
	};System.out.println("""msort2: [T](xs: List[T])(lt: (T, T) => Boolean)List[T]""");$skip(42); val res$1 = 

	msort2(nums)((x: Int, y: Int) => x < y);System.out.println("""res1: List[Int] = """ + $show(res$1))}
}
