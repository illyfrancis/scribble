package week3

object random {
  val google = List("android", "Android", "galaxy", "Galaxy", "nexus", "Nexus")
                                                  //> google  : List[String] = List(android, Android, galaxy, Galaxy, nexus, Nexus
                                                  //| )
  val apple = List("ios", "iOS", "iphone", "iPhone", "ipad", "iPad")
                                                  //> apple  : List[String] = List(ios, iOS, iphone, iPhone, ipad, iPad)


  val googleText = "lorem ipsom nexus"            //> googleText  : String = lorem ipsom nexus

  google.exists(googleText.contains)              //> res0: Boolean = true
  apple.exists(googleText.contains)               //> res1: Boolean = false
  
  val appleText = "Lorem ipsum dolor sit amet, consectetur adipisicing elit, ios sed do eiusmod proident, sunt in culpa qui officia deserunt mollit anim id est laborum."
                                                  //> appleText  : String = Lorem ipsum dolor sit amet, consectetur adipisicing el
                                                  //| it, ios sed do eiusmod proident, sunt in culpa qui officia deserunt mollit a
                                                  //| nim id est laborum.
  google.exists(appleText.contains)               //> res2: Boolean = false
  apple.exists(appleText.contains)                //> res3: Boolean = true
}