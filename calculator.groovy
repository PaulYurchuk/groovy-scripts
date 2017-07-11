/**
 * calculator.groovy Created by student on 7/7/17.
 */


enum Operator {
    PLUS('+', 0, { a, b -> a + b }),
    MINUS('-', 0, { a, b -> a - b }),
    MULTIPLY('*', 1, { a, b -> a * b }),
    DIVIDE('/', 1, { a, b -> a / b })
    String value
    int precedence
    Closure calculate

    private Operator(String value, int precedence, Closure calculate) {
        this.value = value
        this.precedence = precedence
        this.calculate = calculate
    }

    def static valueFrom(string) {
        for (operator in Operator.values()) {
            if (operator.value == string) {
                return operator
            }
        }
        throw new IllegalArgumentException("invalid operator ${string}")
    }

    def int compareTo(other) {
        precedence <=> other.precedence
    }

    def String toString() {
        value
    }
}

class sortStation {
    def static convertToPostfix(infixExpression) {
        def output = []
        def stack = []
        tokenizeProcess(infixExpression).split().each { token ->
            processToken(token, output, stack)
        }
        while (!stack.isEmpty()) {
            output.add stack.pop()
        }
        output.join(' ')
    }

    def static processToken(token, output, stack) {
        if (token.isNumber()) {
            output.add token
        } else {
            switch (token) {
                case "(":
                    stack.add token
                    break
                case ")":
                    while (stack.last() != "(") {
                        output.add stack.pop()
                    }
                    stack.pop()
                    break
                default:
                    def operator = Operator.valueFrom(token)
                    while (!stack.isEmpty() &&
                            stack.last().is(Operator) &&
                            operator <= stack.last()) {
                        output.add stack.pop()
                    }
                    stack.add operator
            }
        }
    }

    def static tokenizeProcess(string) {
        def output = []
        string.each { letter ->
            if (!output.isEmpty() && letter != ' ') {
                def last = output.last()
                if (isParen(letter) && last != ' ') {
                    output.add ' '
                } else if (isParen(last)) {
                    output.add ' '
                }
            }
            output.add letter
        }
        output.join()
    }

    def static isParen(letter) {
        letter == '(' || letter == ')'
    }
}

def computePostfix(postfixExpression) {
    def stack = []
    postfixExpression.split().each { token ->
        if (token.isNumber()) {
            stack.add(token)
        } else {
            def b = stack.pop().toBigDecimal()
            def a = stack.pop().toBigDecimal()
            stack.add(Operator.valueFrom(token).calculate(a, b))
        }
    }
    stack.pop()
}

def computeInfix(infixExpression) {
    computePostfix(sortStation.convertToPostfix(infixExpression))
}

def infixExpression = '5.5 + ((6 + 2) * 4) - -3 * 1.5'
def expectedResult = 42
assert computeInfix(infixExpression) == expectedResult






