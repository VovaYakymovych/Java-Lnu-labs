package lab3;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class InternetTVBilling extends JFrame {
    private JTable table;
    private DefaultTableModel model;
    private JLabel minLabel, sumLabel, corrLabel;

    private static final String[] firstNames = {
            "Олександр","Іван","Марія","Анна","Петро","Олег","Наталія","Юрій","Оксана","Андрій","Катерина"
    };
    private static final String[] lastNames = {
            "Коваль","Шевченко","Бондар","Ткаченко","Кравченко",
            "Мельник","Сидоренко","Гриценко","Лисенко","Романюк","Дмитренко"
    };

    public InternetTVBilling() {
        setTitle("Оплата послуг - Будинок №116, вул. Шевченка");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(800, 520);
        setLayout(new BorderLayout());

        JPanel topPanel = new JPanel();
        JButton genButton = new JButton("Згенерувати дані");
        genButton.addActionListener(this::generateData);
        JButton calcButton = new JButton("Обчислити");
        calcButton.addActionListener(this::calculate);
        topPanel.add(genButton);
        topPanel.add(calcButton);
        add(topPanel, BorderLayout.NORTH);

        model = new DefaultTableModel(11, 4);
        model.setColumnIdentifiers(new String[]{"Клієнт", "Пакет", "Трафік (ГБ)", "Оплата"});
        table = new JTable(model);
        add(new JScrollPane(table), BorderLayout.CENTER);

        JPanel bottomPanel = new JPanel(new GridLayout(3, 1));
        minLabel = new JLabel("Мінімальний платіж: ");
        sumLabel = new JLabel("Сумарний платіж: ");
        corrLabel = new JLabel("Коефіцієнт кореляції: ");
        bottomPanel.add(minLabel);
        bottomPanel.add(sumLabel);
        bottomPanel.add(corrLabel);
        add(bottomPanel, BorderLayout.SOUTH);

        setVisible(true);
    }

    private void generateData(ActionEvent e) {
        String[] packages = {"Базовий", "Стандарт", "Преміум"};
        int[] basePrice = {200, 350, 500};

        Random random = new Random();
        int rows = 11;
        model.setRowCount(rows);

        for (int i = 0; i < rows; i++) {
            String clientName = firstNames[random.nextInt(firstNames.length)] + " " +
                    lastNames[random.nextInt(lastNames.length)];
            int pkgIndex = random.nextInt(packages.length);
            int traffic = 10 + random.nextInt(91); // 10..100 ГБ
            double price = basePrice[pkgIndex] + traffic * 2.5; // Double

            model.setValueAt(clientName, i, 0);
            model.setValueAt(packages[pkgIndex], i, 1);
            model.setValueAt(traffic, i, 2);
            model.setValueAt(price, i, 3); // записуємо Double, не відформатований рядок
        }
    }

    private void calculate(ActionEvent e) {
        int rows = model.getRowCount();
        List<Double> trafficList = new ArrayList<>();
        List<Double> paymentList = new ArrayList<>();
        double sum = 0;
        Double min = null;

        for (int i = 0; i < rows; i++) {
            Object tObj = model.getValueAt(i, 2);
            Object pObj = model.getValueAt(i, 3);

            if (tObj == null || pObj == null) continue;
            String tStr = tObj.toString().trim();
            String pStr = pObj.toString().trim();
            if (tStr.isEmpty() || pStr.isEmpty()) continue;

            try {
                double t = Double.parseDouble(tStr.replace(',', '.'));
                double p = Double.parseDouble(pStr.replace(',', '.'));

                trafficList.add(t);
                paymentList.add(p);
                sum += p;
                if (min == null || p < min) min = p;
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this,
                        "Некоректні дані у рядку " + (i + 1) +
                                "\nТрафік: \"" + tStr + "\"  Оплата: \"" + pStr + "\"\nВиправте або згенеруйте дані повторно.");
                return;
            }
        }

        int n = paymentList.size();
        if (n == 0) {
            JOptionPane.showMessageDialog(this, "Немає коректних рядків для обчислення. Натисніть 'Згенерувати дані' або заповніть таблицю.");
            return;
        }

        double[] traffic = new double[n];
        double[] payment = new double[n];
        for (int i = 0; i < n; i++) { traffic[i] = trafficList.get(i); payment[i] = paymentList.get(i); }

        double corr = correlation(traffic, payment);

        minLabel.setText("Мінімальний платіж: " + String.format("%.2f грн", min));
        sumLabel.setText("Сумарний платіж: " + String.format("%.2f грн", sum));
        corrLabel.setText("Коефіцієнт кореляції: " + String.format("%.4f", corr));
    }

    private double correlation(double[] x, double[] y) {
        int n = x.length;
        double meanX = mean(x);
        double meanY = mean(y);
        double num = 0, denX = 0, denY = 0;
        for (int i = 0; i < n; i++) {
            num += (x[i] - meanX) * (y[i] - meanY);
            denX += Math.pow(x[i] - meanX, 2);
            denY += Math.pow(y[i] - meanY, 2);
        }
        double denom = Math.sqrt(denX * denY);
        if (denom == 0) return 0.0;
        return num / denom;
    }

    private double mean(double[] a) {
        double s = 0;
        for (double v : a) s += v;
        return s / a.length;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(InternetTVBilling::new);
    }
}
