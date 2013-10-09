# Content

[toc]

# Simple logic

## One missing number

There's an array of integer and the array holds a sequence of numbers but it's missing one number. 
Write a program to find the missing number.

## Two missing numbers

What if there are two missing numbers?

## Reverse the words in a string

Write a short program in Java to reverse the order of words in the following string:

    String dwarves = "bashful doc dopey grumpy happy sleepy sneezy";

Your output should look like this:

    sneezy sleepy happy grumpy dopey doc bashful

> This answer splits the string on whitespace and then uses a LIFO (last in, first out) stack to reverse the list:

    import java.util.*;
    import java.lang.*;

    public class Main
    {
        public static void main (String[] args) 
        throws java.lang.Exception

        {
            String dwarves = 
            "bashful doc dopey grumpy happy sleepy sneezy";

            List<String> list = Arrays.asList(dwarves.split(" "));
            Stack<String> s = new Stack<String>();
            s.addAll(list);

            String sevrawd = "";

            while (!s.empty()) {
                sevrawd += s.pop() + " ";
            }

            System.out.println(sevrawd);
        }
    }

> or another attempt without stack

    @Test
    public void testStringReverse() {
        String dwarves = "bashful doc dopey grumpy happy sleepy sneezy";

        String reversed = "";
        String[] dwarfArray = dwarves.split(" ");
        for (int i = 0; i < dwarfArray.length; i++) {
            reversed = dwarfArray[i] + " " + reversed;
        }
        
        reversed = reversed.trim();
        assertEquals("sneezy sleepy happy grumpy dopey doc bashful", reversed);
    }

## Finding the duplicate

An array contains n numbers ranging from 0 to n-2. There is exactly one number duplicated in the array. How do you find the duplicated number? For example, if an array with length 5 contains numbers {0, 2, 1, 3, 2}, the duplicated number is 2.

### Answer

Suppose that the duplicated number in the array is m. The sum of all numbers in the array, denoted as sum1, should be the result of 0+1+…+(n-2)+m. It is not difficult to get the sum result of 0+1+…+(n-2), which is denoted as sum2. The duplicated number m is the difference between sum1 and sum2. The corresponding code in Java is shown in Listing 3-2.
Listing 3-2. Java Code to Get a Duplicated Number in an Array

	int duplicate(int numbers[]) {
	    int length = numbers.length;

	    int sum1 = 0;
	    for(int i = 0; i < length; ++i) {
	        if(numbers[i] < 0 || numbers[i] > length - 2)
	            throw new IllegalArgumentException("Invalid numbers.");

	        sum1 += numbers[i];
	    }

	    int sum2 = ((length - 1) * (length - 2)) >> 1;

	    return sum1 - sum2;
	}

### Test Cases:
- Normal case: an array with size n has a duplication
- Boundary case: an array {0, 0} with size 2
- Some numbers are out of the range of 0 to n-2 in an array of size n

# Recursion

## Fibonacci number

Given a number n, please find the nth element in the Fibonacci Sequence, which is defined as the following equation:

    f(0) = 0;
    f(1) = 1;
    f(n) = f(n-1) + f(n-2);

#### Recursive and Inefficient

In many textbooks, the Fibonacci Sequence is taken as an example to illustrate recursive functions, so many candidates are quite familiar with the recursive solution and can write code quickly, as shown in Listing 4-3.
Listing 4-3. Recursive C Code for Fibonacci Sequence

	long long Fibonacci(unsigned int n) {
	    if(n <= 0)
	        return 0;

	    if(n == 1)
	        return 1;

	    return Fibonacci(n - 1) + Fibonacci(n - 2);
	}

However, recursion is not the best solution for this problem since it has serious performance issues. Let’s take f(10) as an example to analyze the recursive process. The element f(10) is calculated based on f(9) and f(8). Similarly, f(9) is based on f(8) and f(7), and so on. The dependency can be visualized as a tree (Figure 4-1).

#### Iterative Solution with O(n) Time Complexity

It is not difficult to improve this performance. Since the slowness is caused by duplicated calculations, let’s try to void the duplications. A solution is to cache the calculated elements. Before an element is calculated, we first check whether it is cached. It is not calculated again if an element is already in the cache.

A simpler solution is to calculate using a bottom-up process. The elements f(0) and f(1) are already known, and f(2) can be calculated based on them. Similarly, f(3) can be calculated based on f(2) and f(1), and so on. It iterates until f(n) is calculated. The code for this iterative solution is shown in Listing 4-4.

Listing 4-4. Iterative C Code for Fibonacci Sequence

	long long Fibonacci_Solution2(unsigned int n) {
	    int result[2] = {0, 1};
	    long long  fibNMinusOne = 1;
	    long long  fibNMinusTwo = 0;
	    long long  fibN = 0;
	    unsigned int i;

	    if(n < 2)
	        return result[n];

	    for(i = 2; i <= n; ++ i) {
	        fibN = fibNMinusOne + fibNMinusTwo;

	        fibNMinusTwo = fibNMinusOne;
	        fibNMinusOne = fibN;
	    }

	     return fibN;
	}

Obviously, the time complexity for this iterative solution is O(n).

More Efficient in O(logn) Time
Usually, the solution costing O(n) time is the expected solution. However, interviewers may require a solution with O(logn) time complexity if they have higher expectation on performance.

#### Test Cases
- Normal case: 3, 5, 10
- Boundary case: 0, 1, 2
- Big numbers for performance tests, such as 40, 50, 100


# Data structures

## Give examples of different data structures?

array, list hash table, queue, stack, tree, graph

## Linked list versus array

What are some important differences between a linked list and an array?

