package stasiak.karol.fimpp

import collection.mutable

case class Function(name:String, argNames:List[String], body:List[Statement], returnVar: String){
  def call(context: Context, args: List[RuntimeValue]):RuntimeValue={
    val c = context.forFunctionCall(this, args)
    body foreach (_ interpret c)
    c.get(returnVar)
  }
}

sealed trait Statement{
  def interpret(context: Context): Unit
}
case class ClassImportStat(variable: String, id: List[String])  extends Statement {
  def interpret(context: Context) {
    context.set(variable, RuntimeClass(JavaInterop.loadClass(id)))
  }
}
case class MethodCall(returnTo: Option[String], clazz: Option[String], id: String, args: List[Expr]) extends Statement {
  def interpret(context: Context) {
    clazz match {
      case Some(cla) =>
        context get cla match {
          case RuntimeClass(cl)=>
            val result = JavaInterop.callMethod(context, cl, id, args.map(_ eval context))
            returnTo foreach { context.set(_, result) }
          case _ => throw new FimException("‘"+cla+"’ is not a class")
        }
      case None =>
        val evaluatedArgs = args.map(_ eval context)
        if(args.isEmpty) throw new FimException("I have no idea what to do")
        if(evaluatedArgs.head == null) throw new FimException("There's nothing here")
        val result = JavaInterop.callMethod(context, evaluatedArgs.head.toJava.getClass, id, evaluatedArgs)
        returnTo foreach { context.set(_, result) }
    }
  }
}
case class ConstructorCall(returnTo: Option[String], clazz: String, args: List[Expr]) extends Statement {
  def interpret(context: Context) {
    context get clazz match {
      case RuntimeClass(cl)=>
        val result = JavaInterop.callConstructor(context, cl, args.map(_ eval context))
        returnTo foreach { context.set(_, result) }
      case _ => throw new FimException("‘"+clazz+"’ is not a class")
    }
  }
}
case class FieldAssignment(objClass: String, field: Expr, value: Expr) extends Statement {
  def interpret(context: Context) {
    val oc = context get objClass
    val v = value eval context
    (oc,field) match {
      case (RuntimeClass(cl),VariableValue(n)) => JavaInterop.setField(context, Left(cl), Right(n),v)
      case (RuntimeClass(cl),NumberValue(n)) => JavaInterop.setField(context, Left(cl), Left(n.toInt),v)
      case (RuntimeJavaObject(w),VariableValue(n)) => JavaInterop.setField(context, Right(w), Right(n),v)
      case (RuntimeJavaObject(w),NumberValue(n)) => JavaInterop.setField(context, Right(w), Left(n.toInt),v)
      case _ => throw new FimException("TODO")
    }
  }
}
case class FieldRetrieval(objClass: String, field: Expr, otherVar: String) extends Statement {
  def interpret(context: Context) {
    val oc = context get objClass
    val result = (oc,field) match {
      case (RuntimeClass(cl),VariableValue(n)) => JavaInterop.getField(Left(cl), Right(n))
      case (RuntimeClass(cl),StringValue(n)) => JavaInterop.getField(Left(cl), Right(n))
      case (RuntimeClass(cl),Concatenation(List(StringValue(n)))) => JavaInterop.getField(Left(cl), Right(n))
      case (RuntimeClass(cl),NumberValue(n)) => JavaInterop.getField(Left(cl), Left(n.toInt))
      case (RuntimeJavaObject(v),VariableValue(n)) => JavaInterop.getField(Right(v), Right(n))
      case (RuntimeJavaObject(v),StringValue(n)) => JavaInterop.getField(Right(v), Right(n))
      case (RuntimeJavaObject(v),Concatenation(List(StringValue(n)))) => JavaInterop.getField(Right(v), Right(n))
      case (RuntimeJavaObject(v),NumberValue(n)) => JavaInterop.getField(Right(v), Left(n.toInt))
      case _ => throw new FimException("TODO: "+oc+" "+field)
    }
    context.set(otherVar, result)
  }
}
case class Assignment(variable: String, value: Expr) extends Statement {
  def interpret(context: Context) {
    context.set(variable, value eval context)
  }
}
case class ArrayAssignment(arrayVariable: String, index: Expr, value: Expr) extends Statement {
  def interpret(context: Context) {
    context.get(arrayVariable) match {
      case a:RuntimeArray =>
        index.eval(context) match {
          case RuntimeNumber(i) => a.set(i, value eval context)
          case _ => throw new FimException("This is not a page number")
        }
      case _ => throw new FimException(arrayVariable+" is not a book")
    }
  }
}
case class ArrayRetrieval(arrayVariable: String, index: Expr, otherVariable:String) extends Statement {
  def interpret(context: Context) {
    context.get(arrayVariable) match {
      case a:RuntimeArray =>
        index.eval(context) match {
          case RuntimeNumber(i) => context.set(otherVariable, a.get(i))
          case _ => throw new FimException("This is not a page number")
        }
      case _ => throw new FimException(arrayVariable+" is not a book")
    }
  }
}
case class ArrayInit(arrayVariable: String) extends Statement {
  def interpret(context: Context) {
    context.set(arrayVariable, RuntimeArray())
  }
}
case class ExprStat(e: Expr) extends Statement {
  def interpret(context: Context) {
    e.eval(context)
  }
}
case object NopStat extends Statement {
  def interpret(context: Context) {}
}
case class Increment(variable: String, value: Expr) extends Statement  {
  def interpret(context: Context) {
    (context.get(variable),value.eval(context)) match {
      case (RuntimeNumber(x),RuntimeNumber(y)) => context.set(variable, RuntimeNumber(x+y) )
      case _ => throw new FimException("Cannot increment")
    }
  }
}
case class IfStat(cond: Condition, body: List[Statement], body2: List[Statement]) extends Statement{
  def interpret(context: Context) {
    if (cond.eval(context)) {
      body.foreach(_ interpret context)
    } else {
      body2.foreach(_ interpret context)
    }
  }
}
case class WhileStat(cond: Condition, body:List[Statement], catchBody: Option[List[Statement]], finallyBody:List[Statement]) extends Statement{
  def interpret(context: Context) {
    catchBody match{
      case Some(cBody) =>
        try{
          while (cond.eval(context)) {
            body.foreach(_ interpret context)
          }

        } catch{
          case e:FimException => cBody.foreach(_ interpret context)
        } finally {
          finallyBody.foreach(_ interpret context)
        }
      case None =>
        try{
          while (cond.eval(context)) {
            body.foreach(_ interpret context)
          }
        } finally {
          finallyBody.foreach(_ interpret context)
        }
    }

  }
}
case class GlobalDeclStat(id:String) extends Statement{
  def interpret(context: Context) {
    context.treatAsGlobal(id)
  }
}
case class RepeatStat(times: Long, body:List[Statement], catchBody: Option[List[Statement]], finallyBody:List[Statement]) extends Statement{
  def interpret(context: Context) {
    catchBody match{
      case Some(cBody) =>
        try{
          for(_ <- 0L until times) {
            body.foreach(_ interpret context)
          }

        } catch{
          case e:FimException => cBody.foreach(_ interpret context)
        } finally {
          finallyBody.foreach(_ interpret context)
        }
      case None =>
        try{
          for(_ <- 0L until times)  {
            body.foreach(_ interpret context)
          }
        } finally {
          finallyBody.foreach(_ interpret context)
        }
    }

  }
}

case class PrintStat(expr: Expr) extends Statement {
  def interpret(context: Context) = print(expr eval context toString)
}
case class Module(name:String, functions:List[Function]){
  def run() = functions.head.call(new Context(this), Nil)
}