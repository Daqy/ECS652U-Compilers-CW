class Main {
  io : CustomIO <- new CustomIO;
  stack : Stack <- new Stack;
  command : String <- "continue";

  main() : Object {{
    while (not command = "x") loop {
      command <- io.input(">");
      if command = "e" then stack <- stack.evaluate() else
      if command = "d" then stack.display() else
      if not command = "x" then stack <- stack.push(command) else "end"
      fi fi fi;
    } pool;
  }};
};

class CustomIO inherits IO {
  input(out : String) : String {{ out_string(out); in_string(); }};
  output(out : String) : SELF_TYPE {{ out_string(out); out_string("\n"); }};
};

class Stack {
  isEmpty() : Bool { true };
  length() : Int { 0 };
  push(element : String) : Stack {(new StackCommand).init(element, self)};
  evaluate() : Stack { self };
  head() : String {{ abort(); "empty"; }};
  tail() : Stack {{ abort(); self; }};
  display() : Stack { self };
  pop() : Stack { self };
  evaluateAdditionCommand() : Stack {{ abort(); self; }};
  evluateSwappingCommand() : Stack {{ abort(); self; }};
  addStack(additionStack : Stack) : Int {{ abort(); 0; }};
};

class StackCommand inherits Stack {
  io : CustomIO <- new CustomIO;
  convert : A2I <- new A2I;
  stack : Stack;
  value : String;
  tail : Stack;
  length : Int;
  commandValues : Stack;
  result : Int;

  isEmpty() : Bool { false };
  head() : String { value };
  tail() : Stack { tail };
  init(element : String, rest : Stack) : Stack {{ value <- element; tail <- rest; self; }};
  pop() : Stack { self.tail() };
  length() : Int {{
    stack <- self;
    length <- 0;

    while (not stack.isEmpty()) loop {
      length <- length + 1;
      stack <- stack.tail();
    } pool;
    length;
  }};
  display() : Stack {{
    stack <- self;

    while (not stack.isEmpty()) loop {
      io.output(stack.head());
      stack <- stack.tail();
    } pool;
    stack;
  }};
  evaluate() : Stack {{
    stack <- self;

    if not stack.isEmpty() then {
      if stack.head() = "+" then stack <- stack.evaluateAdditionCommand() else
      if stack.head() = "s" then stack <- stack.evluateSwappingCommand() else stack fi fi;
    } else stack fi;
    stack;
  }};
  evaluateAdditionCommand() : Stack {{
    stack <- self;
    commandValues <- new Stack;

    while (not 2 <= commandValues.length()) loop {
      if not stack.head() = "+" then { commandValues <- commandValues.push(stack.head()); stack <- stack.tail(); } else stack <- stack.tail() fi;
    } pool;
    stack <- self;
    result <- self.addStack(commandValues);
    if 3 < stack.length() then {stack <- self.pop();stack <- stack.pop();stack <- stack.pop(); stack <- stack.push(convert.i2a(result));} else stack <- (new StackCommand).init(convert.i2a(result), new Stack) fi;
    stack;
  }};

  addStack(additionStack : Stack) : Int {{
    result <- 0;
    while (not additionStack.isEmpty()) loop {
      result <- result + convert.a2i(additionStack.head());
      additionStack <- additionStack.tail();
    } pool;
    result;
  }};
  evluateSwappingCommand() : Stack {{
    stack <- self;
    commandValues <- new Stack;

    commandValues <- commandValues.push(stack.tail().tail().head());
    commandValues <- commandValues.push(stack.tail().head());
    if 3 < stack.length() then {stack <- self.pop(); stack <- stack.pop(); stack <- stack.pop();} else {
      stack <- (new StackCommand).init(commandValues.head(), new Stack); 
      commandValues <- commandValues.tail();
    } fi;
    while (not commandValues.isEmpty()) loop {
      stack <- stack.push(commandValues.head());
      commandValues <- commandValues.tail();
    } pool;
    stack; 
  }};
};