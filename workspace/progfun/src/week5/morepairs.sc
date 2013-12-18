package week5

object morepairs {
  val pair = ("answer", 42)                       //> pair  : (String, Int) = (answer,42)

  val (label, value) = pair                       //> label  : String = answer
                                                  //| value  : Int = 42

  "what is the " + label                          //> res0: String = what is the answer
  label + " is " + value                          //> res1: String = answer is 42

  def merge(xs: List[Int], ys: List[Int]): List[Int] =
    xs match {
      case Nil => ys
      case x :: xs1 =>
        ys match {
          case Nil => xs
          case y :: ys1 =>
            if (x < y) x :: merge(xs1, ys)
            else y :: merge(xs, ys1)
        }
    }                                             //> merge: (xs: List[Int], ys: List[Int])List[Int]

  merge(List(1, 3, 5), List(2, 4, 6))             //> res2: List[Int] = List(1, 2, 3, 4, 5, 6)

  def merger(xs: List[Int], ys: List[Int]): List[Int] = (xs, ys) match {
    case (Nil, ys) => ys
    case (xs, Nil) => xs
    case (x :: xs1, y :: ys1) =>
    	if (x < y) x :: merger(xs1, ys)
    	else y :: merger(xs, ys1)
  }                                               //> merger: (xs: List[Int], ys: List[Int])List[Int]
  
  merger(List(1, 3, 5), List(2, 4, 6))            //> res3: List[Int] = List(1, 2, 3, 4, 5, 6)
}