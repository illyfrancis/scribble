package week5

object lists {;import org.scalaide.worksheet.runtime.library.WorksheetSupport._; def main(args: Array[String])=$execute{;$skip(73); 
  println("Welcome to the Scala worksheet");$skip(62); 
  val fruit: List[String] = List("apple", "oranges", "pears");System.out.println("""fruit  : List[String] = """ + $show(fruit ));$skip(29); 
  
  println(fruit.toString);$skip(52); 
  
  val colors = "blue" :: "red" :: "green" :: Nil;System.out.println("""colors  : List[String] = """ + $show(colors ));$skip(18); 
  val empty = Nil;System.out.println("""empty  : scala.collection.immutable.Nil.type = """ + $show(empty ));$skip(39); 
  val greys = Nil.::(20).::(25).::(30);System.out.println("""greys  : List[Int] = """ + $show(greys ));$skip(19); val res$0 = 
  
  empty.isEmpty;System.out.println("""res0: Boolean = """ + $show(res$0));$skip(13); val res$1 = 
  fruit.head;System.out.println("""res1: String = """ + $show(res$1));$skip(18); val res$2 = 
  fruit.tail.head;System.out.println("""res2: String = """ + $show(res$2));$skip(123); 
  
  def isort(xs: List[Int]): List[Int] = xs match {
  	case List() => List()
  	case y :: ys => insert(y, isort(ys))
  };System.out.println("""isort: (xs: List[Int])List[Int]""");$skip(172); 
  
  def insert(x: Int, xs: List[Int]): List[Int] = xs match {
  	case List() => x :: Nil
  	case y :: ys => {
  		if (x <= y) x :: xs
  		else y :: insert(x, ys)
  	}
  };System.out.println("""insert: (x: Int, xs: List[Int])List[Int]""");$skip(33); 
  
  val nums = List(7, 3, 9, 2);System.out.println("""nums  : List[Int] = """ + $show(nums ));$skip(14); val res$3 = 
  isort(nums);System.out.println("""res3: List[Int] = """ + $show(res$3));$skip(30); val res$4 = 
  
  List(('a', 1), ('b', 3));System.out.println("""res4: List[(Char, Int)] = """ + $show(res$4));$skip(22); 
  val what = ('a', 1);System.out.println("""what  : (Char, Int) = """ + $show(what ));$skip(21); 
  val say = 'a' -> 1;System.out.println("""say  : (Char, Int) = """ + $show(say ));$skip(29); val res$5 = 
  
  List(1, 2) ::: 0 :: Nil;System.out.println("""res5: List[Int] = """ + $show(res$5))}
}
