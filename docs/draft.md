# Introduction

## Background

The purpose of this document is to state the design assumptions and to provide high-level design for VUS system. Also it aims to outline the integration approach with its external dependencies. The design is based on the business requirements document (BRD) titled `??? title` prepared by `??? author` that captures the business drivers and the detail description of the business requirements.

The reader of this document should refer to the business requirements document to grasp the full business context on which the high-level design is based.

## Assumptions

The high-level design is produced to fulfill the business requirements outlined in the BRD. Along with the requirements the BRD states a number of assumptions with regards to the external systems development and readiness of those systems. The details are captured in the BRD under the section `Assumptions` and it is reproduced below in verbatim for the convenience of the reader.

#### Assumptions from BRD

1.	Asia Blueprint Phase 1A and 1B will be in production and all of the upstream processing applications, except Mutual Funds, will pass cash data through CTA to a Real Time Cash Repository (RTCR).  Mutual Funds cash data flows into the RTCR via GTPS, GFTS or Tiger depending on the transaction.
2.	Spectrum will have replaced FX Legacy for Foreign Exchange processing.
3.	A new FX Away process will have been developed in support of SEB’s FX away requirements.
4.	The UAF will store an account relationship table linking the A accounts to the B and/or C accounts.  
5.	The existing FX Y/N indicator on an ActionWorld R/P will be part of the AW cash transaction data sent to the RTCR.
6.	The existing Withholding Agent indicator on an ActionWorld R/P will be part of the AW cash transaction data sent to the RTCR.
7.	MFPS will have created a new FX Y/N indicator as part of the F/X Away process.

In addition to the assumptions from the BRD, this document makes the the following design assumptions and observations.

#### Transaction information

* The source of the transaction information is originated from RTCR and RTCR only.
* Each transaction message sourced from RTCR must contain (in addition to other transaction information):
    - Unique RTCR Id
    - Primary ***transaction identifier***, comprised of source type and transaction number
    - Associated ***transaction identifier*** (this is the other leg of transactions) ???
    - Related head account number (full 11 digits)

#### Transaction identifier

* The uniqueness of the transaction is determined by the combination of the source system type and the transaction number. For example a cash trade transaction record from RTCR can be uniquely identified by `SONIC 7890`.
* Any transaction records within RTCR with the same transaction identifier (e.g. `SONIC 7890`) refer to the same underlying transaction.
* All external systems will provide ***transaction identifier*** in the messages to VUS where the ***transaction identifier*** follows the same scheme as above. i.e. ` Source system type + Transaction number `.

#### RTCR

As briefly noted, VUS expects the transaction information to flow from RTCR. It is expected that RTCR (or its subsystem) establish a connection to VUS via message channel and place or publish a transaction information in the form of a message on the message channel.

Furthermore, the expectation is that RTCR (or its subsystem) only directs 'cash-away' related transaction messages. Initially it will be for SEB only but it must allow for more than one 'cash-away' client.

#### FX Away

FX Away is to produce two types of messages to VUS. One message is to indicate if a transaction would require an FX and the other message type is for providing FX execution details for a transaction. 

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
2. Derive a SEB FX Counterparty information (??? need to confirm what the input is???)

For both UAF and 'Static database', it is assumed that:

* the preceding capabilities are provided as callable services (i.e. NOT just exposing database tables that has to be queried by VUS)
* the functions from the services execute and perform according to a SLA and NFR where NFR is to be defined later

#### Message types & formats

The assumption is that there is one message type to describe all VUS messages (1-7). However there may be an unforeseen situation where a single message type may not sufficiently support the business requirement or there is potential for performance gain by defining multiple types. Under these circumstances the system may introduce an additional message types.

??? message format for VUS - XML? who should decide ???

#### Posting of Provisional Cash Transaction

As of `1 Aug ???`, the requirements relating to the treatment of 'provisional' messages are still under discussion within the business. When the details are confirmed and captured as concrete business requirements, the review of the high-level design presented in this document must take place to validate the current design and update as necessary.

#### General validity of the assumptions

It is critical to note that the high-level design is based on the validity / correctness of the assumptions.

As at `???`, the BRD is still being finalized and as such any major changes or addendum to the requirements may impact the high-level design outlined in this document and may invalidate the fundamental design decisions.

