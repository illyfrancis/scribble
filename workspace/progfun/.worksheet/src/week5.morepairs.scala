package week5

object morepairs {;import org.scalaide.worksheet.runtime.library.WorksheetSupport._; def main(args: Array[String])=$execute{;$skip(62); 
  val pair = ("answer", 42);System.out.println("""pair  : (String, Int) = """ + $show(pair ));$skip(30); 

  val (label, value) = pair;System.out.println("""label  : String = """ + $show(label ));System.out.println("""value  : Int = """ + $show(value ));$skip(27); val res$0 = 

  "what is the " + label;System.out.println("""res0: String = """ + $show(res$0));$skip(25); val res$1 = 
  label + " is " + value;System.out.println("""res1: String = """ + $show(res$1));$skip(283); 

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
    };System.out.println("""merge: (xs: List[Int], ys: List[Int])List[Int]""");$skip(40); val res$2 = 

  merge(List(1, 3, 5), List(2, 4, 6));System.out.println("""res2: List[Int] = """ + $show(res$2));$skip(230); 

  def merger(xs: List[Int], ys: List[Int]): List[Int] = (xs, ys) match {
    case (Nil, ys) => ys
    case (xs, Nil) => xs
    case (x :: xs1, y :: ys1) =>
    	if (x < y) x :: merger(xs1, ys)
    	else y :: merger(xs, ys1)
  };System.out.println("""merger: (xs: List[Int], ys: List[Int])List[Int]""");$skip(42); val res$3 = 
  
  merger(List(1, 3, 5), List(2, 4, 6));System.out.println("""res3: List[Int] = """ + $show(res$3))}
}
