val test = List[Option[Int]](Option(0),None,Option(2),Option(3))
val wi = test.zipWithIndex
val i = wi.flatMap(item => {
  item._1 match
    case Some(_) => List()
    case None => List(item._2)
})
println(i.head)