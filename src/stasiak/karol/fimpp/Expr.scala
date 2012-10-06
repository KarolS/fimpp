package stasiak.karol.fimpp

sealed trait Expr {
  def eval(context:Context):RuntimeValue
}
case object EmptyExpr extends Expr {
  def eval(context:Context) = RuntimeNull
}
case class NumberValue(value: Long)  extends Expr {
  def eval(context:Context) = RuntimeNumber(value)
}
case class StringValue(value: String) extends Expr {
  def eval(context:Context) = RuntimeString(value)
}
case class Concatenation(exprs: List[Expr]) extends Expr {
  def eval(context:Context) = RuntimeString(exprs.map(_.eval(context).toString).mkString(""))
}
case class ListExpression(elems: List[Expr]) extends Expr {
  def eval(context:Context) = RuntimeList(elems.map(_.eval(context)))
}
case class VariableValue(ident: String) extends Expr {
  def eval(context: Context) = context.get(ident)
}
case class FunctionCall(function: String, args: List[Expr])  extends Expr {
  def eval(context: Context) = {
    context.get(function) match {
      case RuntimeFunction(f) => f.call(context, args map (_ eval context))
      case RuntimeBuiltin(f) => f(args map (_ eval context))
      case z => throw new FimException("Cannot call "+z)
    }
  }
}
case class FunctionCallEach(function: String, args: Expr)  extends Expr {
  def eval(context: Context) = {
    context.get(function) match {
      case RuntimeFunction(f) =>
        val e = args.eval(context)
        e match {
          case RuntimeList(es) => f.call(context, es)
          case RuntimeArray(es) => f.call(context, es.toList)
          case _ => throw new FimException("This are not multiple arguments")
        }
      case RuntimeBuiltin(f) =>
        val e = args.eval(context)
        e match {
          case RuntimeList(es) => f(es)
          case RuntimeArray(es) => f(es.toList)
          case _ => throw new FimException("This are not multiple arguments")
        }
      case z => throw new FimException("Cannot call "+z)
    }
  }
}
sealed trait Condition {
  def eval(context:Context):Boolean
}
case class Conjunction(conds: List[Condition])extends Condition {
  def eval(context:Context) = conds.forall(_.eval(context))
}
case class Alternative(conds: List[Condition])extends Condition  {
  def eval(context:Context) = conds.exists(_.eval(context))
}
case class Relational(left: Expr, op: String, right: Expr)extends Condition {
  def helper(e1:RuntimeValue, op:String, e2:RuntimeValue) :Boolean = {
    (e1,op,e2) match {
      case (RuntimeNumber(x),">",RuntimeNumber(y)) => x>y
      case (RuntimeNumber(x),"<",RuntimeNumber(y)) => x<y
      case (RuntimeNumber(x),">=",RuntimeNumber(y)) => x>=y
      case (RuntimeNumber(x),"<=",RuntimeNumber(y)) => x<=y
      case (x,"==",y) => x==y
      case (x,"=",y) => x==y
      case (x,"!=",y) => x!=y
      case (RuntimeList(xs),"all>",y) => xs.forall(x=>helper(x,">",y))
      case (RuntimeList(xs),"all<",y) => xs.forall(x=>helper(x,"<",y))
      case (RuntimeList(xs),"any>",y) => xs.exists(x=>helper(x,">",y))
      case (RuntimeList(xs),"any<",y) => xs.exists(x=>helper(x,"<",y))
      case _ => throw new FimException("Unsupported comparison between "+e1+" and "+e1+" using ‘"+op+"’")
    }
  }
  def eval(context:Context) = {
    val e1 = left.eval(context)
    val e2 = right.eval(context)
    helper(e1,op,e2)
  }
}
case class TrivialCondition(value: Boolean) extends Condition {
  def eval(context:Context) = value
}
