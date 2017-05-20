Documentation (Feb 05 2014)
By Xavier Dolques


 ##Relational Context Family editor##

commandline:
java -jar rcaexplore.jar editor [file]

Tool for creating a relational context family. Can only load RCFT file format
but can generate LaTeX and Galicia files.


 ##Interactive concept generator##

commandline:

java -jar rcaexplore.jar explogui [<file> <folder>]
will launch the explorer version of RCA with a graphical user interface
 on file and output the result in folder

java -jar rcaexplore.jar explocli <file> <folder>
will launch the explorer version of RCA with a commandline interface
 on file and output the result in folder. Warning : this interface is
 not maintained anymore and may not work correctly.
 
proposed algorithms are: 
-fca
-aoc-poset (hermes and ares are proposed)
-iceberg fca (from iceberg90, iceberg60... to iceberg90. icebergXX will 
generate only the concept which extent are equal or more than XX% of the 
total number of object in the context)

output files:  

-a dot file for every concept poset generated
-a svg file for every concept poset family generated (optional)
-a tex file for every object-attribute context generated at each step (optional)
-a xml file containing all the lattices and transient contexts. (optional)
-a trace file to sum up the choice of each step

 ##Non-interactive concept generator##

commandline:
java -jar rcaexplore.jar auto <file> <folder> [--output-svg] [--output-tex] [--output-xml]

 ##Concepts browser##

commandline:
java -jar rcaexplore.jar browser <XML file>

To see the content of a concept, chose a step, a context and type the 
name of the concept then click update. Concepts names of a selected 
context are display with their support between parenthesis. It is possible
to filter this list to obtain only concepts of a minimal support.
From a concept are available its parents, its children, its extent, its intent
and its simplified intent. If an element of the simplified intent and
and an element of the intent are selected, a representation of the corresponding
rule is displayed on the lower part of the window. 


 ##Known bugs##

Concept Generator

-inefficient stop condition

Concept browser

I/O

-algorithm value is not read