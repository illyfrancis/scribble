package week5

object lists {
  println("Welcome to the Scala worksheet")       //> Welcome to the Scala worksheet
  val fruit: List[String] = List("apple", "oranges", "pears")
                                                  //> fruit  : List[String] = List(apple, oranges, pears)
  
  println(fruit.toString)                         //> List(apple, oranges, pears)
  
  val colors = "blue" :: "red" :: "green" :: Nil  //> colors  : List[String] = List(blue, red, green)
  val empty = Nil                                 //> empty  : scala.collection.immutable.Nil.type = List()
  val greys = Nil.::(20).::(25).::(30)            //> greys  : List[Int] = List(30, 25, 20)
  
  empty.isEmpty                                   //> res0: Boolean = true
  fruit.head                                      //> res1: String = apple
  fruit.tail.head                                 //> res2: String = oranges
  
  def isort(xs: List[Int]): List[Int] = xs match {
  	case List() => List()
  	case y :: ys => insert(y, isort(ys))
  }                                               //> isort: (xs: List[Int])List[Int]
  
  def insert(x: Int, xs: List[Int]): List[Int] = xs match {
  	case List() => x :: Nil
  	case y :: ys => {
  		if (x <= y) x :: xs
  		else y :: insert(x, ys)
  	}
  }                                               //> insert: (x: Int, xs: List[Int])List[Int]
  
  val nums = List(7, 3, 9, 2)                     //> nums  : List[Int] = List(7, 3, 9, 2)
  isort(nums)                                     //> res3: List[Int] = List(2, 3, 7, 9)
  
  List(('a', 1), ('b', 3))                        //> res4: List[(Char, Int)] = List((a,1), (b,3))
  val what = ('a', 1)                             //> what  : (Char, Int) = (a,1)
  val say = 'a' -> 1                              //> say  : (Char, Int) = (a,1)
  
  List(1, 2) ::: 0 :: Nil                         //> res5: List[Int] = List(1, 2, 0)
}