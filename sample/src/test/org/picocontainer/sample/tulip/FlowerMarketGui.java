package org.picocontainer.sample.tulip;

import org.picocontainer.lifecycle.Startable;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

/**
 * @author Aslak Helles&oslash;y
 * @author Jon Tirs&eacute;n
 * @version $Revision$
 */
public class FlowerMarketGui extends FlowerMarketStub implements Startable {
    private DefaultTableModel bidsTableModel;

    public void start() {
        JFrame frame = new JFrame();
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        bidsTableModel = new DefaultTableModel();
        bidsTableModel.addColumn("Flower");
        bidsTableModel.addColumn("Type");
        frame.getContentPane().setLayout(new BorderLayout());
        JTable table = new JTable(bidsTableModel);
        frame.getContentPane().add(table, BorderLayout.CENTER);
        frame.pack();
        frame.setBounds(700, 100, 300, 600);
        frame.show();
    }

    public void sellBid(String flower) {
        super.sellBid(flower);
        bidsTableModel.addRow(new Object[]{flower, "Sell"});
    }

    public void buyBid(String flower) {
        super.sellBid(flower);
        bidsTableModel.addRow(new Object[]{flower, "Buy"});
    }

}
