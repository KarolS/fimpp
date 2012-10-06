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
  def interpret(context: Context)
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
  def interpret(context: Context) = println(expr eval context toString)
}
case class Module(name:String, functions:List[Function]){
  def run() = functions.head.call(new Context(this), Nil)
}