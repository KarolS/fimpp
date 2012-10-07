FIM++ conditional statements
=========================

Conditional expressions are used in some statements. They differ in syntax from other expressions.

In all examples below, parts in braces {...} are optional, and parts in parentheses separated by bars (...|...) are equivalent alternatives.

All comparisons are strictly typed, so the number 2 and the string "2" are not considered equal.

Numerical comparisons
-------------------

    <expression> (has|have|had) more than <expression>
    <expression> (has|have|had) less than <expression>
    <expression> (has|have|had) not more than <expression>
    <expression> (has|have|had) not less than <expression>
    (everypony|everything) in <tuple-holding variable> (has|have|had) more than <expression>
    (anypony|anything) in <tuple-holding variable> (has|have|had) more than <expression>
    ...

Equality
-------

    <expression> (is|are|was|were) equal to <expression>
    <expression> (is|are|was|were) not equal to <expression>
    <expression> (is|are|was|were) (an element|elements) of <expression>
    <expression> (is|are|was|were) not (an element|elements) of <expression>

In future releases, the form using the word "element" might be restricted to boolean values.

Alternative
----------

    <simple condition> or <simple condition>
    <simple condition>, <simple condition> or <simple condition>
    either <simple condition> or <simple condition>
    either  <simple condition>, <simple condition> or <simple condition>
    ...
    
Conjunction
----------

    <simple condition> and <simple condition>
    <simple condition>, <simple condition> and <simple condition>
    ...
