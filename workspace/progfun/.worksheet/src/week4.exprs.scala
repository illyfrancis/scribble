package week4

object exprs {;import org.scalaide.worksheet.runtime.library.WorksheetSupport._; def main(args: Array[String])=$execute{;$skip(231); 
  def show(e: Expr): String = e match {
    case Number(n) => n.toString
    case Var(v) => v
    case Sum(l, r) => show(l) + " + " + show(r)
    case Prod(e1, e2) => paren(e1) + " * " + paren(e2)
  };System.out.println("""show: (e: week4.Expr)String""");$skip(110); 
  
  def paren(e: Expr): String = e match {
  	case Sum(_, _) => "(" + show(e) + ")"
  	case _ => show(e)
  };System.out.println("""paren: (e: week4.Expr)String""");$skip(20); val res$0 = 

  show(Number(1));System.out.println("""res0: String = """ + $show(res$0));$skip(34); val res$1 = 
  show(Sum(Number(1), Number(3)));System.out.println("""res1: String = """ + $show(res$1));$skip(17); val res$2 = 
  show(Var("x"));System.out.println("""res2: String = """ + $show(res$2));$skip(33); val res$3 = 
  show(Sum(Number(5), Var("y")));System.out.println("""res3: String = """ + $show(res$3));$skip(34); val res$4 = 
  show(Prod(Number(2), Var("y")));System.out.println("""res4: String = """ + $show(res$4));$skip(49); val res$5 = 
  show(Sum(Prod(Number(2), Var("x")), Var("y")));System.out.println("""res5: String = """ + $show(res$5));$skip(49); val res$6 = 
  show(Prod(Sum(Number(2), Var("x")), Var("y")));System.out.println("""res6: String = """ + $show(res$6));$skip(65); val res$7 = 
  show(Prod(Sum(Number(2), Var("x")), Sum(Number(7), Var("y"))));System.out.println("""res7: String = """ + $show(res$7))}
}
