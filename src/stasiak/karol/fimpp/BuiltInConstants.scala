package stasiak.karol.fimpp

import collection.mutable

/**
 * Created with IntelliJ IDEA.
 * User: karol
 * Date: 06.10.12
 * Time: 17:33
 * To change this template use File | Settings | File Templates.
 */
object BuiltInConstants {
  def apply(targetMap: mutable.Map[String,RuntimeValue]){
    targetMap ++= Map(
      "new line" -> RuntimeString("\n"),
      "apostrophe" -> RuntimeString("\'"),
      "quote" -> RuntimeString("\""),
      "tabulation" -> RuntimeString("\t"),
      "nothing" -> RuntimeNull,
      "harmony" -> RuntimeBoolean(true),
      "chaos" -> RuntimeBoolean(false)
    )
  }
}
