import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.Stack;

public class Calculator extends JFrame {

    private final JFrame frame;
    private final JPanel buttonPanel;
    private final JPanel textPanel;
    private final JTextField textField;
    private final JButton[] buttons;
    private final JTextPane textPane;

    public static void main(String[] args) {
        new Calculator();
    }

    private Calculator() {
        // Initialize the Swing components
        frame = new JFrame("Calculator");
        buttonPanel = new JPanel();
        textPanel = new JPanel();
        textField = new JTextField(40);
        buttons = new JButton[16];
        textPane = new JTextPane();

        // Set the properties for the textPane (the result of the equation)
        textPane.setEditable(false);
        textPane.setBackground(Color.WHITE);
        textPane.setFont(textPane.getFont().deriveFont(20f));

        // Set the properties for the frame
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(300, 300);
        frame.setLayout(new GridLayout(2, 1));

        // Set the properties for the buttonPanel
        buttonPanel.setLayout(new GridLayout(4, 4));
        initButtons();

        // Set the properties for the textField (the equation)
        textField.setFont(textField.getFont().deriveFont(20f));
        textField.setHorizontalAlignment(SwingConstants.CENTER);
        textField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if(e.getKeyCode() == KeyEvent.VK_ENTER) {
                    printResult();
                }
            }
        });

        // Set the properties for the textPanel (the panel that contains the textField and the textPane)
        textPanel.setLayout(new BorderLayout());
        textPanel.add(textField, BorderLayout.NORTH);
        textPanel.add(textPane, BorderLayout.SOUTH);

        // Add the components to the frame
        frame.add(textPanel);
        frame.add(buttonPanel);
        frame.setVisible(true);
        frame.setResizable(false);
        frame.setLocationRelativeTo(null);
    }


    public double calculate() {
        String input = textField.getText(); // Get the input from the current equation
        input = input.replaceAll("\\s+", ""); // Remove all whitespaces from the input string
        Stack<Double> numberStack = new Stack<>(); // Create a stack for the numbers
        Stack<String> operatorStack = new Stack<>(); // Create a stack for the operators
        for (int i = 0; i < input.length(); i++) {
            char c = input.charAt(i);
            if (c == '(') { // If the character is a left parenthesis, push it to the operator stack
                operatorStack.push("(");
            } else if (c == ')') { // If the character is a right parenthesis, pop the operator stack until the left parenthesis is found
                while (!operatorStack.peek().equals("(")) { // While the top of the operator stack is not a left parenthesis
                    String operator = operatorStack.pop();
                    double b = numberStack.pop();
                    double a = numberStack.pop();
                    double result = operate(a, b, operator);
                    numberStack.push(result);
                }
                operatorStack.pop();
            } else if (Character.isDigit(c) || c == '.') { // If the character is a number, push it to the number stack
                int j = i;
                while (j < input.length() && (Character.isDigit(input.charAt(j)) || input.charAt(j) == '.')) {
                    j++;
                }
                String numberStr = input.substring(i, j);
                double number = Double.parseDouble(numberStr);
                numberStack.push(number);
                i = j - 1;
            } else if (isOperator(Character.toString(c))) { // If the character is an operator, pop the operator stack until the top of the stack has a lower precedence than the current operator
                while (!operatorStack.empty() && !operatorStack.peek().equals("(") && hasPrecedence(Character.toString(c), operatorStack.peek())) {
                    String operator = operatorStack.pop();
                    double b = numberStack.pop();
                    double a = numberStack.pop();
                    double result = operate(a, b, operator);
                    numberStack.push(result);
                }
                operatorStack.push(Character.toString(c));
            } else { // If the character is not a number, operator or parenthesis, the input is invalid
                textPane.setText("Invalid input");
            }
        }
        while (!operatorStack.empty()) { // Pop the operator stack until it is empty
            String operator = operatorStack.pop();
            double b = numberStack.pop();
            double a = numberStack.pop();
            double result = operate(a, b, operator);
            numberStack.push(result);
        }
        return numberStack.pop();
    }

    private boolean isOperator(String s) {
        return s.equals("+") || s.equals("-") || s.equals("*") || s.equals("/");
    }

    private boolean hasPrecedence(String op1, String op2) {
        return (op1.equals("*") || op1.equals("/")) && (op2.equals("+") || op2.equals("-"));
    }

    private double operate(double a, double b, String operator) {
        return switch (operator) {
            case "+" -> a + b;
            case "-" -> a - b;
            case "*" -> a * b;
            case "/" -> a / b;
            default -> throw new IllegalArgumentException("Unknown operator: " + operator);
        };
    }

    private void initButtons() {
        String[] buttonNames = {"7", "8", "9", "/", "4", "5", "6", "*", "1", "2", "3", "-", "0", ".", "=", "+"};
        for (int i = 0; i < buttons.length; i++) { // Create the buttons and add them to the buttonPanel
            buttons[i] = new JButton(buttonNames[i]);
            buttonPanel.add(buttons[i]);
            buttons[i].addActionListener(e -> {
                if(e.getActionCommand().equals("=")) { // If the button is the equals button, calculate the result
                    printResult();
                } else { // If the button is not the equals button, add the button text to the textField
                    String button = e.getActionCommand();
                    textField.setText(textField.getText() + button);
                }
            });
        }
    }
    private void printResult() { // Print the result of the equation to the textPane
        double result = calculate();
        if((int) result == result)
            textPane.setText(Integer.toString((int) result));
        else 
            textPane.setText(Double.toString(result));
        textField.setText("");
    }
}
