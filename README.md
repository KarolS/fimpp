fimpp
=====

FIM++ interpreter, released under GNU General Public License 3.

Inspired by [this Equestria Daily post](http://www.equestriadaily.com/2012/10/editorial-fim-pony-programming-language.html) and [this DeviantArt sample program and some comments below it](http://deftcrow.deviantart.com/art/FiM-Programming-Hello-World-99-Jugs-of-Cider-330736334).

FIM++ is imperative, dynamically-typed, interpreted language. Currently, it supports integer arithmetic, console output, dynamically growing arrays, and subroutines.

FIM++ can use Java classes, which opens it to the rich and wide JVM ecosystem. Also, it makes it possible to write a simple GUI program using Swing.

Running
-------

In the bin directory, there are all necessary JARs and scripts to run this interpreter on any machine with Java installed. Know that the fimpp.jar in that directory might lag behind the actual interpreter sources, so you might want to compile the interpreter yourself.

Anyway, to run the interpreter with given FIM++ source file from your command line, assuming that the bin directory is in your PATH environment variable:

    $ fimpp <FIM++ script file> 

Compilation
----------

You will need Scala compiler from [Scala's website](http://www.scala-lang.org/downloads) and JDK.

The easiest way is to use the provided makefile:

    $ make

And that's all! A freshly compiled JAR lands in your bin directory!

If you want to compile it manually (e.g. no _make_ on your system), follow these steps:

    $ mkdir target
    $ scalac -d target src/stasiak/karol/fimpp/*
    $ cd target
    $ jar cfe ../bin/fimpp.jar stasiak.karol.fimpp.Main stasiak
    
(By the way, the steps above should also work on Windows.)
     
Examples
--------

See examples directory

FIM++ Syntax
-----------

See syntax directory.


