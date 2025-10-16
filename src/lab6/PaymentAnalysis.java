package lab6;


import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.*;

public class PaymentAnalysis extends JFrame {
    private JTable table;
    private JTextArea outputArea;

    private String[] users = {"Олег Коваль", "Марія Іванчук", "Тарас Шевченко"};
    private String[] years = {"2020", "2021"};
    private double[][] payments; // 6 x 3 (3 користувачі * 2 роки, 3 квартальні значення)

    public PaymentAnalysis() {
        setTitle("Облік оплати користувачів");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(800, 600);
        setLayout(new BorderLayout());

        JButton generateBtn = new JButton("Згенерувати дані");
        JButton calculateBtn = new JButton("Обчислити");

        generateBtn.addActionListener(e -> generateData());
        calculateBtn.addActionListener(e -> analyzeData());

        JPanel topPanel = new JPanel();
        topPanel.add(generateBtn);
        topPanel.add(calculateBtn);
        add(topPanel, BorderLayout.NORTH);

        table = new JTable();
        add(new JScrollPane(table), BorderLayout.CENTER);

        outputArea = new JTextArea(5, 30);
        add(new JScrollPane(outputArea), BorderLayout.SOUTH);
    }

    private void generateData() {
        payments = new double[6][3]; // 6 рядків (3 користувачі × 2 роки), 3 квартали

        Random random = new Random();
        for (int i = 0; i < payments.length; i++) {
            for (int j = 0; j < 3; j++) {
                // Іноді генеруємо "пропущене" значення
                if (random.nextDouble() < 0.2) {
                    payments[i][j] = 0;
                } else {
                    payments[i][j] = 300 + random.nextInt(400);
                }
            }
        }

        String[] columns = {"Користувач", "Рік", "Q1", "Q2", "Q3"};
        DefaultTableModel model = new DefaultTableModel(columns, 0);

        int index = 0;
        for (String user : users) {
            for (String year : years) {
                Object[] row = new Object[5];
                row[0] = user;
                row[1] = year;
                for (int q = 0; q < 3; q++) {
                    row[q + 2] = payments[index][q] == 0 ? "—" : payments[index][q];
                }
                model.addRow(row);
                index++;
            }
        }
        table.setModel(model);
        outputArea.setText("Дані згенеровано!\n");
    }

    private void analyzeData() {
        if (payments == null) {
            JOptionPane.showMessageDialog(this, "Спочатку згенеруйте дані!");
            return;
        }

        // Заповнюємо пропущені дані середніми
        double sumAll = 0;
        int countAll = 0;
        for (double[] row : payments) {
            for (double val : row) {
                if (val != 0) {
                    sumAll += val;
                    countAll++;
                }
            }
        }
        double avg = sumAll / countAll;
        for (int i = 0; i < payments.length; i++) {
            for (int j = 0; j < 3; j++) {
                if (payments[i][j] == 0) payments[i][j] = avg;
            }
        }

        // Знаходимо мінімальний платіж
        double minPayment = Arrays.stream(payments)
                .flatMapToDouble(Arrays::stream)
                .min()
                .orElse(0);

        // Знаходимо суму всіх оплат
        double total = Arrays.stream(payments)
                .flatMapToDouble(Arrays::stream)
                .sum();

        // Коефіцієнт кореляції між 2020 і 2021 роками (по користувачах)
        double[] avg2020 = new double[3];
        double[] avg2021 = new double[3];
        for (int i = 0; i < 3; i++) {
            avg2020[i] = Arrays.stream(payments[i]).average().orElse(0);
            avg2021[i] = Arrays.stream(payments[i + 3]).average().orElse(0);
        }
        double correlation = calculateCorrelation(avg2020, avg2021);

        outputArea.setText("📊 РЕЗУЛЬТАТИ АНАЛІЗУ:\n");
        outputArea.append("Мінімальний платіж: " + String.format("%.2f", minPayment) + " грн\n");
        outputArea.append("Сумарний розмір оплати: " + String.format("%.2f", total) + " грн\n");
        outputArea.append("Коефіцієнт кореляції (2020–2021): " + String.format("%.4f", correlation) + "\n");
    }

    private double calculateCorrelation(double[] x, double[] y) {
        double meanX = Arrays.stream(x).average().orElse(0);
        double meanY = Arrays.stream(y).average().orElse(0);
        double num = 0, denomX = 0, denomY = 0;
        for (int i = 0; i < x.length; i++) {
            num += (x[i] - meanX) * (y[i] - meanY);
            denomX += Math.pow(x[i] - meanX, 2);
            denomY += Math.pow(y[i] - meanY, 2);
        }
        return num / Math.sqrt(denomX * denomY);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new PaymentAnalysis().setVisible(true));
    }
}

