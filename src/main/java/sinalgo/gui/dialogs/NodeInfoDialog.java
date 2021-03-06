/*
BSD 3-Clause License

Copyright (c) 2007-2013, Distributed Computing Group (DCG)
                         ETH Zurich
                         Switzerland
                         dcg.ethz.ch
              2017-2018, André Brait

All rights reserved.

Redistribution and use in source and binary forms, with or without
modification, are permitted provided that the following conditions are met:

* Redistributions of source code must retain the above copyright notice, this
  list of conditions and the following disclaimer.

* Redistributions in binary form must reproduce the above copyright notice,
  this list of conditions and the following disclaimer in the documentation
  and/or other materials provided with the distribution.

* Neither the name of the copyright holder nor the names of its
  contributors may be used to endorse or promote products derived from
  this software without specific prior written permission.

THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE
FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
*/
package sinalgo.gui.dialogs;

import sinalgo.configuration.Configuration;
import sinalgo.exception.SinalgoWrappedException;
import sinalgo.gui.GUI;
import sinalgo.gui.GuiHelper;
import sinalgo.gui.helper.NonRegularGridLayout;
import sinalgo.gui.helper.UnborderedJTextField;
import sinalgo.nodes.Node;
import sinalgo.nodes.Position;
import sinalgo.runtime.Global;
import sinalgo.runtime.SinalgoRuntime;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Enumeration;

/**
 * The class for the dialog displaying information about a node.
 */
public class NodeInfoDialog extends JDialog implements ActionListener, PropertyChangeListener {

    private static final long serialVersionUID = 5403782066445725367L;

    private GUI parent;
    private Node node;

    private JFormattedTextField nodeNumber = new JFormattedTextField();

    private JTextField positionX = new JTextField();
    private JTextField positionY = new JTextField();
    private JTextField positionZ = new JTextField();

    private JTextArea infoText = new JTextArea();

    private UnborderedJTextField implementationText = new UnborderedJTextField();
    private UnborderedJTextField connectivityText = new UnborderedJTextField();
    private UnborderedJTextField interferenceText = new UnborderedJTextField();
    private UnborderedJTextField mobilityText = new UnborderedJTextField();
    private UnborderedJTextField reliabilityText = new UnborderedJTextField();

    private JButton prevNode = new JButton("Previous Node");
    private JButton nextNode = new JButton("Next Node");

