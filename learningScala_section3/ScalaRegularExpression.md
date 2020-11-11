### 1. Introduction 




Regular Expression are settings which can be used to find patterns in data. Any string can be converted to a regular expression using the .r 
method.


### 2. Basic 


|Expressions|Matching Rules|
|-----|:---:|
|^|Match the input string begins.|
|$|Match the input end of the string position.|
|.|Matches any single character except "\ r \ n" is.|
|[...]|character set. Matches any character included. For example, "[abc]" matches "plain" in the "a".|
|[^ ...]|Reverse character set. Matches any character not included. For example, "[^ abc]" matches "plain" in the "p", "l", "i", "n".|
|\\ A|Match the input string start position (no multi-line support)|
|\\z|End of the string ($ similar, but not affect the treatment options for multiple rows)|
|\\Z|End of the string or the end of the line (from treatment on multiple-line options)|
|re * | Repeated zero or more times|
|re +|Repeated one or more times|
|re? |Repeated zero or one times|
|re {n}|	Repeated n times|
|re {n, m}|Repeated n to m times|
|"a | b" |A match or b  |
|(Re) |Match re, and capture the text to auto-named group |
|\\w|Match letters or numbers, or underscore characters or |
|\\W |Not match any letters, numbers, underscores, Chinese characters |
|\\s |Matches any whitespace, equivalent to [\ t \ n \ r \ f] |
|\\S| Not match any whitespace character|
|\\d |Matching numbers, similar to [0-9] |
|\\D |Matches any non-numeric characters |


### 2. Methods Commonly Used in Regular Expression

```scala
val numberPattern:Regex="[0-9]".r

numberPattern.findFirstMatch("naver@com") match{
//"_" indicates anything that satifies the expression we specify
case(_)=>println("Password Ok")
case None=>println("Password must contain a number")
}
```



