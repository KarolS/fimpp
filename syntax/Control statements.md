FIM++ control statements
======================

In all the following examples, you can use full stops instead of colons, and parts in braces {...} are optional.

While loop
----------

Syntax:

    I did this while <conditional expression>:
        <first group of statements>
    { It didn't work, but I knew why:
        <second group of statements> }
    { In the end, I did this instead:
        <third group of statements> }
    That's what I did.
    
This construct executes the first group of statements while the conditional expressions is true. 

When an error occurs, the loop is broken. If there is no second group of statements, the error is propagated back up the stack.

In any case, the third group of statements is guarenteed to execute after the loop ends, by natural or erratic ways.

Constant loop
------------

Syntax:

    I did this <number literal> times :
        <first group of statements>
    { It didn't work, but I knew why:
        <second group of statements> }
    { In the end, I did this instead:
        <third group of statements> }
    That's what I did.

Instead of writing "I did this 1 times", you can write "I did this once" or even "I did this".

This loops works similar to the one above, but it only loops a constant number of times.

Conditional statement
-------------------

    When <conditional expression> :
        <first group of statements>
    { In the end, I did this instead:
        <second group of statements> }
    That's what I did.
    
This construct executes the first group of statements when the conditional expression is true, and the second one if it's false.

Note that although similar, this construct does not perform any error handling!
   