    /**
     * The constructor for the NodeInfoDialog class.
     *
     * @param p The parent gui where this dialog is attached to.
     * @param n The node the information is about.
     */
    public NodeInfoDialog(GUI p, Node n) {
        super(p, "Edit Node " + n.ID, true);
        GuiHelper.setWindowIcon(this);
        parent = p;
        node = n;

        node.highlight(true);

        this.setLayout(new BorderLayout());

        JPanel nodeSelAndPosition = new JPanel();
        nodeSelAndPosition.setLayout(new BorderLayout());

        JPanel nodeSel = new JPanel();

        // evaluate whether a node is the first or the last in the enumeration and thus
        // has no
        // previous respective next element.
        boolean hasPrev = false;
        Enumeration<Node> nodesEnumer = SinalgoRuntime.nodes.getNodeEnumeration();
        while (nodesEnumer.hasMoreElements()) {
            Node nd = nodesEnumer.nextElement();
            if (nd.ID == n.ID) {
                if (!nodesEnumer.hasMoreElements()) {
                    nextNode.setEnabled(false);
                }
                prevNode.setEnabled(hasPrev);
                break;
            }
            hasPrev = true;
        }

        prevNode.addActionListener(this);
        nodeNumber.setColumns(6);
        nodeNumber.setValue(node.ID);
        nodeNumber.addPropertyChangeListener("value", this);
        nextNode.addActionListener(this);
        nodeSel.add(prevNode);
        nodeSel.add(nodeNumber);
        nodeSel.add(nextNode);
        this.add(nodeSel);

        Position pos = node.getPosition();

        positionX.setText(String.valueOf(pos.xCoord));
        positionY.setText(String.valueOf(pos.yCoord));
        positionZ.setText(String.valueOf(pos.zCoord));

        JPanel position = new JPanel();
        position.setBorder(BorderFactory.createTitledBorder("Position"));
        position.setLayout(new BoxLayout(position, BoxLayout.Y_AXIS));
        position.setPreferredSize(new Dimension(80, 80));

        position.add(positionX);
        position.add(positionY);
        if (Configuration.dimensions == 3) {
            position.add(positionZ);
        }

        nodeSelAndPosition.add(BorderLayout.NORTH, nodeSel);
        nodeSelAndPosition.add(BorderLayout.SOUTH, position);

        this.add(BorderLayout.NORTH, nodeSelAndPosition);

        JPanel info = new JPanel();
        info.setBorder(BorderFactory.createTitledBorder("Node Info"));
        info.setLayout(new BoxLayout(info, BoxLayout.Y_AXIS));
        this.add(BorderLayout.CENTER, info);

        JPanel infoPanel = new JPanel();
        NonRegularGridLayout nrgl = new NonRegularGridLayout(6, 2, 5, 5);
        nrgl.setAlignToLeft(true);
        infoPanel.setLayout(nrgl);
        info.add(infoPanel);

        UnborderedJTextField typeLabel = new UnborderedJTextField("Node Implementation:", Font.BOLD);
        implementationText.setText(Global.toShortName(node.getClass().getName()));
        implementationText.setEditable(false);
        infoPanel.add(typeLabel);
        infoPanel.add(implementationText);

        UnborderedJTextField connectivityLabel = new UnborderedJTextField("Node Connectivity:", Font.BOLD);
        connectivityText.setText(Global.toShortName(node.getConnectivityModel().getClass().getName()));
        connectivityText.setEditable(false);
        infoPanel.add(connectivityLabel);
        infoPanel.add(connectivityText);

        UnborderedJTextField interferenceLabel = new UnborderedJTextField("Node Interference:", Font.BOLD);
        interferenceText.setText(Global.toShortName(node.getInterferenceModel().getClass().getName()));
        interferenceText.setEditable(false);
        infoPanel.add(interferenceLabel);
        infoPanel.add(interferenceText);

        UnborderedJTextField mobilityLabel = new UnborderedJTextField("Node Mobility:", Font.BOLD);
        mobilityText.setText(Global.toShortName(node.getMobilityModel().getClass().getName()));
        mobilityText.setEditable(false);
        infoPanel.add(mobilityLabel);
        infoPanel.add(mobilityText);

        UnborderedJTextField reliabilityLabel = new UnborderedJTextField("Node Reliability:", Font.BOLD);
        reliabilityText.setText(Global.toShortName(node.getReliabilityModel().getClass().getName()));
        reliabilityText.setEditable(false);
        infoPanel.add(reliabilityLabel);
        infoPanel.add(reliabilityText);

        infoText.setText(node.toString());
        JLabel infoTextLabel = new JLabel("Info Text:");
        infoText.setEditable(false);
        infoText.setBackground(infoTextLabel.getBackground());
        infoPanel.add(infoTextLabel);
        infoPanel.add(infoText);

        JPanel buttons = new JPanel();

        JButton ok = new JButton("OK");
        ok.addActionListener(this);
        buttons.add(ok);

        JButton cancel = new JButton("Cancel");
        cancel.addActionListener(this);
        buttons.add(cancel);

        this.add(BorderLayout.SOUTH, buttons);

        WindowListener listener = new WindowAdapter() {

            @Override
            public void windowClosing(WindowEvent event) {
                node.highlight(false);
                parent.redrawGUI();
            }
        };
        this.addWindowListener(listener);

        this.setResizable(true);
        this.getRootPane().setDefaultButton(ok);

        // Detect ESCAPE button
        KeyboardFocusManager focusManager = KeyboardFocusManager.getCurrentKeyboardFocusManager();
        focusManager.addKeyEventPostProcessor(e -> {
            if (!e.isConsumed() && e.getID() == KeyEvent.KEY_PRESSED && e.getKeyCode() == KeyEvent.VK_ESCAPE) {
                node.highlight(false);
                NodeInfoDialog.this.setVisible(false);
                parent.redrawGUINow(); // needs blocking redrawing
            }
            return false;
        });

        // Redraw the graph to have the selected node painted in the right color.
        parent.redrawGUI();
        this.pack();
        this.setLocationRelativeTo(p);
        this.setVisible(true);
    }

