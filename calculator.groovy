import jdk.nashorn.internal.runtime.regexp.joni.Regex
import org.apache.tools.ant.util.regexp.Regexp
import org.codehaus.groovy.ant.Groovy

import java.util.regex.Matcher

/**
 * Created by student on 7/6/17.
 */


//add brackets support
def ainput = "32 / 8 - 48 + 540 + 6 * 2"
def operators = ['/','*','-','+']

print ainput + " = "

class inputParserClass {
    String[] parsedInput
    inputParserClass(String input) {
        parsedInput = input.findAll("(\\d+\\.?\\d*|[\\/\\*\\-\\+\\(\\)])")
    }

}
class atomicOperation {
    String performAtomicOperation(String leftArgument, String rightArgument, String operation) {
        switch (operation[0]){
            case '+' :
                return (leftArgument.toDouble() + rightArgument.toDouble()).toString()
                break
            case '-' :
                return (leftArgument.toDouble() - rightArgument.toDouble()).toString()
                break
            case '/' :
                //to do: division by zero check
                return (leftArgument.toDouble() / rightArgument.toDouble()).toString()
                break
            case '*' :
                return (leftArgument.toDouble() * rightArgument.toDouble()).toString()
                break
        }
    }
}


def calc = new atomicOperation()
def input = new inputParserClass(ainput)


while(input.parsedInput.findIndexOf {it in operators} > -1 ) {
    def  currentOperationIndex =  input.parsedInput.findIndexOf {it in operators}
    input.parsedInput[currentOperationIndex] = calc.performAtomicOperation(input.parsedInput[currentOperationIndex - 1],input.parsedInput[currentOperationIndex + 1], input.parsedInput[currentOperationIndex])
    input.parsedInput = input.parsedInput.minus(input.parsedInput[currentOperationIndex - 1], input.parsedInput[currentOperationIndex + 1])

}

print(input.parsedInput)

