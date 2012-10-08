package stasiak.karol.fimpp

import collection.mutable
import collection.mutable.ListBuffer
import reflect.ClassManifest
import java.awt.event.{ActionEvent, ActionListener}

/**
 * Created with IntelliJ IDEA.
 * User: karol
 * Date: 06.10.12
 * Time: 12:57
 * To change this template use File | Settings | File Templates.
 */
object RuntimeValue {
  def fromJava(jObj: Any) = jObj match {
    case null => RuntimeNull
    case i:Int => RuntimeNumber(i)
    case i:Short => RuntimeNumber(i)
    case i:Byte => RuntimeNumber(i)
    case i:Long => RuntimeNumber(i)
    case c:Char => RuntimeString(c.toString)
    case s:String => RuntimeString(s)
    //TODO
    case _ => RuntimeJavaObject(jObj)
  }
}
sealed trait RuntimeValue {
  def matches(clazz:Class[_]): Double
  def convertTo(context: Context, clazz: Class[_]): Any
  def toJava: Any
}
case class RuntimeString(str: String) extends RuntimeValue {
  override def toString = str
  def matches(clazz:Class[_]) = clazz match {
    case m if m == classOf[Char] => if(str.length==1) 1.0 else 0.0
    case m if m == classOf[java.lang.Character] => if(str.length==1) 1.0 else 0.0
    case m if m == classOf[String] => 1.0
    case m if m == classOf[AnyRef]  => 0.5
    case _ => 0 //TODO
  }
  def convertTo(context: Context, clazz:Class[_]) = clazz match {
    case m if m == classOf[Char] => str(0)
    case m if m == classOf[java.lang.Character] => str(0)
    case m if m == classOf[String] => str
    case m if m == classOf[AnyRef] => str
    case _ => null //TODO
  }
  def toJava = str
}
case class RuntimeNumber(num: Long) extends RuntimeValue  {
  override def toString = num.toString
  def matches(clazz:Class[_]) = clazz match {
    case m if m == classOf[Long] || m == classOf[java.lang.Long] => 1.0
    case m if m == classOf[Int] || m == classOf[java.lang.Integer] => 1.0
    case m if m == classOf[AnyRef] => 0.3
    case _ => 0 //TODO
  }
  def convertTo(context: Context, clazz:Class[_]) = clazz match {
    case m if m==classOf[java.lang.Integer] => num.toInt
    case m if m==classOf[Int] => num.toInt
    case m if m==classOf[java.lang.Object] => num.toInt
    case m if m==classOf[java.lang.Long] => num
    case m if m==classOf[Long] => num
    case _ => null //TODO
  }
  def toJava = num.toInt
}
case class RuntimeBoolean(truth: Boolean) extends RuntimeValue  {
  override def toString = if (truth) "harmony" else "chaos"
  def matches(clazz:Class[_]) = clazz match {
    case m if m==classOf[java.lang.Boolean] => 1.0
    case m if m==classOf[Boolean] => 1.0
    case m if m==classOf[java.lang.Object] => 0.3
    case _ => 0 //TODO
  }
  def convertTo(context: Context, clazz:Class[_]) = java.lang.Boolean.valueOf(truth)
  def toJava = truth
}
case class RuntimeList(elems: List[RuntimeValue]) extends RuntimeValue  {
  override def toString = if(elems.isEmpty) "nothing" else elems.mkString(", ")
  def matches(clazz:Class[_]) =  0
  def convertTo(context: Context, clazz:Class[_]) =  throw new FimException("TODO")
  def toJava = throw new FimException("TODO")
}
case object RuntimeNull extends RuntimeValue  {
  override def toString = "nothing"
  def matches(clazz:Class[_]) = 0.8
  def convertTo(context: Context, clazz:Class[_]) = null
  def toJava = null
}
case class RuntimeFunction(function: Function) extends RuntimeValue {
  override def toString = "<function "+function.name.capitalize+">"
  def matches(clazz:Class[_]) =  clazz match {
    case m if m==classOf[ActionListener] => 0.5
    case _ => 0 //TODO
  }
  def convertTo(context: Context, clazz:Class[_]) =  clazz match {
    case m if m==classOf[ActionListener] => new ActionListener {
      def actionPerformed(p1: ActionEvent) {
        function.call(context, Nil)
      }
    }
    case _ => throw new FimException("TODO")
  }
  def toJava = throw new FimException("TODO")
}
case class RuntimeBuiltin(function: List[RuntimeValue]=>RuntimeValue) extends RuntimeValue {
  override def toString = "<builtin function>"
  def matches(clazz:Class[_]) =  0
  def convertTo(context: Context, clazz:Class[_]) =  throw new FimException("TODO")
  def toJava = throw new FimException("TODO")
}
case object RuntimeGlobalReference extends RuntimeValue {
  override def toString = "<function>"
  def matches(clazz:Class[_]) =  throw new FimException("I don't know what went wrong")
  def convertTo(context: Context, clazz:Class[_]) = throw new FimException("I don't know what went wrong")
  def toJava = throw new FimException("I don't know what went wrong")
}
case class RuntimeArray(array: ListBuffer[RuntimeValue]= new ListBuffer) extends RuntimeValue {
  override def toString = array.mkString(", ")
  def get(index: Long):RuntimeValue = {
    if (index<=0) throw new FimException("Negative page")
    if (index>array.size) RuntimeNull
    array(index.toInt-1)
  }
  def set(index: Long, value: RuntimeValue) {
    while(array.size<index) array += RuntimeNull
    array(index.toInt-1) = value
  }
  def size = array.size
  def matches(clazz:Class[_]) = 0
  def convertTo(context: Context, clazz:Class[_]) = throw new FimException("TODO")
  def toJava = throw new FimException("TODO")
}

case class RuntimeJavaObject(obj:Any) extends RuntimeValue{
  override def toString = "alien thing called ‘"+obj+"’"
  def matches(clazz:Class[_]) = if (clazz.isAssignableFrom(obj.getClass)) 1 else 0      //TODO
  def convertTo(context: Context, clazz:Class[_]) = obj //TODO
  def toJava = obj
}
case class RuntimeClass(clazz: java.lang.Class[_]) extends RuntimeValue{
  override def toString = "<class "+clazz.getCanonicalName+">"
  def matches(clazz2:Class[_]) = if (clazz2.isAssignableFrom(classOf[Class[_]])) 1 else 0      //TODO
  def convertTo(context: Context, clazz2:Class[_]) = clazz //TODO
  def toJava = clazz
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
  def set(name:String, value: RuntimeValue) {
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
