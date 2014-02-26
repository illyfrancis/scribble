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

From the other side (i.e. service side)...

- Can S 
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