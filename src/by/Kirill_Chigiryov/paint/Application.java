package by.Kirill_Chigiryov.paint;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Objects;

public class Application extends JFrame {
    private BufferedImage bufferedImage =
            new BufferedImage(900, 600, BufferedImage.TYPE_INT_RGB);
    private Color color = Color.WHITE;
    private int brushWidth = 1;
    private int x1, y1, x2, y2;
    private boolean isFirst = false;
    private boolean afterBrush = true;
    private final JTextArea sizeSetter;
    private final JComboBox figures;

    Application(String title) {
        super(title);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setMinimumSize(new Dimension(getToolkit().getScreenSize().width / 3,
                getToolkit().getScreenSize().height / 3));
        setSize(getToolkit().getScreenSize().width * 3 / 4,
                getToolkit().getScreenSize().height * 3 / 4);

        DrawingSpace drawingArea = new DrawingSpace();
        drawingArea.setBorder(BorderFactory.createLineBorder(Color.black));
        drawingArea.setPreferredSize(new Dimension(this.getWidth() * 3 / 4,
                this.getHeight() * 3 / 4));
        JScrollPane scrollPane = new JScrollPane(drawingArea,
                ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS,
                ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);
        scrollPane.setPreferredSize(new Dimension(this.getWidth() * 3 / 4,
                this.getHeight() * 3 / 4));
        add(scrollPane);

        drawingArea.addMouseMotionListener(new MouseAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                if (Objects.equals(figures.getSelectedItem().toString(), "Кисть")) {
                    x1 = e.getX();
                    y1 = e.getY();
                    Graphics g = bufferedImage.getGraphics();
                    Graphics2D gr = (Graphics2D) g;
                    BasicStroke brushForm = new BasicStroke(brushWidth, BasicStroke.CAP_SQUARE, BasicStroke.JOIN_BEVEL);
                    gr.setStroke(brushForm);
                    gr.setColor(color);
                    gr.drawLine(x1, y1, x2, y2);
                    x2 = x1;
                    y2 = y1;
                    repaint();
                    afterBrush = true;
                }
            }
        });
        drawingArea.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                if (Objects.equals(figures.getSelectedItem().toString(), "Кисть")) {
                    super.mousePressed(e);
                    x1 = e.getX();
                    y1 = e.getY();
                    x2 = x1;
                    y2 = y1;
                    repaint();
                    afterBrush = true;
                }
                if (afterBrush) {
                    isFirst = true;
                    afterBrush = false;
                }
                if (isFirst) {
                    x1 = e.getX();
                    y1 = e.getY();
                    isFirst = false;
                } else {
                    Graphics g = bufferedImage.getGraphics();
                    x2 = e.getX();
                    y2 = e.getY();
                    g.setColor(color);
                    if (x2 < x1) {
                        int temp = x2;
                        x2 = x1;
                        x1 = temp;
                    }
                    if (y2 < y1) {
                        int temp = y2;
                        y2 = y1;
                        y1 = temp;
                    }
                    if (Objects.equals(figures.getSelectedItem().toString(),
                            "Прямоугольник")) {
                        g.drawRect(x1, y1, x2 - x1, y2 - y1);
                    } else if (Objects.equals(figures.getSelectedItem().toString(),
                            "Круг")) {
                        g.drawOval(x1, y1, x2 - x1, y2 - y1);
                    }
                    repaint();
                    isFirst = true;
                }
            }
        });

        Box box = Box.createVerticalBox();
        JLabel sizeText1 = new JLabel("Выберите размер");
        JLabel sizeText2 = new JLabel("кисти");
        JLabel sizeText3 = new JLabel("от 0 до 99:");
        JButton sizeConfirm = new JButton("Выбрать");
        box.add(Box.createVerticalStrut(50));
        sizeConfirm.addActionListener(new sizeConfirmActionListener());
        sizeSetter = new JTextArea("1");

        String[] figureNames = {"Кисть", "Прямоугольник", "Круг"};
        figures = new JComboBox<>(figureNames);
        figures.setPreferredSize(new Dimension(100, 80));

        JButton selectBrushColor = new JButton("Цвет");
        selectBrushColor.setPreferredSize(new Dimension(100, 80));
        selectBrushColor.addActionListener(new colorButtonActionListener());

        getContentPane().setLayout(new FlowLayout());

        JButton openFile = new JButton("Открыть файл");

        JButton saveToFile = new JButton("Сохранить");

        box.add(sizeText1);
        box.add(sizeText2);
        box.add(sizeText3);
        box.add(sizeSetter);
        box.add(Box.createVerticalStrut(20));
        box.add(sizeConfirm);
        box.add(Box.createVerticalStrut(20));
        box.add(figures);
        box.add(Box.createVerticalStrut(20));
        box.add(Box.createVerticalStrut(20));
        box.add(selectBrushColor);
        box.add(Box.createVerticalStrut(20));
        box.add(openFile);
        box.add(Box.createVerticalStrut(20));
        box.add(saveToFile);
        add(box);

        openFile.addActionListener(e -> {
            JFileChooser fc = new JFileChooser();
            fc.setFileSelectionMode(JFileChooser.FILES_ONLY);
            int returnVal = fc.showOpenDialog(null);
            if (returnVal == JFileChooser.APPROVE_OPTION) {
                File file = fc.getSelectedFile();
                if (file != null) {
                    try {
                        BufferedImage loaded = ImageIO.read(file);
                        bufferedImage = loaded;
                        drawingArea.setPreferredSize(new Dimension(loaded.getWidth(),
                                loaded.getHeight()));
                    } catch (IOException ex) {
                        JOptionPane.showMessageDialog(Application.this,
                                "Файл не прочитан или не найден");
                    }
                }
            }
        });

        saveToFile.addActionListener(e -> {
            JFileChooser fc = new JFileChooser();
            fc.setFileSelectionMode(JFileChooser.FILES_ONLY);
            int returnVal = fc.showSaveDialog(null);
            if (returnVal == JFileChooser.APPROVE_OPTION) {
                File file = fc.getSelectedFile();
                try {
                    ImageIO.write(bufferedImage, "jpg", file);
                } catch (IOException ex) {
                    JOptionPane.showMessageDialog(Application.this,
                            "Файл не может быть прочитан или найден");
                }
            }
        });
    }

    public class DrawingSpace extends JPanel {
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            g.drawImage(bufferedImage, 0, 0, this);
        }
    }

    private class colorButtonActionListener implements ActionListener {

        public void actionPerformed(ActionEvent e) {

            color = JColorChooser.showDialog(((Component) e.getSource()).getParent(),
                    "Select brush color panel", color);

        }

    }

    private class sizeConfirmActionListener implements ActionListener {

        public void actionPerformed(ActionEvent e) {

            String str = sizeSetter.getText();
            boolean b = str.matches("\\d+");

            if (b && str.length() <= 2) {

                brushWidth = Integer.parseInt(sizeSetter.getText());

            } else {
                JOptionPane.showMessageDialog(null,
                        "Не увлекайтесь размером кисти и не вводите" +
                                " буквенные символы! Значение более 99" +
                                " не рекомедуется.", "Ошибочное значение",
                        JOptionPane.ERROR_MESSAGE);
            }

        }

    }

}
