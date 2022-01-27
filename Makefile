.PHONY: help
help:
	@echo "Helper to remove build (i.e., non-source) files from assignments dirs."
	@echo "Usage:"
	@echo "\tmake clean"

.PHONY: clean
clean:
	rm -f assignments/pa0/stack.s
	
	rm -f assignments/pa1/*.java
	rm -f assignments/pa1/*.class
	rm -f assignments/pa1/*.tokens
	rm -f assignments/pa1/ast/*.class
	rm -f assignments/pa1/ast/visitor/*.class
	rm -fd assignments/pa1/ast/visitor
	rm -fd assignments/pa1/ast
	rm -f assignments/pa1/build.log
	rm -f assignments/pa1/myfrontend
	rm -f assignments/pa1/reffrontend
	rm -rf assignments/pa1/testsuite
	
	rm -f assignments/pa2/*.java
	rm -f assignments/pa2/*.class
	rm -f assignments/pa2/*.tokens
	rm -f assignments/pa2/ast/*.class
	rm -f assignments/pa2/ast/parser/*.class
	rm -fd assignments/pa2/ast/parser
	rm -f assignments/pa2/ast/visitor/*.class
	rm -fd assignments/pa2/ast/visitor
	rm -fd assignments/pa2/ast
	rm -f assignments/pa2/build.log
	rm -f assignments/pa2/mysemant
	rm -f assignments/pa2/refsemant
	rm -rf assignments/pa2/testsuite
	
	rm -f assignments/pa3/*.java
	rm -f assignments/pa3/*.class
	rm -f assignments/pa3/*.tokens
	rm -f assignments/pa3/ast/*.class
	rm -f assignments/pa3/ast/parser/*.class
	rm -fd assignments/pa3/ast/parser
	rm -f assignments/pa3/ast/visitor/*.class
	rm -fd assignments/pa3/ast/visitor
	rm -fd assignments/pa3/ast
	rm -f assignments/pa3/build.log
	rm -f assignments/pa3/mybackend
	rm -f assignments/pa3/hello_world.s
	rm -rf assignments/pa3/testsuite

