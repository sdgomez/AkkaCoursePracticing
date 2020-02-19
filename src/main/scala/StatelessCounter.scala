import akka.actor.{Actor, Props}

class StatelessCounter extends Actor{
  override def receive = counterReceive(0)
  def counterReceive(currentCount:Int):Receive = {
    case Increment => context.become(counterReceive(currentCount + 1))
    case Decrement => context.become(counterReceive(currentCount - 1))
    case Print => println(s"[counter] my current count is $currentCount")
  }
}

object StatelessCounter {
  case object Increment
  case object Decrement
  case object Print
  def props = Props(new StatelessCounter())
}
