import Citizen.Vote
import Mom.MomStart
import VoteAggregator.AggregateVotes
import akka.actor.{Actor, ActorRef, ActorSelection, ActorSystem, Props}

case class ComplexMessage(body:String, replyTo:ActorRef)
case class ComplexResponse(body:String)

object AppExercise1 extends App {
  class MessageActor extends Actor {
    override def receive: Receive = {
      case "Hola" =>
        val publicador: ActorSelection =  context.actorSelection("../publicadorActor")
        println("Me habéis saludado tío!")
        publicador ! "Recibido"
      case ComplexMessage(body, replyTo) =>
        println(s"El actor $replyTo me ha mandado este mensaje: $body")
        replyTo ! ComplexResponse("Muchas gracias por tu mensaje!")
    }
  }
  class PublicadorActor extends Actor {
    override def receive = {
      case "Recibido" =>
        println(s"Me ha respondido correctamente el actor: ${sender().toString}")
      case ComplexResponse(body) =>
        println(s"El actor $sender me ha enviado el siguiente mensaje: $body")
      case Deposit(value, ref) =>
        ref ! Deposit(value)
      case WithDraw(value, ref) =>
        ref ! WithDraw(value, ref)
    }
  }

  val actorSystem = ActorSystem("Mensajeria")
  val messageActor: ActorRef = actorSystem.actorOf(Props[MessageActor], "messageActor")
  val publicadorActor = actorSystem.actorOf(Props[PublicadorActor], "publicadorActor")
  // messageActor ! "Hola"
  // messageActor ! ComplexMessage("Hola", publicadorActor)

  // actor created with companion object!
  val counter = actorSystem.actorOf(Counter.props, "CounterActor")
//  counter ! Increment
//  counter ! Print
//  counter ! Increment
//  counter ! Print
//  counter ! Decrement
//  counter ! Print
  val r= scala.util.Random
  val numeroDeCuenta:String = s"91821569${r.nextInt()}"
  val bankAccount = actorSystem.actorOf(BankAccount.props(numeroDeCuenta), numeroDeCuenta)
//  bankAccount ! Deposit(5200000.23, bankAccount)
//  bankAccount ! Deposit(527480.23, bankAccount)
//  bankAccount ! GetBalance
//  bankAccount ! WithDraw(47274, bankAccount)
//  bankAccount ! GetBalance
  val fussyKid = actorSystem.actorOf(Props[StatelessFussyKid], "fussyKid")
  val mom = actorSystem.actorOf(Props[Mom], "mom")
//  mom ! MomStart(fussyKid)
//  val statelessCounter = actorSystem.actorOf(StatelessCounter.props, "statelesscounter")
//  (1 to 5).foreach(_ => statelessCounter ! Increment)
//  (1 to 3).foreach(_ => statelessCounter ! Decrement)
//  statelessCounter ! Print
  val alice = actorSystem.actorOf(Citizen.props)
  val bob = actorSystem.actorOf(Citizen.props)
  val charlie = actorSystem.actorOf(Citizen.props)
  val daniel = actorSystem.actorOf(Citizen.props)

  alice ! Vote("Martin")
  bob ! Vote("Jonas")
  charlie ! Vote("Roland")
  daniel ! Vote("Roland")

  val voteAggregator = actorSystem.actorOf(VoteAggregator.props)
  voteAggregator ! AggregateVotes(Set(alice, bob, charlie, daniel))
}
