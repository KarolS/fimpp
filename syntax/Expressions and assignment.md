FIM++ expressions and assignment statements
======================================

In all examples below, parts in braces {...} are optional, and parts in parentheses separated by bars (...|...)are equivalent alternatives.

FIM++ has many different constructs used for variable assignment.

Simple assignment
---------------

    Did you know that <identifier: variable name> (is|are|likes|like) <expression> ?
    
The expression can be an identifier or a literal. Tuple literals are allowed.

Assignment of a function call result
-------------------------------

    <identifier: variable name> (did|made) <identifier: function name> of <list of expressions> .

The list cannot contain tuple literals.

    <identifier: variable name> (did|made) <identifier: function name> of each of <identifier: tuple-holding variable> .

The tuple is deconstructed and its elements are passed as function arguments.

If you don't care about the function result, you can use keyword "I" (or words "I also") as the variable name. In that case, you can also use "caused" instead of "did"/"made".

Array cration/initialization
-------------------------

	{Today} I found a book (named|titled) <identifier> {today} .
	
Array store
----------

    On <page expression> of {the} {book} <identifier> I (wrote|scribbled|noted) {(what I knew about|about)} <expression> .

Page expression have any of the following forms:

    1st page
    the 2nd page
    fifth page
    the tenth page
    page numbered by Rarity
    the page numbered by Rainbow Crash

Array read
---------

    On <page expression> of {the} {book} <identifier> I read about <expression> .

    



