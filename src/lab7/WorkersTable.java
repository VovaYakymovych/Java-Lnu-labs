package lab7;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class WorkersTable extends JFrame {

    public WorkersTable() {
        setTitle("Облік оплати працівників сервісу");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(700, 400);
        setLayout(new BorderLayout());

        // Початкові дані
        String[] workers = {
                "Іваненко", "Петренко", "Сидоренко", "Коваль",
                "Мельник", "Ткачук", "Гриценко", "Бондар"
        };

        double[] serviceCost = {800, 950, 700, 1200, 1100, 650, 900, 1300};
        double[] distance = {5.0, 8.5, 12.0, 4.0, 10.0, 6.5, 15.0, 9.0};
        double[] complexity = {150, 200, 100, 250, 180, 120, 300, 170};

        // Формуємо таблицю даних
        String[] columns = {"Працівник", "Вартість послуг", "Відстань (км)", "Надбавка", "Загальні витрати"};
        DefaultTableModel model = new DefaultTableModel(columns, 0);

        for (int i = 0; i < 8; i++) {
            double total = serviceCost[i] + complexity[i] + distance[i] * 10; // Наприклад: доїзд 10 грн/км
            model.addRow(new Object[]{
                    workers[i], serviceCost[i], distance[i], complexity[i], total
            });
        }

        JTable table = new JTable(model);
        add(new JScrollPane(table), BorderLayout.CENTER);

        JButton filterButton = new JButton("Відфільтрувати (шлях > середнього)");
        JButton specificButton = new JButton("Показати питомі витрати");

        JPanel panel = new JPanel();
        panel.add(filterButton);
        panel.add(specificButton);
        add(panel, BorderLayout.SOUTH);

        // Кнопка фільтрації
        filterButton.addActionListener(e -> {
            double avgDistance = 0;
            for (int i = 0; i < model.getRowCount(); i++) {
                avgDistance += (double) model.getValueAt(i, 2);
            }
            avgDistance /= model.getRowCount();

            DefaultTableModel filteredModel = new DefaultTableModel(columns, 0);
            for (int i = 0; i < model.getRowCount(); i++) {
                double d = (double) model.getValueAt(i, 2);
                if (d > avgDistance) {
                    filteredModel.addRow(new Object[]{
                            model.getValueAt(i, 0),
                            model.getValueAt(i, 1),
                            d,
                            model.getValueAt(i, 3),
                            model.getValueAt(i, 4)
                    });
                }
            }

            table.setModel(filteredModel);
            JOptionPane.showMessageDialog(this, "Середній шлях: " + String.format("%.2f км", avgDistance));
        });

        // Кнопка для розрахунку питомих витрат
        specificButton.addActionListener(e -> {
            String[] cols = {"Працівник", "Питомі витрати (грн/км)"};
            DefaultTableModel resultModel = new DefaultTableModel(cols, 0);

            for (int i = 0; i < model.getRowCount(); i++) {
                double total = (double) model.getValueAt(i, 4);
                double dist = (double) model.getValueAt(i, 2);
                double specific = total / dist;
                resultModel.addRow(new Object[]{
                        model.getValueAt(i, 0),
                        String.format("%.2f", specific)
                });
            }

            JTable resultTable = new JTable(resultModel);
            JOptionPane.showMessageDialog(this, new JScrollPane(resultTable),
                    "Питомі витрати", JOptionPane.INFORMATION_MESSAGE);
        });
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new WorkersTable().setVisible(true));
    }
}

