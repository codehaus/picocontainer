== Building the book ==

1) Make sure the book module is checked out next to the java module
2) Install Python
3) Run ant

== Continuous Integration of the Book ==

The book is DamageControlled and lands here upon checkin:
http://www.picocontainer.org/book.html

== Including source code in the book ==
You should *never* put any code in the text itself, since
you have no way of proving its correctness. Here is an
example: 
(the author forgot to make getInstance() static)

Instead, use a {snippet} in your code, such as here:
http://cvs.picocontainer.codehaus.org/viewrep/picocontainer/book/nanowar-basics.t2t?r=1.3

It will be sucked in and end up looking like this:
http://www.picocontainer.org/book.html#toc34

Never use absolute URLs in the {snippet}s, as we want both 
offline people and DC to be able to build the book. Use 
@JAVA_MODULE_URL@ in the beginning of the URL instead. 
The ant script will take care of replacing the tokens.

== Where to put new source code for the book ==
I suggest we create various org.[pico|nano]container.[module].book
packages in the existing test source trees for now (in the java module), 
since that is already set up to be built. Remember to write tests 
for the book source examples too. (For example, the singleton 
in the antipattern chapter compiles, but can't be used, and 
that would only be captured by a test).