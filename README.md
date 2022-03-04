# ECS652U-cw-190434953
Custom error handling was not implemented apart from the lexer.

The lexer catches:
Invalid characters that cant be tokenized 
Unterminated string constants
Strings constants too long
String contains escaped null character
EOF in string constant
Unmatched Parenthesis

This allows the parser to run correctly and receover from errors during testing.