    private void revalidate(Node n, boolean hasPrev, boolean hasNext) {
        this.setTitle("Edit Node " + n.ID);

        node.highlight(false);

        node = n;

        node.highlight(true);

        this.prevNode.setEnabled(hasPrev);
        this.nextNode.setEnabled(hasNext);

        Position pos = node.getPosition();

        positionX.setText(String.valueOf(pos.xCoord));
        positionY.setText(String.valueOf(pos.yCoord));
        positionZ.setText(String.valueOf(pos.zCoord));

        implementationText.setText(Global.toShortName(node.getClass().getName()));

        connectivityText.setText(Global.toShortName(node.getConnectivityModel().getClass().getName()));
        interferenceText.setText(Global.toShortName(node.getInterferenceModel().getClass().getName()));
        mobilityText.setText(Global.toShortName(node.getMobilityModel().getClass().getName()));
        reliabilityText.setText(Global.toShortName(node.getReliabilityModel().getClass().getName()));

        infoText.setText(node.toString());
        parent.redrawGUI();
    }

    @Override
    public void actionPerformed(ActionEvent event) {
        switch (event.getActionCommand()) {
            case "Cancel":
                node.highlight(false);
                this.setVisible(false);
                parent.redrawGUINow(); // needs blocking redrawing
                break;
            case "OK":
                try {
                    node.setPosition(new Position(Double.parseDouble(positionX.getText()),
                            Double.parseDouble(positionY.getText()), Double.parseDouble(positionZ.getText())));
                } catch (NumberFormatException nFE) {
                    throw new SinalgoWrappedException(nFE);
                }
                node.highlight(false);
                this.setVisible(false);
                parent.redrawGUINow(); // needs blocking redrawing
                break;
            case "Next Node": {
                Enumeration<Node> nodesEnumer = SinalgoRuntime.nodes.getNodeEnumeration();
                while (nodesEnumer.hasMoreElements()) {
                    Node nd = nodesEnumer.nextElement();
                    if (nd.ID == node.ID + 1) {
                        nodeNumber.setValue(nd.ID);
                        // this triggers a property change event.
                        break;
                    }
                }
                break;
            }
            case "Previous Node": {
                Enumeration<Node> nodesEnumer = SinalgoRuntime.nodes.getNodeEnumeration();
                while (nodesEnumer.hasMoreElements()) {
                    Node nd = nodesEnumer.nextElement();
                    if (nd.ID == node.ID - 1) {
                        nodeNumber.setValue(nd.ID);
                        // this triggers a property change event.
                        break;
                    }
                }
                break;
            }
        }
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {

        int newId = (Integer) evt.getNewValue();

        boolean hasPrev = false;
        Enumeration<Node> nodesEnumer = SinalgoRuntime.nodes.getNodeEnumeration();
        while (nodesEnumer.hasMoreElements()) {
            Node nd = nodesEnumer.nextElement();
            if (nd.ID == newId) {
                boolean hasNext = true;
                if (!nodesEnumer.hasMoreElements()) {
                    hasNext = false;
                }
                this.revalidate(nd, hasPrev, hasNext);
                break;
            }
            hasPrev = true;
        }
    }
}
