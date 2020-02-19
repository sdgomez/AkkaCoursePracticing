import akka.actor.{Actor, Props}
case object Increment
case object Decrement
case object Print
class Counter extends Actor {
  var counter:Int = 0
  def receive = {
    case Increment =>
      counter = counter + 1
    case Decrement =>
      counter = counter - 1
    case Print =>
      println(s"El valor del contador es: $counter")
  }
}

object Counter{
  def props = Props(new Counter())
}
