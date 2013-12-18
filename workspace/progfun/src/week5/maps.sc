package week5

object maps {
	val list = List(1, 2, 3, 4, 5)            //> list  : List[Int] = List(1, 2, 3, 4, 5)
	
	def squareList(xs: List[Int]): List[Int] = xs match {
		case Nil => Nil
		case y :: ys => (y * y) :: squareList(ys)
	}                                         //> squareList: (xs: List[Int])List[Int]
	
	squareList(list)                          //> res0: List[Int] = List(1, 4, 9, 16, 25)
	
	def squareList2(xs: List[Int]): List[Int] =
		xs map (y => y * y)               //> squareList2: (xs: List[Int])List[Int]

	squareList2(list)                         //> res1: List[Int] = List(1, 4, 9, 16, 25)
	
	def even(x: Int) = x % 2 == 0             //> even: (x: Int)Boolean
	
	list filter even                          //> res2: List[Int] = List(2, 4)
	list filterNot even                       //> res3: List[Int] = List(1, 3, 5)
	list partition even                       //> res4: (List[Int], List[Int]) = (List(2, 4),List(1, 3, 5))
	
	list takeWhile even                       //> res5: List[Int] = List()
	list dropWhile even                       //> res6: List[Int] = List(1, 2, 3, 4, 5)
	list span even                            //> res7: (List[Int], List[Int]) = (List(),List(1, 2, 3, 4, 5))
}