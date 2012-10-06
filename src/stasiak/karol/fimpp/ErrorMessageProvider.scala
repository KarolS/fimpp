package stasiak.karol.fimpp

/**
 * Created with IntelliJ IDEA.
 * User: karol
 * Date: 06.10.12
 * Time: 09:52
 * To change this template use File | Settings | File Templates.
 */
trait ErrorMessageProvider {
  def syntaxError(functionName: String):String
  def invalidIdentifier(id: String):String
  def mismatchedFunctionNameInFooter(expected: String, found: String):String
  def unknownError:String
}

object Trollestia extends ErrorMessageProvider {
  def syntaxError(functionName: String) = "Now say, do you like mmm-syntax errors? You have one in function ‘"+
    functionName+"’ after all. No? So let me get it straight: you're a mmm-biatch that does not like syntax errors. " +
    "That's good, because there are no syntax errors... ON THE MOOOON!"
  def mismatchedFunctionNameInFooter(expected: String, found: String) = "This function is a changeling! At the beginning it was named ‘" +
    expected + "’, and now it's ‘" + found + "’!"
  def unknownError = "To the moon with that code!"
  def invalidIdentifier(id: String) = "‘"+id.capitalize+"’ ain't no pony I've heard of. Do they have tea parties with ‘"+id.capitalize+"’?"
}