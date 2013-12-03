package week3

object random {;import org.scalaide.worksheet.runtime.library.WorksheetSupport._; def main(args: Array[String])=$execute{;$skip(111); 
  val google = List("android", "Android", "galaxy", "Galaxy", "nexus", "Nexus");System.out.println("""google  : List[String] = """ + $show(google ));$skip(69); 
  val apple = List("ios", "iOS", "iphone", "iPhone", "ipad", "iPad");System.out.println("""apple  : List[String] = """ + $show(apple ));$skip(43); 


  val googleText = "lorem ipsom nexus";System.out.println("""googleText  : String = """ + $show(googleText ));$skip(39); val res$0 = 

  google.exists(googleText.contains);System.out.println("""res0: Boolean = """ + $show(res$0));$skip(36); val res$1 = 
  apple.exists(googleText.contains);System.out.println("""res1: Boolean = """ + $show(res$1));$skip(173); 
  
  val appleText = "Lorem ipsum dolor sit amet, consectetur adipisicing elit, ios sed do eiusmod proident, sunt in culpa qui officia deserunt mollit anim id est laborum.";System.out.println("""appleText  : String = """ + $show(appleText ));$skip(36); val res$2 = 
  google.exists(appleText.contains);System.out.println("""res2: Boolean = """ + $show(res$2));$skip(35); val res$3 = 
  apple.exists(appleText.contains);System.out.println("""res3: Boolean = """ + $show(res$3))}
}
