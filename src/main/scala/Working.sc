import java.util.Random

val r = Random(System.currentTimeMillis())
def randos = Seq
  .fill(10)(r.nextGaussian(1000.0, 100.0))
  .map(_.toInt)

randos