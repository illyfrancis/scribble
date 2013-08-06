# Introduction

## Background

The purpose of this document is to provide high-level design for VUS system and to outline the integration approach with its external dependencies. The design is based on the business requirements document (BRD) titled `P6 ????` prepared by `??? author ???` that captures the business drivers and the detail description of the business requirements.

The reader of this document should refer to the business requirements document to grasp the full business context for which the solution is designed.

## Audience ???

This document is prepared for (??? Who is the target audience ???).

## Assumptions

The high-level design is produced to fulfill the business requirements outlined in the BRD. Along with the requirements the BRD states a number of assumptions with regards to external systems development and readyness of those systems. The details are captured in the BRD under the section `Assumptions` and it is reproduced below, in verbatim, for the convenience of the reader.

#### Assumptions from BRD

1.	Asia Blueprint Phase 1A and 1B will be in production and all of the upstream processing applications, except Mutual Funds, will pass cash data through CTA to a Real Time Cash Repository (RTCR).  Mutual Funds cash data flows into the RTCR via GTPS, GFTS or Tiger depending on the transaction.
2.	Spectrum will have replaced FX Legacy for Foreign Exchange processing.
3.	A new FX Away process will have been developed in support of SEB’s FX away requirements.
4.	The UAF will store an account relationship table linking the A accounts to the B and/or C accounts.  
5.	The existing FX Y/N indicator on an ActionWorld R/P will be part of the AW cash transaction data sent to the RTCR.
6.	The existing Withholding Agent indicator on an ActionWorld R/P will be part of the AW cash transaction data sent to the RTCR.
7.	MFPS will have created a new FX Y/N indicator as part of the F/X Away process.

In addition to aforementioned assumptions from the BRD, this document makes the the following design assumptions and observations.

#### Transaction information

* The source of the transaction information is originated from RTCR and RTCR only.
* It is also expected that RTCR (or its subsystem) establish a connection to VUS via message channel and place (publish or put) a transaction information in the form of a message on the message channel.
* RTCR (or its subsystem) only directs 'cash-away' related transaction messages. Initially it will be for SEB only but it must allow for more than one 'cash-away' client.
* Each transaction message sourced from RTCR must contain (in addtion to other transaction information):
    - Unique RTCR Id
    - Primary ***transaction identifier***, comprised of source type and transaction number
    - Associated ***transaction identifier*** (this is the other leg of transactions) ???
    - Related head account number (full 11 digits)

#### Trasanction identifer

* The uniqueness of the transaction is determined by the combination of the source system type and the transaction number. For example a cash trade transaction record from RTCR can be uniquely identified by `SONIC 7890`.
* Any transaction records within RTCR with the same transaction identifier (e.g. `SONIC 7890`) refer to the same underlying transaction.
* All external systems will provide ***transaction identifier*** in the messages to VUS where the ***transaction identifier*** follows the same scheme as above. i.e. `Source system type + Transaction number`.

#### RTCR

As briefly noted, VUS expects the transaction information to flow from RTCR. It is expected that RTCR (or its subsystem) establish a connection to VUS via message channel and place or publish a transaction information in the form of a message on the message channel.

Furthermore, the expectation is that RTCR (or its subsystem) only directs 'cash-away' related transaction messages. Initially it will be for SEB only but it must allow for more than one 'cash-away' client.

#### FX Away

FX Away is to produce two types of messages to VUS. One message is to indicate if a trasanction would require an FX and the other message type is for providing FX execution details for a transaction. 

For both cases, the expectation is that each message to contain ***transaction identifier*** such that the information provided in the message can correlate to the transaction in consistent manner.

#### MFPS

As per FX Away, any additional information relating to a transaction provided by MFPS will contain ***transaction identifier***. 

There are two scenarios where MFPS sends messages to VUS, one for providing FX requirement for a transaction and the other to supply information relating to fees for a transaction. In either cases, VUS expects consistent treatment of the ***transaction identifier***.

#### Reference data

In addition to transaction information (from RTCR), FX information (from FX Away and MFPS) and fees information (from MFPS), the BRD assumes certain functionality and reference information to be provided by UAF and by the 'Static database' (as it is named in BRD).

To summarize, UAF will provide 'Dual Account Service' that fulfills the following:

