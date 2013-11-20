# Failure - The Good Parts

By Viktor Klang - Webinar on Oct 30 2013.

# Failure - The Bad Parts

** Ariane 5

- launch 4 June 1996
- 10 years of research
- $7 billion invested
- explored within a minute of take-off
- loss estimate $370 million
- why?
	- trying to stuff a 64-bit float into a 16-bit int
	- o_O + wat

# Failure is an option

# Failure Recovery

No plan...

# Define Failure

# Undefine Failure

# Software fails

## Runtime

- VM
- OS
- Drivers
- Firmware
- Overload/Exhaustion
	- Stack
	- Heap
	- FDs
	- ...
- Starvation

# Hardware fails

- CPUs (Pentium FDIV error)
- RAM
	- DRAM errors in the wild: A Large-scale field study (see below)
- HDD
	- Failure trends in a large disk drive population

### DRAM errors in the wild

- Memory errors were between 15-120 times more common than had previously been assumed
- More than 90% of the problem with a given platform were caused by about 20% of the machines who had errors.
- without ECC (error correcting code) memory the rates are higher on different platform
- Temperature didn't seem to make a big difference
- Irreparable problems were more common than transient problems
- increased number of errors with age, setting in as early as 10-18 months in the field
	- if you're using non-ECC e.g. macbook etc more error prone
	- should run memory test once in a while

### Failure trends in a large disk drive population

- Failure trends by age
	- more likely to fail earlier than 1 year (down until one year)
	- then goes up after 2 years
- Failure Trends by utilization and age
	- essentially the more use it's likely to break in the initial term
	- but after 3 years, the lower use it's likely to break more for lower use than higher use

# The Network is Reliable

LOL

Kyle Kingsbury's blog on post failure etc

# Human error

> An expert is a man who has made all the mistakes which can be made in a narrow field (Niels Bohr)

# Validation vs Failure

Do not conflate the two.

	Validation is intentianal		vs		Failure is unintentional

# Flows of information

- Results & validation (could go in the same channel)
- Failures & recovery
- Don't complect them!

# The Little Vending Machine That Could

- validation is the communication between vending and customer
- but failure is not
- is the consumer should be responsible for failures in the vending machine?
	- No. customer or consumer is not responsible to fix the issue or even to report the problem.

# Outcome awareness

- known-knowns
- known-unknowns
- unknown-knowns (we might not remember)
- unknown-unknowns

# Failure awareness

The same

- known-knowns
- known-unknowns
- unknown-knowns (we might not remember)
- unknown-unknowns (we don't even know if it exists)


# Possibilities

- result
- invalid input
	- illegal value
	- illegal value combination
- capability / dependency violation
- nothing
	- uninvoked
	- response lost

# Testing & Checking

Testing is good for

- Known-knowns

Checking is good for

- Unknown-knowns
- known-unknowns
- unknown-unknowns

Use both.

> scala check  - quick check for checking...

What the heck is:

	System.out.checkError 

Apparently you can check and reset the faulty output stream.

# Death & Delay & Distributed Programs

- There's no apparent difference between death and delay in a distributed system
- "Distrub Programming is all about retries and timeouts"
- without distribution you'll always have SPOF (Single point of failure)
- ... but the more hardware you have the higher the risk of failures

# Traditional Blocking RPC

- what if: request is lost
- what if: response is lost
- caller is held hostage by the callee
- ... stockholm syndrome anyone?

# Defensive programming

- "Paranoid programming"
- Mixes concerns
- Unclear responsibilities
- At best gives sense of false security
- Yields systems that fail extraordinarily 
	because only caters for known-knowns

# To make failure ...

- Distribution
- Replication & failover

# CircuitBreakers

- Benefits
	- relieves pressure on failing parts
	- are self-healing
	- can be operated manually

# Supervisors

- components dealing with the failure of subcomponents
- decouples failure from validation
- makes it obvious who is responsible for what

# Supervision

Who guards the guardians... 

Quis Custodiet ipsos custodes? Decimus lunius luvenalis

# Bulkheading

- techniques used by boat builders
- compartmentalization
- prevent failures from cascading
- plays well with redundancy and failover


# Graceful degradation

- an escalator can never break; it can only become stairs.
you should never see an esca temporariy out or

Mitch ... comedian

# Vik crystal ball

## Microservices
- does one thing well
- concurrent & compartmentalized
- location transparent
- typed endpoints producing typed streams of data
- exhibit compositionality
- are async and non-blocking (for better use of resources)
- support backpressure & flow control

# Summary

- failure management 
	- is not validation
	- need not be boring
	- is not optional
- there are real consequences
	- and there are ways to avoid them