class Stack inherits IO {
  isNil() : Bool { true };
  head()  : String { { abort(); "0"; } };
  tail()  : Stack { { abort(); self; } };
  element(i : String) : Stack {
    (new Element).init(i, self)
  };
  display() : Stack {{ abort(); self; }};
  evaluate() : Stack { self };
  pop() : Stack {{ abort(); self; }};
  plus() : Stack {{ abort(); self; }};
  swap() : Stack {{ abort(); self; }};
  add(num1 : String, num2 : String) : String {{ abort(); "0"; }};
  length() : Int { 0 };
};

class Element inherits Stack {
  headerValue : String;
  stack : Stack;
  tempList : Stack;
  converter : A2I;
  value : String;
  tnum1 : Int;
  tnum2 : Int;
  tempNumber1 : String;
  tempNumber2 : String;
  result : String;
  counter : Int;

  isNil() : Bool { false };
  head() : String { headerValue };
  tail() : Stack { stack };
  init(i : String, rest : Stack) : Stack {{
	 headerValue <- i;
	 stack <- rest;
	 self;
  }};
  display() : Stack {{
    tempList <- self;
    while (not tempList.isNil()) loop {
	    out_string(tempList.head());
      out_string("\n");
	    tempList <- tempList.tail();
	  } pool;
    self;
  }};
  evaluate() : Stack {{
    tempList <- self;
    if not tempList.isNil() then {
      if tempList.head() = "+" then {
        tempList <- tempList.plus();
      } else 
      if tempList.head() = "s" then {
        tempList <- tempList.swap();
      } else tempList fi fi;
    } else tempList fi;
    tempList;
  }};
  pop() : Stack {{
    self.tail();
  }};
  plus() : Stack {{
    tempList <- self;
    tempList <- tempList.pop();
    tempNumber1 <- tempList.head();
    tempList <- tempList.pop();
    tempNumber2 <- tempList.head();
    value <- tempList.add(tempNumber1, tempNumber2);

    if 1<tempList.length() then {
      tempList <- tempList.pop();
      tempList <- tempList.element(value);
    } else tempList <- (new Element).init(value, new Stack) fi;
    tempList;
  }};
  length() : Int {{
    tempList <- self;
    counter <- 0;
    while (not tempList.isNil()) loop {
	    counter <- counter + 1;
	    tempList <- tempList.tail();
	  } pool;
    counter;
  }};
  add(num1 : String, num2 : String) : String {{
    converter <- new A2I;
    tnum1 <- converter.a2i(num1);
    tnum2 <- converter.a2i(num2);
    result <- converter.i2a((tnum1+tnum2));
    result;
  }};
  swap() : Stack {{
    tempList <- self;
    tempList <- tempList.pop();
    tempNumber1 <- tempList.head();
    tempList <- tempList.pop();
    tempNumber2 <- tempList.head();

    if 1<tempList.length() then {
      tempList <- tempList.pop();
      tempList <- tempList.element(tempNumber1);
      tempList <- tempList.element(tempNumber2);
    } else {
      tempList <- (new Element).init(tempNumber1, new Stack);
      tempList <- tempList.element(tempNumber2);
    } fi;
    tempList;
  }};
};

class Main inherits IO {
  mylist : Stack;
  value : String;
  inputStream : Bool <- true;

  main() : Object {{
    mylist <- new Stack;
    while (inputStream) loop {
      out_string(">");
      value <- in_string();
      if value = "e" then mylist <- mylist.evaluate() else
      if value = "d" then mylist.display() else
      if value = "x" then inputStream <- false else 
      { mylist <- mylist.element(value); }
        fi fi fi;
    }
    pool;
  }};

};