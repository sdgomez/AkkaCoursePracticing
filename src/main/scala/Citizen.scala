import Citizen.{Vote, VoteStatusReply, VoteStatusRequest}
import VoteAggregator.AggregateVotes
import akka.actor.{Actor, ActorRef, Props}

class Citizen extends Actor{
  override def receive: Receive = {
    case Vote(c) => context.become(voted(c)) // candidate = Some(c)
    case VoteStatusRequest => sender() ! VoteStatusReply(None)
  }

  def voted(candidate:String): Receive = {
    case VoteStatusRequest => sender() ! VoteStatusReply(Some(candidate))
  }
}

object Citizen {
  case class Vote(candidate:String)
  case object VoteStatusRequest
  case class VoteStatusReply(candidate:Option[String])
  def props = Props(new Citizen)
}

class VoteAggregator extends Actor{

  override def receive: Receive = awaitingCommand
      def awaitingCommand: Receive = {
        case AggregateVotes(citizens) =>
          citizens.foreach(citizensref => citizensref ! VoteStatusRequest)
          context.become(awaitingStatuses(citizens, Map()))
      }

      def awaitingStatuses(stillWaiting: Set[ActorRef], currentStatus:Map[String, Int]): Receive = {
        case VoteStatusReply(None) => // citizen hasn't voted yet
          sender() ! VoteStatusRequest
        case VoteStatusReply(Some(candidate)) =>
          val newStillWaiting = stillWaiting - sender()
          val currentVotesOfCandidate = currentStatus.getOrElse(candidate, 0)
          val newCurrentStatus = currentStatus + (candidate -> (currentVotesOfCandidate + 1))
          if (newStillWaiting.isEmpty){
            println(s"[aggregator] poll stats: $newCurrentStatus")
          }else{
            context.become(awaitingStatuses(newStillWaiting, newCurrentStatus))
          }
      }
}

object VoteAggregator{
  case class AggregateVotes(citizens: Set[ActorRef])
  def props = Props(new VoteAggregator)
}


