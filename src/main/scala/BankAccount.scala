import akka.actor.{Actor, ActorRef, Props}
case class Deposit(value:Double, actorRef: ActorRef = Actor.noSender)
case class WithDraw(value:Double, actorRef: ActorRef = Actor.noSender)
case object GetBalance
case object Success
case object Failure
class BankAccount(accountId:String) extends Actor {
  var balance: Double = 0.0
  override def receive = {
    case Deposit(value, ref) =>
      if (value > 0) {
        balance = balance + value
        //sender() ! Success
      } else {
        println(s"No se pudo consignar el valor $value porque es negativo!")
        //sender() ! Failure
      }
    case WithDraw(value, ref) =>
      var lefttover = balance - value
      if (lefttover >= 0){
        balance = lefttover
        println(balance)
        //sender() ! Success
      }else{
        println(s"No se pudo retirar el $value de la cuenta $accountId porque no cuenta con los fondos suficientes!")
        //sender() ! Failure
      }
    case GetBalance =>
      println(s"el balance es: $balance")
  }
}

object BankAccount {
  def props(accountId:String) = Props(new BankAccount(accountId))
}
