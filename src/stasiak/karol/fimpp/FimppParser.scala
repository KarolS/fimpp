package stasiak.karol.fimpp

import util.parsing.combinator.RegexParsers

object FimppParser extends RegexParsers {

  var ERRORS:ErrorMessageProvider = Trollestia
  val keywords =
    Set("yes","and","got","i","my","me","with","about", "I",
      "either","or","has","is","have","are","likes","did","only", "what",
      "like","when","had","was","were","in","of","on","today","made")                 //TODO
  val numbers = List("zero","one","two","three","four","five","six","seven","eight","nine","ten","eleven","twelve")
  val ordinals = List(null, "first","second","third","fourth","fifth","sixth","seventh","eighth","ninth","tenth","eleventh","twelfth")

  def fullStop: Parser[Unit] = opt(comma~kw("because")~commentContent)~"." ^^^ ()
  def headerEnd: Parser[Unit] = opt(comma~kw("because")~commentContent)~("."|":") ^^^ ()
  def sentenceEnd: Parser[Unit] = opt(comma~kw("because")~commentContent)~("."|"!") ^^^ ()
  def questionEnd: Parser[Unit] = opt(comma~kw("because")~commentContent)~("?") ^^^ ()
  def comma: Parser[Unit] = "," ^^^ ()

  def commentContent: Parser[Unit] = """[^\.\?!]*""".r ^^^ ()

  def kw(strs: String*): Parser[Unit] = strs match{
    case Seq() => failure(ERRORS.unknownError)
    case Seq(str) =>
      if (str.length()>1 && str.charAt(str.length-1)=='?')
        opt(kw(str.substring(0,str.length-1))) ^^^ ()
      else if (str.length()>1 && str.charAt(str.length-1)=='*')
        rep(kw(str.substring(0,str.length-1))) ^^^ ()
      else if (str.length()>1 && str.charAt(str.length-1)=='+')
        rep1(kw(str.substring(0,str.length-1))) ^^^ ()
      else ("""(?i)\b\Q"""+str+"""\E\b""").r  ^^^ ()
    case _ => kw(strs.head) ~ kw(strs.tail:_*)   ^^^ ()
  }
  def altkw(strs: String*):Parser[Unit] = strs match{
    case Seq() => failure(ERRORS.unknownError).asInstanceOf[Parser[Unit]]
    case Seq(str) => kw(str) ^^^ ()
    case _ => (kw(strs.head) | altkw(strs.tail:_*)) ^^^ ()
  }
  def rawWord: Parser[String] = ("""[A-Za-z]+('[a-z]+)?""".r)
  def word: Parser[String] = ("""[A-Za-z]+('[a-z]+)?""".r) ^? ({
    case word if !keywords.contains(word) =>
      if(word.toLowerCase=="friendship") "magic" else word.toLowerCase
  },{ case notAWord =>
    ERRORS.invalidIdentifier(notAWord.toLowerCase)
  })

  def rawIdentifier: Parser[String] = rep1(rawWord) ^^ {l=>l.mkString(" ")}
  def identifier: Parser[String] = rep1(word) ^^ {l=>l.mkString(" ")}  //TODO: the/a/an

  def listOfIdentifiers: Parser[List[String]] = (
    identifier~opt(comma)~and~identifier ^^ {case a~_~_~b => List(a,b)}
    | identifier~comma~listOfIdentifiers ^^ {case a~_~bs => a::bs}
    | identifier ^^ {x:String=>List(x)}
    )

  def and: Parser[Unit] = kw("and")

  def or: Parser[Unit] = kw("or")

  def number: Parser[Long] = (
    (opt(kw("the?","number"))~>"""[0-9]+""".r) ^^ {s=>s.toLong}
    | opt(kw("the?","number"))~>word ^? {case word if numbers.contains(word) => numbers.indexOf(word).toLong}
    ) //TODO
  def ordinalLiteral: Parser[Long] = (
      (opt("the") ~> """[0-9]+""".r <~ """(?i)(st|th|nd|rd)\b""".r) ^^ {s=>s.toLong}
        | word ^? {case word if ordinals.contains(word) => ordinals.indexOf(word).toLong}
      ) //TODO

