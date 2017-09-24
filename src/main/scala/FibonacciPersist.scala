package com.andyr

//#persistent-actor-example
import akka.actor._
import akka.persistence._
import akka.actor.ActorLogging
object Fib {

  case class Cmd(data: String)
  case object Evt
  case class FibonacciState(events: List[Long] = List(1, 1)) {
    def next: FibonacciState = {
      val n = events.head + events.tail.head
      copy(n :: events)
    }
    def size: Int = events.length
    override def toString: String = events.reverse.toString
  }
  class ExamplePersistentActor extends PersistentActor with ActorLogging {
    override def persistenceId = "sample-id-1"

    var state = FibonacciState(List(1, 1))

    def updateState: Unit =
      state = state.next

    def numEvents =
      state.size

    val receiveRecover: Receive = {
      case Evt =>
        log.info(s"Receive Recov event")
        updateState
      case SnapshotOffer(_, snapshot: FibonacciState) =>
        log.info(s"Receive Recov : $snapshot")
        state = snapshot
    }

    val receiveCommand: Receive = {
      case Cmd =>
        log.info(s"Receive Command ")
        persist(Evt)(_ => updateState)
        persist(Evt) { event =>
          updateState
          context.system.eventStream.publish(event)
        }
      case "snap" =>
        log.info(s"Receive Comm : snap ")
        saveSnapshot(state)
      case "print" =>
        log.info(s"Receive Comm : print ")
        println(state)
    }

  }

}
//#persistent-actor-example

object FibPersistentActorExample extends App {

  val system = ActorSystem("example")
  val persistentActor = system.actorOf(Props[Fib.ExamplePersistentActor], "persistentActor-4-scala")

  persistentActor ! Fib.Cmd
  persistentActor ! Fib.Cmd
  persistentActor ! Fib.Cmd
  persistentActor ! "snap"
  persistentActor ! Fib.Cmd
  persistentActor ! "print"

  Thread.sleep(10000)
  system.terminate()
}


