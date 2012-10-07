fimpp
=====

FIM++ interpreter, released under GNU General Public License 3.

Inspired by [this Equestria Daily post](http://www.equestriadaily.com/2012/10/editorial-fim-pony-programming-language.html) and [this DeviantArt sample program and some comments below it](http://deftcrow.deviantart.com/art/FiM-Programming-Hello-World-99-Jugs-of-Cider-330736334).

FIM++ is imperative, dynamically-typed, interpreted language. Currently, it support integer arithmetic, console output, dynamically growing arrays, and subroutines.

Running
-------

In the bin directory, there are all necessary JARs and scripts to run this interpreter on any machine with Java installed. Know that the fimpp.jar in that directory might lag behind the actual interpreter sources, so you might want to compile the interpreter yourself.

Anyway, to run the interpreter with given FIM++ source file from your command line, assuming that the bin directory is in your PATH environment variable:

    $ fimpp <FIM++ script file> 

Compilation
----------

You will need Scala compiler from [Scala's website](http://www.scala-lang.org/downloads). JDK would also come in handy.

    $ mkdir target
    $ scalac -d target src/stasiak/karol/fimpp/*
    $ cd target
    $ jar cfe fimpp.jar stasiak.karol.fimpp.Main stasiak
    
To run it, simply use Scala or Java:

    $ scala fimpp.jar <FIM++ script file>
    or
    $ java --cp /path/to/scala-library.jar:fimpp.jar stasiak.karol.fimpp.Main <FIM++ script file>

or copy it into the bin directory in place of the old one and use the scripts.
     
Examples
--------

See examples directory

FIM++ Syntax
-----------

See syntax directory.