  def stringLiteral: Parser[Expr] = "\"[^\"]*\"".r ^^ {sl =>
    StringInnardsParser.parseAll(StringInnardsParser.stringInnards,sl.stripPrefix("\"").stripSuffix("\"")).get
  }

  def literal: Parser[Expr] = (
    stringLiteral
    | number ^^ {v=>NumberValue(v)}
    )//TODO
  def listExpression: Parser[ListExpression] = (
      simpleExpression~opt(comma)~and~simpleExpression ^^ {case a~_~_~b => ListExpression(List(a,b))}
        | simpleExpression~comma~listExpression ^^ {case a~_~ListExpression(bs) => ListExpression(a::bs)}
      )
  def arglist: Parser[List[Expr]] = (
    simpleExpression~opt(comma)~and~simpleExpression ^^ {case a~_~_~b => List(a,b)}
      | simpleExpression~comma~arglist ^^ {case a~_~bs => a::bs}
      | simpleExpression ^^ {e=>List(e)}
    )
  def expression: Parser[Expr] =
    listExpression | kw("only")~>simpleExpression ^^ {e=>ListExpression(List(e))} | simpleExpression
  def simpleExpression: Parser[Expr] = literal | identifier ^^{id=>VariableValue(id)} //TODO

  def ordinalExpression: Parser[Expr] = (
    ordinalLiteral<~kw("page")^^ {v=>NumberValue(v)}
      | kw("the?","page","numbered","by")~>simpleExpression
    )

  def condition:Parser[Condition] = andCondition | orCondition | simpleCondition
  def simpleCondition:Parser[Condition] = relationalCondition //TODO
  def andCondition:Parser[Conjunction] = (
      simpleCondition~and~simpleCondition ^^ {case c1~_~c2 => Conjunction(List(c1,c2))}
        | simpleCondition~comma~andCondition ^^ {case c1~_~Conjunction(cs) => Conjunction(c1::cs)}
      )
  def orCondition = kw("either?")~>eitherLessOrCondition
  def eitherLessOrCondition:Parser[Alternative] = (
    simpleCondition~or~simpleCondition ^^ {case c1~_~c2 => Alternative(List(c1,c2))}
      | simpleCondition~comma~eitherLessOrCondition ^^ {case c1~_~Alternative(cs) => Alternative(c1::cs)}
    )
  def relationalCondition:Parser[Condition] = (
    expression~condOperator~expression ^^ {case e1~op~e2 => Relational(e1,op,e2)}
      | altkw("everything","everypony")~>kw("in")~>expression~condOperator~expression ^^ {case e1~op~e2 => Relational(e1,"all"+op,e2)}
      | altkw("anything","anypony")~>kw("in")~>expression~condOperator~expression ^^ {case e1~op~e2 => Relational(e1,"any"+op,e2)}
    )
  def hasOrHave:Parser[Unit] = altkw("has","have","had")
  def isOrAre:Parser[Unit] = altkw("is","are","was","were")
  def condOperator:Parser[String] = (
    hasOrHave~kw("less","than") ^^^ "<"
      | hasOrHave~kw("more","than") ^^^ ">"
      | hasOrHave~kw("not","less","than") ^^^ ">="
      | hasOrHave~kw("not","more","than") ^^^ "<="
      | isOrAre~kw("equal","to") ^^^ "="
      | isOrAre~kw("not", "equal","to") ^^^ "!="
      | isOrAre~(kw("an?","element")|kw("elements"))~kw("of") ^^^ "="
      | isOrAre~kw("not")~(kw("an?","element")|kw("elements"))~kw("of") ^^^ "!="
    ) //TODO

