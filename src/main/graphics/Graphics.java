package main.graphics;

import javax.swing.*;
import java.awt.*;

public class Graphics extends JFrame {

    public static void main(String[] args) {

        Graphics frame = new Graphics();
        frame.setVisible(true);
        frame.setSize(800, 800);
        frame.setDefaultCloseOperation(EXIT_ON_CLOSE);

        Panel panel = new Panel();
        frame.add(panel);

    }

    public Graphics() {



    }

}
