FIM++ program structure
=====================

FIM++ programs can have one of two structures:

* Main function contains functions inside (wrapped style).

* Functions precede the main function, and the main function, being the last one in the code, contains statements (C style).

In all examples below, colons can be replaced with full stops, except after the word Celestia. Parts in braces {...} are optional.

Wrapped style:
-------------

Execution of the program will start from the function that has the same name as the main function. It will be called with no arguments.

    Dear Princess Celestia: <module identifier> :

	Today I learned {about} <identifier: main function name>:

		<function declarations>

	Your faithful student, <identifier>.
	
C style
------

Execution of the program will start from the main function.

    Dear Princess Celestia: <module identifier> :

    <function declarations>

	Today I learned {about} {<identifier: main function name>}:

		<statements>

	Your faithful student, <identifier>.
