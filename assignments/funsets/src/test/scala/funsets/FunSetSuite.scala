package funsets

import org.scalatest.FunSuite

import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner

/**
 * This class is a test suite for the methods in object FunSets. To run
 * the test suite, you can either:
 *  - run the "test" command in the SBT console
 *  - right-click the file in eclipse and chose "Run As" - "JUnit Test"
 */
@RunWith(classOf[JUnitRunner])
class FunSetSuite extends FunSuite {

  /**
   * Link to the scaladoc - very clear and detailed tutorial of FunSuite
   *
   * http://doc.scalatest.org/1.9.1/index.html#org.scalatest.FunSuite
   *
   * Operators
   *  - test
   *  - ignore
   *  - pending
   */

  /**
   * Tests are written using the "test" operator and the "assert" method.
   */
  test("string take") {
    val message = "hello, world"
    assert(message.take(5) == "hello")
  }

  /**
   * For ScalaTest tests, there exists a special equality operator "===" that
   * can be used inside "assert". If the assertion fails, the two values will
   * be printed in the error message. Otherwise, when using "==", the test
   * error message will only say "assertion failed", without showing the values.
   *
   * Try it out! Change the values so that the assertion fails, and look at the
   * error message.
   */
  test("adding ints") {
    assert(1 + 2 === 3)
  }

  import FunSets._

  test("contains is implemented") {
    assert(contains(x => true, 100))
  }

  /**
   * When writing tests, one would often like to re-use certain values for multiple
   * tests. For instance, we would like to create an Int-set and have multiple test
   * about it.
   *
   * Instead of copy-pasting the code for creating the set into every test, we can
   * store it in the test class using a val:
   *
   *   val s1 = singletonSet(1)
   *
   * However, what happens if the method "singletonSet" has a bug and crashes? Then
   * the test methods are not even executed, because creating an instance of the
   * test class fails!
   *
   * Therefore, we put the shared values into a separate trait (traits are like
   * abstract classes), and create an instance inside each test method.
   *
   */

  trait TestSets {
    val s1 = singletonSet(1)
    val s2 = singletonSet(2)
    val s3 = singletonSet(3)
  }

  /**
   * This test is currently disabled (by using "ignore") because the method
   * "singletonSet" is not yet implemented and the test would fail.
   *
   * Once you finish your implementation of "singletonSet", exchange the
   * function "ignore" by "test".
   */
  test("singletonSet(1) contains 1") {

    /**
     * We create a new instance of the "TestSets" trait, this gives us access
     * to the values "s1" to "s3".
     */
    new TestSets {
      /**
       * The string argument of "assert" is a message that is printed in case
       * the test fails. This helps identifying which assertion failed.
       */
      assert(contains(s1, 1), "Singleton")
    }
  }

  test("singletonSet(1) does not contains 3") {
    new TestSets {
      assert(!contains(s1, 3), "Singleton")
    }
  }

  test("union contains all elements") {
    new TestSets {
      val s = union(s1, s2)
      assert(contains(s, 1), "Union 1")
      assert(contains(s, 2), "Union 2")
      assert(!contains(s, 3), "Union 3")
    }
  }

  test("intersect only common elements") {
    new TestSets {
      val s = intersect(union(s1, s2), union(s2, s3))
      assert(!contains(s, 1), "Intersect 1")
      assert(contains(s, 2), "Intersect 2")
      assert(!contains(s, 3), "Intersect 3")
    }
  }

  test("diff only elements from first set") {
    new TestSets {
      val s123 = union(union(s1, s2), s3)
      val s = diff(s123, s3)
      assert(contains(s, 1), "Diff 1")
      assert(contains(s, 2), "Diff 2")
      assert(!contains(s, 3), "Diff 3")
    }
  }

  test("even filter") {
    new TestSets {
      val s4 = singletonSet(4)
      val s1234 = union(union(union(s1, s2), s3), s4)

      def even = (e: Int) => e % 2 == 0
      val s = filter(s1234, even)
      assert(!contains(s, 1), "Filter 1")
      assert(contains(s, 2), "Filter 2")
      assert(!contains(s, 3), "Filter 3")
      assert(contains(s, 4), "Filter 4")
    }
  }

  test("forall even numbers") {
    new TestSets {
      val s0 = singletonSet(0)
      val s4 = singletonSet(4)
      val s6 = singletonSet(6)
      val s8 = singletonSet(8)
      val s = union(union(union(union(s0, s2), s4), s6), s8)

      def even = (e: Int) => e % 2 == 0
      assert(forall(s, even), "forall even")
    }
  }

  test("forall even numbers and some odd") {
    new TestSets {
      val s0 = singletonSet(0)
      val s4 = singletonSet(4)
      val s6 = singletonSet(6)
      val s9 = singletonSet(9)
      val s = union(union(union(union(s0, s2), s4), s6), s9)

      def even = (e: Int) => e % 2 == 0
      assert(!forall(s, even), "forall not all even")
    }
  }

  test("exists -999") {
    new TestSets {
      val s = singletonSet(-999)

      def id = (e: Int) => e == -999
      assert(exists(s, id), "exists -999")
    }
  }

  test("map - add one") {
    new TestSets {
      val s0 = singletonSet(0)
      val s4 = singletonSet(4)
      val s6 = singletonSet(6)
      val s8 = singletonSet(8)
      val s = union(union(union(union(s0, s2), s4), s6), s8)

      val t = map(s, e => e + 1)

      assert(!contains(t, 0), "Map 1")
      assert(contains(t, 1), "Map 2")
      assert(!contains(t, 2), "Map 3")
      assert(contains(t, 3), "Map 4")
      assert(!contains(t, 4), "Map 5")
      assert(contains(t, 5), "Map 6")
      assert(!contains(t, 6), "Map 7")
      assert(contains(t, 7), "Map 8")
      assert(!contains(t, 8), "Map 9")
      assert(contains(t, 9), "Map 10")
    }
  }
}

