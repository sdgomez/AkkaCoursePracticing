import akka.actor.{Actor, ActorRef}

class StatelessFussyKid extends Actor {
  import StatelessFussyKid._
  import Mom._
  override def receive = happyReceive

  def happyReceive:Receive = {
    case Food(VEGETABLE) =>
      // Change my receive
      context.become(sadReceive)
    case Food(CHOCOLATE) =>
    case Ask(_) => sender() ! KidAccept
  }

  def sadReceive:Receive = {
    case Food(VEGETABLE) => //stay sad
    case Food(CHOCOLATE) =>
      // Change my receive to happy
      context.become(happyReceive)
    case Ask(_) => sender() ! KidReject
  }
}

object StatelessFussyKid {
  case object KidAccept
  case object KidReject
  val HAPPY = "happy"
  val SAD = "sad"
}

class Mom extends Actor {
  import Mom._
  import StatelessFussyKid._
  override def receive = {
    case MomStart(ref) =>
      ref ! Food(VEGETABLE)
      ref ! Ask("Do you want to play?")
    case KidAccept => println("Yay, my kid is happy!")
    case KidReject => println("mi kid is sad, but he is healthy")
  }
}

object Mom {
  case class MomStart(kidRef:ActorRef)
  case class Food(food:String)
  case class Ask(message:String)
  val VEGETABLE = "veggies"
  val CHOCOLATE = "chocolate"
}
