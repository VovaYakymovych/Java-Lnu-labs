package lab1;


import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;

public class MatrixFrame extends JFrame {
    private JTable table;
    private JSpinner rowsSpinner, colsSpinner;
    private JLabel resultLabel;

    public MatrixFrame() {
        setTitle("Обробка матриці");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(600, 400);
        setLayout(new BorderLayout());

        // Панель керування зверху
        JPanel controlPanel = new JPanel();
        controlPanel.add(new JLabel("Rows:"));
        rowsSpinner = new JSpinner(new SpinnerNumberModel(3, 1, 10, 1));
        controlPanel.add(rowsSpinner);

        controlPanel.add(new JLabel("Columns:"));
        colsSpinner = new JSpinner(new SpinnerNumberModel(3, 1, 10, 1));
        controlPanel.add(colsSpinner);

        JButton generateButton = new JButton("Generate data");
        generateButton.addActionListener(this::generateTable);
        controlPanel.add(generateButton);

        JButton calcButton = new JButton("Calculate");
        calcButton.addActionListener(this::calculate);
        controlPanel.add(calcButton);

        add(controlPanel, BorderLayout.NORTH);

        // Таблиця по центру
        table = new JTable(3, 3);
        add(new JScrollPane(table), BorderLayout.CENTER);

        // Результат внизу
        resultLabel = new JLabel("Result: ");
        add(resultLabel, BorderLayout.SOUTH);

        setVisible(true);
    }

    // Генеруємо таблицю та випадкові числа
    private void generateTable(ActionEvent e) {
        int rows = (Integer) rowsSpinner.getValue();
        int cols = (Integer) colsSpinner.getValue();

        DefaultTableModel model = new DefaultTableModel(rows, cols);
        table.setModel(model);

        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                int randomValue = (int)(Math.random() * 21 - 10); // від -10 до 10
                model.setValueAt(randomValue, r, c);
            }
        }

        resultLabel.setText("Result: (null)");
    }

    // Обчислення добутку від'ємних чисел у кожному стовпці
    private void calculate(ActionEvent e) {
        int rows = table.getRowCount();
        int cols = table.getColumnCount();

        double[] products = new double[cols];

        for (int c = 0; c < cols; c++) {
            double product = 1;
            boolean hasNegative = false;

            for (int r = 0; r < rows; r++) {
                Object value = table.getValueAt(r, c);
                if (value != null) {
                    try {
                        double num = Double.parseDouble(value.toString());
                        if (num < 0) {
                            product *= num;
                            hasNegative = true;
                        }
                    } catch (NumberFormatException ex) {
                        JOptionPane.showMessageDialog(this, "Error in [" + r + "," + c + "]");
                        return;
                    }
                }
            }

            products[c] = hasNegative ? product : Double.POSITIVE_INFINITY;
        }

        int minCol = -1;
        double minVal = Double.POSITIVE_INFINITY;

        for (int i = 0; i < cols; i++) {
            if (products[i] != Double.POSITIVE_INFINITY &&
                    Math.abs(products[i]) < Math.abs(minVal)) {
                minVal = products[i];
                minCol = i;
            }
        }

        if (minCol == -1) {
            resultLabel.setText("Result: no negative numbers in any column");
        } else {
            resultLabel.setText("Result: column " + (minCol + 1) + ", product of numbers = " + minVal);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(MatrixFrame::new);
    }
}


