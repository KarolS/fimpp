package stasiak.karol.fimpp

import java.lang.reflect.{Field, Modifier, Constructor, Method}
import Modifier.STATIC
import Modifier.PRIVATE
import javax.swing.JFrame
import java.util
import collection.mutable.ListBuffer


object JavaInterop {
  def normalizeFromFim(str: String) = str.toLowerCase.replaceAll(" ","").replaceAll("\'","").replaceAll("-","")
  def normalizeFromJava(str: String) = (
    str.toLowerCase.replaceAll("_","")
      .replaceAll("0","zero").replaceAll("1","one")
      .replaceAll("2","two").replaceAll("3","three")
      .replaceAll("4","four").replaceAll("5","five")
      .replaceAll("6","six").replaceAll("7","seven")
      .replaceAll("8","eight").replaceAll("9","nine")
    )
  def normalizedMatch(fromFimNormalized: String, fromJavaNormalized:String):Boolean = {
    if(fromFimNormalized.charAt(0)=='?') "is"+fromFimNormalized.substring(1)==fromJavaNormalized ||"get"+fromFimNormalized.substring(1)==fromJavaNormalized
    else fromFimNormalized==fromJavaNormalized
  }
  def isPrivate(flags: Int) = 0!=(flags&PRIVATE)
  def isStatic(flags: Int) = 0!=(flags&STATIC)
  def callMethod(context: Context, clazz: Class[_], methodName: String, methodParams: List[RuntimeValue]): RuntimeValue = {
    val n1 = normalizeFromFim(methodName)
    var best: Method = null
    var bestScore: Double = 0.001
    //println(methodParams)
    //println(RuntimeNumber(1).matches(classOf[Int]))
    clazz.getMethods.foreach{ method =>
      val n2 = normalizeFromJava(method.getName)
      if(!isPrivate(method.getModifiers)
        && normalizedMatch(n1,n2)
        && method.getParameterTypes.size == (if(isStatic(method.getModifiers))methodParams.size else methodParams.size-1)) {
        val score = (if(isStatic(method.getModifiers))methodParams else methodParams.tail).zip(method.getParameterTypes).map {
          case (rv,cl) =>
            rv.matches(cl)
        }.product * (if(isStatic(method.getModifiers)) 1 else methodParams.head.matches(clazz))
        //println(method->score)
        //println(method.getParameterTypes.toSeq)
        if (score>bestScore){
          bestScore = score
          best = method
        }
      }
    }
    if(best eq null) throw new FimException("Method ‘"+methodName+"’ not found")
    if (isStatic(best.getModifiers)){
      val args = (methodParams zip best.getParameterTypes).map {
        case (rv,cl) =>
          rv.convertTo(context,cl)
      }.toSeq.asInstanceOf[Seq[java.lang.Object]]
      RuntimeValue.fromJava(best.invoke(null, args:_*))
    } else {
      val args = (methodParams.tail zip best.getParameterTypes).map {
        case (rv,cl) =>
          rv.convertTo(context,cl)
      }.toSeq.asInstanceOf[Seq[java.lang.Object]]
      RuntimeValue.fromJava(best.invoke(methodParams.head.convertTo(context,clazz), args:_*))
    }
  }
  def callConstructor(context: Context, clazz: Class[_], constructorParams: List[RuntimeValue]): RuntimeValue = {
    var best: Constructor[_] = null
    var bestScore: Double = 0.001
    clazz.getConstructors.foreach{ constructor =>
      if(!isPrivate(constructor.getModifiers) && constructor.getParameterTypes.size == constructorParams.size) {
        val score = constructorParams.zip(constructor.getParameterTypes).map {
          case (rv,cl) =>
            rv.matches(cl)
        }.product
        if (score>bestScore){
          bestScore = score
          best = constructor
        }
      }
    }
    if(best eq null) throw new FimException("Couldn't wake up")
    val args = (constructorParams zip best.getParameterTypes).map {
      case (rv,cl) =>
        rv.convertTo(context,cl)
    }.toSeq.asInstanceOf[Seq[java.lang.Object]]
    RuntimeValue.fromJava(best.newInstance(args:_*))
  }

