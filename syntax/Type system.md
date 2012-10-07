FIM++ type system
===============

FIM++ has only several types. It's not an object-oriented language yet, nor can it be called functional (although you can pass functions around). All assignments (both to variables and to function arguments) are by reference. It doesn't matter much though, because arrays are the only mutable type as for now.

Numbers
------

Literal syntax:

    1
    90
    seven
    the number 42
    the number five
    number 10
    
Only positive literals are supported as for now. Words work only from 0 to 12.

Strings
-------

Literal syntax:

    "This is a string"
    "This string will have a a value of Applejack inserted at runtime: 'Applejack'. See?"
    
Tuples
-----

Tuples are formed in a way similar to the argument lists. They currently suck though, so there's little use for them:

    1, 2, and "string"
    Twilight, Rarity and Spike
    
Booleans
-------

Literal syntax:

    harmony
    chaos

You can't use booleans directly in conditional expressions, so you can't write "When Spike: ...", you need to write "When Spike was an element of harmony: ..."

Note: the "literals" above are actually defined as built-in global variables. You can redefine them, but good luck then!

Arrays
------

Arrays are dynamically growing lists of any kinds of values. They are indexed from one (not from zero!) If you try to write past the size, it will simply grow. If you try to read, you'll get special value called nothing.

Nothing
------

This is the value that is returned from reading arrays from uninitialized cells and from some other places. It has no literal, so you can't even check if you got it.

