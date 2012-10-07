package stasiak.karol.fimpp
import collection.mutable
import collection.mutable.ListBuffer

/**
 * Created with IntelliJ IDEA.
 * User: karol
 * Date: 06.10.12
 * Time: 16:47
 * To change this template use File | Settings | File Templates.
 */
object BuiltInFunctions {
  def apply(targetMap: mutable.Map[String,RuntimeValue]){
    targetMap ++= Map(
      "sum" -> RuntimeBuiltin(sum _),
      "difference" -> RuntimeBuiltin(difference _),
      "product" -> RuntimeBuiltin(product _),
      "quotient" -> RuntimeBuiltin(quotient _),
      "remainder" -> RuntimeBuiltin(remainder _),
      "dictionary" -> RuntimeBuiltin(dictionary _),
      "letters" -> RuntimeBuiltin(letters _),
      "first" -> RuntimeBuiltin(first _),
      "character by code" -> RuntimeBuiltin(characterByCode _)
    )
  }
  def sum(args: List[RuntimeValue]) = {
    RuntimeNumber(
      args.foldLeft(0L)((acc,value) =>
        value match {
          case RuntimeNumber(v) => acc+v
          case _ => throw new FimException("Trying to add not a number")
        }
      )
    )
  }
  def product(args: List[RuntimeValue]) = {
    RuntimeNumber(
      args.foldLeft(1L)((acc,value) =>
        value match {
          case RuntimeNumber(v) => acc*v
          case _ => throw new FimException("Trying to multiply not a number")
        }
      )
    )
  }
  def difference(args: List[RuntimeValue]) = args match {
    case List(RuntimeNumber(a1), RuntimeNumber(a2)) => RuntimeNumber(a1-a2)
    case _ => throw new FimException("Trying to subtract something weird")
  }
  def remainder(args: List[RuntimeValue]) = args match {
    case List(RuntimeNumber(a1), RuntimeNumber(a2)) => RuntimeNumber(a1%a2)
    case _ => throw new FimException("Trying to divide something weird")
  }
  def quotient(args: List[RuntimeValue]) = args match {
    case List(RuntimeNumber(a1), RuntimeNumber(a2)) => RuntimeNumber(a1/a2)
    case _ => throw new FimException("Trying to divide something weird")
  }
  def dictionary(args: List[RuntimeValue]) = args match {
    case List(RuntimeList(l)) => RuntimeArray(new ListBuffer[RuntimeValue]()++l)
    case _ => throw new FimException("This is not a list")
  }
  def first(args: List[RuntimeValue]) = args match {
    case List(RuntimeList(l)) => l match {
      case x::_ => x
      case Nil  => throw new FimException("This list is empty")
    }
    case List(array:RuntimeArray) => array.get(1)
    case _ => throw new FimException("Trying to get a first element from something that is neither list nor book")
  }
  def letters(args: List[RuntimeValue]) = args match {
    case List(RuntimeString(l)) =>
      val result = RuntimeArray()
      for (x<-l) result.array += RuntimeString(x.toString)
      result
    case _ => throw new FimException("You can extract letters from only one string")
  }
  def characterByCode(args: List[RuntimeValue]) = args match {
    case List(RuntimeNumber(l)) => RuntimeString(l.toChar.toString)
    case _ => throw new FimException("This is not a code of any character")
  }
}
