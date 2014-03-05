# Integration Tests are a Scam

> From http://www.infoq.com/presentations/integration-tests-scam

### Some notable mentions

- Book Growing object oriented software... GOOS
- Test doubles
- SOLID, clean code, 
- Four rules/elements of simple design
	1. runs all the tests | passes its tests
	2. no duplication | minimizes duplication
	3. expresses developer intent | maximizes clarity
	4. minimizes the number of classes and methods | has fewer elements
- But point 1 & 4 are redundant so
	1. minimizes duplication
	2. maximizes clarity
- Or simply, **remove duplication and fix bad names**.

### DDD recap

- Values: no id
- Entities: has id
- Services: stateless

### Some battle stories

What's the problem with long running tests?

- slow feedback -> eventually no feedback
- people ends up short-circuit the tests and only run partial tests
- or not run at all
- leads to false security

Value of having the test == Value of not having test at all.

### Test shapes (00:16)

Remind that all tests are in the form of:

- Arrange
- Act
- Assert

Small cluster of tests -> Excessive test set up

What's the prob with this?

1. Because of Excessive test set up, ppl tend to assert a lot (multiple assertions). Which goes against the principle of single assertion per test.
2. Don't be tempted to include multiple tests into one. (00:21)
3. Problem with tests overlap, same assertions.
4. Caused by hard-coded dependencies that cannot be tested in isolation. (00:26)

### Prob with integration test (00:27)

- when fails it's unclear where the failure is
- tends to take longer than isolated / focused tests
- get false sense of security
- when testing in clusters, you need to write a lot -> not possible (00:30) -> and doesn't give more coverage even writing more

### What do we do

Refer to GOOS book.

Common objection:

- not customer tests (has to be end to end), but context is the programmer tests
- basic correctness 
	- means given the myth of perfect technology, do we compute the answer
	- not dealing with non-functional requirements (scalability, performance ...)
	- test one thing at a time (focused object tests) -> fast test execution

### Focused object test (00:41) - Collaboration tests

Don't test platform or library.

What do they look like?
- assume the object depends on four services
- put an interface in front of services
- the goal is answer two questions
	- Q1. Do I ask the collaborators right questions? (does it invoke the service method with the right parameter?)
	- Q2. Can I handle all their responses?
	- use test doubles (mock) for this

Example:

- controller calls the repository (the model) with the right data (00:50)
- repository returns a set of customer
- for this stub out the responses

These are called interaction based tests (he likes to calls it collaboration test).

These test still isn't enough. It's only half the tests (00:53) We still get integration failures.

### The other half (0:55:40) - Contract tests

From C side,

- Do I ask the right question
- Can I handle the response

From the other side (i.e. service side)...

- Can S handle the question
- Does S respond that way, really...

Test that verifies the 

...

### How do we know there's contract test for collaboration tests (01:10)

Can't automate it yet...

But manually justify mocked objects by having the contract tests.

### Questions (01:16)

Q1: (1:16) still have big setup problem even with Mock
Q2: (1:21:24) solid principals etc clean code -> four elements of ... Kent Beck (remove duplication and fix bad name)
Q3: (1:24:55) don't throw away BDD, still need to clarify the requirement but cannot substitute basic correctness test.
Q4: (1:27:30) only put interfaces in front of service, not values or entity
Q5: (1:30:30) couldn't hear well but... what if mocking is harder than creating a real object... -> code smell, need to refactor possibly doing too much.

### Examples

If A calls B.foo(x,y,z) and I want to check that A gets x,y,z right, then I write collaboration tests that show how A chooses characteristic values for x,y,z. For example, which inputs to A or which system states cause A to choose x = 12 instead of x = 15 or 20?

If B.foo(x,y,z) rejects x < 0, y < 10, z > 50 then write contract tests on B.foo() for the boundary cases.

# My questions

When writing contract tests can it depend on mocks? This [question](http://thecodewhisperer.tumblr.com/post/1172613515/stub-your-data-access-layer-it-wont-hurt) asks the same thing but no clear answer.

### More questions

- This [link](https://groups.google.com/forum/#!topic/growing-object-oriented-software/ma3WPD45Oek) contains other presentations on TDD etc including
	- [TDD, where did it all go wrong](http://vimeo.com/68375232)
		- okay watch, don't agree entirely
	- [Test driven development (that's not waht we meant)](http://vimeo.com/83960706)
		- [slide](http://gotocon.com/dl/goto-berlin-2013/slides/SteveFreeman_TestDrivenDevelopmentThatsNotWhatWeMeant.pdf) for it

# TDD, where did it all go wrong

> http://vimeo.com/68375232

- Re-read test-driven development by example (kent beck)

## Zen of TDD

#### Avoid testing implementation details, test behaviors
- A test-case per class approach fails to capture the ethos for TDD
	- Adding a new class is not the trigger for writing tests. The trigger is implementing a requirement
- Test outside-in, (though use ports and adapters and making the 'outside' the port), writing tests to cover then use cases
- Only writing tests to cover the implementation details when you need to better understand the refactoring of the simple implementation we start with

#### Dan North (who the hell is he?)

Agile expert and originator of BDD

Act | Arrange | Assert

#### What is a unit test?
- Kent Beck, a test that 'runs in isolation' from other tests
- ??? (22:00???)

#### Red-green-refactor

#### Clean code when?
- Now
- Test behaviors not implementations

### Ports and adapters

Ice-cream vs testing pyramid

... not bored 47:56

# Test driven development (that's not waht we meant)

> http://vimeo.com/83960706

### Security Theater

Doesn't add anything...

-> Testing Theater

### Example

A common bad test.

```
BasketTest.add_adding_item()
	sut = new Basket()
	sut.add(ITEM)
	assertEquals(
		ITEM,
		backdoor(sut, "itemList")[0])
```

There's no intend, no consequences.

Instead write readable code.

```
is_empty_when_created()
	assertThat( new Basket().itemCount(), equals(0))

returns_items_in_the_order_they_were_added()
	basket = new Basket()
				.add(pen).add(ink).add(paper)
	assertThat(basket,
			hasItems(pen, ink, paper))

totals_up_the_cost_of_its_items()

fails_when_removing_an_absent_item()
...
```

- Interfaces not internals
- Protocols, not interfaces
- From simple to general
- It's about explaining the domain, not about proving the correctness of the code (Andrew Parker)

### When you're lost, slow down