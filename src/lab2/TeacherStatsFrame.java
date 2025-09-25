package lab2;


import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.Arrays;
import java.util.Random;

public class TeacherStatsFrame extends JFrame {
    private JTable table;
    private JLabel meanLabel, medianLabel, varianceLabel;
    private DefaultTableModel model;
    private JSpinner teacherCountSpinner;

    public TeacherStatsFrame() {
        setTitle("Аналіз даних викладачів");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(750, 600);
        setLayout(new BorderLayout());

        // Панель керування
        JPanel topPanel = new JPanel();
        topPanel.add(new JLabel("Кількість викладачів:"));
        teacherCountSpinner = new JSpinner(new SpinnerNumberModel(30, 1, 100, 1));
        topPanel.add(teacherCountSpinner);

        JButton generateButton = new JButton("Згенерувати");
        generateButton.addActionListener(this::generateRandomData);
        topPanel.add(generateButton);

        add(topPanel, BorderLayout.NORTH);

        // Таблиця
        model = new DefaultTableModel(30, 2);
        model.setColumnIdentifiers(new String[]{"Вік", "Лікарняних"});
        table = new JTable(model);
        add(new JScrollPane(table), BorderLayout.CENTER);

        // Панель результатів
        JPanel bottomPanel = new JPanel(new GridLayout(6, 1));
        JButton calcButton = new JButton("Обчислити");
        calcButton.addActionListener(this::calculateStats);
        bottomPanel.add(calcButton);

        meanLabel = new JLabel("Середнє: ");
        medianLabel = new JLabel("Медіана: ");
        varianceLabel = new JLabel("Дисперсія: ");

        bottomPanel.add(meanLabel);
        bottomPanel.add(medianLabel);
        bottomPanel.add(varianceLabel);

        add(bottomPanel, BorderLayout.SOUTH);

        setVisible(true);
    }

    private void generateRandomData(ActionEvent e) {
        int count = (int) teacherCountSpinner.getValue();
        model.setRowCount(count);
        Random random = new Random();

        for (int i = 0; i < count; i++) {
            int age = 25 + random.nextInt(46);       // 25..70
            int sickDays = random.nextInt(11);       // 0..10
            model.setValueAt(age, i, 0);
            model.setValueAt(sickDays, i, 1);
        }
    }

    private void calculateStats(ActionEvent e) {
        int rows = table.getRowCount();
        double[] values = new double[rows];
        int count = 0;

        for (int i = 0; i < rows; i++) {
            Object value = table.getValueAt(i, 1);
            if (value != null) {
                try {
                    values[count++] = Double.parseDouble(value.toString());
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(this, "Помилка в рядку " + (i + 1));
                    return;
                }
            }
        }

        if (count == 0) {
            JOptionPane.showMessageDialog(this, "Немає даних для обчислення!");
            return;
        }

        double[] data = Arrays.copyOf(values, count);

        double mean = mean(data);
        double median = median(data);
        double variance = variance(data, mean);

        meanLabel.setText("Середнє арифметичне: " + String.format("%.2f", mean));
        medianLabel.setText("Медіана: " + String.format("%.2f", median));
        varianceLabel.setText("Дисперсія: " + String.format("%.2f", variance));

        showHistogram(data);
    }

    private void showHistogram(double[] data) {
        // Рахуємо частоти
        int[] freq = new int[11];
        for (double v : data) {
            int index = (int) v;
            if (index >= 0 && index <= 10) freq[index]++;
        }

        JFrame frame = new JFrame("Гістограма");
        frame.setSize(500, 300);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        frame.add(new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                int width = getWidth();
                int height = getHeight();
                int barWidth = width / freq.length;

                int max = Arrays.stream(freq).max().orElse(1);
                for (int i = 0; i < freq.length; i++) {
                    int barHeight = (int) ((double) freq[i] / max * (height - 40));
                    g.setColor(Color.BLUE);
                    g.fillRect(i * barWidth + 5, height - barHeight - 20, barWidth - 10, barHeight);
                    g.setColor(Color.BLACK);
                    g.drawString(String.valueOf(i), i * barWidth + barWidth / 2, height - 5);
                }
            }
        });

        frame.setVisible(true);
    }


    private double mean(double[] data) {
        double sum = 0;
        for (double v : data) sum += v;
        return sum / data.length;
    }

    private double median(double[] data) {
        double[] sorted = Arrays.copyOf(data, data.length);
        Arrays.sort(sorted);
        int n = sorted.length;
        if (n % 2 == 0) {
            return (sorted[n / 2 - 1] + sorted[n / 2]) / 2.0;
        } else {
            return sorted[n / 2];
        }
    }

    private double variance(double[] data, double mean) {
        double sumSq = 0;
        for (double v : data) sumSq += Math.pow(v - mean, 2);
        return sumSq / data.length;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(TeacherStatsFrame::new);
    }
}
