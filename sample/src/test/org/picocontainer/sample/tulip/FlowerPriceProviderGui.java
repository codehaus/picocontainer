package org.picocontainer.sample.tulip;

import org.picocontainer.Startable;

import javax.swing.*;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

/**
 * @author Jon Tirs&eacute;n
 * @version $Revision$
 */
public class FlowerPriceProviderGui extends FlowerPriceProviderStub implements Startable {
    private DefaultTableModel pricesTableModel;

    public void start() {
        JFrame frame = new JFrame();
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        pricesTableModel = new DefaultTableModel();
        pricesTableModel.addColumn("Flower");
        pricesTableModel.addColumn("Price");
        pricesTableModel.addRow(new Object[]{"Rose", "500"});
        pricesTableModel.addRow(new Object[]{"Tulip", "70"});
        pricesTableModel.addRow(new Object[]{"Forgetmenot", "20"});
        frame.getContentPane().setLayout(new BorderLayout());
        JTable table = new JTable(pricesTableModel);
        pricesTableModel.addTableModelListener(new TableModelListener() {
            public void tableChanged(TableModelEvent e) {
                changePrices();
            }
        });
        frame.getContentPane().add(table, BorderLayout.CENTER);
        frame.pack();
        frame.setBounds(100, 100, 300, 600);
        frame.show();
    }

    public void stop() {
    }

    private void changePrices() {
        for (int i = pricesTableModel.getRowCount() - 1; i >= 0; i--) {
            String flower = (String) pricesTableModel.getValueAt(i, 0);
            int price = Integer.parseInt((String) pricesTableModel.getValueAt(i, 1));
            changeFlowerPrice(flower, price);
        }
    }
}