  def increment: Parser[Increment] = (
    identifier~(kw("got"))~number~(kw("less")^^^(-1) | kw("fewer")^^^ (-1) | kw("more")^^^1)<~sentenceEnd
    ^^ {
      case i~_~n~dir =>
        Increment(i,NumberValue(n*dir))
    }
  )
  def assignment: Parser[Assignment] = (
    kw("did","you","know","that?")
    ~> identifier
    ~ altkw("likes","is","like","are")
    ~expression<~ questionEnd
    ^^ {
      case i~_~e=> Assignment(i,e);
    })
  def ifStat: Parser[IfStat] = (
    (kw("when") ~> condition <~ headerEnd)
      ~  rep(statement)
      ~ opt(kw("in","the","end")~comma~kw("i","did","this","instead")~headerEnd~>rep(statement))
      <~ kw("that's","what","i","did")<~sentenceEnd
      ^^ {
      case cond~body1~eBody =>
        IfStat(cond,body1,eBody.getOrElse(Nil))
    }
    )
  def whileStat: Parser[WhileStat] = (
    (kw("i","did","this","while") ~> condition <~ headerEnd)
      ~  rep(statement)
      ~ opt(kw("it","didn't","work")~comma~kw("but","i","knew","why")~headerEnd~>rep(statement))
      ~ opt(kw("in","the","end")~comma~kw("i","did","this","instead")~headerEnd~>rep(statement))
      <~ kw("that's","what","i","did")<~sentenceEnd
      ^^ {
      case cond~body1~cBody~fBody =>
        WhileStat(cond,body1,cBody, fBody.getOrElse(Nil))
    }
    )
  def repeatStat: Parser[RepeatStat] = (
    (kw("i","did","this") ~> (opt(number <~ kw("times") | kw("once")^^^1L)^^{ot=>ot.getOrElse(1L)}) <~ headerEnd)
      ~  rep(statement)
      ~ opt(kw("it","didn't","work")~comma~kw("but","i","knew","why")~headerEnd~>rep(statement))
      ~ opt(kw("in","the","end")~comma~kw("i","did","this","instead")~headerEnd~>rep(statement))
      <~ kw("that's","what","i","did")<~sentenceEnd
      ^^ {
      case t~body1~cBody~fBody =>
        RepeatStat(t,body1,cBody, fBody.getOrElse(Nil))
    }
    )
  def globalDeclStat: Parser[GlobalDeclStat] = (
    kw("yes")~>comma~>opt(kw("i","mean"))~>kw("that")~>identifier<~sentenceEnd ^^ {i=>GlobalDeclStat(i)}
    )
  def callFunctionStat: Parser[ExprStat] = (
    kw("i","also?")~>altkw("did","made","caused")~>identifier~kw("of")~arglist<~sentenceEnd ^^{
      case id~_~lArgs => ExprStat(FunctionCall(id, lArgs))
    }
    )
  def callFunctionEachStat: Parser[ExprStat] = (
    kw("i","also?")~>altkw("did","made","caused")~>identifier~kw("of","each","of?")~expression<~sentenceEnd ^^{
      case id~_~lArgs => ExprStat(FunctionCallEach(id, lArgs))
    }
    )
  def functionCallAssign: Parser[Assignment] = (
    identifier
      ~ (altkw("did","made")~>opt(altkw("a","an","the")) ~> identifier)
      ~ (kw("of")~> arglist <~sentenceEnd) ^^{
        case who~function~args => Assignment(who,FunctionCall(function,args))
      }
    )
  def functionCallEachAssign: Parser[Assignment] = (
    identifier
      ~ (altkw("did","made")~>opt(altkw("a","an","the")) ~> identifier)
      ~ (kw("of","each","of?")~> expression <~sentenceEnd) ^^{
      case who~function~args => Assignment(who,FunctionCallEach(function,args))
    }
    )
  def printStat: Parser[PrintStat] = kw("i")~>altkw("sang","wrote","said")~>opt(kw("that")|comma|":")~>expression<~sentenceEnd ^^ {e=>PrintStat(e)}
  def commentStat: Parser[Statement] = kw("by the way")~commentContent~sentenceEnd ^^^ NopStat
  def arrayAssignment: Parser[ArrayAssignment] = (
    (kw("on")~>ordinalExpression)
      ~ (kw("of","the?","book?")~>identifier)
      ~(kw("i")~>altkw("wrote","scribbled","noted")~>opt(kw("what","i","knew","about")|kw("about"))~>expression)
      <~sentenceEnd
      ^^{
      case ord~book~e => ArrayAssignment(book,ord,e)
    }
    )
  def arrayRetrieval: Parser[ArrayRetrieval] = (
    (kw("on")~>ordinalExpression)
      ~ (kw("of","the?","book?")~>identifier)
      ~(kw("i","read","about")~>identifier)
      <~sentenceEnd
      ^^{
      case ord~book~varName => ArrayRetrieval(book,ord,varName)
    }
    )
  def arrayInit: Parser[ArrayInit] = (
    kw("today?","i","found","a","book")~>opt("named"|"titled")~>identifier<~opt(kw("today"))<~sentenceEnd
      ^^ {id => ArrayInit(id)}
    )
  def statement: Parser[Statement] = (
    assignment | arrayAssignment
      | callFunctionStat | callFunctionEachStat
      | ifStat | whileStat | repeatStat
      | printStat | globalDeclStat
      | arrayInit | arrayRetrieval
      | commentStat
      | functionCallAssign | functionCallEachAssign //these two have to be here
      | increment    //increment has to be the last one!
    )

