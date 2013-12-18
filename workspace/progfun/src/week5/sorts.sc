package week5

object mergesort {
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
	}                                         //> msort: (xs: List[Int])List[Int]
	
	val nums = List(2, -4, 5, 7, 1)           //> nums  : List[Int] = List(2, -4, 5, 7, 1)
	msort(nums)                               //> res0: List[Int] = List(-4, 1, 2, 5, 7)
	
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
	}                                         //> msort2: [T](xs: List[T])(lt: (T, T) => Boolean)List[T]

	msort2(nums)((x: Int, y: Int) => x < y)   //> res1: List[Int] = List(-4, 1, 2, 5, 7)
}