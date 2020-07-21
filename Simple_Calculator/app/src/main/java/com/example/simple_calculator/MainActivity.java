package com.example.simple_calculator;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * Implement the main activity of the calculator app interface, that develop the method for
 * each button's function.
 *
 * Note: Due to the time limit, I re-use the code from Boann in Evaluate class to evaluate
 *       the parentheses arithmetic expression. However, it does not support the percentage function,
 *       so currently the percentage button is disabled for parentheses operation.
 *       In the future development, please revise the private method bracketCal to enable percentage in
 *       parentheses operations.
 *
 * @author: Ziming(Pthahnil) Guo (ZGuo059@uOttawa.ca)
 * @License: https://github.com/MikuFan239/Andriod_Calculator/blob/master/LICENSE
 */
public class MainActivity extends AppCompatActivity {
    //Button Variables that associate with the button on the layout:
    Button button0, button1, button2, button3, button4, button5, button6, button7, button8, button9, buttonDecimal, buttonEqual,
            buttonClean, buttonPercent, buttonDivide, buttonMultiply, buttonSubtract, buttonAdd, buttonBracket, buttonSign, buttonDelete;

    //TextView Variables that display any contents on the screen:
    TextView infoTextEditor, resultTextEditor;

    //Class variables use to determine the operation state of calculator:
    boolean addition, subtraction, multiplication, division, percentage, decimal, waitForValue2, equal, emptyTextBracket, bracket, sign, includeBracket, delete;

    //Class variables use to store operands' values:
    double previousValue = Double.NaN, previousResult, currentResult, value1, value2 = Double.NaN, beforePercentage;

    int numberOfOpenBracket = 0, numberOfCloseBracket = 0;

    //Class variables use to store previous operation's operator and how many zeros after decimal
    String previousOperator, operatorBeforeBracket, zerosAfterDecimal = "", bracketedOperation = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //All buttons added by id
        button0 = (Button) findViewById(R.id.btn0);
        button1 = (Button) findViewById(R.id.btn1);
        button2 = (Button) findViewById(R.id.btn2);
        button3 = (Button) findViewById(R.id.btn3);
        button4 = (Button) findViewById(R.id.btn4);
        button5 = (Button) findViewById(R.id.btn5);
        button6 = (Button) findViewById(R.id.btn6);
        button7 = (Button) findViewById(R.id.btn7);
        button8 = (Button) findViewById(R.id.btn8);
        button9 = (Button) findViewById(R.id.btn9);
        buttonDecimal = (Button) findViewById(R.id.btn_Dec);
        buttonEqual = (Button) findViewById(R.id.btn_Equ);
        buttonClean = (Button) findViewById(R.id.btnC);
        buttonPercent = (Button) findViewById(R.id.btn_percent);
        buttonDivide = (Button) findViewById(R.id.btn_Div);
        buttonMultiply = (Button) findViewById(R.id.btn_Multiply);
        buttonSubtract = (Button) findViewById(R.id.btn_Sub);
        buttonAdd = (Button) findViewById(R.id.btn_Add);
        buttonSign = (Button) findViewById(R.id.btn_Sign);
        buttonBracket = (Button) findViewById(R.id.btn_Bracket);
        buttonDelete = (Button) findViewById(R.id.btn_Delete);

        //TextEditor
        infoTextEditor = (TextView) findViewById(R.id.infoText);
        resultTextEditor = (TextView) findViewById(R.id.resultText);

        /**
         *-----------------------------------------
         * Numerical Buttons' methods
         *-----------------------------------------
         */

        button0.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                char lastChar;
                try{
                    lastChar= infoTextEditor.getText().toString().charAt(infoTextEditor.getText().length()-1);
                }
                catch (Exception e){
                    lastChar = ' ';
                }

                //If there is percentage sign in front of it
                if (percentage) {
                    buttonMultiply.performClick();
                }

