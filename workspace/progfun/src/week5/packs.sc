package week5

object packs {
	def pack[T](xs: List[T]): List[List[T]] = xs match {
		case Nil => Nil
		case x :: xs1 =>
			val (first, second) = xs span (s => s == x)
			first :: pack(second)
	}                                         //> pack: [T](xs: List[T])List[List[T]]
	
	val list = List('a', 'a', 'a', 'b', 'c', 'c', 'a')
                                                  //> list  : List[Char] = List(a, a, a, b, c, c, a)
	
	pack(list)                                //> res0: List[List[Char]] = List(List(a, a, a), List(b), List(c, c), List(a))
	
	def encode[T](xs: List[T]): List[(T, Int)] = {
		pack(xs) map (xs => (xs.head, xs.length))
	}                                         //> encode: [T](xs: List[T])List[(T, Int)]

	encode(list)                              //> res1: List[(Char, Int)] = List((a,3), (b,1), (c,2), (a,1))
}