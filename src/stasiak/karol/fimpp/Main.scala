package stasiak.karol.fimpp

import io.Source


/**
 * Created with IntelliJ IDEA.
 * User: karol
 * Date: 06.10.12
 * Time: 11:26
 * To change this template use File | Settings | File Templates.
 */
object Main {
  def main(args: Array[String]){
    //TODO: flags
    args.length match {
      case 0 =>
        println("To run a FIM++ program, pass it as the first argument to this program.")
      case 1=>
        println("parsing: "+args(0))
        val program = Source.fromFile(args(0)).getLines().mkString("\n")
        val parsed = FimppParser.parseAll(FimppParser.module, program)
        if (parsed.successful) {
          println("interpreting: "+args(0))
          parsed.get.run()
        }
        else println(parsed)
      case _ =>
        println("Hold your horses! One file at a time, please!")
    }

  }
}