  def getField(objClass: Either[Class[_],Any], field: Either[Int, String]):RuntimeValue = RuntimeValue.fromJava(
    (objClass, field) match {
      case (Left(clazz),Left(i)) => throw new FimException("Classes are not indexable")
      case (Right(obj),Left(i)) => throw new FimException("TODO")
      case (Left(clazz),Right(fn)) => {
        val n1 = normalizeFromFim(fn)
        var field: Field = null
        clazz.getFields.foreach{ f=>
          val n2 = normalizeFromJava(f.getName)
          if(isStatic(f.getModifiers) && n1==n2) field=f
        }
        if(field!=null) field.get(null)
        else throw new FimException("Not a  static field")
      }
      case (Right(obj),Right(fn)) => {
        val n1 = normalizeFromFim(fn)
        var field: Field = null
        obj.getClass.getFields.foreach{ (f:Field)=>
          val n2 = normalizeFromJava(f.getName)
          if(!isStatic(f.getModifiers) && n1==n2) field=f
        }
        if(field!=null) field.get(obj)
        else throw new FimException("Not a field")
      }
    }
  )
  def setField(context: Context, objClass: Either[Class[_],Any], field: Either[Int, String], v: RuntimeValue):Unit =
    (objClass, field) match {
      case (Left(clazz),Left(i)) => throw new FimException("Classes are not indexable")
      case (Right(obj),Left(i)) => throw new FimException("TODO")
      case (Left(clazz),Right(fn)) => {
        val n1 = normalizeFromFim(fn)
        var field: Field = null
        clazz.getFields.foreach{ f=>
          val n2 = normalizeFromJava(f.getName)
          if(isStatic(f.getModifiers) && n1==n2) field=f
        }
        if(field!=null) field.set(null,v.convertTo(context, field.getType))
        else throw new FimException("Not a  static field")
      }
      case (Right(obj),Right(fn)) => {
        val n1 = normalizeFromFim(fn)
        var field: Field = null
        obj.getClass.getFields.foreach{ (f:Field)=>
          val n2 = normalizeFromJava(f.getName)
          if(!isStatic(f.getModifiers) && n1==n2) field=f
        }
        if(field!=null) field.set(obj,v.convertTo(context, field.getType))
        else throw new FimException("Not a field")
      }
    }
  val digits = List("zero","one","two","three","four","five","six","seven","eight","nine")
  def loadClass(id: List[String]):Class[_] = {
    val possibleClassNames = new ListBuffer[String]
    def allPossibleShortClassNames(a:List[String]):List[String] = a match {
      case Nil=> List("")
      case x::xs =>
        val shorter = allPossibleShortClassNames(xs)
        val xl = x.toLowerCase.replaceAll("-","_").replaceAll("\'","")
        var tmp = shorter.map(xl.capitalize+_)
        if(x.length>1 && x.length<3){
          tmp reverse_:::  shorter.map(x.toUpperCase+_)
        }
        if(digits.contains(xl)) {
          tmp reverse_::: shorter.map(digits.indexOf(xl)+_)
        }
        tmp
    }
    val allPossibleClasses = for (packageLength <- 1 until id.length;
      pack = id take packageLength map (_.toLowerCase) mkString "." ;
      cn<-allPossibleShortClassNames(id.drop(packageLength)) ) yield pack+"."+cn
    //println(allPossibleClasses)
    val classes = allPossibleClasses.filter {
      className =>
        try{
          Class.forName(className)
          true
        } catch {
          case _:Exception => false
        }
    }

    if(classes.length==0){
      throw new FimException("Couldn't find class "+id.mkString(" "))
    }
    if(classes.length>1){
      throw new FimException("Couldn't find class "+id.mkString(" ")+"\nPossible candidates:\n\t"+classes.mkString("\n\t"))
    }
    Class.forName(classes.head)
  }
}