> In most frameworks the memory reserved for an array is pre-allocated before the array is used. This predefined limit constrains how many elements you can add to an array before it overflows. In contrast, linked lists can consume all available memory before overflowing and without any pre-declaration of size.

> Reassigning pointers is generally quicker than moving records around in memory, which means that adding elements to a linked list is generally quicker than the equivalent operation on an array.

> Each record in an array is generally a fixed length, which means that it is a simple calculation to obtain the address of an array element. This calculation is a fixed number of steps, and so it always takes time O(1). In contrast, accessing the nth element of a linked list requires traversal of n – 1 nodes, and therefore takes time O(n). In other words accessing elements in arrays is usually faster than accessing elements in linked lists.

## Array versus associative array

Describe a common scenario that would lead you to prefer an associative array to a simple array.

> An associative array is perfect when you need to quickly look up values based on keys. You don't need to iterate through the collection looking for a key; you use the key directly, as in the following example:

	var myValue = myHashtable[myKey];

You can also easily and quickly test whether a hash table contains a key:

	bool keyExists = myHashtable.Contains(myKey); // quick!

Contrast this with the equivalent code for a simple array where you must iterate to find a given element:

	foreach (int i=0; i<myArray.Length;i++)
	   if (myArray[i] == myKey)
	      return true; // Found key

	return false; // Did not find key (and took a long time)

## Hash table

#### Write a class named `MyKey` to be used as a key to a map

MyKey has a field called identity of a type String. The identity field will be unique.

Looking for the candidate to implement:

- boolean equals(Obj o)
- int hashCode()

#### What happens if hashCode returns a same integer every time?

Get candidate to explain what happens. 

- What is hash table?
- How does hash table work?
- What if two different keys's hash code turns out to be the same? 
- How to resolve that?

> Hash collision, equals(Object) & int hashCode() methods

## Immutability

1. Let's add a date field to `MyKey`
1. Extend the `MyKey` class to be immutable.
2. Looking for `final` & `private` fields
3. Prevent sub classing (by final or with private constructor and use factory)
4. No modifier (e.g. no setter method)
5. The accessor for date should return a copy

## Add factory or builder to it

With private constructor, one cannot create an instance.

# DI and testing

## Scenario

Let's say, we have an Item with an id (int)

    public class Item {
    	private int id,
    	private String name
    }

we have an interface named Repository with get() and save(Item item)

    public interface Repository {
    	public Item get(int id);
    	public void save(Item item);
    }

There will be different implementations of Repository depending on the persistence technology of choice. E.g. RDbRepository, GraphDbRepository etc. But it's not done yet.

We need to create a process in a Service that uses the Repository to retrieve an item by its id. Apply some logic to it then save it back.

Write the test first?
Write the service class. You can use DI framework if needed.

    public class Service {
    	public String process(int id);
    }

# Concurrency

## Account example

A banking system provides an illustration of basic threading concepts and the necessity of thread synchronization. The system consists of a program running on a single central computer that controls multiple automated teller machines (ATMs) in different locations. Each ATM has its own thread so that the machines can be used simultaneously and easily share the bank’s account data.

The banking system has an Account class with a method to deposit and withdraw money from a user’s account. The following code is written as a Java class but the code is almost identical to what you’d write in C#:

	public class Account {
	    int    userNumber;
	    String userLastName;
	    String userFirstName;
	    double userBalance;
	    public boolean deposit( double amount ){
	        double newBalance;
	        if( amount < 0.0 ){
	            return false; /* Can’t deposit negative amount */
	        } else {
	            newBalance = userBalance + amount;
	            userBalance = newBalance;
	            return true;
	        }
	    }
	    public boolean withdraw( double amount ){
	        double newBalance;
	        if( amount < 0.0 || amount > userBalance ){
	            return false; /* Negative withdrawal or insufficient funds */
	        } else {
	            newBalance = userBalance - amount;
	            userBalance = newBalance;
	            return true;
	        }
	    }
	}

Suppose a husband and wife, Ron and Sue, walk up to different ATMs to withdraw $100 each from their joint account. The thread for the first ATM deducts $100 from the couple’s account, but the thread is switched out after executing this line:
newBalance = userBalance – amount;

Processor control then switches to the thread for Sue’s ATM, which is also deducting $100. When that thread deducts $100, the account balance is still $500 because the variable, userBalance, has not yet been updated. Sue’s thread executes until completing this function and updates the value of userBalance to $400. Then, control switches back to Ron’s transaction. Ron’s thread has the value $400 in newBalance. Therefore, it simply assigns this value to userBalance and returns. Thus, Ron and Sue have deducted $200 total from their account, but their balance still indicates $400, or a net $100 withdrawal. This is a great feature for Ron and Sue, but a big problem for the bank.
Fixing this problem is trivial in Java. Just use the synchronized keyword to create a monitor:

	public class Account {
	    int    userNumber;
	    String userLastName;
	    String userFirstName;
	    double userBalance;
	    public synchronized boolean deposit( double amount ){
	        double newBalance;
	        if( amount < 0.0 ){
	            return false; /* Can’t deposit negative amount */
	        } else {
	            newBalance = userBalance + amount;
	            userBalance = newBalance;
	            return true;
	        }
	    }
	    public synchronized boolean withdraw( double amount ){
	        double newBalance;
	        if( amount < 0.0 || amount > userBalance ){
	            return false; /* Negative withdrawal or insufficient funds */
	        } else {
	            newBalance = userBalance – amount;
	            userBalance = newBalance;
	            return true;
	        }
	    }
	}

