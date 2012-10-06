package stasiak.karol.fimpp

import collection.mutable
import collection.mutable.ListBuffer

/**
 * Created with IntelliJ IDEA.
 * User: karol
 * Date: 06.10.12
 * Time: 12:57
 * To change this template use File | Settings | File Templates.
 */
sealed trait RuntimeValue
case class RuntimeString(str: String) extends RuntimeValue {
  override def toString = str
}
case class RuntimeNumber(num: Long) extends RuntimeValue  {
  override def toString = num.toString
}
case class RuntimeBoolean(truth: Boolean) extends RuntimeValue  {
  override def toString = if (truth) "harmony" else "chaos"
}
case class RuntimeList(elems: List[RuntimeValue]) extends RuntimeValue  {
  override def toString = if(elems.isEmpty) "nothing" else elems.mkString(", ")
}
case object RuntimeNull extends RuntimeValue  {
  override def toString = "nothing"
}
case class RuntimeFunction(function: Function) extends RuntimeValue {
  override def toString = "<function "+function.name.capitalize+">"
}
case class RuntimeBuiltin(function: List[RuntimeValue]=>RuntimeValue) extends RuntimeValue {
  override def toString = "<builtin function>"
}
case object RuntimeGlobalReference extends RuntimeValue {
  override def toString = "<function>"
}
case class RuntimeArray(array: ListBuffer[RuntimeValue]= new ListBuffer) extends RuntimeValue {
  override def toString = array.mkString(", ")
  def get(index: Long):RuntimeValue = {
    if (index<=0) throw new FimException("Negative page")
    if (index>array.size) return RuntimeNull
    return array(index.toInt-1)
  }
  def set(index: Long, value: RuntimeValue) {
    while(array.size<index) array += RuntimeNull
    array(index.toInt-1) = value
  }
  def size = array.size
}
class Context private (map: mutable.Map[String,RuntimeValue], globalMap: mutable.Map[String,RuntimeValue]) {

  def this(module:Module) {
    this(mutable.Map(), mutable.Map())
    BuiltInConstants(globalMap)
    BuiltInFunctions(globalMap)
    for (f<-module.functions){
      if(f!=null) globalMap(f.name) = RuntimeFunction(f)
    }
    //println(globalMap)
  }

  def getLocal(name: String) = {
    map.get(name).getOrElse(RuntimeString(name.capitalize))
  }
  def getGlobal(name: String) = {
    globalMap.get(name).getOrElse(RuntimeString(name.capitalize))
  }
  def get(name: String) = {
    map.get(name) match {
      case None => getGlobal(name)
      case Some(RuntimeGlobalReference) => getGlobal(name)
      case Some(x) => x
    }
  }
  def set(name:String, value: RuntimeValue) = {
    if (map.get(name)==Some(RuntimeGlobalReference)) {
      globalMap(name) = value
    } else {
      map(name) = value
    }
  }
  def treatAsGlobal(name:String) {
    map(name) = RuntimeGlobalReference
  }
  def forFunctionCall(f:Function, args: List[RuntimeValue])={
    new Context(mutable.Map(f.argNames zip args : _*),globalMap)
  }
}
