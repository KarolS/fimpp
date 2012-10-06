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
    if (args.length>0){
      println("parsing: "+args(0))
      val program = Source.fromFile(args(0)).getLines().mkString("\n")
      val parsed = FimppParser.parseAll(FimppParser.module, program)
      if (parsed.successful) {
        println("interpreting: "+args(0))
        parsed.get.run()
      }
      else println(parsed)
      return;
    }
    println(FimppParser.parseAll(FimppParser.condition,
      "applejack has more than one".toLowerCase()))
    println(FimppParser.parseAll(FimppParser.identifier~FimppParser.kw("i")|FimppParser.kw("x")~FimppParser.kw("i"),
      "x i".toLowerCase()))
    println(FimppParser.parseAll(FimppParser.stringLiteral,
      "\"fafdsfs 'sdfsdfs' dsfsfsf\"".toLowerCase()))
    println(FimppParser.parseAll(FimppParser.statement,
      "Did you know that Applejack likes the number 99?".toLowerCase()))
    println(FimppParser.parseAll(FimppParser.statement,
      """I sang " 'Applejack' jugs of cider on the wall, 'Applejack' jugs of cider,".""".toLowerCase()))
    println(FimppParser.parseAll(FimppParser.statement,
      """
        |When Applejack had more than 1:
        |  I wrote the number twelve.
        |In the end, I did this instead.
        |  I sang "No more jugs of cider on the wall, no more jugs of cider.
        |Go to the store and buy some more, 99 jugs of cider on the wall.".
        |  That's what I did.""".stripMargin.toLowerCase()))
    println(FimppParser.parseAll(FimppParser.statement,
      """Applejack got one less.""".toLowerCase()))
    val result = FimppParser.parseAll(FimppParser.module,
      """
        |Dear Princess Celestia: Letter One.
        |
        |Today I learned Applejack's Drinking Song.
        |
        |I learned about Applejack's Drinking Song:
        |
        |  Did you know that Applejack likes the number 99?
        |
        |  I did this while Applejack had more than 1.
        |    I sang " 'Applejack' jugs of cider on the wall, 'Applejack' jugs of cider,".
        |    Applejack got one less.
        |
        |    When Applejack had more than 1:
        |      I sang "Take one down and pass it around, 'Applejack' jugs of cider on the wall.".
        |    In the end, I did this instead:
        |      I sang "Take one down and pass it around, 1 jug of cider on the wall.
        |1 jug of cider on the wall, 1 jug of cider.
        |Take one down and pass it around, no more jugs of cider on the wall.".
        |    That's what I did.
        |
        |    In the end, I did this instead.
        |      I sang "No more jugs of cider on the wall, no more jugs of cider.
        |Go to the store and buy some more, 99 jugs of cider on the wall.".
        |    That's what I did.
        |
        |  That's about Applejack's Drinking Song with Applejack!
        |
        |Your faithful student, Twilight Sparkle.      """.stripMargin)
    println(result)
    //result.get.run()
  }
}