The first thread that enters either deposit or withdraw blocks all other threads from entering either method. This protects the userBalance class data from being changed simultaneously by different threads. The preceding code can be made marginally more efficient by having the monitor synchronize only the code that uses or alters the value of userBalanceinstead of the entire method:

	public class Account {
	    int    userNumber;
	    String userLastName;
	    String userFirstName;
	    double userBalance;
	    public boolean deposit( double amount ){
	        double newBalance;
	        if( amount < 0.0 ){
	            return false; /* Can’t deposit negative amount */
	        } else {
	            synchronized( this ){
	                newBalance = userBalance + amount;
	                userBalance = newBalance;
	            }
	            return true;
	        }
	    }
	    public boolean withdraw( double amount ){
	        double newBalance;
	        synchronized( this ){
	            if( amount < 0.0 || amount > userBalance ){
	                return false; 
	            } else {
	                newBalance = userBalance – amount;
	                userBalance = newBalance;
	                return true;
	            }
	        }
	    }
	}

In fact, in Java a synchronized method such as:

	synchronized void someMethod(){
	    .... // the code to protect
	}

is exactly equivalent to:

	void someMethod(){
	    synchronized( this ){
	        .... // the code to protect
	    }
	}

The lock statement in C# can be used in a similar manner, but only within a method:

	void someMethod(){
	    lock( this ){
	        .... // the code to protect
	    }
	}
In either case, the parameter passed to synchronize or lock is the object to use as the lock.
Note that the C# lock isn’t as flexible as the Java synchronized because the latter allows threads to suspend themselves while waiting for another thread to signal them that an event has occurred. In C# this must be done using event semaphores.


# Garbage collection

## What is structure of Java Heap? 

What is Perm Gen space in Heap?

#### Answer

In order to better perform in Garbage collection questions in any Java interview, It’s important to have basic understanding of  Java Heap space. To learn more about heap, see my post 10 points on Java heap space. By the way Heap is divided into different generation e.g. new generation, old generation and PermGen space.PermGen space is used to store class’s metadata and filling of PermGen space can cause java.lang.OutOfMemory:PermGen space. Its also worth noting to remember JVM option to configure PermGen space in Java.


## When does an Object becomes eligible for Garbage collection in Java?

#### Answer

An object becomes eligible for garbage collection when there is no live reference for that object or it can not be reached by any live thread. Cyclic reference doesn’t count as live reference and if two objects are pointing to each other and there is no live reference for any of them, than both are eligible for GC. Also Garbage collection thread is a daemon thread which will run by JVM based upon GC algorithm and when runs it collects all objects which are eligible for GC.


## Can we force Garbage collector to run at any time?

#### Answer

No, you can not force Garbage collection in Java. Though you can request it by calling Sytem.gc() or its cousin Runtime.getRuntime().gc(). It’s not guaranteed that GC will run immediately as result of calling these method.


# Design patterns

## Singleton

Implement a singleton class using Enum OR ask the question below

Describe the design pattern that is being implemented in the following code:

    public enum Highlander {
    	INSTANCE;
    	public void execute () {
    		//... perform operation here ...
    	}
    }

##  Decorator versus Inheritance

PROBLEM: Why would you use the Decorator pattern instead of inheritance?

Recall that the Decorator pattern wraps one object with another object to change the original object’s behavior. The wrapper object can take the place of the original object because they share the same abstract base class (or implement the same interface).
Both the Decorator pattern and inheritance provide means of modifying the behavior of an object of the underlying class, but in different ways. Inheritance typically allows modification of the parent class only at compile time, while decorations are applied dynamically at run time.
Suppose you have an object that needs to dynamically change behavior. Accomplishing this with inheritance may be cumbersome and inefficient: Every time you need to change behavior, you’ll probably need to construct a new object of a different child class with the desired behavior, copy the state from the existing object to the new one, and throw the old one away. In contrast, modifying the behavior of the existing object using the Decorator pattern is much simpler — just add the appropriate decoration (that is, wrap the existing object with another wrapper that implements the modified behavior).

The dynamic nature of the Decorator pattern has another advantage. Suppose you have several behavior modifications that you’d like to implement for a class. Assume that none of these modifications interfere with any of the others, so you can apply them in any combination. A classic example of this is a GUI toolkit with a Window class that may be modified by multiple different behaviors, such as Bordered, Scrollable, Disabled, and so on. You could implement this with inheritance: deriving BorderedWindow from Window, ScrollableBorderedWindow and DisabledBorderedWindow from BorderedWindow, and so on. This is reasonable for a small number of behaviors, but as the number of behaviors increases, your class hierarchy rapidly gets out of hand. The number of classes doubles each time you add a new behavior. You can avoid this explosion of largely redundant classes with the Decorator pattern. Each behavior is completely described by a single Decorator class, and you can generate whatever combination of behaviors you need by applying the appropriate set of decorations.
The Decorator pattern simplifies object-oriented design when applied correctly, but may have the opposite effect when used indiscriminately. If you don’t need to dynamically modify the behavior of an object, then it’s probably best to use simple inheritance and avoid the complexity of this pattern. Also, Concrete Decorator classes generally shouldn’t expose new public methods; so if you need to do this, using Decorators probably isn’t the best approach (Concrete Decorator classes shouldn’t expose new public methods because they would likely become inaccessible when subsequent decorations are added.) Finally, you should make sure that your Concrete Decorator classes are truly mutually non-interfering. There’s no good way to forbid combinations of decorations that are conflicting or don’t make sense, so using the Decorator pattern in these circumstances may invite bugs later on.


# House keeping

## Relational database

What is?

- inner join
- outer join

## Security

What is?

- SQL injection
- XSS

How to prevent the problem?

## Agile, scrum?

