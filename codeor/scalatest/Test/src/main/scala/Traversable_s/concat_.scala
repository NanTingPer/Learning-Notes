package Traversable_s

object concat_ {
    def main(args: Array[String]): Unit = {
        var t1 = Traversable(1,2,3);
        var t2 = Traversable(465,123);
        var t3 = Traversable(98,12,4);
        t1 = Traversable.concat(t1,t2,t3);
        println(t1);

//        def concat[A](xss: Traversable[A]*): CC[A] = {
//            val b = newBuilder[A]
//            // At present we're using IndexedSeq as a proxy for "has a cheap size method".
//            if (xss forall (_.isInstanceOf[IndexedSeq[_]]))
//                b.sizeHint(xss.map(_.size).sum)
//            for (xs <- xss.seq) b ++= xs
//            b.result()
//        }
    }
}
