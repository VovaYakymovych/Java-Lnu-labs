package lab4;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.Random;

public class MatrixCalculator extends JFrame {
    private final DefaultTableModel modelA;
    private final DefaultTableModel modelB;
    private final DefaultTableModel modelC;

    private final int ROWS = 6;
    private final int COLS = 3;

    public MatrixCalculator() {
        super("Робота з матрицями");

        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(900, 500);
        setLayout(new BorderLayout());

        // Панель таблиць
        JPanel tablePanel = new JPanel(new GridLayout(1, 3, 10, 10));
        modelA = new DefaultTableModel(ROWS, COLS);
        modelB = new DefaultTableModel(ROWS, COLS);
        modelC = new DefaultTableModel(ROWS, COLS);

        JTable tableA = new JTable(modelA);
        JTable tableB = new JTable(modelB);
        JTable tableC = new JTable(modelC);

        tablePanel.add(new JScrollPane(tableA));
        tablePanel.add(new JScrollPane(tableB));
        tablePanel.add(new JScrollPane(tableC));

        // Панель кнопок
        JPanel buttonPanel = new JPanel();
        JButton generateBtn = new JButton("Згенерувати матриці");
        JButton calcBtn = new JButton("Обчислити");
        buttonPanel.add(generateBtn);
        buttonPanel.add(calcBtn);

        add(tablePanel, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);

        generateBtn.addActionListener(this::generateMatrices);
        calcBtn.addActionListener(this::calculate);
    }

    private void generateMatrices(ActionEvent e) {
        Random random = new Random();
        for (int i = 0; i < ROWS; i++) {
            for (int j = 0; j < COLS; j++) {
                modelA.setValueAt(random.nextInt(11) - 5, i, j); // -5..5
                modelB.setValueAt(random.nextInt(11) - 5, i, j);
                modelC.setValueAt("", i, j);
            }
        }
    }

    private void calculate(ActionEvent e) {
        double[][] A = new double[ROWS][COLS];
        double[][] B = new double[ROWS][COLS];
        double[][] C = new double[ROWS][COLS];

        // Зчитування A і B
        for (int i = 0; i < ROWS; i++) {
            for (int j = 0; j < COLS; j++) {
                try {
                    A[i][j] = Double.parseDouble(modelA.getValueAt(i, j).toString());
                    B[i][j] = Double.parseDouble(modelB.getValueAt(i, j).toString());
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(this, "Некоректні дані у матрицях!");
                    return;
                }
            }
        }

        // C = A^2 + 2.5 * A * B
        for (int i = 0; i < ROWS; i++) {
            for (int j = 0; j < COLS; j++) {
                C[i][j] = Math.pow(A[i][j], 2) + 2.5 * A[i][j] * B[i][j];
                modelC.setValueAt(String.format("%.2f", C[i][j]), i, j);
            }
        }

        // Добуток ненульових елементів кожного стовпця
        double[] colProducts = new double[COLS];
        for (int j = 0; j < COLS; j++) {
            double product = 1;
            boolean hasNonZero = false;
            for (int i = 0; i < ROWS; i++) {
                if (C[i][j] != 0) {
                    product *= C[i][j];
                    hasNonZero = true;
                }
            }
            colProducts[j] = hasNonZero ? product : 0;
        }

        // Знаходження стовпця з найбільшим добутком за модулем
        int maxIndex = 0;
        double maxAbs = Math.abs(colProducts[0]);
        for (int j = 1; j < COLS; j++) {
            if (Math.abs(colProducts[j]) > maxAbs) {
                maxAbs = Math.abs(colProducts[j]);
                maxIndex = j;
            }
        }

        // Середнє арифметичне 1-го і 3-го стовпців (бо всього 3)
        double avg1 = 0, avg3 = 0;
        for (int i = 0; i < ROWS; i++) {
            avg1 += C[i][0];
            avg3 += C[i][COLS - 1];
        }
        avg1 /= ROWS;
        avg3 /= ROWS;

        StringBuilder result = new StringBuilder("Результати обчислень:\n\n");
        result.append("Добутки стовпців:\n");
        for (int j = 0; j < COLS; j++) {
            result.append("Стовпець ").append(j + 1).append(": ").append(String.format("%.3f", colProducts[j])).append("\n");
        }
        result.append("\nСтовпець з найбільшим добутком за модулем: ").append(maxIndex + 1);
        result.append("\nСереднє 1-го стовпця: ").append(String.format("%.3f", avg1));
        result.append("\nСереднє 3-го стовпця: ").append(String.format("%.3f", avg3));

        JOptionPane.showMessageDialog(this, result.toString(), "Результати", JOptionPane.INFORMATION_MESSAGE);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new MatrixCalculator().setVisible(true));
    }
}