1. Given an account number, UAF can determine if the account belongs to SEB client
2. Given an account number, UAF can indicate the account types according to "cash-away" types (e.g. A, B or C)
3. Given an account number, UAF can provide related "cash-away" account number(s). For example,
    - Given account number of type A, UAF is expected to return an account number for type B and/or account number for type C
    - Given account number of type B, UAF is expected to return an account number for type A
    - Given account number of type C, UAF is expected to return an account number for type A

And the 'Static database' provides reference service with the following:

1. Given an account number and the transaction type (either RVP or DVP), the 'static database' can determine if it is contractual or actual
2. Dervice a SEB FX Counterparty information (??? need to confirm what the input is???)

For both UAF and 'Static database', it is assumed that:

* the preceding capabilities are provided as callable/executable functions (NOT just some database tables containing these information that has to be queried by VUS)
* the functions executes and performs according to a SLA and NFR where NFR is to be defined later

#### Message types

The assumption is that there is one message type to describe all VUS messages (1-7). However there may be an unforeseen situation where a single message type may not sufficiently support the business requirement or it may not be the most efficient use of the message. Under these circumstances the system may introduce an additional message types.

#### Posting of Provisional Cash Transaction

As of `1 Aug ???`, the requirements relating to the treatment of 'provisional' messages are still under discussion within the business. When the details are confirmed and captured as concrete business requirements, the review of the high-level design presented in this document must take place to validate the current design and update as necessary.

#### Caution

It is critical to note that the high-level design is based on the validity/correctness of the assumptions.

As at `???`, the BRD is still being finalized and as such any major changes or addendums to the requirements may impact the high-level design outlined in this document and may invalidate the fundamental design decisions.

However, note that the high-level design should withstand minor requirement changes and it is the goal of the design to produce decoupled and modular system such that the impact of minor changes can be isolated and contained within a component.

can be accomodated as the the affect should be isolated and contained within a single component.

# High-level design

???

## Design goal (?)

Aside from the 

* decouple
* extensible
    * client neutral solution
* scalable
* performance
* reliability

???

