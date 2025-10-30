package pianokeys;

import javax.swing.*;
import java.awt.*;

import static java.awt.Color.WHITE;

public class IconButton extends JButton
{

    private Color iconColor;
    private String shape;

    public IconButton(String text, Color iconColor, String shape)
    {
        super(text);
        this.iconColor = iconColor;
        this.shape = shape;

        setIcon(new Icon()
        {
            @Override
            public void paintIcon(Component c, Graphics g, int x, int y)
            {
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2d.setColor(iconColor);

                switch (shape)
                {
                    case "erase":
                        // eraser
                        g2d.fillRect(x + 2, y + 2, 12, 12);
                        g2d.setColor(WHITE);
                        g2d.drawLine(x + 4, y + 4, x + 12, y + 12);
                        break;
                    case "restart":
                        // circular arrow
                        g2d.drawArc(x + 2, y + 2, 12, 12, 45, 300);
                        g2d.fillPolygon(new int[]{x + 14, x + 14, x + 10}, new int[]{y + 2, y + 6, y + 4}, 3);
                        break;
                    case "record":
                        // circle (record button)
                        g2d.fillOval(x + 3, y + 3, 10, 10);
                        break;
                    case "play":
                        // triangle (play button)
                        g2d.fillPolygon(new int[]{x + 4, x + 4, x + 13}, new int[]{y + 2, y + 14, y + 8}, 3);
                        break;
                    case "instrument":
                        // musical note
                        g2d.fillOval(x + 3, y + 10, 6, 6);
                        g2d.fillRect(x + 8, y + 3, 2, 10);
                        g2d.fillRect(x + 8, y + 3, 5, 2);
                        break;
                    default:
                        System.err.println("Unknown icon shape: " + shape);
                        break;
                }
            }

            @Override
            public int getIconWidth()
            {
                return 16;
            }

            @Override
            public int getIconHeight()
            {
                return 16;
            }
        });
    }
}