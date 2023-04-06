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
        frame = new JFrame("Calculator");
        buttonPanel = new JPanel();
        textPanel = new JPanel();
        textField = new JTextField(20);
        buttons = new JButton[16];
        textPane = new JTextPane();

        textPane.setEditable(false);
        textPane.setBackground(Color.WHITE);
        textPane.setFont(textPane.getFont().deriveFont(20f));

        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(300, 300);
        frame.setLayout(new GridLayout(2, 1));

        buttonPanel.setLayout(new GridLayout(4, 4));
        initButtons();

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

        textPanel.setLayout(new BorderLayout());
        textPanel.add(textField, BorderLayout.NORTH);
        textPanel.add(textPane, BorderLayout.SOUTH);

        frame.add(textPanel);
        frame.add(buttonPanel);
        frame.setVisible(true);
        frame.setResizable(false);
        frame.setLocationRelativeTo(null);
    }


    public double calculate() {
        String input = textField.getText();
        input = input.replaceAll("\\s+", ""); // Remove all whitespaces from the input string
        Stack<Double> numberStack = new Stack<>();
        Stack<String> operatorStack = new Stack<>();
        for (int i = 0; i < input.length(); i++) {
            char c = input.charAt(i);
            if (c == '(') {
                operatorStack.push("(");
            } else if (c == ')') {
                while (!operatorStack.peek().equals("(")) {
                    String operator = operatorStack.pop();
                    double b = numberStack.pop();
                    double a = numberStack.pop();
                    double result = operate(a, b, operator);
                    numberStack.push(result);
                }
                operatorStack.pop();
            } else if (Character.isDigit(c) || c == '.') {
                int j = i;
                while (j < input.length() && (Character.isDigit(input.charAt(j)) || input.charAt(j) == '.')) {
                    j++;
                }
                String numberStr = input.substring(i, j);
                double number = Double.parseDouble(numberStr);
                numberStack.push(number);
                i = j - 1;
            } else if (isOperator(Character.toString(c))) {
                while (!operatorStack.empty() && !operatorStack.peek().equals("(") && hasPrecedence(Character.toString(c), operatorStack.peek())) {
                    String operator = operatorStack.pop();
                    double b = numberStack.pop();
                    double a = numberStack.pop();
                    double result = operate(a, b, operator);
                    numberStack.push(result);
                }
                operatorStack.push(Character.toString(c));
            } else {
                throw new IllegalArgumentException("Invalid character: " + c);
            }
        }
        while (!operatorStack.empty()) {
            String operator = operatorStack.pop();
            double b = numberStack.pop();
            double a = numberStack.pop();
            double result = operate(a, b, operator);
            numberStack.push(result);
        }
        return numberStack.pop();
    }

    private static boolean isOperator(String s) {
        return s.equals("+") || s.equals("-") || s.equals("*") || s.equals("/");
    }

    private static boolean hasPrecedence(String op1, String op2) {
        return (op1.equals("*") || op1.equals("/")) && (op2.equals("+") || op2.equals("-"));
    }

    private static double operate(double a, double b, String operator) {
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
        for (int i = 0; i < buttons.length; i++) {
            buttons[i] = new JButton(buttonNames[i]);
            buttonPanel.add(buttons[i]);
            buttons[i].addActionListener(e -> {
                if(e.getActionCommand().equals("=")) {
                    printResult();
                } else {
                    String button = e.getActionCommand();
                    textField.setText(textField.getText() + button);
                }
            });
        }
    }
    private void printResult() {
        double result = calculate();
        if((int) result == result)
            textPane.setText(Integer.toString((int) result));
        else 
            textPane.setText(Double.toString(result));
        textField.setText("");
    }
}
