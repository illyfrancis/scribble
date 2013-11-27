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

  test("union contains all elements") {
    new TestSets {
      val s = union(s1, s2)
      assert(contains(s, 1), "Union 1")
      assert(contains(s, 2), "Union 2")
      assert(!contains(s, 3), "Union 3")
    }
  }

  test("intersect") {
    new TestSets {
      val first = union(s1, s2)
      val second = union(s2, s3)

      assert(!contains(intersect(first, second), 1), "Intersect 1")
      assert(contains(intersect(first, second), 2), "Intersect 2")
      assert(!contains(intersect(first, second), 3), "Intersect 3")
    }
  }

  test("diff") {
    new TestSets {
      val first = union(s1, s2)
      val second = union(s2, s3)

      assert(contains(diff(first, second), 1), "Diff 1")
      assert(!contains(diff(first, second), 2), "Diff 2")
      assert(!contains(diff(first, second), 3), "Diff 3")
    }
  }

  test("filter - even numbers") {
    new TestSets {
      val s = union(union(union(s1, s2), s3), singletonSet(4))

      val filtered = filter(s, x => x % 2 == 0)
      assert(!contains(filtered, 1), "Filter - even 1")
      assert(contains(filtered, 2), "Filter - even 2")
      assert(!contains(filtered, 3), "Filter - even 3")
      assert(contains(filtered, 4), "Filter - even 4")
      assert(!contains(filtered, 5), "Filter - even 5")
      assert(!contains(filtered, 6), "Filter - even 6")
    }
  }

  test("filter - odd numbers") {
    new TestSets {
      val s = union(union(union(s1, s2), s3), singletonSet(4))

      val filtered = filter(s, x => x % 2 == 1)
      assert(contains(filtered, 1), "Filter - odd 1")
      assert(!contains(filtered, 2), "Filter - odd 2")
      assert(contains(filtered, 3), "Filter - odd 3")
      assert(!contains(filtered, 4), "Filter - odd 4")
      assert(!contains(filtered, 5), "Filter - odd 5")
      assert(!contains(filtered, 6), "Filter - odd 6")
    }
  }

  test("no filter") {
    new TestSets {
      val s = union(union(union(s1, s2), s3), singletonSet(4))

      val filtered = filter(s, x => true)
      assert(contains(filtered, 1), "No filter 1")
      assert(contains(filtered, 2), "No filter 2")
      assert(contains(filtered, 3), "No filter 3")
      assert(contains(filtered, 4), "No filter 4")
      assert(!contains(filtered, 5), "No filter 5")
    }
  }

  test("filter all") {
    new TestSets {
      val s = union(union(union(s1, s2), s3), singletonSet(4))

      val filtered = filter(s, x => false)
      assert(!contains(filtered, 1), "Filter all 1")
      assert(!contains(filtered, 2), "Filter all 2")
      assert(!contains(filtered, 3), "Filter all 3")
      assert(!contains(filtered, 4), "Filter all 4")
      assert(!contains(filtered, 5), "Filter all 5")
    }
  }

  test("forall even numbers in a set [2, 4, 6, 8]") {
    new TestSets {
      val even = union(union(union(s2, singletonSet(4)), singletonSet(6)), singletonSet(8))
      val isAllEven = forall(even, x => x % 2 == 0)
      assert(isAllEven, "Forall even 1")
    }
  }

  test("forall even numbers in a set of [1, 4, 6, 8]") {
    new TestSets {
      val someEven = union(union(union(s1, singletonSet(4)), singletonSet(6)), singletonSet(8))
      val isAllEven = forall(someEven, x => x % 2 == 0)
      assert(!isAllEven, "Forall even 2")
    }
  }

  test("forall of singles") {
    new TestSets {
      val isTrue = forall(s1, x => x == 1)
      assert(isTrue, "Forall singles 1")

      val isNotTrue = forall(s1, x => x == 2)
      assert(!isNotTrue, "Forall singles 2")
    }
  }

  test("forall of an empty set should return true") {
    new TestSets {
      val empty = diff(s1, s1)
      val shouldBeTrue = forall(empty, x => x > 0)
      assert(shouldBeTrue, "Forall empty")
    }
  }

  test("forall") {
    new TestSets {
      assert(forall(s1, x => true))
      assert(!forall(union(s1, s2), x => x == 1))
    }
  }

  test("exists") {
    new TestSets {
      assert(exists(s1, x => x == 1), "1 exists")
      assert(!exists(s1, x => x == 2), "2 does not exist")
    }
  }

  test("exists - ") {
    new TestSets {
      assert(exists(union(s1, s2), x => x == 1))
      assert(!exists(union(s1, s2), x => x == 3))
    }
  }

  test("maps to square") {
    new TestSets {
      val s = union(s1, union(s2, s3))
      printSet(s)
      val m = map(s, x => x * x)
      printSet(m)

      assert(contains(map(s, x => x * x), 1), "maps 1")
      assert(contains(map(s, x => x * x), 4), "maps 2")
      assert(contains(map(s, x => x * x), 9), "maps 3")

      assert(!contains(map(s, x => x * x), 2), "maps 4")
      assert(!contains(map(s, x => x * x), 3), "maps 5")
    }
  }
}