## Basic flow / High-level diagram

      [MFPS]                                        [XZY]
             \                                   /
      [RTCR]  --> (===() --> [ VUS ] --> (===() --> [SEB]
             /                  |                \
     [FX Away]                  |                   [Informediary]
           /              [UAF]   [SD]      
      [SEB]                

* what else is in here???

## External dependencies

As stated in the `assumptions`, VUS depends on a number of external systems to obtain 'transaction' related messages. Along with the message processing, VUS requires reference data to further enrich the output message to a 'cash-away' client. 

The service that provides reference data lookup is to be implemented by the underlying system that manages and maintains the reference data. In the case of 'accounts' related services, which are in the domain of UAF, it is UAF that assumes the role of a service provider. Similarly, the other 'static' look up services are in the domain of 'Static database' system.

In a broad sense, the external systems can be categoried into the following three types.

1. Source
2. Destination
3. Reference data

where the **source** systems are:

* RTCR
* FX Away
* MFPS
* Client (e.g. SEB)

and the **destination** systems are:

* Client (e.g. SEB)
* Infomediary

lastly, the **reference data** systems are:

* UAF
* 'Static database'

The integration with the **source** and the **destination** systems is assumed to be based on message queue solution. One of the main design concerns for system integration is to minimize tight coupling between systems and the message queue solution is proven to be a good candidate for this concern. In addition, the asynchronous nature of the message flows between the systems is well matched by the message queue solution.

On the other hand, the **reference data** services require synchronous exchange for which RESTful web services approach is better suited than a message queue solution.

The `integration` section below describes the integration approach for each external system in detail.

## VUS Components
* Core process design detail???
    * use of framework

### 1. VUS Core

### 2. Event Source Adapters

* content based routing (per client)
* filter and transform
* validation - and error handling


### 3. Router and Event Sink

### 4. Reference data service

As noted in the assumptions section, the reference data 'look up' service for dual accounts relationship is provided by UAF. Even though the underlying implementation detail of the functionality in UAF may be unknown at this stage and the approach for the integration (which is discussed in the next section) can be deffered, the main concern for the reference data service component is to fulfill, largely, the implementation of the following component interface.

    enum DualAccountType {
      TYPE_A, TYPE_B, TYPE_C;
    }
    
    interface DualAccountService {
      DualAccountType determineType(int accountNumber);
      int getAccountNumberTypeA(int accountNumber);
      int getAccountNumberTypeB(int accountNumber);
      int getAccountNumberTypeC(int accountNumber);
      int getClientAccountNumber(int accountNumber);
    }

And for the 'static database', the component interface would resemble the following:

    enum TransactionType {
      RVP, DVP;
    }

    interface ReferenceData {
      boolean isContractual(int accountNumber, TransactionType txType);
      String fxCounterPartyForSEB(???);
    }

Having the component interface delineates the underlying implementation concerns tied to the external data sources from the core processing component. Through the interface design the overall solution can avoid tight coupling between multiple systems. Also the core component does not concern itself with the specific implementation detail of the reference data source but only depends on the interface. 

The main concerns of this component 'Reference data service' are:

* to define the interface for the core component
* implementation of the interface
* make the implementation available to the core component

Additionally, the Reference data service could implement some level of 'caching' for performance improvement but as with any performance-optimization related activities, exercise caution to avoid premature optimization. Also any optimization exercise should be supported with well-thought out test cases to demonstrate the effect of optimization. More often than not, ill-conceived notion of optimization adds a little or no benefit for the complexity it may require.

### 5. Integration

The intergration components are largely concerned with the definition of message types and the connectivity between systems. By connectivity, it does not refer to the physical connections or the network level connectivity but rather in conceptual term. As an example, the connectivity in terms of EIP solution, it would relate to message endpoints on which a system can use to send or receive data/messages.

The purpose of the integration components (largely the responsibility of the message endpoints) are to encapsulate the concerns regarding the message formats, messaging channels etc.

Extending it further, the design and the specification of message channels, queues/topics and other messaging related concerns, such as durable messages, ack/nack, dead-letter queue etc, are part of the integration components responsibility. However the act of creating a queue in a specific message queue product such as WebSphere MQ, do not belong in this but rather it is the operational tasks that to be carried out by a specialized team.

In addition to the message queue solution, the integration components must provide alternate integration options when necessary. For example, a RESTful service would well suit the need of the integration between the reference data service and VUS as dicussed below.

#### Client (i.e. SEB)

In terms of VUS messages, the interaction between the client (in the immediate case SEB) and the VUS can be categories as:

1. Outbound messages from VUS to SEB
2. Incomming message from SEB to VUS

In the context of message channels it is assumed that each categories of messages to have separate and its own message channels.

At high-level, the integration component for the client deals with:

* Defining the connectivity between VUS and the client
* Design of reply/response (or Ack/Nack) paradigm
* Consideration for durable messages and its implementation
* Handling dead-letter queue

#### Reference data

Both UAF and the 'Static database' provide reference data to VUS. As noted, message queue solution for these are not well suited and therefore different integration options are explored and listed in the order of preference.

1. RESTful API (preferred)
2. Database ETL specific to VUS, replicate only necessary data
3. Message (synchronous)

When the above cannot be met by the underlying service provider (i.e. UAF or 'static database') consider the following options but avoid as much as possible.

1. RPC provided by UAF
2. direct access to underlying data source
3. JCA

Assuming UAF and 'Static database' can provide the RESTful API, the scope of the integration component for the reference data would involve the design of message exchange format (e.g. JSON message) with the counterpart systems and the implementation of RESTful clients for the data exchange.

#### Source systems / incomming systems

blah ???

#### Destination systems

blah ???


## Development approach
* map components to stream

## Deployment diagram - ??? (defer)

## Tool selections - ???
* Java version 6 or 7
* Actor model
* Graph DB
* DI framework?

## HW requirements (defer)

## NFR? concerns

***




### UI Requirements
* Who are the users?
* Level of ACL to manage?

#### Assume ?
* Further analysis and detailed requirement will be provided in due course

### Alert/Monitoring
* From BRD,
    * Have an alert process to generate a warning if VUS 4 or VUS 4 PROV have been sent but VUS 5 or VUS 5 PROV have not been received
* Who to alert?
* When to alert? - what is the condition?
* How? Is it an UI?
* How to set up?


## Resource dependencies
* Availability of MQ resources

## Assumptions on NFR (lowest priority)
* Transaction volume
* the number of SEB's clients, or the number accounts for SEB's business
* For capacity estimates/planning etc
    * Redundancy
* Latency and throughput requirements
