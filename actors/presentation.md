# Actor Interaction Patterns

> http://www.infoq.com/presentations/Actor-Interaction-Patterns

### Seed pattern

Presented with Humus

Refer to http://www.dalnefre.com/wp/humus/

### Actor fundamentals

- create
- send
- become

### Also look for YouTube channel

Search for Dale Schumacher

# Introduction to Akka Systems

> http://www.infoq.com/presentations/Akka-Actors

### Using Actors
- message handlers
- asynchronous
- stateful

### Actors are NOT
- agents
- message driven beans
- observers/listeners
- threads

### Example Actor

```
class PongActor extends akka.actors.Actor {
	def receive = {
		case Ping =>
			if (aware) { sender ! Pong }
			else { sender ! Score }
			updateAwareness()
		case Score =>
			updateScore()
			if (!won) sender ! Pong
	}
}
```

### Rule #1

All comms asynch messages

### Using actors from the outside

```
val system: ActorRef = ...

system ! Howdy

val result: Future[EmotionalState] =
  system ? HowsItGoing(...)

Await.await(result, Duratuion.Inf)
```

### Scatter Gather...

...

### Actor Systems (00:47)
- **Partition state** into small pieces
- Communicate with immutable **messages**
- Spawn **new actors** to tract **temporary state**
- Design as a **Topology**
- ***Partition threads* on the topology
- **Bubble errors** on the topology

### Key Point
Model a concurrent process with a concurrent process

### Resources
- http://github.com/jsuereth/intro-to-actors
- https://github.com/jsuereth/spring-akka-sample
- Event Sourcing (taking domain events one at a time)
- 1000 yr old design patterns (talk by Ulf Wiger)

### Q&A
- You can Pin an actor to a thread
- Assign blocking ops actors to a separate dispatcher/pool (worth re-watching)
- Use Typesafe console for visualization (free download for developer)
  - there might be new ones now

# 1000 Year-old Design Patterns

> http://www.infoq.com/presentations/1000-Year-old-Design-Patterns

### Tetris Management
- Used in a derogatory sense at a major software development project
- As in "reactive management without a plan"
- Basically, don't let your project become a tetris game

### Q&A

