package com.example.simple_calculator;

/**
 * This class only contains one method which implements the algorithm of evaluating arithmetic expression.
 *
 * Note: this class is re-using the code of Boann (https://stackoverflow.com/users/964243/boann),
 *       and as Boann mentioned this code is released to the public domain (No Copyright),
 *       see: https://creativecommons.org/publicdomain/zero/1.0/.
 *
 * @Title: How to evaluate a math expression given in string form?
 * @Author: Boann (Stackoverflow user), Ziming (Pthahnil) Guo
 * @Date: July 16, 2016
 * @Availability: https://stackoverflow.com/questions/3422673/how-to-evaluate-a-math-expression-given-in-string-form
 */

public class Evaluate {

    /**
     * Method that help to calculate the arithmetic expression.
     *
     * @param str the math expression need to be calculated
     * @return double the result of calculation
     */
    public static double eval(final String str) {
        return new Object() {
            int pos = -1, ch;

            void nextChar() {
                ch = (++pos < str.length()) ? str.charAt(pos) : -1;
            }

            boolean eat(int charToEat) {
                while (ch == ' ') nextChar();
                if (ch == charToEat) {
                    nextChar();
                    return true;
                }
                return false;
            }

            double parse() {
                nextChar();
                double x = parseExpression();
                if (pos < str.length()) throw new RuntimeException("Unexpected: " + (char)ch);
                return x;
            }

            // Grammar:
            // expression = term | expression `+` term | expression `-` term
            // term = factor | term `×` factor | term `÷` factor
            // factor = `+` factor | `-` factor | `(` expression `)`
            //        | number | functionName factor | factor `^` factor

            double parseExpression() {
                double x = parseTerm();
                for (;;) {
                    if      (eat('+')) x += parseTerm(); // addition
                    else if (eat('-')) x -= parseTerm(); // subtraction
                    else return x;
                }
            }

            double parseTerm() {
                double x = parseFactor();
                for (;;) {
                    if      (eat('×')) x *= parseFactor(); // multiplication
                    else if (eat('÷')) x /= parseFactor(); // division
                    else return x;
                }
            }

            double parseFactor() {
                if (eat('+')) return parseFactor(); // unary plus
                if (eat('-')) return -parseFactor(); // unary minus

                double x;
                int startPos = this.pos;
                if (eat('(')) { // parentheses
                    x = parseExpression();
                    eat(')');
                } else if ((ch >= '0' && ch <= '9') || ch == '.') { // numbers
                    while ((ch >= '0' && ch <= '9') || ch == '.') nextChar();
                    x = Double.parseDouble(str.substring(startPos, this.pos));
                } else {
                    throw new RuntimeException("Unexpected: " + (char)ch);
                }

                return x;
            }
        }.parse();
    }
}
