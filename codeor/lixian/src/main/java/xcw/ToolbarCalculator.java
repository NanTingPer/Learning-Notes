package xcw;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class ToolbarCalculator extends JFrame {
    private JTextField displayField;
    private double num1 = 0;
    private double num2 = 0;
    private String operator = "";
    private boolean isNewInput = true;

    public ToolbarCalculator() {
        setTitle("Toolbar Calculator");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(400, 400);
        setLayout(new BorderLayout());
        displayField = new JTextField();
        displayField.setEditable(false);
        displayField.setPreferredSize(new Dimension(300, 50));
        add(displayField, BorderLayout.NORTH);
        JToolBar toolBar = new JToolBar();
        JButton clearButton = new JButton("Clear All");
        clearButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                displayField.setText("");
                num1 = 0;
                num2 = 0;
                operator = "";
                isNewInput = true;
            }
        });
        toolBar.add(clearButton);

        add(toolBar, BorderLayout.SOUTH);

        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new GridLayout(5, 4));

        String[] buttons = {
            "AC", "←", "+/-", "÷",
            "7", "8", "9", "×",
            "4", "5", "6", "−",
            "1", "2", "3", "+",
            "%", "0", ".", "="
        };

        for (String buttonText : buttons) {
            JButton button = new JButton(buttonText);
            button.addActionListener(new ButtonClickListener());
            buttonPanel.add(button);
        }

        add(buttonPanel, BorderLayout.CENTER);
        pack();
        setVisible(true);
    }

    private class ButtonClickListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            String command = e.getActionCommand();
            if (command.matches("[0-9.]")) {
                if (isNewInput) {
                    displayField.setText(command);
                    isNewInput = false;
                } else {
                    displayField.setText(displayField.getText() + command);
                }
            } else if (command.equals("AC")) {
                displayField.setText("");
                num1 = 0;
                num2 = 0;
                operator = "";
                isNewInput = true;
            } else if (command.equals("←")) {
                String text = displayField.getText();
                if (text.length() > 0) {
                    displayField.setText(text.substring(0, text.length() - 1));
                    if (displayField.getText().isEmpty()) {
                        isNewInput = true;
                    }
                }
            } else if (command.equals("+/-")) {
                String text = displayField.getText();
                if (!text.isEmpty()) {
                    double num = Double.parseDouble(text);
                    num = -num;
                    displayField.setText(String.valueOf(num));
                }
            } else if (command.equals("%")) {
                String text = displayField.getText();
                if (!text.isEmpty()) {
                    double num = Double.parseDouble(text);
                    num = num / 100;
                    displayField.setText(String.valueOf(num));
                }
            } else if (command.equals("÷") || command.equals("×") || command.equals("−") || command.equals("+")) {
                if (!displayField.getText().isEmpty()) {
                    num2 = Double.parseDouble(displayField.getText());
                    if (!operator.isEmpty()) {
                        num1 = performOperation(num1, num2, operator);
                    } else {
                        num1 = num2;
                    }
                    operator = command;
                    displayField.setText("");
                    isNewInput = true;
                }
            } else if (command.equals("=")) {
                if (!displayField.getText().isEmpty()) {
                    num2 = Double.parseDouble(displayField.getText());
                    num1 = performOperation(num1, num2, operator);
                }
                displayField.setText(String.valueOf(num1));
                num1 = 0;
                num2 = 0;
                operator = "";
                isNewInput = true;
            }
        }

        private double performOperation(double num1, double num2, String operator) {
            switch (operator) {
                case "+":
                    return num1 + num2;
                case "−":
                    return num1 - num2;
                case "×":
                    return num1 * num2;
                case "÷":
                    if (num2 != 0) {
                        return num1 / num2;
                    } else {
                        displayField.setText("Error");
                        return 0;
                    }
                default:
                    return num2;
            }
        }
    }

    public static void main(String[] args) {
        new ToolbarCalculator();
    }
}