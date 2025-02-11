import java.util.UUID

case class TestCC(name: String):
  val id: String = UUID.randomUUID().toString
  

val test1 = TestCC("Simon")
val test2 = TestCC("Dave")
val test3 = test1.copy()

test1 == test2
test1 == test3
test1.id == test3.id