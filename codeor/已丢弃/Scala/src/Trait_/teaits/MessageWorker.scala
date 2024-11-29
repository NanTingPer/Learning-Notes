package Trait_.teaits

class MessageWorker extends MassageSender with MessageReceiver {
  override def send(msg: String): Unit = ???
  override def recei(): Unit = ???
}