                if(!bracket) {
                    //Only one zero can be entered at beginning, such as 0, or 1+0
                    if (!infoTextEditor.getText().equals("0") && (value2 != 0 || decimal)) {
                        //Display the button pressed on text editor
                        if (!equal) {
                            if(lastChar == ')'){//Add multiplication automatically
                                buttonMultiply.performClick();
                            }
                            infoTextEditor.setText(infoTextEditor.getText() + "0");
                        } else {
                            if(lastChar == ')'){//Add multiplication automatically
                                buttonMultiply.performClick();
                            }
                            infoTextEditor.setText("0");
                            equal = false;
                        }

                        //Automatically compute the result and display the result into
                        //resultTextEditor when a number got entered after the operator
                        if (waitForValue2 && Double.isNaN(value2)) {
                            value2 = 0;
                            compute();
                        } else if (waitForValue2 && !Double.isNaN(value2)) {
                            //Decimal situation such as from 1.1 + 1. to 1.1 + 1.1
                            if (decimal) {
                                zerosAfterDecimal += "0";
                            } else {
                                value2 = value2 * 10;
                            }
                            compute();
                        }
                    }
                    emptyTextBracket = false;
                }
                else{//To be revised if develop way to calculate parentheses with percentage button
                    if(lastChar == ')'){//Add multiplication automatically
                        infoTextEditor.setText(infoTextEditor.getText() + "×0");
                        bracketedOperation += "×0";
                    }
                    else{
                        infoTextEditor.setText(infoTextEditor.getText() + "0");
                        bracketedOperation += "0";
                    }

                    sign = false;
                    if(emptyTextBracket || delete){
                        resultTextEditor.setText(format(bracketCal(bracketedOperation)));
                    }
                    else{
                        value2 = bracketCal(bracketedOperation);
                        compute();
                    }
                }
            }
        });

        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                char lastChar;
                try{
                    lastChar= infoTextEditor.getText().toString().charAt(infoTextEditor.getText().length()-1);
                }
                catch (Exception e){
                    lastChar = ' ';
                }

                //If there is percentage sign in front of it
                if(percentage){
                    buttonMultiply.performClick();
                }

                if(!bracket) {
                    //Display the button pressed on text editor
                    if (!equal) {
                        if (infoTextEditor.getText().equals("0")) {//Situation that there is only 0 present
                            infoTextEditor.setText("1");
                        } else if (value2 == 0 && !decimal) {//Situation such as 1+0, if 1 has pressed we change to 1+1
                            if(lastChar == ')'){//Add multiplication automatically
                                buttonMultiply.performClick();
                            }
                            String currentText = infoTextEditor.getText().toString();
                            infoTextEditor.setText(currentText.substring(0, infoTextEditor.getText().length() - 1) + "1");
                        } else {
                            if(lastChar == ')'){//Add multiplication automatically
                                buttonMultiply.performClick();
                            }
                            infoTextEditor.setText(infoTextEditor.getText() + "1");
                        }
                    } else {
                        if(lastChar == ')'){//Add multiplication automatically
                            buttonMultiply.performClick();
                        }
                        infoTextEditor.setText("1");
                        equal = false;
                    }

                    //Automatically compute the result and display the result into
                    //resultTextEditor when a number got entered after the operator
                    String currentText = infoTextEditor.getText().toString();
                    if (waitForValue2 && Double.isNaN(value2)) {
                        value2 = 1;
                        compute();
                    } else if (waitForValue2 && !Double.isNaN(value2)) {
                        //Decimal situation such as from 1.1 + 1. to 1.1 + 1.1
                        if (currentText.charAt(infoTextEditor.getText().length() - 2) == '.') {
                            value2 = value2 + 0.1;
                        }
                        //There are zeros after decimal
                        else if (!zerosAfterDecimal.equals("")) {
                            if (value2 % 1 != 0) {//Situation such as from 1.10 to 1.101
                                String newValue2 = value2 + zerosAfterDecimal + "1";
                                value2 = Double.parseDouble(newValue2);
                            } else {//Situation such as from 1.0 to 1.01
                                String value2String = Double.toString(value2);
                                String newValue2 = value2String.substring(0, value2String.length() - 1) + zerosAfterDecimal + "1";
                                value2 = Double.parseDouble(newValue2);
                            }
                        }
                        //Decimal situation such as from 1.1 + 1.1 to 1.1 + 1.11 and so on.
                        else if (value2 % 1 != 0) {
                            String newValue2 = value2 + "1";
                            value2 = Double.parseDouble(newValue2);
                        } else {
                            value2 = value2 * 10 + 1;
                        }
                        compute();
                    }
                    emptyTextBracket = false;
                    zerosAfterDecimal = "";
                }
                else{//To be revised if develop way to calculate parentheses with percentage button
                    if(lastChar == ')'){//Add multiplication automatically
                        infoTextEditor.setText(infoTextEditor.getText() + "×1");
                        bracketedOperation += "×1";
                    }
                    else{
                        infoTextEditor.setText(infoTextEditor.getText() + "1");
                        bracketedOperation += "1";
                    }

                    sign = false;
                    if(emptyTextBracket || delete){
                        resultTextEditor.setText(format(bracketCal(bracketedOperation)));
                    }
                    else{
                        value2 = bracketCal(bracketedOperation);
                        compute();
                    }
                }
            }
        });

        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                char lastChar;
                try{
                    lastChar= infoTextEditor.getText().toString().charAt(infoTextEditor.getText().length()-1);
                }
                catch (Exception e){
                    lastChar = ' ';
                }

                //If there is percentage sign in front of it
                if(percentage){
                    buttonMultiply.performClick();
                }

                if(!bracket) {
                    //Display the button pressed on text editor
                    if (!equal) {
                        if (infoTextEditor.getText().equals("0")) {//Situation that there is only 0 present
                            infoTextEditor.setText("2");
                        } else if (value2 == 0 && !decimal) {//Situation such as 1+0, if 1 has pressed we change to 1+1
                            if(lastChar == ')'){//Add multiplication automatically
                                buttonMultiply.performClick();
                            }
                            String currentText = infoTextEditor.getText().toString();
                            infoTextEditor.setText(currentText.substring(0, infoTextEditor.getText().length() - 1) + "2");
                        } else {
                            if(lastChar == ')'){//Add multiplication automatically
                                buttonMultiply.performClick();
                            }
                            infoTextEditor.setText(infoTextEditor.getText() + "2");
                        }
                    } else {
                        if(lastChar == ')'){//Add multiplication automatically
                            buttonMultiply.performClick();
                        }
                        infoTextEditor.setText("2");
                        equal = false;
                    }

                    //Automatically compute the result and display the result into
                    //resultTextEditor when a number got entered after the operator
                    String currentText = infoTextEditor.getText().toString();
                    if (waitForValue2 && Double.isNaN(value2)) {
                        value2 = 2;
                        compute();
                    } else if (waitForValue2 && !Double.isNaN(value2)) {
                        //Decimal situation such as from 1.1 + 1. to 1.1 + 1.1
                        if (currentText.charAt(infoTextEditor.getText().length() - 2) == '.') {
                            value2 = value2 + 0.2;
                        }
                        //There are zeros after decimal
                        else if (!zerosAfterDecimal.equals("")) {
                            if (value2 % 1 != 0) {//Situation such as from 1.10 to 1.101
                                String newValue2 = value2 + zerosAfterDecimal + "2";
                                value2 = Double.parseDouble(newValue2);
                            } else {//Situation such as from 1.0 to 1.01
                                String value2String = Double.toString(value2);
                                String newValue2 = value2String.substring(0, value2String.length() - 1) + zerosAfterDecimal + "2";
                                value2 = Double.parseDouble(newValue2);
                            }
                        }
                        //Decimal situation such as from 1.1 + 1.1 to 1.1 + 1.11 and so on.
                        else if (value2 % 1 != 0) {
                            String newValue2 = value2 + "2";
                            value2 = Double.parseDouble(newValue2);
                        } else {
                            value2 = value2 * 10 + 2;
                        }
                        compute();
                    }
                    emptyTextBracket = false;
                    zerosAfterDecimal = "";
                }
                else{//To be revised if develop way to calculate parentheses with percentage button
                    if(lastChar == ')'){//Add multiplication automatically
                        infoTextEditor.setText(infoTextEditor.getText() + "×2");
                        bracketedOperation += "×2";
                    }
                    else{
                        infoTextEditor.setText(infoTextEditor.getText() + "2");
                        bracketedOperation += "2";
                    }

                    sign = false;
                    if(emptyTextBracket || delete){
                        resultTextEditor.setText(format(bracketCal(bracketedOperation)));
                    }
                    else{
                        value2 = bracketCal(bracketedOperation);
                        compute();
                    }
                }
            }
        });

        button3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                char lastChar;
                try{
                    lastChar= infoTextEditor.getText().toString().charAt(infoTextEditor.getText().length()-1);
                }
                catch (Exception e){
                    lastChar = ' ';
                }

                //If there is percentage sign in front of it
                if(percentage){
                    buttonMultiply.performClick();
                }

                if(!bracket) {
                    //Display the button pressed on text editor
                    if (!equal) {
                        if (infoTextEditor.getText().equals("0")) {//Situation that there is only 0 present
                            infoTextEditor.setText("3");
                        } else if (value2 == 0 && !decimal) {//Situation such as 1+0, if 1 has pressed we change to 1+1
                            if(lastChar == ')'){//Add multiplication automatically
                                buttonMultiply.performClick();
                            }
                            String currentText = infoTextEditor.getText().toString();
                            infoTextEditor.setText(currentText.substring(0, infoTextEditor.getText().length() - 1) + "3");
                        } else {
                            if(lastChar == ')'){//Add multiplication automatically
                                buttonMultiply.performClick();
                            }
                            infoTextEditor.setText(infoTextEditor.getText() + "3");
                        }
                    } else {
                        if(lastChar == ')'){//Add multiplication automatically
                            buttonMultiply.performClick();
                        }
                        infoTextEditor.setText("3");
                        equal = false;
                    }

                    //Automatically compute the result and display the result into
                    //resultTextEditor when a number got entered after the operator
                    String currentText = infoTextEditor.getText().toString();
                    if (waitForValue2 && Double.isNaN(value2)) {
                        value2 = 3;
                        compute();
                    } else if (waitForValue2 && !Double.isNaN(value2)) {
                        //Decimal situation such as from 1.1 + 1. to 1.1 + 1.1
                        if (currentText.charAt(infoTextEditor.getText().length() - 2) == '.') {
                            value2 = value2 + 0.3;
                        }
                        //There are zeros after decimal
                        else if (!zerosAfterDecimal.equals("")) {
                            if (value2 % 1 != 0) {//Situation such as from 1.10 to 1.101
                                String newValue2 = value2 + zerosAfterDecimal + "3";
                                value2 = Double.parseDouble(newValue2);
                            } else {//Situation such as from 1.0 to 1.01
                                String value2String = Double.toString(value2);
                                String newValue2 = value2String.substring(0, value2String.length() - 1) + zerosAfterDecimal + "3";
                                value2 = Double.parseDouble(newValue2);
                            }
                        }
                        //Decimal situation such as from 1.1 + 1.1 to 1.1 + 1.11 and so on.
                        else if (value2 % 1 != 0) {
                            String newValue2 = value2 + "3";
                            value2 = Double.parseDouble(newValue2);
                        } else {
                            value2 = value2 * 10 + 3;
                        }
                        compute();
                    }
                    emptyTextBracket = false;
                    zerosAfterDecimal = "";
                }
                else{//To be revised if develop way to calculate parentheses with percentage button
                    if(lastChar == ')'){//Add multiplication automatically
                        infoTextEditor.setText(infoTextEditor.getText() + "×3");
                        bracketedOperation += "×3";
                    }
                    else{
                        infoTextEditor.setText(infoTextEditor.getText() + "3");
                        bracketedOperation += "3";
                    }

                    sign = false;
                    if(emptyTextBracket || delete){
                        resultTextEditor.setText(format(bracketCal(bracketedOperation)));
                    }
                    else{
                        value2 = bracketCal(bracketedOperation);
                        compute();
                    }
                }
            }
        });

        button4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                char lastChar;
                try{
                    lastChar= infoTextEditor.getText().toString().charAt(infoTextEditor.getText().length()-1);
                }
                catch (Exception e){
                    lastChar = ' ';
                }

                //If there is percentage sign in front of it
                if(percentage){
                    buttonMultiply.performClick();
                }

                if(!bracket) {
                    //Display the button pressed on text editor
                    if (!equal) {
                        if (infoTextEditor.getText().equals("0")) {//Situation that there is only 0 present
                            infoTextEditor.setText("4");
                        } else if (value2 == 0 && !decimal) {//Situation such as 1+0, if 1 has pressed we change to 1+1
                            if(lastChar == ')'){//Add multiplication automatically
                                buttonMultiply.performClick();
                            }
                            String currentText = infoTextEditor.getText().toString();
                            infoTextEditor.setText(currentText.substring(0, infoTextEditor.getText().length() - 1) + "4");
                        } else {
                            if(lastChar == ')'){//Add multiplication automatically
                                buttonMultiply.performClick();
                            }
                            infoTextEditor.setText(infoTextEditor.getText() + "4");
                        }
                    } else {
                        if(lastChar == ')'){//Add multiplication automatically
                            buttonMultiply.performClick();
                        }
                        infoTextEditor.setText("4");
                        equal = false;
                    }

                    //Automatically compute the result and display the result into
                    //resultTextEditor when a number got entered after the operator
                    String currentText = infoTextEditor.getText().toString();
                    if (waitForValue2 && Double.isNaN(value2)) {
                        value2 = 4;
                        compute();
                    } else if (waitForValue2 && !Double.isNaN(value2)) {
                        //Decimal situation such as from 1.1 + 1. to 1.1 + 1.1
                        if (currentText.charAt(infoTextEditor.getText().length() - 2) == '.') {
                            value2 = value2 + 0.4;
                        }
                        //There are zeros after decimal
                        else if (!zerosAfterDecimal.equals("")) {
                            if (value2 % 1 != 0) {//Situation such as from 1.10 to 1.101
                                String newValue2 = value2 + zerosAfterDecimal + "4";
                                value2 = Double.parseDouble(newValue2);
                            } else {//Situation such as from 1.0 to 1.01
                                String value2String = Double.toString(value2);
                                String newValue2 = value2String.substring(0, value2String.length() - 1) + zerosAfterDecimal + "4";
                                value2 = Double.parseDouble(newValue2);
                            }
                        }
                        //Decimal situation such as from 1.1 + 1.1 to 1.1 + 1.11 and so on.
                        else if (value2 % 1 != 0) {
                            String newValue2 = value2 + "4";
                            value2 = Double.parseDouble(newValue2);
                        } else {
                            value2 = value2 * 10 + 4;
                        }
                        compute();
                    }
                    emptyTextBracket = false;
                    zerosAfterDecimal = "";
                }
                else{//To be revised if develop way to calculate parentheses with percentage button
                    if(lastChar == ')'){//Add multiplication automatically
                        infoTextEditor.setText(infoTextEditor.getText() + "×4");
                        bracketedOperation += "×4";
                    }
                    else{
                        infoTextEditor.setText(infoTextEditor.getText() + "4");
                        bracketedOperation += "4";

                    }

                    sign = false;
                    if(emptyTextBracket || delete){
                        resultTextEditor.setText(format(bracketCal(bracketedOperation)));
                    }
                    else{
                        value2 = bracketCal(bracketedOperation);
                        compute();
                    }
                }
            }
        });

        button5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { char lastChar;
                try{
                    lastChar= infoTextEditor.getText().toString().charAt(infoTextEditor.getText().length()-1);
                }
                catch (Exception e){
                    lastChar = ' ';
                }

                //If there is percentage sign in front of it
                if(percentage){
                    buttonMultiply.performClick();
                }

                if(!bracket) {
                    //Display the button pressed on text editor
                    if (!equal) {
                        if (infoTextEditor.getText().equals("0")) {//Situation that there is only 0 present
                            infoTextEditor.setText("5");
                        } else if (value2 == 0 && !decimal) {//Situation such as 1+0, if 1 has pressed we change to 1+1
                            if(lastChar == ')'){//Add multiplication automatically
                                buttonMultiply.performClick();
                            }
                            String currentText = infoTextEditor.getText().toString();
                            infoTextEditor.setText(currentText.substring(0, infoTextEditor.getText().length() - 1) + "5");
                        } else {
                            if(lastChar == ')'){//Add multiplication automatically
                                buttonMultiply.performClick();
                            }
                            infoTextEditor.setText(infoTextEditor.getText() + "5");
                        }
                    } else {
                        if(lastChar == ')'){//Add multiplication automatically
                            buttonMultiply.performClick();
                        }
                        infoTextEditor.setText("5");
                        equal = false;
                    }

                    //Automatically compute the result and display the result into
                    //resultTextEditor when a number got entered after the operator
                    String currentText = infoTextEditor.getText().toString();
                    if (waitForValue2 && Double.isNaN(value2)) {
                        value2 = 5;
                        compute();
                    } else if (waitForValue2 && !Double.isNaN(value2)) {
                        //Decimal situation such as from 1.1 + 1. to 1.1 + 1.1
                        if (currentText.charAt(infoTextEditor.getText().length() - 2) == '.') {
                            value2 = value2 + 0.5;
                        }
                        //There are zeros after decimal
                        else if (!zerosAfterDecimal.equals("")) {
                            if (value2 % 1 != 0) {//Situation such as from 1.10 to 1.101
                                String newValue2 = value2 + zerosAfterDecimal + "5";
                                value2 = Double.parseDouble(newValue2);
                            } else {//Situation such as from 1.0 to 1.01
                                String value2String = Double.toString(value2);
                                String newValue2 = value2String.substring(0, value2String.length() - 1) + zerosAfterDecimal + "5";
                                value2 = Double.parseDouble(newValue2);
                            }
                        }
                        //Decimal situation such as from 1.1 + 1.1 to 1.1 + 1.11 and so on.
                        else if (value2 % 1 != 0) {
                            String newValue2 = value2 + "5";
                            value2 = Double.parseDouble(newValue2);
                        } else {
                            value2 = value2 * 10 + 5;
                        }
                        compute();
                    }
                    emptyTextBracket = false;
                    zerosAfterDecimal = "";
                }
                else{//To be revised if develop way to calculate parentheses with percentage button
                    if(lastChar == ')'){//Add multiplication automatically
                        infoTextEditor.setText(infoTextEditor.getText() + "×5");
                        bracketedOperation += "×5";
                    }
                    else{
                        infoTextEditor.setText(infoTextEditor.getText() + "5");
                        bracketedOperation += "5";
                    }

                    sign = false;
                    if(emptyTextBracket || delete){
                        resultTextEditor.setText(format(bracketCal(bracketedOperation)));
                    }
                    else{
                        value2 = bracketCal(bracketedOperation);
                        compute();
                    }
                }
            }
        });

        button6.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                char lastChar;
                try{
                    lastChar= infoTextEditor.getText().toString().charAt(infoTextEditor.getText().length()-1);
                }
                catch (Exception e){
                    lastChar = ' ';
                }

                //If there is percentage sign in front of it
                if(percentage){
                    buttonMultiply.performClick();
                }

                if(!bracket) {
                    //Display the button pressed on text editor
                    if (!equal) {
                        if (infoTextEditor.getText().equals("0")) {//Situation that there is only 0 present
                            infoTextEditor.setText("6");
                        } else if (value2 == 0 && !decimal) {//Situation such as 1+0, if 1 has pressed we change to 1+1
                            if(lastChar == ')'){//Add multiplication automatically
                                buttonMultiply.performClick();
                            }
                            String currentText = infoTextEditor.getText().toString();
                            infoTextEditor.setText(currentText.substring(0, infoTextEditor.getText().length() - 1) + "6");
                        } else {
                            if(lastChar == ')'){//Add multiplication automatically
                                buttonMultiply.performClick();
                            }
                            infoTextEditor.setText(infoTextEditor.getText() + "6");
                        }
                    } else {
                        if(lastChar == ')'){//Add multiplication automatically
                            buttonMultiply.performClick();
                        }
                        infoTextEditor.setText("6");
                        equal = false;
                    }

                    //Automatically compute the result and display the result into
                    //resultTextEditor when a number got entered after the operator
                    String currentText = infoTextEditor.getText().toString();
                    if (waitForValue2 && Double.isNaN(value2)) {
                        value2 = 6;
                        compute();
                    } else if (waitForValue2 && !Double.isNaN(value2)) {
                        //Decimal situation such as from 1.1 + 1. to 1.1 + 1.1
                        if (currentText.charAt(infoTextEditor.getText().length() - 2) == '.') {
                            value2 = value2 + 0.6;
                        }
                        //There are zeros after decimal
                        else if (!zerosAfterDecimal.equals("")) {
                            if (value2 % 1 != 0) {//Situation such as from 1.10 to 1.101
                                String newValue2 = value2 + zerosAfterDecimal + "6";
                                value2 = Double.parseDouble(newValue2);
                            } else {//Situation such as from 1.0 to 1.01
                                String value2String = Double.toString(value2);
                                String newValue2 = value2String.substring(0, value2String.length() - 1) + zerosAfterDecimal + "6";
                                value2 = Double.parseDouble(newValue2);
                            }
                        }
                        //Decimal situation such as from 1.1 + 1.1 to 1.1 + 1.11 and so on.
                        else if (value2 % 1 != 0) {
                            String newValue2 = value2 + "6";
                            value2 = Double.parseDouble(newValue2);
                        } else {
                            value2 = value2 * 10 + 6;
                        }
                        compute();
                    }
                    emptyTextBracket = false;
                    zerosAfterDecimal = "";
                }
                else{//To be revised if develop way to calculate parentheses with percentage button
                    if(lastChar == ')'){//Add multiplication automatically
                        infoTextEditor.setText(infoTextEditor.getText() + "×6");
                        bracketedOperation += "×6";
                    }
                    else{
                        infoTextEditor.setText(infoTextEditor.getText() + "6");
                        bracketedOperation += "6";
                    }

                    sign = false;
                    if(emptyTextBracket || delete){
                        resultTextEditor.setText(format(bracketCal(bracketedOperation)));
                    }
                    else{
                        value2 = bracketCal(bracketedOperation);
                        compute();
                    }
                }
            }
        });

        button7.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                char lastChar;
                try{
                    lastChar= infoTextEditor.getText().toString().charAt(infoTextEditor.getText().length()-1);
                }
                catch (Exception e){
                    lastChar = ' ';
                }

                //If there is percentage sign in front of it
                if(percentage){
                    buttonMultiply.performClick();
                }

                if(!bracket) {
                    //Display the button pressed on text editor
                    if (!equal) {
                        if (infoTextEditor.getText().equals("0")) {//Situation that there is only 0 present
                            infoTextEditor.setText("7");
                        } else if (value2 == 0 && !decimal) {//Situation such as 1+0, if 1 has pressed we change to 1+1
                            if(lastChar == ')'){//Add multiplication automatically
                                buttonMultiply.performClick();
                            }
                            String currentText = infoTextEditor.getText().toString();
                            infoTextEditor.setText(currentText.substring(0, infoTextEditor.getText().length() - 1) + "7");
                        } else {
                            if(lastChar == ')'){//Add multiplication automatically
                                buttonMultiply.performClick();
                            }
                            infoTextEditor.setText(infoTextEditor.getText() + "7");
                        }
                    } else {
                        if(lastChar == ')'){//Add multiplication automatically
                            buttonMultiply.performClick();
                        }
                        infoTextEditor.setText("7");
                        equal = false;
                    }

                    //Automatically compute the result and display the result into
                    //resultTextEditor when a number got entered after the operator
                    String currentText = infoTextEditor.getText().toString();
                    if (waitForValue2 && Double.isNaN(value2)) {
                        value2 = 7;
                        compute();
                    } else if (waitForValue2 && !Double.isNaN(value2)) {
                        //Decimal situation such as from 1.1 + 1. to 1.1 + 1.1
                        if (currentText.charAt(infoTextEditor.getText().length() - 2) == '.') {
                            value2 = value2 + 0.7;
                        }
                        //There are zeros after decimal
                        else if (!zerosAfterDecimal.equals("")) {
                            if (value2 % 1 != 0) {//Situation such as from 1.10 to 1.101
                                String newValue2 = value2 + zerosAfterDecimal + "7";
                                value2 = Double.parseDouble(newValue2);
                            } else {//Situation such as from 1.0 to 1.01
                                String value2String = Double.toString(value2);
                                String newValue2 = value2String.substring(0, value2String.length() - 1) + zerosAfterDecimal + "7";
                                value2 = Double.parseDouble(newValue2);
                            }
                        }
                        //Decimal situation such as from 1.1 + 1.1 to 1.1 + 1.11 and so on.
                        else if (value2 % 1 != 0) {
                            String newValue2 = value2 + "7";
                            value2 = Double.parseDouble(newValue2);
                        } else {
                            value2 = value2 * 10 + 7;
                        }
                        compute();
                    }
                    emptyTextBracket = false;
                    zerosAfterDecimal = "";
                }
                else{//To be revised if develop way to calculate parentheses with percentage button
                    if(lastChar == ')'){//Add multiplication automatically
                        infoTextEditor.setText(infoTextEditor.getText() + "×7");
                        bracketedOperation += "×7";
                    }
                    else{
                        infoTextEditor.setText(infoTextEditor.getText() + "7");
                        bracketedOperation += "7";
                    }

                    sign = false;
                    if(emptyTextBracket || delete){
                        resultTextEditor.setText(format(bracketCal(bracketedOperation)));
                    }
                    else{
                        value2 = bracketCal(bracketedOperation);
                        compute();
                    }
                }
            }
        });

        button8.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                char lastChar;
                try{
                    lastChar= infoTextEditor.getText().toString().charAt(infoTextEditor.getText().length()-1);
                }
                catch (Exception e){
                    lastChar = ' ';
                }

                //If there is percentage sign in front of it
                if(percentage){
                    buttonMultiply.performClick();
                }

                if(!bracket) {
                    //Display the button pressed on text editor
                    if (!equal) {
                        if (infoTextEditor.getText().equals("0")) {//Situation that there is only 0 present
                            infoTextEditor.setText("8");
                        } else if (value2 == 0 && !decimal) {//Situation such as 1+0, if 1 has pressed we change to 1+1
                            if(lastChar == ')'){//Add multiplication automatically
                                buttonMultiply.performClick();
                            }
                            String currentText = infoTextEditor.getText().toString();
                            infoTextEditor.setText(currentText.substring(0, infoTextEditor.getText().length() - 1) + "8");
                        } else {
                            if(lastChar == ')'){//Add multiplication automatically
                                buttonMultiply.performClick();
                            }
                            infoTextEditor.setText(infoTextEditor.getText() + "8");
                        }
                    } else {
                        if(lastChar == ')'){//Add multiplication automatically
                            buttonMultiply.performClick();
                        }
                        infoTextEditor.setText("8");
                        equal = false;
                    }

                    //Automatically compute the result and display the result into
                    //resultTextEditor when a number got entered after the operator
                    String currentText = infoTextEditor.getText().toString();
                    if (waitForValue2 && Double.isNaN(value2)) {
                        value2 = 8;
                        compute();
                    } else if (waitForValue2 && !Double.isNaN(value2)) {
                        //Decimal situation such as from 1.1 + 1. to 1.1 + 1.1
                        if (currentText.charAt(infoTextEditor.getText().length() - 2) == '.') {
                            value2 = value2 + 0.8;
                        }
                        //There are zeros after decimal
                        else if (!zerosAfterDecimal.equals("")) {
                            if (value2 % 1 != 0) {//Situation such as from 1.10 to 1.101
                                String newValue2 = value2 + zerosAfterDecimal + "8";
                                value2 = Double.parseDouble(newValue2);
                            } else {//Situation such as from 1.0 to 1.01
                                String value2String = Double.toString(value2);
                                String newValue2 = value2String.substring(0, value2String.length() - 1) + zerosAfterDecimal + "8";
                                value2 = Double.parseDouble(newValue2);
                            }
                        }
                        //Decimal situation such as from 1.1 + 1.1 to 1.1 + 1.11 and so on.
                        else if (value2 % 1 != 0) {
                            String newValue2 = value2 + "8";
                            value2 = Double.parseDouble(newValue2);
                        } else {
                            value2 = value2 * 10 + 8;
                        }
                        compute();
                    }
                    emptyTextBracket = false;
                    zerosAfterDecimal = "";
                }
                else{//To be revised if develop way to calculate parentheses with percentage button
                    if(lastChar == ')'){//Add multiplication automatically
                        infoTextEditor.setText(infoTextEditor.getText() + "×8");
                        bracketedOperation += "×8";
                    }
                    else{
                        infoTextEditor.setText(infoTextEditor.getText() + "8");
                        bracketedOperation += "8";
                    }

                    sign = false;
                    if(emptyTextBracket || delete){
                        resultTextEditor.setText(format(bracketCal(bracketedOperation)));
                    }
                    else{
                        value2 = bracketCal(bracketedOperation);
                        compute();
                    }
                }
            }
        });

        button9.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                char lastChar;
                try{
                    lastChar= infoTextEditor.getText().toString().charAt(infoTextEditor.getText().length()-1);
                }
                catch (Exception e){
                    lastChar = ' ';
                }

                //If there is percentage sign in front of it
                if(percentage){
                    buttonMultiply.performClick();
                }

                if(!bracket) {
                    //Display the button pressed on text editor
                    if (!equal) {
                        if (infoTextEditor.getText().equals("0")) {//Situation that there is only 0 present
                            infoTextEditor.setText("9");
                        } else if (value2 == 0 && !decimal) {//Situation such as 1+0, if 1 has pressed we change to 1+1
                            if(lastChar == ')'){//Add multiplication automatically
                                buttonMultiply.performClick();
                            }
                            String currentText = infoTextEditor.getText().toString();
                            infoTextEditor.setText(currentText.substring(0, infoTextEditor.getText().length() - 1) + "9");
                        } else {
                            if(lastChar == ')'){//Add multiplication automatically
                                buttonMultiply.performClick();
                            }
                            infoTextEditor.setText(infoTextEditor.getText() + "9");
                        }
                    } else {
                        if(lastChar == ')'){//Add multiplication automatically
                            buttonMultiply.performClick();
                        }
                        infoTextEditor.setText("9");
                        equal = false;
                    }

                    //Automatically compute the result and display the result into
                    //resultTextEditor when a number got entered after the operator
                    String currentText = infoTextEditor.getText().toString();
                    if (waitForValue2 && Double.isNaN(value2)) {
                        value2 = 9;
                        compute();
                    } else if (waitForValue2 && !Double.isNaN(value2)) {
                        //Decimal situation such as from 1.1 + 1. to 1.1 + 1.1
                        if (currentText.charAt(infoTextEditor.getText().length() - 2) == '.') {
                            value2 = value2 + 0.9;
                        }
                        //There are zeros after decimal
                        else if (!zerosAfterDecimal.equals("")) {
                            if (value2 % 1 != 0) {//Situation such as from 1.10 to 1.101
                                String newValue2 = value2 + zerosAfterDecimal + "9";
                                value2 = Double.parseDouble(newValue2);
                            } else {//Situation such as from 1.0 to 1.01
                                String value2String = Double.toString(value2);
                                String newValue2 = value2String.substring(0, value2String.length() - 1) + zerosAfterDecimal + "9";
                                value2 = Double.parseDouble(newValue2);
                            }
                        }
                        //Decimal situation such as from 1.1 + 1.1 to 1.1 + 1.11 and so on.
                        else if (value2 % 1 != 0) {
                            String newValue2 = value2 + "9";
                            value2 = Double.parseDouble(newValue2);
                        } else {
                            value2 = value2 * 10 + 9;
                        }
                        compute();
                    }
                    emptyTextBracket = false;
                    zerosAfterDecimal = "";
                }
                else{//To be revised if develop way to calculate parentheses with percentage button
                    if(lastChar == ')'){//Add multiplication automatically
                        infoTextEditor.setText(infoTextEditor.getText() + "×9");
                        bracketedOperation += "×9";
                    }
                    else{
                        infoTextEditor.setText(infoTextEditor.getText() + "9");
                        bracketedOperation += "9";
                    }

                    sign = false;
                    if(emptyTextBracket || delete){
                        resultTextEditor.setText(format(bracketCal(bracketedOperation)));
                    }
                    else{
                        value2 = bracketCal(bracketedOperation);
                        compute();
                    }
                }
            }
        });

        /**
         *-----------------------------------------
         * Operator buttons' methods
         *-----------------------------------------
         */

        buttonDecimal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!bracket) {
                    if (infoTextEditor.getText().length() == 0 || (waitForValue2 && Double.isNaN(value2))) {
                        infoTextEditor.setText(infoTextEditor.getText() + "0.");
                        decimal = true;
                    } else if (!decimal) {
                        infoTextEditor.setText(infoTextEditor.getText() + ".");
                        decimal = true;
                    }
                }
                else{
                    char lastChar;
                    try{
                        lastChar= infoTextEditor.getText().toString().charAt(infoTextEditor.getText().length()-1);
                    }
                    catch (Exception e){
                        lastChar = ' ';
                    }

                    if(lastChar == ')'){//Add multiplication automatically
                        infoTextEditor.setText(infoTextEditor.getText() + "×0.");
                        bracketedOperation += "×0.";
                    }
                    else{
                        if(lastChar != '(' || lastChar != '-') {//'-' for sign
                            infoTextEditor.setText(infoTextEditor.getText() + ".");
                            bracketedOperation += ".";
                        }
                    }

                }
            }
        });

        buttonAdd.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                char lastChar;
                try{
                    lastChar= infoTextEditor.getText().toString().charAt(infoTextEditor.getText().length()-1);
                }
                catch (Exception e){
                    lastChar = ' ';
                }

                //Make sure that there is operand and the add button has not pressed yet
                if (infoTextEditor.getText().length() != 0 && lastChar != '+') {
                    //If user want to change operation from the others to add before input value2
                    if ("-×÷".contains(lastChar + "")) {
                        infoTextEditor.setText(infoTextEditor.getText().toString().
                                substring(0, infoTextEditor.getText().length() - 1));
                    }

                    if(!bracket) {
                        if (!waitForValue2) {//no need to store previous operation result
                            if (percentage) {
                                String value1String = infoTextEditor.getText().toString().
                                        substring(0, infoTextEditor.getText().length() - 1);
                                value1 = Double.parseDouble(value1String);
                                value1 = value1 / 100;
                            } else {
                                if(emptyTextBracket){//We have parentheses operation fisrt
                                    value1 = Double.parseDouble(resultTextEditor.getText() + "");
                                }
                                else {
                                    value1 = Double.parseDouble(infoTextEditor.getText() + "");
                                }
                            }
                            waitForValue2 = true;
                        } else {//need to store previous operation result from resultTextEditor
                            try {
                                previousResult = value1 = Double.parseDouble(resultTextEditor.getText() + "");
                            } catch (Exception e) {
                                //do nothing, value1 remain same only change operator.
                                previousResult = value1;
                            }
                        }

                        value2 = Double.NaN;
                        zerosAfterDecimal = "";
                        subtraction = multiplication = division = percentage = decimal = equal = false;
                        addition = true;
                        infoTextEditor.setText(infoTextEditor.getText() + "+");
                        resultTextEditor.setText(null);
                    }
                    else{
                        if(lastChar != '(' || lastChar != '-') {//'-' for sign
                            infoTextEditor.setText(infoTextEditor.getText() + "+");
                            bracketedOperation += "+";
                        }
                    }
                }
            }
        });

        buttonSubtract.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                char lastChar;
                try{
                    lastChar= infoTextEditor.getText().toString().charAt(infoTextEditor.getText().length()-1);
                }
                catch (Exception e){
                    lastChar = ' ';
                }

                //Make sure that there is operand and the subtract button has not pressed yet
                if (infoTextEditor.getText().length() != 0 && lastChar != '-') {
                    //If user want to change operation from the others to subtract before input value2
                    if ("+×÷".contains(lastChar + "")) {
                        infoTextEditor.setText(infoTextEditor.getText().toString().
                                substring(0, infoTextEditor.getText().length() - 1));
                    }

                    if(!bracket) {
                        if (!waitForValue2) {//no need to store previous operation result
                            if (percentage) {
                                String value1String = infoTextEditor.getText().toString().
                                        substring(0, infoTextEditor.getText().length() - 1);
                                value1 = Double.parseDouble(value1String);
                                value1 = value1 / 100;
                            } else {
                                if(emptyTextBracket){//We have parentheses operation fisrt
                                    value1 = Double.parseDouble(resultTextEditor.getText() + "");
                                }
                                else {
                                    value1 = Double.parseDouble(infoTextEditor.getText() + "");
                                }
                            }
                            waitForValue2 = true;
                        } else {//need to store previous operation result from resultTextEditor
                            try {
                                previousResult = value1 = Double.parseDouble(resultTextEditor.getText() + "");
                            } catch (Exception e) {
                                previousResult = value1;
                                //do nothing, value1 remain same only change operator.
                            }
                        }

                        value2 = Double.NaN;
                        zerosAfterDecimal = "";
                        addition = multiplication = division = percentage = decimal = equal = false;
                        subtraction = true;
                        infoTextEditor.setText(infoTextEditor.getText() + "-");
                        resultTextEditor.setText(null);
                    }
                    else{
                        if(lastChar != '(' || lastChar != '-') {//'-' for sign
                            infoTextEditor.setText(infoTextEditor.getText() + "-");
                            bracketedOperation += "-";
                        }
                    }
                }
            }
        });

        buttonMultiply.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                previousValue = Double.NaN;
                previousOperator = null;

                char lastChar;
                try{
                    lastChar= infoTextEditor.getText().toString().charAt(infoTextEditor.getText().length()-1);
                }
                catch (Exception e){
                    lastChar = ' ';
                }


                //Make sure that there is operand and the multiply button has not pressed yet
                if (infoTextEditor.getText().length() != 0 && lastChar != '×') {
                    String currentText = infoTextEditor.getText().toString();
                    //If user want to change operation from the others to multiply before input value2
                    if ("+-÷".contains(lastChar + "")) {
                        infoTextEditor.setText(currentText.substring(0, infoTextEditor.getText().length() - 1));
                    }

                    if(!bracket) {
                        //Determine previous operator
                        try {
                            if (!decimal) {
                                if (percentage) {
                                    int value2Length = Double.toString(value2 * 100).length();
                                    previousOperator = Character.toString(currentText.charAt(infoTextEditor.getText().length() - value2Length));
                                } else {
                                    int value2Length = Double.toString(value2).length();
                                    previousOperator = Character.toString(currentText.charAt(infoTextEditor.getText().length() - value2Length + 1));
                                }
                            } else {
                                if (percentage) {
                                    int value2Length = Double.toString(value2 * 100).length();
                                    previousOperator = Character.toString(currentText.charAt(infoTextEditor.getText().length() - value2Length - 1));
                                } else {
                                    int value2Length = Double.toString(value2).length();
                                    previousOperator = Character.toString(currentText.charAt(infoTextEditor.getText().length() - value2Length - 1));
                                }
                            }
                        } catch (Exception e) {
                            previousOperator = "";
                        }

                        //Situations that we need to obey math rule, for example: 2+2*2
                        if (previousOperator.equals("+") || previousOperator.equals("-")) {
                            if (percentage) {
                                previousValue = value1;
                                value1 = beforePercentage / 100;
                            } else {//value1 and value2 is still remain same value as last operation
                                previousValue = value1;
                                value1 = value2;
                            }
                        } else {//no addition or subtraction in last operation
                            if (!waitForValue2) {//no need to store previous operation result
                                if (percentage) {
                                    String value1String = infoTextEditor.getText().toString().
                                            substring(0, infoTextEditor.getText().length() - 1);
                                    value1 = Double.parseDouble(value1String);
                                    value1 = value1 / 100;
                                } else {
                                    if(emptyTextBracket){//We have parentheses operation fisrt
                                        value1 = Double.parseDouble(resultTextEditor.getText() + "");
                                    }
                                    else {
                                        value1 = Double.parseDouble(infoTextEditor.getText() + "");
                                    }
                                }
                                waitForValue2 = true;
                            } else {//need to store previous operation result from resultTextEditor
                                try {
                                    value1 = Double.parseDouble(resultTextEditor.getText() + "");
                                } catch (Exception e) {
                                    //do nothing, value1 remain same only change operator.
                                }
                            }
                        }

                        value2 = Double.NaN;
                        zerosAfterDecimal = "";
                        addition = subtraction = division = decimal = percentage = equal = false;
                        multiplication = true;
                        infoTextEditor.setText(infoTextEditor.getText() + "×");
                        resultTextEditor.setText(null);
                    }
                    else{
                        if(lastChar != '(' || lastChar != '-') {//'-' for sign
                            infoTextEditor.setText(infoTextEditor.getText() + "×");
                            bracketedOperation += "×";
                        }
                    }
                }
            }
        });

        buttonDivide.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                previousValue = Double.NaN;
                previousOperator = null;

                char lastChar;
                try{
                    lastChar= infoTextEditor.getText().toString().charAt(infoTextEditor.getText().length()-1);
                }
                catch (Exception e){
                    lastChar = ' ';
                }

                //Make sure that there is operand and the divide button has not pressed yet
                if (infoTextEditor.getText().length() != 0 && lastChar != '÷') {
                    String currentText = infoTextEditor.getText().toString();
                    //If user want to change operation from the others to multiply before input value2
                    if ("+-×".contains(lastChar + "")) {
                        infoTextEditor.setText(currentText.substring(0, infoTextEditor.getText().length() - 1));
                    }

                    if(!bracket) {
                        //Determine previous operator
                        try {
                            if (!decimal) {
                                if (percentage) {
                                    int value2Length = Double.toString(value2 * 100).length();
                                    previousOperator = Character.toString(currentText.charAt(infoTextEditor.getText().length() - value2Length));
                                } else {
                                    int value2Length = Double.toString(value2).length();
                                    previousOperator = Character.toString(currentText.charAt(infoTextEditor.getText().length() - value2Length + 1));
                                }
                            } else {
                                if (percentage) {
                                    int value2Length = Double.toString(value2 * 100).length();
                                    previousOperator = Character.toString(currentText.charAt(infoTextEditor.getText().length() - value2Length - 1));
                                } else {
                                    int value2Length = Double.toString(value2).length();
                                    previousOperator = Character.toString(currentText.charAt(infoTextEditor.getText().length() - value2Length - 1));
                                }
                            }
                        } catch (Exception e) {
                            previousOperator = "";
                        }

                        //Situations that we need to obey math rule, for example: 2+2*2
                        if (previousOperator.equals("+") || previousOperator.equals("-")) {
                            if (percentage) {
                                previousValue = value1;
                                value1 = beforePercentage / 100;
                            } else {//value1 and value2 is still remain same value as last operation
                                previousValue = value1;
                                value1 = value2;
                            }
                        } else {//no addition or subtraction in last operation
                            if (!waitForValue2) {//no need to store previous operation result
                                if (percentage) {
                                    String value1String = infoTextEditor.getText().toString().
                                            substring(0, infoTextEditor.getText().length() - 1);
                                    value1 = Double.parseDouble(value1String);
                                    value1 = value1 / 100;
                                } else {
                                    if(emptyTextBracket){//We have parentheses operation fisrt
                                        value1 = Double.parseDouble(resultTextEditor.getText() + "");
                                    }
                                    else {
                                        value1 = Double.parseDouble(infoTextEditor.getText() + "");
                                    }
                                }
                                waitForValue2 = true;
                            } else {//need to store previous operation result from resultTextEditor
                                try {
                                    value1 = Double.parseDouble(resultTextEditor.getText() + "");
                                } catch (Exception e) {
                                    //do nothing, value1 remain same only change operator.
                                }
                            }
                        }

                        //Since we need to know the value of the secondary operand, so the step of
                        //determine previousValue is done in compute()

                        value2 = Double.NaN;
                        zerosAfterDecimal = "";
                        addition = subtraction = multiplication = decimal = percentage = equal = false;
                        division = true;
                        infoTextEditor.setText(infoTextEditor.getText() + "÷");
                        resultTextEditor.setText(null);
                    }
                    else{
                        if(lastChar != '(' || lastChar != '-') {//'-' for sign
                            infoTextEditor.setText(infoTextEditor.getText() + "÷");
                            bracketedOperation += "÷";
                        }
                    }
                }
            }
        });

        buttonPercent.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                char lastChar;
                try{
                    lastChar= infoTextEditor.getText().toString().charAt(infoTextEditor.getText().length()-1);
                }
                catch (Exception e){
                    lastChar = ' ';
                }

                //Make sure that there is operand and any operator buttons include itself has not pressed yet
                if(infoTextEditor.getText().length() != 0 && !"+-×÷%(".contains(lastChar + "") && !bracket) {

                    infoTextEditor.setText(infoTextEditor.getText() + "%");
                    String currentText = infoTextEditor.getText().toString();
                    String value; //Use to determine the value of operand that need to be percentage
                    int percentageIndex = currentText.lastIndexOf("%"); //find the index of the last occurrence of %

                    //Value2 should be value2% of the first operand, such as 5+50% = 7.5
                    if(!Double.isNaN(value2)){
                        beforePercentage = value2;//For backup

                        if(multiplication && !Double.isNaN(previousValue)){//Has previous operation
                            value2 = value1*(value2/100);
                            value1 = previousValue;
                            previousValue = Double.NaN;
                            //Since we have already down the multiplication above
                            multiplication = false;

                            if(previousOperator.equals("+")){
                                addition = true;
                            }
                            else{
                                subtraction = true;
                            }
                            //recalculate the result
                            compute();
                        }
                        else if(division && !Double.isNaN(previousValue)){//Has previous operation
                            value2 = value1/(value2/100);
                            value1 = previousValue;
                            previousValue = Double.NaN;
                            //Since we have already down the division above
                            division = false;

                            if(previousOperator.equals("+")){
                                addition = true;
                            }
                            else{
                                subtraction = true;
                            }
                            //recalculate the result
                            compute();
                        }
                        else{//no previous operation
                            if(addition || subtraction){
                                value2 = value1*(value2/100);
                                //recalculate the result
                                compute();
                            }
                            else{//only multiplication or division
                                if(multiplication) {
                                    BigDecimal result = new BigDecimal(Double.toString(value1 * (value2/100)));
                                    BigDecimal resultBD = result.setScale(10, RoundingMode.HALF_UP);
                                    resultTextEditor.setText(format(resultBD.doubleValue()));
                                }
                                else{
                                    BigDecimal result = new BigDecimal(Double.toString(value1 / (value2/100)));
                                    BigDecimal resultBD = result.setScale(10, RoundingMode.HALF_UP);
                                    resultTextEditor.setText(format(resultBD.doubleValue()));
                                }
                            }
                        }
                    }
                    else{
                        //The first operand (value1) needs to be percentage
                        value = currentText.substring(0, percentageIndex);
                        resultTextEditor.setText(Double.parseDouble(value)/100 + "");
                    }

                    zerosAfterDecimal = "";
                    percentage = true;
                }
            }
        });

        buttonBracket.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                char lastChar;
                try{
                    lastChar= infoTextEditor.getText().toString().charAt(infoTextEditor.getText().length()-1);
                }
                catch (Exception e){
                    lastChar = ' ';
                }

                if(infoTextEditor.getText().length() == 0){//Empty text
                    emptyTextBracket = true;
                    if(sign){//+/- button has been pressed
                        infoTextEditor.setText(infoTextEditor.getText() + "(-");
                        bracketedOperation += "(-";
                    }else {
                        infoTextEditor.setText(infoTextEditor.getText() + "(");
                        bracketedOperation += "(";
                    }
                    numberOfOpenBracket += 1;
                    bracket = true;
                }
                else if (("0123456789".contains(lastChar + "") && !bracket) ||
                        (lastChar == ')' && numberOfOpenBracket== numberOfCloseBracket)) {//If there is a number or closed bracket in front
                    buttonMultiply.performClick();

                    if(sign){//+/- button has been pressed
                        infoTextEditor.setText(infoTextEditor.getText() + "(-");
                        bracketedOperation += "(-";
                    }else {
                        infoTextEditor.setText(infoTextEditor.getText() + "(");
                        bracketedOperation += "(";
                    }
                    numberOfOpenBracket += 1;
                    bracket = true;
                }
                else {//Normal situation
                    if ("+-×÷".contains(lastChar + "") || lastChar == '(') {//Continuously enter open bracket
                        if(sign){//+/- button has been pressed
                            infoTextEditor.setText(infoTextEditor.getText() + "(-");
                            bracketedOperation += "(-";
                        }else {
                            infoTextEditor.setText(infoTextEditor.getText() + "(");
                            bracketedOperation += "(";
                        }
                        numberOfOpenBracket += 1;
                        bracket = true;
                    }
                    else if (("0123456789".contains(lastChar + "") || lastChar == ')') && bracketedOperation.contains("(")){//Situation to enter close bracket
                        infoTextEditor.setText(infoTextEditor.getText() + ")");
                        bracketedOperation += ")";//To be removed
                        numberOfCloseBracket += 1;
                        bracket = true;
                    }
                }

                if(bracket && numberOfOpenBracket == numberOfCloseBracket){
                    numberOfOpenBracket = numberOfCloseBracket = 0;
                    bracketedOperation = "";
                    bracket = sign = false;
                }
            }
        });

        buttonSign.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                if(!sign){
                    sign = true;
                    buttonBracket.performClick();
                }
                else{
                    int length = infoTextEditor.getText().length();
                    String currentText = infoTextEditor.getText().toString();

                    //Remove sign for double click if no number has been entered
                    if(infoTextEditor.getText().toString().substring(length - 2, length).equals("(-")){
                        infoTextEditor.setText(currentText.substring(0, length - 2));
                        bracketedOperation = bracketedOperation.substring(0, bracketedOperation.length() - 2);
                        sign = false;
                    }
                }
            }
        });

        buttonDelete.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                if(infoTextEditor.getText().length() != 0) {
                    delete = bracket = true;
                    //Delete one character
                    bracketedOperation = infoTextEditor.getText().toString().substring(0, infoTextEditor.getText().length() - 1);
                    infoTextEditor.setText(bracketedOperation);

                    if(bracketedOperation.length() == 0){
                        buttonClean.performClick();
                    }else {
                        char lastChar;
                        try {
                            lastChar = bracketedOperation.charAt(bracketedOperation.length() - 1);
                        } catch (Exception e) {
                            lastChar = ' ';
                        }

                        if (!bracketedOperation.contains("+") && !bracketedOperation.contains("-") &&
                                !bracketedOperation.contains("×") && !bracketedOperation.contains("÷")) {//only number
                            value1 = Double.parseDouble(bracketedOperation);
                            infoTextEditor.setText(format(value1) + "");
                        } else {
                            if ("0123456789".contains(lastChar + "") || lastChar == ')') {//Deleted to digit
                                resultTextEditor.setText(format(bracketCal(bracketedOperation)));
                            }
                            else if ("+-×÷".contains(lastChar + "") || lastChar == '(') {//Deleted to operator
                                resultTextEditor.setText("");
                            }
                        }
                    }
                }
            }
        });

        //Clean button that resets all variables to initial state
        buttonClean.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                previousValue = value2 = Double.NaN;
                previousOperator = operatorBeforeBracket = null;
                zerosAfterDecimal = bracketedOperation = "";
                infoTextEditor.setText(null);
                resultTextEditor.setText(null);
                waitForValue2 = addition = subtraction = multiplication = division = percentage =
                        decimal = equal = emptyTextBracket = bracket = includeBracket = sign = delete = false;
                numberOfOpenBracket = numberOfCloseBracket = 0;
            }
        });

        /**
         * Since compute method has down the calculation, the equal button
         * will just put the content in resultTextEditor into infoTextEditor
         * and clear the resultTextEditor
         */
        buttonEqual.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                if(!equal && !resultTextEditor.getText().equals("")){
                    operatorBeforeBracket = null;
                    zerosAfterDecimal = bracketedOperation = "";
                    equal = true;
                    waitForValue2 = percentage = decimal = emptyTextBracket = bracket = sign = delete = false;
                    numberOfOpenBracket = numberOfCloseBracket = 0;

                    if((infoTextEditor.getText().toString().charAt(0) != '(' ||
                            infoTextEditor.getText().toString().charAt(infoTextEditor.getText().length()-1) != ')') && Double.isNaN(value2)){
                        includeBracket = true;
                    }

                    infoTextEditor.setText(resultTextEditor.getText());
                    resultTextEditor.setText(null);
                }
                else if(equal){//Repeat same operation
                    if(!includeBracket) {
                        value1 = Double.parseDouble(infoTextEditor.getText() + "");
                        compute();
                        infoTextEditor.setText(resultTextEditor.getText());
                        resultTextEditor.setText(null);
                    }
                    else{//No operation to repeat
                        includeBracket = equal = false;
                    }
                }
            }
        });
    }

    /**
     *-----------------------------------------
     * Additional private helper methods
     *-----------------------------------------
     */

    /**
     * Private method that will be called to calculate the result and display
     * the result to resultTextEditor automatically, when there is a number
     * entered as an operand after any operator or any updates to the current operand.
     */
    private void compute() {
        BigDecimal result, resultBD;

        if(addition){
            result = new BigDecimal(Double.toString(value1 + value2));
            resultBD = result.setScale(10, RoundingMode.HALF_UP);
            resultTextEditor.setText(format(resultBD.doubleValue()));
        }
        else if(subtraction){
            result = new BigDecimal(Double.toString(value1 - value2));
            resultBD = result.setScale(10, RoundingMode.HALF_UP);
            resultTextEditor.setText(format(resultBD.doubleValue()));
        }
        else if(multiplication){
            //Situations that we need to compute × firstly
            if (!Double.isNaN(previousValue)) {//need to calculate subtraction first
                currentResult = value1 * value2;

                if (previousOperator.equals("+")) {
                    result = new BigDecimal(Double.toString(previousValue + currentResult));
                    resultBD = result.setScale(10, RoundingMode.HALF_UP);
                    resultTextEditor.setText(format(resultBD.doubleValue()));
                } else if (previousOperator.equals("-")) {
                    result = new BigDecimal(Double.toString(previousValue - currentResult));
                    resultBD = result.setScale(10, RoundingMode.HALF_UP);
                    resultTextEditor.setText(format(resultBD.doubleValue()));
                }
            } else {//no need to calculate multiplication first
                result = new BigDecimal(Double.toString(value1 * value2));
                resultBD = result.setScale(10, RoundingMode.HALF_UP);
                resultTextEditor.setText(format(resultBD.doubleValue()));
            }
        }
        else if(division) {

            //Situations that we need to compute ÷ firstly
            if (value2 != 0) {//O cannot be divisor
                if (!Double.isNaN(previousValue)) {//need to calculate subtraction first
                    currentResult = value1 / value2;

                    if (previousOperator.equals("+")) {
                        result = new BigDecimal(Double.toString(previousValue + currentResult));
                        resultBD = result.setScale(10, RoundingMode.HALF_UP);
                        resultTextEditor.setText(format(resultBD.doubleValue()));
                    } else if (previousOperator.equals("-")) {
                        result = new BigDecimal(Double.toString(previousValue - currentResult));
                        resultBD = result.setScale(10, RoundingMode.HALF_UP);
                        resultTextEditor.setText(format(resultBD.doubleValue()));
                    }
                } else {//no need to calculate division first
                    result = new BigDecimal(Double.toString(value1 / value2));
                    resultBD = result.setScale(10, RoundingMode.HALF_UP);
                    resultTextEditor.setText(format(resultBD.doubleValue()));
                }
            }
        }
    }

    /**
     * Private method that help to calculate the operation in parentheses
     * which stored in the map called operationMap.
     *
     * Note: this method needs to be revised if developed the way
     *       to calculate the parentheses involve percentage, and replace/revise the class Evaluate.
     *
     * @param operation the operation need to be calculated
     * @return double the result of calculation
     */
    private double bracketCal(String operation){
        Evaluate evaluate = new Evaluate();
        return evaluate.eval(operation);
    }


    /**
     * Private method that present Double without decimal as Integer,
     * and remove unnecessary zeros after decimal.
     *
     * @param input double value that need to be formatted.
     * @return String value that has been formatted.
     */
    private String format(double input) {
        if(input == (long) input){
            return String.format("%d",(long) input);
        }
        else {
            return String.format("%s", input);
        }
    }
}