- used any tools?
- agile (what's the done criteria) - describe their process
- write any testing? what coverage? what tools have you used?

## Testing

Familiar with?

- JUnit
- Mock framework
- Difference between unit testing and integration testing?
- Load testing?




---

### Find the duplicate - more advanced

An array contains n numbers ranging from 0 to n-1. There are some numbers duplicated in the array. It is not clear how many numbers are duplicated or how many times a number gets duplicated. How do you find a duplicated number in the array? For example, if an array of length 7 contains the numbers {2, 3, 1, 0, 2, 5, 3}, the implemented function (or method) should return either 2 or 3.

#### Answer 

A naive solution for this problem is to sort the input array because it is easy to find duplication in a sorted array. As we know, it costs O(nlogn) time to sort an array with n elements.

Another solution is the utilization of a hash set. All numbers in the input array are scanned sequentially. When a number is scanned, we check whether it is already in the hash set. If it is, it is a duplicated number. Otherwise, it is inserted into the set. The data structure HashSet in Java is quite helpful in solving this problem. Even though this solution is simple and intuitive, it has costs: O(n) auxiliary memory to accommodate a hash set. Let’s explore a better solution that only needs O(1) memory.

Indexes in an array with length n are in the range 0 to n-1. If there were no duplication in the n numbers ranging from 0 to n-1, we could rearrange them in sorted order, locating the number i as the ith number. Since there are duplicate numbers in the array, some locations are occupied by multiple numbers, but some locations are vacant.

Now let’s rearrange the input array. All numbers are scanned one by one. When the ith number is visited, first it checks whether the value (denoted as m) is equal to i. If it is, we continue to scan the next number. Otherwise, we compare it with the mth number. If the ith number equals the mth number, duplication has been found. If not, we locate the number m in its correct place, swapping it with the mth number. We continue to scan, compare, and swap until a duplicated number is found.

Take the array {2, 3, 1, 0, 2, 5, 3} as an example. The first number 2 does not equal its index 0, so it is swapped with the number with index 2. The array becomes {1, 3, 2, 0, 2, 5, 3}. The first number after swapping is 1, which does not equal its index 0, so two elements in the array are swapped again and the array becomes {3, 1, 2, 0, 2, 5, 3}. It continues to swap since the first number is still not 0. The array is {0, 1, 2, 3, 2, 5, 3} after swapping the first number and the number with index 3. Finally, the first number becomes 0.
Let’s move on to scan the next numbers. Because the following three numbers, 1, 2 and 3, are all equal to their indexes, no swaps are necessary for them. The following number, 2, is not the same as its index, so we check whether it is the same as the number with index 2. Duplication is found since the number with index 2 is also 2.

With an understanding of the detailed step-by-step analysis, it is time to implement code. Sample code in Java is shown in Listing 3-3.
Listing 3-3. Java Code to Get a Duplicated Number in an Array

	int duplicate(int numbers[]) {
	    int length = numbers.length;
	    for(int i = 0; i < length; ++i) {
	        if(numbers[i] < 0 || numbers[i] > length - 1)
	            throw new IllegalArgumentException("Invalid numbers.");
	    }

	    for(int i = 0; i < length; ++i) {
	        while(numbers[i] != i) {
	            if(numbers[i] == numbers[numbers[i]]) {
	                return numbers[i];
	            }

	            // swap numbers[i] and numbers[numbers[i]]
	            int temp = numbers[i];
	            numbers[i] = numbers[temp];
	            numbers[temp] = temp;
	        }
	    }

	    throw new IllegalArgumentException("No duplications.");
	}

It throws two exceptions in the code to make the code complete and robust. If there are any numbers out of the range between 0 and n-1, the first exception is thrown. If there is no duplication in the array, the second exception is thrown. It is important for candidates to write complete and robust code during interviews.

## Advanced complex

### Question 53 

Please print a matrix in spiral order, clockwise from outer rings to inner rings. For example, the matrix below is printed in the sequence of `1, 2, 3, 4, 8, 12, 16, 15, 14, 13, 9, 5, 6, 7, 11, 10`.

	1   2   3   4
	5   6   7   8
	9   10  11  12
	13  14  15  16

It looks like a simple problem because it is not about any complex data structures or advanced algorithms. However, the source code to solve this problem contains many loops with lots of boundary values. Many candidates find themselves in a pickle if they begin to write code before they get clear ideas of all the issues involved.
Figures are helpful tools to analyze problems. Since it is required to print to a matrix from outer rings to inner ones, a matrix is viewed as a set of concentric rings. Figure 6-4 shows a ring in a square matrix. A matrix can be printed in a for or while loop starting with outer rings and moving to the interior in each iteration.

Figure 6-4. A matrix is composed of a set of rings.

Let’s analyze when to end the iteration. Suppose there are r rows and c columns in a matrix. Notice the row index and column index are always identical in the beginning element in each ring at the top left corner. The index of the beginning element in the ith ring is denoted as (i, i). The statements `c>i×2` and `r>i×2` are always true for all rings in a matrix. Therefore, a matrix can be printed iteratively with the code shown in Listing 6-3.
Listing 6-3. Java Code to Print a Matrix

	void printMatrixClockwise(int numbers[][]) {
	    int rows = numbers.length;
	    int columns = numbers[0].length;
	    int start = 0;

	    while(columns > start * 2 && rows > start * 2) {
	        printRing(numbers, start);

	        ++start;
	    }
	}

Let’s move on to implement the method printRing to print a ring in a matrix. As shown in Figure 6-4, a ring can be printed in four steps. It prints a row from left to right in the first step, a column in top down order in the second step, then another row from right to left in the third step, and finally a column bottom up.
There are many corner cases worthy of attention. The innermost ring in a matrix might only have a column, a row, or even an element. Some corner cases are included in Figure 6-5, where it only needs three steps, two steps, or even one step to print the innermost ring.

Figure 6-5. It may take three, two, or even one step to print the last ring in a matrix.

We have to analyze the prerequisites for each step. The first step is always necessary since there is at least one element in a ring. The second step is not needed if there is only one row remaining in the last ring. Similarly, the third step is needed when there are two rows and two columns at least in a ring, and the fourth step is needed when there are three rows and two columns. Therefore, the method printRing can be implemented as shown in Listing 6-4.
Listing 6-4. Java Code to Print a Ring in a Matrix

	void printRing(int numbers[][], int start) {
	    int rows = numbers.length;
	    int columns = numbers[0].length;
	    int endX = columns - 1 - start;
	    int endY = rows - 1 - start;

	    // Print a row from left to right
	    for(int i = start; i <= endX; ++i) {
	        int number = numbers[start][i];
	        printNumber(number);
	    }

	    // print a column top down
	    if(start < endY) {
	        for(int i = start + 1; i <= endY; ++i) {
	            int number = numbers[i][endX];
	            printNumber(number);
	        }
	    }

	    // print a row from right to left
	    if(start < endX && start < endY) {
	        for(int i = endX - 1; i >= start; --i) {
	            int number = numbers[endY][i];
	            printNumber(number);
	        }
	    }

	    // print a column bottom up
	    if(start < endX && start < endY - 1) {
	        for(int i = endY - 1; i >= start + 1; --i) {
	            int number = numbers[i][start];
	            printNumber(number);
	        }
	    }
	}

#### Test Cases
- Functional Cases (A matrix with multiple rows and columns)
- Boundary Cases (A matrix with only a row, a column, or even an element)

---



---

1. Linked list versus array
What are some important differences between a linked list and an array?
2. Array versus associative array
Describe a common scenario that would lead you to prefer an associative array to a simple array.
3. Self-balancing binary search tree
What is a self-balancing binary search tree?
10. String permutations
Write a method that will generate all possible permutations of the characters in a string. The signature of your method should look like this:
public static List<string> Permutations(string str)
11. Prime numbers
Write a method that will generate N number of primes. Start with a naïve implementation and suggest how it might be optimized.

---


### 10. String permutations

Write a method that will generate all possible permutations of the characters in a string. The signature of your method should look like this:
public static List<string> Permutations(string str)

Finding permutations of the characters in a string is a popular interview puzzle. It is a good example of a problem where you can reduce the general case down to a simple base case that is easily solved, and that makes it a perfect candidate for a recursive solution.
Let's look at the base case first.

For characters in the string “A” there is exactly one

	A

A string of two characters “AB” is nearly as straightforward; there are exactly two permutations:

	AB
	BA
For three characters in the string “ABC” there are six permutations:

	ABC
	ACB
	BAC
	BCA
	CAB
	CBA

If you look closely at the last two permutations for the string “ABC” you will notice that “AB” and “BA” follow the character “C,” which is the exact same result gotten for permutations of the previous example “AB.” This is the clue that leads you to a generalized algorithm for obtaining permutations of characters in a string:

The permutations of the characters in a string are obtained by joining each character of the string with the permutations of all remaining characters in the string.

So for “ABC” you first list each character:

	A
	B
	C

Then to each character you append permutations of the remaining characters in the string.
For A you need to append the permutations of “BC,” giving

	A + BC = ABC
	A + CB = ACB
For “B” you append the permutations of “AC” as follows:

	B + AC = BAC
	B + CA = BCA
For “C” you append the permutations of “AB” as follows:

	C + AB = CAB
	C + BA = CBA
This translates into the following code:

	public static List<string> Permutations(string str)
	{
	    // Each permutation is stored in a List of strings
	    var result = new List<string>();

	    // The base case...
	    if (str.Length == 1)

	        result.Add(str);

	    else

	        // For each character in the string...
	        for (int i = 0; i < str.Length; i++)

	            // For each permutation of everything else...
	            foreach (var p in Permutations(EverythingElse(str, i)))

	                // Add the current char + each permutation
	                result.Add(str[i] + p);

	    return result;
	}

	// Return everything in a string except the char at IndexToIgnore
	private static string EverythingElse(string str, int IndexToIgnore)
	{
	    StringBuilder result = new StringBuilder();

	    for (int j = 0; j < str.Length; j++)
	        if (IndexToIgnore != j)
	            result.Append(str[j]);

	    return result.ToString();
	}

The number of permutations for a string of n characters is equal to n!, which makes this function impractical for all but the smallest strings (as you can see from Table 5.5).
Table 5.5 Permutations Generated for Characters in a String

	Size of String	Number of Permutations
	1	1
	2	2
	3	6
	4	24
	5	120
	6	720
	7	5,040
	8	40,320
	9	362,880
	10	3,628,800
	11	39,916,800
	12	479,001,600
	13	6,227,020,800
	14	87,178,291,200
	15	1,307,674,368,000

Assuming the earlier code was able to generate 100,000 permutations per second (which is probably an optimistic estimate), then for a string of 15 characters it would take around 151 days to run. Besides the impractical run time, the machine would almost certainly run out of memory before the program finished, unless you took steps to offload the generated permutations to disk.

### 11. Prime numbers

Write a method that will generate N number of primes. Start with a naïve implementation and suggest how it might be optimized.

Writing a method that can generate prime numbers is relatively easy. The challenge in this question is to improve on the naïve implementation. If you start with a very simple algorithm you will produce something like this:

	public static List<int> GeneratePrimes(int n)
	{
	    var primes = new List<int>();

	    int nextCandidatePrime = 2;

	    primes.Add(nextCandidatePrime);

	    while (primes.Count < n)
	    {
	        if (isPrime (nextCandidatePrime))
	            primes.Add(nextCandidatePrime);

	        nextCandidatePrime += 1;
	    }
	    return primes;
	} 

	private static bool isPrime (int n)
	{
	    for (int i = 2; i < n; i++)
	    {
	        if (n % i == 0)
	            return false;
	    }
	    return true;
	}

This is a terrible algorithm, completely unoptimized, but it works. I tested this on my Asus Zenbook laptop (I'm a masochist) and found it took well over seven minutes to find 100,000 primes. You can do a lot better than that!

The first obvious optimization is that you don't need to test so many numbers. When you're testing for numbers that divide evenly into n you need to check only the numbers up to the square root of n. The reason is simple: If n is not a prime then there must be two numbers a and b such that

	a × b = n

If both a and b were greater than the square root of n then a × b would be greater than n; therefore, at least one of these numbers must be less than or equal to the square root of n. Because you only need to find one of these numbers in order to conclude n is not a prime number, you don't need to look any further than the square root.

	private static bool isPrime(int n)
	{
	    for (int i = 2; i <= Math.Sqrt(n); i++)
	    {
	        if (n % i == 0)
	            return false;
	    }
	    return true;
	}
With this simple modification you now have a much faster algorithm. Testing on the same laptop shows that you have improved from well over seven minutes to about 1.5 seconds:
100000 primes generated in 00:00:01.3663523

You can still do better. Another optimization is that you don't need to check every divisor in sequence; you can skip all the even numbers. The reason is that if a candidate prime is also an even number then it is obviously not a prime number. If you add a simple test to check for even numbers in the isPrime function, then you can skip all the multiples of 2 in the inner loop:

	private static bool isPrime(int n)
	{
	    if (n % 2 == 0) return false;
	    for (int i = 3; i <= Math.Sqrt(n); i += 2)
	    {
	        if (n % i == 0)
	            return false;
	    }
	    return true;
	}

This approximately halves the run time of the isPrime method:
100000 primes generated in 00:00:00.7139317

Not only can you skip all the even numbers, you can also skip numbers that are divisible by 3, or by 5 (skipping 4 because it is a multiple of 2), or by 7, and so on, all the way up to the square root of the number being evaluated. In a flash of insight you might realize that the numbers just described are in fact the prime numbers themselves, and further, that because you're building up this set of prime numbers as you go you will have them conveniently available to use when testing each candidate.

	public static List<int> GeneratePrimesOptimized(int n)
	{
	    var primes = new List<int>();

	    // Prime our list of primes
	    primes.Add(2);
	            
	    // Start from 3, since we already know 2 is a prime
	    int nextCandidatePrime = 3;

	    // Keep going until we have generated n primes
	    while (primes.Count < n)
	    {
	        // Assume the number is prime
	        bool isPrime = true;

	        // Test if the candidate is evenly divisible
	        // by any of the primes up to sqrt(candidate)
	        for (int i = 0; 
	             primes[i] <= Math.Sqrt(nextCandidatePrime); 
	             i++)
	        {
	            if (nextCandidatePrime % primes[i] == 0)
	            {
	                isPrime = false;
	                break;
	            }
	        }
	        if (isPrime)
	            primes.Add(nextCandidatePrime);
	                
	        // We proceed in steps of 2, avoiding the even numbers
	        nextCandidatePrime += 2;
	    }
	    return primes;
	}

Once again you have approximately halved the run time:
100000 primes generated in 00:00:00.3538022

This is about as fast as it gets with a naïve algorithm. You may also benefit from investigating alternative algorithms, and a good starting place would be to familiarize yourself with the sieve of Eratosthenes, an ancient method that works as follows.
To find all prime numbers up to n:

1. Create a list of integers from 2 to n.
2. Start with the first prime number, p = 2.
3. Count up from the start of the list in increments of p and cross out each of these numbers.
4. Find the first available number greater than p in the list. If there is no such number then stop, otherwise, replace p with this number (which is also the next prime) and repeat from step 3.

When this algorithm terminates, all the numbers remaining in the list are prime numbers.
Here is an illustration of how each step refines the list, “sieving” the numbers to leave just primes.

You start with a consecutive sequence of integers:

	2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, …

You then remove multiples of 2 (greater than 2):

	2, 3, , 5, , 7, , 9, , 11, , 13, , 15, …

You then remove multiples of 3 (greater than 3) and so on, leaving just prime numbers:

	2, 3, , 5, , 7, , , , 11, , 13, , , …

As a final note, a practical alternative to generating your own prime numbers would be to obtain a pre-calculated list of prime numbers and store this list in a data structure that affords 0(1) look-up times. An interviewer won't be happy if you “cheat” this way, but at the same time she will admit that in practice you could reuse an existing list rather than reinvent your own. You might get marks for pragmatism if not lateral thinking.

---

# General

## Describe some things you always do when troubleshooting

Some programmers seem to have a knack for troubleshooting. When a customer reports a problem that can't be reproduced in the testing environment, these savant programmers will start twitching and blinking, and before you know it they have guessed the problem and are halfway done coding a solution.

The rest of us, unfortunately, need to work a bit harder.

What are some things that you always do (or try to do) when diagnosing a problem?
The first thing to do is try to get a clear understanding of the problem. All too often a bug report is vague or ambiguous. This is a common problem because most users are not trained in how to give good bug reports (nor should they necessarily be trained), and so they will make a bug report that reads like a medical complaint.

“I was trying to print the annual report, and when I click on the print button the screen shows red blotches and then nothing happens.”
If you're lucky the bug report will come from a professional tester or another programmer, and clarity won't be an issue.
After you understand the problem the next thing to try to do is reproduce the problem. Sometimes the problem can't be reproduced in the test or development environments and so you need to—very carefully, cognizant of possible bad outcomes—try to reproduce the problem in the production environment.

Reproducing the problem is often a major obstacle. It can be very difficult to do because sometimes the cause of a problem is something that you least expect. Sometimes the cause of a problem is two or more events coinciding. When you're trying to reproduce a problem, keeping alert, observing carefully, and keeping an open mind are vital. At this stage, you are trying to develop testable theories about why and how a problem occurs.
After the problem is reproducible (and sometimes you have to accept that a problem will occur infrequently) the next step is to isolate the source of the problem. This means trying to identify which parts of the system and which lines of code are involved. This, too, can be very difficult, especially when a problem is caused by the design of a system rather than any specific line of code.

To isolate a problem caused by poor coding, reverting to a known good state and working forward until the problem reappears can be helpful. After you know the point at which a problem appears, isolating the code that is potentially causing the problem is much easier.

Sometimes a problem is caused by a new kind of data, something the system has not previously handled. This can uncover latent bugs in code even when the code has not been changed. In large databases it can be very difficult to find the “new type” of data because most often you won't know what exactly you are looking for. Good logging can help; in fact, in all cases having a good logging system in place is helpful.

A good logging system is one that you can switch on when you need it and one where you can adjust the level of detail recorded in the log. A good logging system is one where you can look at the timing of events and where you have enough detail about each event that you can simulate (or even better, replay) the event in your development or test environment.

After you have identified the source of a problem you need to come up with a credible fix. This is sometimes the most dangerous part of troubleshooting, especially when a programmer is under pressure to get something fixed as soon as possible. The danger lies in making a mistake while rushing to fix the problem. If anything, this is the time when the programmer needs to slow down, consider the implications of the fix, have the fix reviewed by another programmer or tester, and then test the fix before releasing it to the live system. The last thing you want to do is make the problem worse or introduce another kind of problem that is worse than the original.

Here are some more ideas for effective troubleshooting. This is not a comprehensive list, and you should be able to think of more things that have worked for you in the past.

When a problem starts occurring you should ask, “What has changed?”
Keep in mind that events that occur close in time are not necessarily related. As they say, correlation does not imply causation.
Check most-likely causes before investigating less-likely causes.
Keep in mind that some bugs are caused by more than one problem.
Some bugs are caused by the precise timing of events.
Sometimes you won't be able to use sophisticated debugging tools, so don't forget about the humble print statement as a debugging tool.
Sometimes the problem will be outside your domain of expertise. If you need help to develop or test theories about a problem, you should not hesitate to involve other domain experts; for example, network or database administrators.

## How do you go about getting familiar with a large code project?

Every time you change jobs a good chance exists that you will need to quickly become familiar with a large new code base.
How do you do that?

There is no single best way for a programmer to quickly become familiar with a large new code project. Here are some things that have worked for me, but your experience might be quite different:

One good way to learn a new code base is to have another programmer, someone who is familiar with it, give you a tour.
Every program has an entry point. Start a debugging session and see how the program sets itself up, which configuration files it reads, what database connections it establishes, which queries it runs, and so on. For most languages the entry point to an application is the main function. For static websites the default page is often default.htm or index.htm but it depends on how the site is configured. For ASP.NET applications usually some start-up code is in global.asax.

Start working with the code base by trying to implement a small feature, perhaps a simple bug fix.
Keep notes about what you find. My experience has been that I rarely refer back to these notes, but the act of writing down important things helps me remember them.

Pay particular attention to anything you don't immediately understand. It could be that the code base relies on a particular convention or an idiomatic style of coding. These “strange” things are often very significant for an application.
Don't forget that non-technical staff can also have good insight into how a system works.
A modern IDE (and even some text editors) will help you navigate a code base; for example, making it easy to jump back and forth between class and method definitions.

Treat any program documentation with suspicion. Look at when it was last updated.
Unit tests (assuming they pass) can be very helpful in understanding how functions are supposed to work, the arguments they accept, and what kind of setup is needed to make things work.
Bug-tracking software can give you an indication of typical problems found in the code base. This can give you some clues about its weaknesses and perhaps also its strengths.
If the application persists data and if the data has been modelled properly you might find important clues about the key entities of the system in the persistence later or the database itself. Foreign-key relationships can give you significant information about how data is structured (for example, “a purchase order is associated with either zero invoices or one invoice”). If the application uses an ORM then you should be able quickly find the key entities, and how they are used throughout the application.
Finally, don't think that you need to understand every little detail in order to work successfully with a new code base. You will need to treat some things as “black boxes,” accepting that they perform a function without necessarily understanding how they work. This will help keep you focused on the big picture.

## Explain the significance of coupling and cohesion.

You often hear these terms in debates about the implementation of an application. What is the programming-related meaning of the words coupling and cohesion, and why are they significant?
If you have just one class in your application and everything you write goes into that class then you probably have low cohesion. On the other hand, if you have classes that follow the single responsibility principle (the “S” in SOLID) then you probably have higher cohesion. Cohesion is a measure of how well the parts of a module (or the members of a class) fit together. It's just like that game on Sesame Street, the one where they sing one of these things is not like the other. If you have lots of things that are not like the other then you probably have low cohesion.
The main problem with low cohesion is that it works against the central aims of writing modular, reusable code. It makes reusing a module harder because you get all the unwanted baggage that comes with it. With low cohesion you also find that changes become more complex, requiring tweaks in more places.

Coupling refers to the degree of interdependence between classes. If you make changes to code in a highly coupled application you are more likely to cause unintended side-effects.
The main problem with highly coupled code is obvious. You don't want to be needlessly worried about unintended consequences when you change a line of code.

Low cohesion and high coupling often go hand in hand, and both indicate a lack of planning (or upkeep) in designing and implementing an application.

## What is the real problem with global variables?

A global variable is a variable that is available throughout an entire application, regardless of where it is referenced.
That sounds quite handy, so what is the real problem with global variables?

In very small programs, say less than 100 lines of code, a global variable isn't such a big deal. The programmer can probably keep all these variables in her head while she writes code, and being able to reference the variable at any point and at any place in the program is probably quite useful.

After a program grows beyond a certain size (as most useful programs invariably do) then the problems of global variables become more obvious. Here are a few of the worst problems:
A global variable relies on the programmer remembering to set it as needed. They are implicitly present everywhere, so forgetting about them is easy.

If a program has more than one thread then these threads can come into conflict when both attempt to set and/or get the value of this variable around the same time.

Global variables make understanding code harder, because their existence must be either remembered or deduced and tracked down.
Global variables never fall out of scope, so they stick around and consume memory for as long as an application is running.

## Explain the term refactoring in terms that a non-technical manager will understand

The term refactoring is commonly used by many programmers and is generally understood to mean something positive. Explain in non-technical terms what the term means.

In the course of writing a program a programmer will routinely make many, many decisions about the detailed design of a program. In an ideal world, many of these decisions will have been considered long before the programmer started writing code but the reality is that writing code often reveals gaps in the analysis of a problem or in the design of a feature. These gaps are most often relatively minor; for example, choosing data types for a function, and the programmer will rely on judgement and experience to make good decisions. If the programmer were to pause for discussion at every decision point then progress would be painfully slow.

Programmers usually make the right decisions as they write code, but not always. Suppose a programmer decides to use a string data type to hold a date value, perhaps thinking that values like “tomorrow” and “five weeks from now” should be allowable in addition to proper dates such as “2001-01-01” and “2029-04-07.”

The programmer who makes a decision like this might come to regret it, perhaps finding that a lot of time and effort is now being spent explaining why “tomorrow” never comes. The programmer might now want to refactor the code so that all date values entered into the system conform to a pattern of “YYYY-MM-DD.” This is not a bug-fix per se, because the decision to permit unusual date values was deliberate, and the program is running according to the programmer's design.

The example of a poorly chosen data type is one where the end user will see evidence of the problem and will, therefore, readily agree that something needs fixing. Persuading a product owner that something needs fixing isn't hard to do when it is readily apparent that something is broken.

Programmers know that many things make the upkeep of a program more difficult than it should be, but that the end user will never see any of these things directly. If you find duplicated code you know that this can cause many kinds of problems but justifying the required time to fix it can be difficult when the end user sees no difference at all in the fixed product.

Refactoring is the process of fixing these internal problems without changing the external behavior of an application.
When trying to persuade a product owner that time should be spent fixing these internal problems, Andrew Hunt and David Thomas, writing The Pragmatic Programmer suggest that you use a medical analogy:

…think of the code that needs refactoring as a “growth.” Removing it requires invasive surgery. You can go in now, and take it out while it is still small. Or, you could wait while it grows and spreads—but removing it then will be both more expensive and more dangerous. Wait even longer, and you may lose the patient entirely.

## What is continuous integration and how is it helpful?

The concept of continuous integration has progressed to the point where you can buy specialized tools to support the practice.

What does the term continuous integration mean, and how is it helpful?

In the (heavily stereotyped) bad old days of programming, individual programmers would work in isolation for periods of time before sharing their efforts (the code they produced) with the rest of the team. This would often result in serious delays and awkward problems caused by a mismatch of expectations and incompatibility between the code submissions of these developers.

Consequently, integrating these individual contributions as often as practically possible is now generally accepted as good practice. Developers following the practice of continuous integration are therefore encouraged to share their work-in-progress, and to accept the work-in-progress of other developers, thus minimizing the potential divergence of ideas and coded implementations within the team.

One of the immediate problems faced by teams practicing continuous integration is that they are often derailed by a submission of faulty code. If one developer “breaks the build” then all developers who have accepted this code into their working copy will have to either work on the problem or wait until another developer fixes it.

The problem of sharing faulty code is addressed by software that performs frequent automated builds. These builds are performed either periodically or whenever a developer commits code to the shared code repository. If the build fails, then developers in the team are notified that the most recent code submission is faulty and should be avoided.

When the automated build system informs the developers that the code has been fixed then they are again free to integrate the latest changes from other developers in the team.

The concept of build failure originally meant simply that the code would not compile, but today it means much more:

Unit tests are run as part of a build, and if any of these tests fail then the build itself is considered to have failed.

Coding conventions are checked by automated tools during the build, and if code is found to not follow these conventions then the build fails.

Code metrics are checked by automated tools, and if these metrics are not within acceptable levels then the build fails.

Documentation can be generated directly from the code, and if it cannot (for example, a public method is not commented) then the build fails.

# Behavioral

This section contains question that you might encounter concerning your past programming experience, your personal goals, your attitude towards groups, and working with others as well as how you would handle certain work situations.

- What do you know about this company?
- Briefly talk me through your work history.
- Describe your most recent role in more detail.
- Tell me about the last team you worked with.
- What motivates you?
- Tell me about the most difficult project you've worked on.
- What is your proudest achievement at work?
- Describe a work situation where you had to resolve a conflict with a peer.
- What kind of role do you typically play in a team?
- Tell me about a time when you argued in favor of an unpopular decision.
- Describe how you handled a situation where you disagreed with a decision for technical reasons, but were overruled for business reasons.
- What would your previous boss say about you if I were to call her?
- What aspects of your experience make you a good fit for this job?
- What things have you learned from you last job?
- Tell me about your most recent project experience.
- What are you least skilled at (non-technical)?
- Describe the best/worst team you've been part of.

