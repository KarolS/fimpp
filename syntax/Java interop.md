FIM++ Java interop
================

In all examples below, parts in braces {...} are optional, and parts in parentheses separated by bars (...|...)are equivalent alternatives.

Loading classes
--------------

    I enchanted <identifier: variable> with <list of words> .

The list of words will be converted to fully qualified Java class name. The loaded class will be stored in the variable for later use.

    javax swing j frame => javax.swing.JFrame
    java lang math => java.lang.Math
    java util hash map => java.util.HashMap 
    org xml sax helpers xml filter impl => org.xml.sax.helpers.XMLFilterImpl
    javax swing graphics two => javax.swing.Graphics2

Creating objects
--------------

    I woke up <identifier: variable> with <identifier: class> {and <argument list>}.
    
Reading fields
------------

    I (took|got|stole) <field name> of <identifier: class or object> and {I} gave (it|them|her|him) to <identifier: target variable to store result> .

Field name can be either a string literal or an identifier. Future version will support numbers, so you will be able to access native Java arrays.

Writing fields
------------

    I gave <expression> to <field name> of <identifier: class or object> .

Field name can be either a string literal or an identifier.

Note: untested. Might not work yet.

Calling methods
-------------

    {I told <identifier: variable for result>} I asked {<identifier: class>} about <argument list> (so|if|what|when) {(it|she|he|they)} <identifier: method name>.

Equivalent of either: 

    result = class.method(arguments); 
    result = ((class)(arguments.head)).method(arguments.tail);

   {I told <identifier: variable for result>} I asked {<identifier: class>} about <argument> made <identifier: property name>.

Equivalent of either: 

    result = class.setproperty(argument, true); 
    result = ((class)argument)).setproperty(true);

   {I told <identifier: variable for result>} I asked {<identifier: class>} about <argument> with <arguments> of <property>.

Equivalent of either: 

    result = class.setproperty(argument, arguments); 
    result = ((class)argument)).setproperty(arguments);

   {I told <identifier: variable for result>} I asked {<identifier: class>} (what|if|when) <argument> {with <arguments>} (is|are|was|were|has|have|had) property .

Equivalent of either: 

    result = class.getproperty(argument, arguments); 
    result = ((class)argument)).getproperty(arguments);
    result = class.isproperty(argument, arguments); 
    result = ((class)argument)).isproperty(arguments);

Automatic conversion from functions to javax.swing.ActionListener
---------------------------------------------------------

You can add FIM++ functions as action listeners to Swing widgets.

The function will be called whenever the action happens without any arguments and its result will be discarded.

