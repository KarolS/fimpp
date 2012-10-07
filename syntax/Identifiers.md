FIM++ identifiers
================

A word is a sequence of ASCII letters, optionally containing at most one apostrophe and any number of hyphens somewhere in the middle. No other characters are allowed in the words. Unicode is not supported yet.

In FIM++, any sequence of words can be an identifier, except of those containing one of the following reseved words:

about, and, are, did, either, got, had, has, have, I, in, is, like, likes, made, me, my, of, on, only, or, today, was, were, what, when, with, yes

The list above might grow in future releases. The only exception are module names: they can contain whatever words you like.

Identifiers are not case sensitive. Also, words a/an/the are stripped from the identifiers during the parsing phase (so identifiers "the Cutie Mark Crusaders" and "CUTIE MARK CRUSADERS" are considered identical).

The following are all valid identifiers:

    Twilight Sparkle
    Crackle's cousin
    Rainbow Dash
    The Great-and-Powerful Trixie

And the following are not:

    The Great and Powerful Trixie
    DJ-Pon3
    Flim & Flam