However, note that the high-level design should withstand minor requirement changes and it is the goal of the design to produce decoupled and modular system such that the impact of minor changes can be isolated and contained within a component.

## Scope ???

What's in and what's out?


# High-level design

??? TODO ???

## Design considerations

Aside from the primary goal of addressing the core functional requirements, the design presented in this document is conceived with the consideration of the following concerns.

* decoupling
* extensibility
    * client neutral solution
* scalability (vertical and horizontal)
* performance
* reliability (fail-over, capacity etc)

??? expand ???

## Systems diagram

      [MFPS]                                        [XZY]
             \                                   /
      [RTCR]  --> (===() --> [ VUS ] --> (===() --> [SEB]
             /                  |                \
     [FX Away]                  |                   [Infomediary]
           /             [UAF]--+--[Static DB]      
      [SEB]                
    

      (===() denotes message channel

??? TODO - draw in Visio ???

## External dependencies

As stated in the `assumptions`, VUS depends on a number of external systems to obtain 'transaction' related messages. Along with the message processing, VUS requires reference data to further enrich the output message to a 'cash-away' client. 

The service that provides reference data lookup is to be implemented by the underlying system that manages and maintains the reference data. In the case of 'accounts' related services, which are in the domain of UAF, it is UAF that assumes the role of a service provider. Similarly, the other 'static' look up services is in the domain of 'Static database' system.

In a broad sense, the external systems can be categorized into the following three types.

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

On the other hand, the **reference data** services require synchronous data exchange for which RESTful web services approach is better suited than a message queue solution.

The ` integration ` section below describes the integration approach for each external system in detail.

## VUS modules

              +--------------------------VUS-------------------------+
              |                                                      |
    source -->+--[(*) Event Source]---[VUS Core]---[Event Sink (*)]--+--> destination
              |                            |                         |
              |                [Reference data service (*)]          |
              |                                         |            |
              +-----------------------------------------+------------+
                                                        |
                                                 reference data
    
    (*)- denotes the message endpoints

The VUS is broken up into five major modules and within each modules there are one or more components depending on the module's specific responsibility. A module in this context refers to a deployable unit and can be thought of as a JAR file in Java application development.

At the lowest level, the ` Integration ` module takes care of the connectivity concerns between the external systems and VUS and it encapsulates much of the underlying messaging details from the rest of the modules.

For each source system there is corresponding component in ` Event Source ` that performs preliminary business process such as message validation, transformation, log of input data and dispatching the data to ` VUS Core `.

` VUS Core ` implements the main business logic according to the BRD. It is within this module that the input data is consumed and the outbound data is created as necessary. The core module interacts with the ` Reference data service ` to enrich the outbound data when needed.

The outbound data is then forwarded to ` Event Sink ` that routes and transforms the outbound data to a specific message format for intended destination. For certain destination, e.g. Infomediary, there is no requirement for message transformation. Any outbound data are also captured for reporting and audit purpose within this module.

The operational concerns such as alerts, monitoring and the reports are captured in ` Operational Concerns `.

### 1. Integration

The integration module is largely concerned with the definition of message types, message format and the communication and connectivity between the external systems identified in the previous section and VUS. As an example, the connectivity in terms of EIP (Enterprise Integration Patterns), it relates to message endpoints on which a system can use to send or receive data/messages.

The purpose of the integration components (largely the responsibility of the message endpoints) are to encapsulate the concerns regarding the message formats, messaging channels or any of the other details of communicating with other applications via messaging.

In addition, the design and the specification of message channels, queues/topics and other messaging related concerns, such as durable messages, ack/nack, dead-letter queue etc, are part of the integration components responsibility. However the act of creating a queue in a specific message queue product such as WebSphere MQ, do not belong in this but rather it is the operational tasks that to be carried out by a specialized team.

Furthermore, the integration components are also responsible for providing alternate integration options when necessary. For example, RESTful service based integration which would suit well for the use cases of connectivity between reference data systems and VUS as discussed below.

#### Client (i.e. SEB)

In terms of VUS messages, the interaction between the client (in the immediate case SEB) and the VUS can be categories as:

1. Outbound messages from VUS to SEB
2. Incoming messages from SEB to VUS

In the context of message channels it is assumed that each category of messages to have its own message channels.

At high-level, the integration component for the client deals with:

* Defining the connectivity between VUS and the client
* Design of reply/response (or Ack/Nack) paradigm
* Consideration for durable messages and its implementation
* Handling dead-letter queue

#### Source systems

The messages 'flow-in' from each source systems to VUS. As seen in the ` External dependencies ` the source systems identified are:

* RTCR
* FX Away
* MFPS
* Client (already covered in ` Client `)

The general responsibilities pointed out regarding the integration components apply to each source systems. That is, the design and the specification of message channels, queues/topics etc.

The design does not dictate the implementation decisions. For example, the decision between having separate queues defined per internal source systems vs. a single queue for all internal source systems will depend on a number of implementation concerns.

And the different aspects of implementation concerns need to be well thought out and compared. It is these types of concerns and consideration that the integration components for source systems are responsible for.

#### Destination systems

The messages 'flow-out' from VUS to destinations. There are two type of destination, first is for the clients and the other for the internal message consumption, in the current BRD, Infomediary. The integration component for the clients is covered previously.

As with other integration components, the same concerns and responsibilities pointed out so far also applies to these. In addition, further responsibility must be considered because of the additional role that VUS is performing as a message producer in this instance. [ ??? expand ???] For example, reliable messageing. ...... 

#### Reference data

Both UAF and the 'Static database' provide reference data to VUS. As noted, message queue solution for these is not well suited and therefore different integration options are explored and listed in the order of preference.

1. RESTful API (preferred)
2. Database ETL specific to VUS, replicate only necessary data
3. Message (synchronous)

When any of the above options cannot be met by the underlying service provider (i.e. UAF, 'static database') the following options can be explored but avoid as much as possible.

1. Direct access to underlying data source
2. JCA based solution*

Assuming UAF and 'Static database' can provide the RESTful API, the scope of the integration component for the reference data would involve the design of message exchange format (e.g. JSON message) with the counterpart systems and the implementation of RESTful clients for the data exchange.


### 2. Event Source

As mentioned, for each source system there is corresponding component in ` Event Source ` that perform preliminary business process and forward the data to ` VUS Core `.

As soon as a message arrives at this component, the input message is persisted for audit and reporting purpose. Then the event source components validate the input message and transform it into a format that is suitable for VUS Core module's consumption. Management of the validation failures is also the responsibility of this module. A general approach is to direct the failed message to a separate queue for further assessment and reports.

The event source components for RTCR messages (i.e. transactions) require an additional responsibility to filter the messages based on its content such that only the relevant transaction messages are dispatched to the VUS Core module. The specific filter rules must be captured in a detail design document but as an example:

    Only process transactions that are either,
      - non-provisional and has account number of type B or type C
      or
      - provisional and has account number of type A
    All other cases, do not process.

Additionally, the components in this module may require to implement the routing capability for handling multiple clients. For example, if the assumption is that there are multiple instances of VUS Core where each instance is partitioned per client, the event source component would route the messages according to its client to the destined instance of VUS Core. 

### 3. Event Sink

After the ` VUS Core ` produces the outbound data it is routed to an ` Event Sink ` according to message type and/or client. The role of the components in this module is to translate the outbound data to a predefined format. For example, a message in an object form may be transformed into an XML document or JSON format. However it does not transform the messages into a wire format that is specific to the messaging solution - it is the responsibility of the ` Integration ` components.

Prior to the message being forwarded to an endpoints defined in the ` Integration ` module, the components also persist the outbound message in a query data store. 

The storage of the outbound message should capture relevant meta-data so that it can be easily correlated with the corresponding inbound messages captured by ` Event Source `. Document-oriented databases, are well suited for this use case in that the messages can be expressed in the form of schema-less documents. Also it is important to note that the query data store is not intended to be used by the core module for message processing.

### 4. Reference data service

As noted in the assumptions section, the reference data 'look up' service for dual accounts relationship is provided by UAF. Even though the underlying implementation detail of the functionality in UAF may be unknown at this stage and the approach for the integration can be deferred, the main concern for the reference data service component is to fulfill, largely, the implementation of the following component interface.

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

The component interface design delineates the underlying implementation concerns relating to the external data sources from the core module. Through the interface design the overall solution can avoid tight coupling between multiple systems. Also the core component does not concern itself with the specific implementation detail of the reference data source but only depends on the interface. 

The main concerns of this component 'Reference data service' are:

* definition of the interface with the core component
* implementation of the interface
* make the implementation available to the core component

Additionally, the Reference data service could implement some level of 'caching' for performance improvement but as with any performance-optimization related activities, exercise caution to avoid premature optimization. Also any optimization exercise should be supported with well-thought out test cases to demonstrate the effect of optimization. More often than not, ill-conceived notion of optimization adds a little or no benefit for the complexity it may introduce.

### 4. VUS Core

It implements the main business logic according to the BRD. It is within this module that the input data is consumed and the outbound data is created as defined by the business requirements. The core module interacts with the ` Reference data service ` to enrich the outbound data when needed.

One of the key responsibilities is the maintenance of the state-transition for each transaction by the input events whereby the core component examines the input event in conjunction with its current state to determine the next valid state and performs predetermined actions. Essentially it requires a representation of a state machine (or FSM - Finite State Machine) for each unique transaction.

There are various approaches for implementing a FSM. At one end of the spectrum, it can be implemented in the database by storing the current state against the transaction records and query the State/Event table that defines the state-transitions.

An advantage with this approach is the durable nature of the database when compared to the states held in-memory, in that the state information is not lost in the event of system failure. However the modeling of the State/Event table becomes non-trivial for complex state-transition logic and any enhancements or changes to the state-transition may require database re-modeling. Also there is potential throughput issue with processing of an input event as each event processing it requires multiple database queries to determine the next state.

In contrast, in-memory model better manages the throughput concerns as it reduces the I/O bound database interactions to a minimum but at the cost of higher memory usage and with the loss of persistence. But with in-memory model, the state-transition logic can be more expressive with the semantics of programming language when compared to modeling the logic in a database table, yielding more maintainable application.

Ideal middle ground is the hybrid of the two approaches where the solution maintains the state in persistent store with higher throughput without consuming too much memory (whilst take advantage of the multiple CPUs / multi-cores ??? Leave in ???).


??? TODO  - do not read below this line, work in progress ???

***

??? key items

* VUS Ref Id generation
    * ensuring uniqueness, durable with re-start
* Persistence and logs


#### Approach

Further breakdown by messages
* VUS 1, 2 or .. 7

Or breakdown by type
* Trade transactions
* MF trade transactions
* Corporate action transactions

Capture design approach for each breakdown and success criteria?


???


## Operational Concerns

> General question on this stream is shouldn't this concern be handled consistently and possibly with one solution/system for all P6 projects? If every sub-projects implement its own UI and monitoring solution in a disparate manner, there will be no consistency in its functionality and operations, resulting in mixture of procedures for the operator.

> Instead, if the operational concerns are addressed as a separate sub-project whereby it defines a standard approach on how the errors are reported and alerts are raised etc (along with audit and logs), the other sub-projects (like VUS) can conform to it but it need not implement its own UI for it. (and managing users etc)

### Monitoring / Alerts
* Need more detailed requirements
* Defining alertable events
    * Need to determine the criteria for raising alerts
* Related to UI
* From BRD,
    * Have an alert process to generate a warning if VUS 4 or VUS 4 PROV have been sent but VUS 5 or VUS 5 PROV have not been received
* Who to alert?
* When to alert? - what is the condition?
* How? Is it an UI?
* How to set up?

### Reporting (only in relation to VUS messages)
* Identify the requirement and source system(s)

### UI
* Who are the users?
* What are the requirements?
* ACL requirements
    * Role based?

### Assume ?
* Further analysis and detailed requirement will be provided in due course

## Development approach
* map components to stream

## Tool selections - ???
* Java version 6 or 7
* Actor model
* Graph DB
* DI framework?

## HW requirements (defer)

## NFR? concerns

## Resource dependencies
* Availability of MQ resources

## Assumptions on NFR (lowest priority)
* Transaction volume
* the number of SEB's clients, or the number accounts for SEB's business
* For capacity estimates/planning etc
    * Redundancy
* Latency and throughput requirements
