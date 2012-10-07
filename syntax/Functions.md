FIM++ functions
=============

In all examples below, colons can be replaced with full stops. Parts in braces {...} are optional.

Syntax:

    I learned {about} <identifier: function name> {with <list of identifiers: argument names>}:
        <statements>
    That's about <identifier: function name> {with <identifier: result variable>}.
    
The function name has to be identical at the baginning and the end, or the interpreter will accuse it of being a changeling.

If the result variable is given, then the function will return that variable's value. If it's not, the local variable with the same name as the function will be returned.

Syntax of the list of identifiers follows basic English rules. Oxford comma is optional.

Variable scope rules
------------------

By default, every store to a variable tries to store into a function-local variable, unless you explicitly say otherwise with the following statement:

    Yes, I mean that <variable name>.
    
From that statement every use of that name will refer to the variable in global scope. Technically, you can replace functions with that, because their name also live in a global scope.

Reading from a variable first tries to read from the local one, and if it is unassigned, tries to read the global one.