  def function: Parser[Function] = (
    (kw("i","learned","about?")~>identifier~opt(kw("with")~>listOfIdentifiers)<~headerEnd)
      ~rep(statement) ~
      (kw("that's","about")~>identifier~opt(kw("with")~>identifier)<~sentenceEnd)) ^? ({
    case (fName~args)~stats~(fName2~retArg) if fName==fName2 =>
      Function(fName, args.getOrElse(Nil),stats,retArg.getOrElse(fName))
  }, {
    case (fName~args)~stats~(fName2~retArg) =>
      ERRORS.mismatchedFunctionNameInFooter(fName,fName2)
    case _ =>
      ERRORS.unknownError
  })

  def mainFunction: Parser[Function] = (
    (kw("today","i","learned","about?")~>opt(identifier)<~headerEnd)
      ~rep(statement)
      ~ ((kw("your","faithful","student")~>comma~>identifier<~opt(sentenceEnd)))
    ) ^^ {
    case fName~stats~retArg =>
      Function(fName.getOrElse("<main>"), Nil,stats,retArg)
  }

  def wrappedModule:Parser[Module] = (
    (kw("dear","princess","celestia") ~> ":" ~> rawIdentifier <~ headerEnd)
      ~ (kw("today","i","learned","about?")~>identifier<~headerEnd)
      ~ rep(function)
      ~ ((kw("your","faithful","student")~>comma~>identifier<~opt(sentenceEnd)))
      ^^ {
      case name~main~functions~retArg =>
        Module(name,Function("letter to celestia",Nil,ExprStat(FunctionCall(main,Nil))::Nil,retArg)::functions)
    }
    )
  def c_likeModule:Parser[Module] = (
    (kw("dear","princess","celestia") ~> ":" ~> rawIdentifier <~ headerEnd)
      ~ rep(function)
      ~ mainFunction
      ^^ {
      case name~functions~m => Module(name,m::functions)
    }
    )
  def module = wrappedModule | c_likeModule
  def parseFim(s:String):Option[Module] = {
    parseAll(module,s) match {
      case Success(fimModule,_) => Some(fimModule)
      case Failure(errorMsg,_) =>
        println(errorMsg)
        None
    }
  }
}

object StringInnardsParser extends RegexParsers {
  override val skipWhitespace = false
  def expression: Parser[Expr] = """[^"']+""".r ^^ {e=> FimppParser.parseAll(FimppParser.expression,e).get}
  def stringInnards: Parser[Concatenation] = rep(
    (
      """[^"']+""".r ^^ {s=>StringValue(s)})
      | ("\'"~>expression<~"\'")
  )^^(l=>Concatenation(l))
}
