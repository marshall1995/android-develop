package com.example.marshall.kalkulator;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    final static int MY_REQUEST_ID = 1;
    final static String CHECK_DELETE = "Delete";

    StringBuffer textView_text;
    StringBuffer history;

    TextView textView_main;
    Button button_plus;
    Button button_minus;
    Button button_multiplication;
    Button button_division;
    Button button_equals;
    Button button_dot;
    Button button_0;
    boolean finishedFirst;
    List<String> operationList = new ArrayList<>();



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        textView_text = new StringBuffer();
        textView_main = (TextView) findViewById(R.id.textView_main);
        history = new StringBuffer();

        button_equals = (Button) findViewById(R.id.button_equals);
        button_0 = (Button) findViewById(R.id.button_0);
        button_dot = (Button) findViewById(R.id.button_dot);

        finishedFirst = false;

        setOperationButtons();
        disableEqualsButton();
        disableOperationButtons();
        disableDotButton();
        enableMinusButton();
        addOperationToList(operationList);

    }

    private void addOperationToList(List<String> operationList) {
        operationList.add("+");
        operationList.add("-");
        operationList.add("*");
        operationList.add("/");
    }

    private void disableOperationButtons() {
        button_plus.setEnabled(false);
        button_minus.setEnabled(false);
        button_division.setEnabled(false);
        button_multiplication.setEnabled(false);
    }

    public void writeNumber (View view) {
        if(!finishedFirst)
        {
            enableOperationButtons();
        }
        else
        {
            disableOperationButtons();
            enableEqualsButton();
        }
        Button button = (Button) view;
        textView_text.append(button.getText());
        displayText();
        checkDot(button);

        Log.i("Button: ",button.getText().toString());
    }

    public void clear(View view) {
        if(textView_text.length()>0){
            resetVariables();
            displayText();
        }
        Log.i("Button: ","Clear");
    }

    public void writeOperation (View view) {
        Button button = (Button) view;
        if(button_plus.isEnabled()){
            disableOperationButtons();
            enableMinusButton();
            disableDotButton();
            finishedFirst = true;
            enableButton0();
        }
        else if(button_minus.isEnabled()){
            disableOperationButtons();
        }
        textView_text.append(button.getText());
        displayText();

        Log.i("Button: ",button.getText().toString());
    }

    private void checkDot(Button button) {
        String currentSign="";
        int textLentgh = textView_text.length()-1;
        while(textLentgh>=0 && !operationList.contains(currentSign)){
            if(currentSign.equals(".")){
                disableDotButton();
                break;
            }
            else{
                enableDotButton();
            }
            currentSign=textView_text.charAt(textLentgh)+"";
            textLentgh--;
        }

    }

    private void enableDotButton() {
        button_dot.setEnabled(true);
    }

    public void calculate(View view){
        String finalText = textView_text.toString();

        StringBuffer textFirst = new StringBuffer();
        StringBuffer textSecond = new StringBuffer();
        Character operation = ' ';

        setFirst(textView_text, textFirst);
        operation = setOperation(operation);
        setSecond(textView_text,textSecond);

        BigDecimal first = new BigDecimal(textFirst.toString());
        BigDecimal second = new BigDecimal(textSecond.toString());

        if(!zeroDivision(operation,second)){
            Log.i("Error: ",finalText);
        }
        else {
            BigDecimal result = DoTheOperation(first,second,operation);

            saveHistory(finalText, result+"");
        }
        resetVariables();
    }

    private void saveHistory(String finalText, String result) {
        textView_text.append(finalText+" = "+result);
        displayText();
        history.append(textView_text.toString()+System.lineSeparator() + System.lineSeparator());
    }

    private BigDecimal DoTheOperation(BigDecimal first, BigDecimal second, Character operation ) {
        switch (operation){
            case '+':
                return first.add(second);
            case '-':
                return first.subtract(second);
            case '*':
                return first.multiply(second);
            case '/':
                return first.divide(second);
        }
        return new BigDecimal("0.0");
    }

    private boolean zeroDivision(Character operation, BigDecimal second) {
        if(operation.equals('/')){
            if(BigDecimal.ZERO.compareTo(second)==0){
                return false;
            }
        }
        return true;
    }

    private void setSecond(StringBuffer textView_text, StringBuffer textSecond) {
        textSecond.append(textView_text.toString());
        textView_text.delete(0,textView_text.length());
    }

    private Character setOperation(Character operationSign) {
        operationSign = textView_text.charAt(0);
        textView_text.deleteCharAt(0);
        return operationSign;
    }

    private void setFirst(StringBuffer textView_text, StringBuffer textFirst) {
        textFirst.append(textView_text.charAt(0));
        textView_text.deleteCharAt(0);

        while(!operationList.contains(textView_text.charAt(0)+"")) {
            textFirst.append(textView_text.charAt(0));
            textView_text.deleteCharAt(0);
        }
    }

    private void resetVariables() {
        textView_text.delete(0,textView_text.length());
        disableEqualsButton();
        disableOperationButtons();
        enableMinusButton();
        disableDotButton();
        enableButton0();
        finishedFirst = false;
    }

    private void enableButton0() {
        button_0.setEnabled(true);
    }

    private void disableDotButton() {
        button_dot.setEnabled(false);
    }

    private void enableMinusButton() {
        button_minus.setEnabled(true);
    }

    private void enableEqualsButton() {
        button_equals.setEnabled(true);
    }

    private void enableOperationButtons() {
        button_plus.setEnabled(true);
        button_minus.setEnabled(true);
        button_division.setEnabled(true);
        button_multiplication.setEnabled(true);
    }

    private void displayText() {
        textView_main.setText(textView_text.toString());
    }

    private void disableEqualsButton() {
        button_equals.setEnabled(false);
    }

    private void setOperationButtons() {
        button_plus = (Button) findViewById(R.id.button_plus);
        button_minus = (Button) findViewById(R.id.button_minus);
        button_multiplication = (Button) findViewById(R.id.button_multiplication);
        button_division = (Button) findViewById(R.id.button_division);
    }

    public void writeDot(View view){
        Button button = (Button) view;
        textView_text.append(button.getText());
        displayText();
        disableDotButton();
        disableOperationButtons();
        disableEqualsButton();
        enableButton0();
        Log.i("Button: ",button.getText().toString());
    }

    public void openHistory(View view){
        Log.i("Button: ","History");
        Intent intentHistoryActivity = new Intent(this, HistoryActivity.class);
        intentHistoryActivity.putExtra("Extra_history",history.toString());
        startActivityForResult(intentHistoryActivity,MY_REQUEST_ID);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        if(requestCode == MY_REQUEST_ID){
            if(resultCode == RESULT_OK){
                deleteHistory(data.getStringExtra(CHECK_DELETE));
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void deleteHistory(String string) {
        if(string.equals("delete")){
            history.delete(0,history.length());
        }
    }

}
