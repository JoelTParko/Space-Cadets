import org.omg.PortableInterceptor.INACTIVE;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Line2D;

public class MathsDrawer extends JFrame implements ActionListener
{
    private JPanel panel;
    private JButton btnDraw, btnSwitch;
    private JTextField outerRadius, offsetBox, innerRadius;
    private JComboBox cbxDrawOptions;

     public void DrawWindow()
     {
         setDefaultCloseOperation(EXIT_ON_CLOSE);
         Container window = getContentPane();
         window.setLayout(new FlowLayout());

         panel = new JPanel();
         panel.setPreferredSize(new Dimension(950, 900));
         panel.setBackground(Color.white);
         window.add(panel);

         JLabel radius1 = new JLabel("Outer Radius");
         Font bigFont = radius1.getFont().deriveFont(Font.PLAIN, 30f);
         radius1.setFont(bigFont);
         window.add(radius1);
         outerRadius = new JTextField(5);
         outerRadius.setFont(bigFont);
         window.add(outerRadius);

         JLabel offsetLabel = new JLabel("Offset");
         offsetLabel.setFont(bigFont);
         window.add(offsetLabel);
         offsetBox = new JTextField(5);
         offsetBox.setFont(bigFont);
         window.add(offsetBox);

         JLabel radius2 = new JLabel("Inner Radius");
         radius2.setFont(bigFont);
         window.add(radius2);
         innerRadius = new JTextField(5);
         innerRadius.setFont(bigFont);
         window.add(innerRadius);

         btnDraw = new JButton("DRAW");
         btnDraw.setFont(bigFont);
         btnDraw.setPreferredSize(new Dimension(400,40));
         btnDraw.addActionListener(this);
         window.add(btnDraw);

         btnSwitch = new JButton("Switch");

         cbxDrawOptions = new JComboBox();
         cbxDrawOptions.setFont(bigFont);
         cbxDrawOptions.addItem("Spirograph");
         cbxDrawOptions.addItem("Ellipse");
         window.add(cbxDrawOptions);

         Graphics g = panel.getGraphics();
         Graphics2D drawGrid = (Graphics2D) g;
         drawGrid.setColor(Color.white);
         drawGrid.fillRect(0, 0, 950, 900);
         drawGrid.setColor(Color.black);
         drawGrid.translate(475, 450);

         drawGrid.drawLine(0,450,950,450);
         drawGrid.drawLine(475, 0, 475, 950);
     }

    @Override
    public void actionPerformed(ActionEvent e)
    {

        Graphics g = panel.getGraphics();
        Graphics2D shape = (Graphics2D) g;
      //  shape.setColor(Color.white);
     //   shape.fillRect(0,0,950,900);
        shape.setColor(Color.black);
        shape.translate(475,450);

        double x, y, outer, inner, oSet;

        outer = Integer.parseInt(outerRadius.getText());
        inner = Integer.parseInt(innerRadius.getText());
        oSet = Integer.parseInt(offsetBox.getText());


        for (double t = 0; t < 100; t+= 0.02)
        {
            x = (outer + inner) * Math.cos(t) - (inner + oSet) * Math.cos(((outer + inner)/inner)*t);
            y = (outer + inner) * Math.sin(t) - (inner + oSet) * Math.sin(((outer + inner)/inner)*t);

            Shape point = new Line2D.Double(x,y,x,y);
            shape.draw(point);

        }





    }
}
