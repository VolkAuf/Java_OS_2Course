import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;

public class Frame {

    private JFrame frame;
    private JButton buttonStart;
    private JButton buttonCreateFile;
    private JButton buttonCreateFolder;
    private JButton buttonCopyEntity;
    private JButton buttonPasteEntity;
    private JButton buttonMoveEntity;
    private JButton buttonDeleteEntity;
    private Panel panelMemory;
    private JTextField fieldMemory;
    private JTextField fieldSector;
    private JTextField fieldFile;
    private JTextField fieldName;
    private int sizeMemory;
    private int sizeSector;
    private Memory memory;
    private JTree tree;
    private FileManager fileManager;
    private DefaultMutableTreeNode selectedNode;
    private DefaultMutableTreeNode root;
    private DefaultMutableTreeNode buffer;

    public Frame() {
        initialization();
    }

    public void initialization() {
        panelMemory = new Panel();
        panelMemory.setBounds(0, 130, 1510, 600);
        frame = new JFrame();
        buttonStart = new JButton("Start");
        buttonStart.setBounds(150, 20, 100, 95);

        buttonStart.addActionListener(e -> {
            buttonCreateFile.setEnabled(true);
            buttonCreateFolder.setEnabled(true);
            buttonDeleteEntity.setEnabled(true);
            buttonCopyEntity.setEnabled(true);
            buttonPasteEntity.setEnabled(true);
            buttonMoveEntity.setEnabled(true);
            sizeMemory = Integer.parseInt(fieldMemory.getText());
            sizeSector = Integer.parseInt(fieldSector.getText());
            memory = new Memory(sizeMemory, sizeSector);
            fileManager = new FileManager(memory);
            buttonStart.setEnabled(false);
            fieldMemory.setEnabled(false);
            fieldSector.setEnabled(false);
            fieldFile.setEnabled(true);
            fieldName.setEnabled(true);
            panelMemory.setMemory(memory);
            panelMemory.setLayout(null);
            root = new DefaultMutableTreeNode(fileManager.getRoot(), true);
            tree = new JTree(root);
            tree.updateUI();
            tree.setBounds(0, 0, 200, 700);

            tree.addTreeSelectionListener(e1 -> {
                selectedNode = (DefaultMutableTreeNode) tree.getLastSelectedPathComponent();
                fileManager.removeSelecting();
                select(selectedNode);
            });

            panelMemory.add(tree);
            frame.repaint();
        });

        buttonCreateFile = new JButton("Create File");
        buttonCreateFile.setBounds(260, 20, 120, 45);
        buttonCreateFile.addActionListener(e -> {
            if (selectedNode != null) {
                if (!selectedNode.getAllowsChildren()) {
                    JOptionPane.showMessageDialog(frame, "Cannot be added to file", "ERROR", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                File file = fileManager.addFile(fieldName.getText(), Integer.parseInt(fieldFile.getText()));
                if (file != null) {
                    DefaultMutableTreeNode node = new DefaultMutableTreeNode(file, false);
                    selectedNode.add(node);
                    tree.updateUI();
                    frame.repaint();
                } else {
                    JOptionPane.showMessageDialog(frame, "Not enough memory!", "ERROR", JOptionPane.ERROR_MESSAGE);
                }
            } else {
                JOptionPane.showMessageDialog(frame, "Folder not selected!", "ERROR", JOptionPane.ERROR_MESSAGE);
            }
        });

        buttonCreateFolder = new JButton("Create Folder");
        buttonCreateFolder.setBounds(260, 70, 120, 45);
        buttonCreateFolder.addActionListener(e -> {
            if (!selectedNode.getAllowsChildren()) {
                JOptionPane.showMessageDialog(frame, "Cannot be added to file", "ERROR", JOptionPane.ERROR_MESSAGE);
                return;
            }
            File folder = fileManager.addFolder(fieldName.getText());
            if (folder != null) {
                DefaultMutableTreeNode node = new DefaultMutableTreeNode(folder, true);
                selectedNode.add(node);
                tree.updateUI();
                frame.repaint();
            } else {
                JOptionPane.showMessageDialog(frame, "Not enough memory", "ERROR", JOptionPane.ERROR_MESSAGE);
            }
        });

        buttonCopyEntity = new JButton("Copy");
        buttonCopyEntity.setBounds(390, 20, 120, 45);
        buttonCopyEntity.addActionListener(e -> {
            copy(selectedNode);
            frame.repaint();
        });

        buttonPasteEntity = new JButton("Paste");
        buttonPasteEntity.setBounds(390, 70, 120, 45);
        buttonPasteEntity.addActionListener(e -> {
            paste(selectedNode);
            frame.repaint();
        });

        buttonMoveEntity = new JButton("Move");
        buttonMoveEntity.setBounds(520, 20, 120, 45);
        buttonMoveEntity.addActionListener(e -> {
            if (selectedNode != null && selectedNode.getParent() != null) {
                DefaultMutableTreeNode buffer = selectedNode;
                ((DefaultMutableTreeNode) selectedNode.getParent()).remove(selectedNode);
                ShowDialogMove showDialogMove = new ShowDialogMove(frame, root);
                DefaultMutableTreeNode parent = showDialogMove.getNode();
                if (parent != null) {
                    parent.add(buffer);
                }
            }
            tree.updateUI();
            frame.repaint();
        });

        buttonDeleteEntity = new JButton("Delete");
        buttonDeleteEntity.setBounds(520, 70, 120, 45);
        buttonDeleteEntity.addActionListener(e -> {
            if (selectedNode != null && selectedNode.getParent() == null) {
                JOptionPane.showMessageDialog(frame, "root cannot be removed", "ERROR", JOptionPane.ERROR_MESSAGE);
                return;
            }
            assert selectedNode != null;
            delete(selectedNode);
            selectedNode = null;
            frame.repaint();
        });

        JLabel labelMemory = new JLabel("Disk Size");
        labelMemory.setBounds(10, 20, 60, 20);
        fieldMemory = new JTextField("131072");
        fieldMemory.setBounds(80, 20, 60, 20);
        JLabel labelSector = new JLabel("Sector Size");
        labelSector.setBounds(10, 45, 60, 20);
        fieldSector = new JTextField("16");
        fieldSector.setBounds(80, 45, 60, 20);
        JLabel labelFileSize = new JLabel("File Size");
        labelFileSize.setBounds(10, 70, 60, 20);
        fieldFile = new JTextField("8");
        fieldFile.setBounds(80, 70, 60, 20);
        JLabel labelName = new JLabel("Name");
        labelName.setBounds(10, 95, 60, 20);
        fieldName = new JTextField("None");
        fieldName.setBounds(80, 95, 60, 20);
        fieldFile.setEnabled(false);
        fieldName.setEnabled(false);
        buttonCreateFile.setEnabled(false);
        buttonCreateFolder.setEnabled(false);
        buttonDeleteEntity.setEnabled(false);
        buttonCopyEntity.setEnabled(false);
        buttonPasteEntity.setEnabled(false);
        buttonMoveEntity.setEnabled(false);
        frame.setBounds(0, 0, 1920, 800);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(null);
        frame.setVisible(true);
        frame.setResizable(false);
        frame.getContentPane().add(panelMemory);
        frame.getContentPane().add(buttonCreateFile);
        frame.getContentPane().add(buttonCreateFolder);
        frame.getContentPane().add(buttonDeleteEntity);
        frame.getContentPane().add(buttonCopyEntity);
        frame.getContentPane().add(buttonPasteEntity);
        frame.getContentPane().add(buttonMoveEntity);
        frame.getContentPane().add(labelMemory);
        frame.getContentPane().add(fieldMemory);
        frame.getContentPane().add(fieldSector);
        frame.getContentPane().add(labelSector);
        frame.getContentPane().add(buttonStart);
        frame.getContentPane().add(fieldFile);
        frame.getContentPane().add(labelFileSize);
        frame.getContentPane().add(fieldName);
        frame.getContentPane().add(labelName);
        frame.repaint();
    }

    private void delete(DefaultMutableTreeNode node) {
        while (!node.isLeaf()) {
            delete((DefaultMutableTreeNode) node.getChildAt(0));
        }
        if (node.getUserObject() != null) {
            File file = (File) node.getUserObject();
            fileManager.deleteFile(file);
            ((DefaultMutableTreeNode) node.getParent()).remove(node);
            tree.updateUI();
            frame.repaint();
        }
    }

    private void copy(DefaultMutableTreeNode node) {
        if (node != null && node.getParent() != null) {
            buffer = node;
        }
    }

    private void paste(DefaultMutableTreeNode parent) {
        if (parent.getAllowsChildren() && buffer != null) {
            parent.add(clone(buffer));
            tree.updateUI();
        }
    }

    private DefaultMutableTreeNode clone(DefaultMutableTreeNode node) {
        File file = (File) node.getUserObject();
        MemoryCell memoryCell = fileManager.selectMemory(file.getSize());
        DefaultMutableTreeNode newNode;
        if (memoryCell != null) {
            newNode = new DefaultMutableTreeNode(new File(node.toString(), file.getSize(), memoryCell), node.getAllowsChildren());
            for (int i = 0; i < node.getChildCount(); i++) {
                newNode.add(clone((DefaultMutableTreeNode) node.getChildAt(i)));
            }
            return newNode;
        } else {
            JOptionPane.showMessageDialog(frame, "Not enough memory!", "ERROR", JOptionPane.ERROR_MESSAGE);
            return null;
        }
    }

    private void select(DefaultMutableTreeNode node) {
        if (node.getUserObject() != null) {
            File file = (File) node.getUserObject();
            MemoryCell memoryCell = file.getCell();
            if (memoryCell != null) {
                fileManager.selectFile(memoryCell);
                frame.repaint();
            }
        }
        if (!node.isLeaf()) {
            for (int i = 0; i < node.getChildCount(); i++) {
                select((DefaultMutableTreeNode) node.getChildAt(i));
            }
        }
    }
}
