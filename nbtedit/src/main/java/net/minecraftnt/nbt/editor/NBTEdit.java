package net.minecraftnt.nbt.editor;

import net.minecraftnt.nbt.NBTReader;
import net.minecraftnt.nbt.NBTWriter;
import net.minecraftnt.nbt.exceptions.UnexpectedNBTNodeException;
import net.minecraftnt.nbt.nodes.NBTCompoundNode;
import net.minecraftnt.nbt.nodes.NBTListNode;
import net.minecraftnt.nbt.nodes.NBTNode;
import net.minecraftnt.nbt.nodes.NBTValueNode;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.plaf.BorderUIResource;
import javax.swing.tree.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.font.FontRenderContext;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.HashMap;
import java.util.Objects;

public class NBTEdit implements Runnable {
    static NBTEdit editor;
    public static void main(String[] args){
        editor = new NBTEdit();
        editor.run();
    }

    private HashMap<String, Icon> nbtTypeIcons = new HashMap<>();

    JFrame frame;
    private JLabel noFileLabel;
    private JMenu nodeMenu;
    private JMenuItem removeChildOption;
    private JMenuItem addChildOption;
    private JMenuItem setValueOption;

    @Override
    public void run() {

        frame = new JFrame("NBTEdit");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(800,600);

        frame.setIconImage(loadIcon("icons/compound.png").getImage());

        JMenuBar menuBar = new JMenuBar();
        JMenu fileMenu = new JMenu("File");
        JMenu aboutMenu = new JMenu("About");
        nodeMenu = new JMenu("Node");
        nodeMenu.setEnabled(false);
        menuBar.add(fileMenu);
        menuBar.add(aboutMenu);
        menuBar.add(nodeMenu);

        JMenuItem newOption = new JMenuItem("New");
        newOption.setActionCommand("new");
        JMenuItem openOption = new JMenuItem("Open");
        openOption.setActionCommand("open");
        JMenuItem saveOption = new JMenuItem("Save");
        saveOption.setActionCommand("save");
        JMenuItem saveAsOption = new JMenuItem("Save As");
        saveAsOption.setActionCommand("saveAs");
        fileMenu.add(newOption);
        fileMenu.add(openOption);
        fileMenu.add(saveOption);
        fileMenu.add(saveAsOption);

        JMenuItem helpOption = new JMenuItem("Help");
        helpOption.setActionCommand("help");
        JMenuItem infoOption = new JMenuItem("Info");
        infoOption.setActionCommand("info");
        aboutMenu.add(helpOption);
        aboutMenu.add(infoOption);

        JMenuItem setNameOption = new JMenuItem("Name");
        setNameOption.setActionCommand("setName");
        setValueOption = new JMenuItem("Value");
        setValueOption.setActionCommand("setValue");
        addChildOption = new JMenuItem("Add Child");
        addChildOption.setActionCommand("addChild");
        removeChildOption = new JMenuItem("Remove Child");
        removeChildOption.setActionCommand("removeChild");
        nodeMenu.add(setNameOption);
        nodeMenu.add(setValueOption);
        nodeMenu.add(addChildOption);
        nodeMenu.add(removeChildOption);

        addChildOption.setEnabled(false);
        removeChildOption.setEnabled(false);

        MenuItemListener menuItemListener = new MenuItemListener();
        newOption.addActionListener(menuItemListener);
        openOption.addActionListener(menuItemListener);
        saveOption.addActionListener(menuItemListener);
        saveAsOption.addActionListener(menuItemListener);
        helpOption.addActionListener(menuItemListener);
        infoOption.addActionListener(menuItemListener);
        setNameOption.addActionListener(menuItemListener);
        setValueOption.addActionListener(menuItemListener);
        addChildOption.addActionListener(menuItemListener);
        removeChildOption.addActionListener(menuItemListener);


        noFileLabel = new JLabel("No file open");
        noFileLabel.setHorizontalAlignment(SwingConstants.CENTER);


        frame.getContentPane().setLayout(new BorderLayout());

        frame.getContentPane().add(BorderLayout.CENTER, noFileLabel);
        frame.getContentPane().add(BorderLayout.NORTH, menuBar);

        frame.setVisible(true);
    }

    private void openFile() {
        JFileChooser chooser = new JFileChooser(System.getProperty("user.dir"));
        chooser.setFileFilter(new FileNameExtensionFilter("NBT files", "nbt", "dat"));
        int option = chooser.showOpenDialog(frame);

        if(option == JFileChooser.APPROVE_OPTION){
            loadFile(chooser.getSelectedFile().getAbsolutePath());
        }
    }

    private DefaultMutableTreeNode addNodes(NBTNode node, DefaultMutableTreeNode parent) {
        DefaultMutableTreeNode treeNode = new DefaultMutableTreeNode(node);
        parent.add(treeNode);


        if(node instanceof NBTListNode listNode) {
            for(Object child : listNode.getData()){
                if(child instanceof NBTNode nbtNode){
                    addNodes(nbtNode, treeNode);
                } else if(child instanceof Byte byteData) {
                    var valueNode = new NBTValueNode<Byte>();
                    valueNode.setValue(byteData);
                    valueNode.setType("byte");
                    addNodes(valueNode, treeNode);
                } else if(child instanceof Integer intData) {
                    var valueNode = new NBTValueNode<Integer>();
                    valueNode.setValue(intData);
                    valueNode.setType("int");
                    addNodes(valueNode, treeNode);
                } else if(child instanceof Long longData) {
                    var valueNode = new NBTValueNode<Long>();
                    valueNode.setValue(longData);
                    valueNode.setType("long");
                    addNodes(valueNode, treeNode);
                }
            }
        } else if(node instanceof NBTValueNode valueNode) {
            DefaultMutableTreeNode valueTree = new DefaultMutableTreeNode(valueNode.getValue());
            treeNode.add(valueTree);

        }

        return treeNode;

    }

    JTree tree;
    private JScrollPane treeView;

    private void loadFile(String path){
        System.out.println("Opening " + path);

        currentFile = path;
        frame.setTitle("NBTEdit - " + currentFile);

        try {
            FileInputStream stream = new FileInputStream(path);

            NBTReader reader = new NBTReader(stream);
            reader.parse();

            frame.remove(noFileLabel);

            rebuildNodeTree(reader.getRoot());

            System.out.println("Loaded!");

        } catch (Throwable e) {
            e.printStackTrace();
        }

    }
    private NBTNode currentRoot;
    private void rebuildNodeTree(NBTNode root) {
        currentRoot = root;
        System.out.println("Building tree");
        DefaultMutableTreeNode rootNode = new DefaultMutableTreeNode(root);

        if(root instanceof NBTListNode listNode) {
            for(Object child : listNode.getData()){
                if(child instanceof NBTNode nbtNode){
                    addNodes(nbtNode, rootNode);
                } if(child instanceof Byte byteData) {
                    var valueNode = new NBTValueNode<Byte>();
                    valueNode.setValue(byteData);
                    valueNode.setType("byte");
                    addNodes(valueNode, rootNode);
                }
            }
        } else if(root instanceof NBTValueNode valueNode) {
            DefaultMutableTreeNode valueTree = new DefaultMutableTreeNode(valueNode.getValue());
            rootNode.add(valueTree);

        }



        TreeCellRenderer renderer = new NBTTreeRenderer();

        tree = new JTree(rootNode);
        tree.setRootVisible(true);
        tree.setCellRenderer(renderer);
        tree.setLayout(null);
        tree.expandPath(new TreePath(rootNode));

        if(treeView != null)
            frame.getContentPane().remove(treeView);

        treeView = new JScrollPane(tree);

        treeView.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        frame.getContentPane().add(treeView);

        frame.revalidate();
        frame.repaint();
    }

    private String currentFile = null;
    private void saveFile() {

        if(currentFile == null) {
            saveFileAs();
            return;
        }

        System.out.println("Saving to " + currentFile);

        NBTWriter writer = null;
        try {
            writer = new NBTWriter(new FileOutputStream(currentFile));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        saveNode(currentRoot, writer);

        try {
            writer.flush();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void saveFileAs() {
        try {
        JFileChooser chooser = new JFileChooser(System.getProperty("user.dir"));
        chooser.setFileFilter(new FileNameExtensionFilter("NBT files", "nbt", "dat"));
        int option = chooser.showSaveDialog(frame);

        if(option == JFileChooser.APPROVE_OPTION){

            String path = chooser.getSelectedFile().getAbsolutePath();
            System.out.println("Saving to " + path);

            NBTWriter writer = new NBTWriter(new FileOutputStream(path));

            saveNode(currentRoot, writer);

            writer.flush();

            currentFile = path;
            frame.setTitle("NBTEdit - " + currentFile);

        }

        } catch (Throwable e) {
            throw new RuntimeException(e);
        }

    }

    private void writeValue(NBTValueNode valueNode, NBTWriter writer) {
        switch (valueNode.getType()) {
            case "byte":
                writer.writeByte(valueNode.getName(), (byte)valueNode.getValue());
                break;
            case "double":
                writer.writeDouble(valueNode.getName(), (double)valueNode.getValue());
                break;
            case "float":
                writer.writeFloat(valueNode.getName(), (float)valueNode.getValue());
                break;
            case "int":
                writer.writeInt(valueNode.getName(), (int)valueNode.getValue());
                break;
            case "long":
                writer.writeLong(valueNode.getName(), (long)valueNode.getValue());
                break;
            case "short":
                writer.writeShort(valueNode.getName(), (short)valueNode.getValue());
                break;
            case "string":
                writer.writeString(valueNode.getName(), (String)valueNode.getValue());
                break;
            case "byte[]":
                writer.writeBytes(valueNode.getName(), (byte[])valueNode.getValue());
                break;
            case "int[]":
                writer.writeInts(valueNode.getName(), (int[])valueNode.getValue());
                break;
            case "long[]":
                writer.writeLongs(valueNode.getName(), (long[])valueNode.getValue());
                break;
        }
    }

    private void saveNode(NBTNode node, NBTWriter writer){
        if(node instanceof NBTCompoundNode compoundNode) {
            writer.beginCompound(node.getName());

            for(int i = 0; i < compoundNode.getLength(); i++){
                saveNode(compoundNode.get(i), writer);
            }

            writer.endCompound();

        } else if(node instanceof NBTListNode<?> listNode){


            if(node.getType().endsWith("[]")){

                switch (node.getType()){
                    case "byte[]":
                        byte[] bytes = new byte[listNode.getLength()];

                        for(int i = 0; i < listNode.getLength(); i++){
                            bytes[i] = (byte)listNode.get(i);
                        }
                        writer.writeBytes(node.getName(), bytes);
                        break;
                    case "int[]":
                        int[] ints = new int[listNode.getLength()];

                        for(int i = 0; i < listNode.getLength(); i++){
                            ints[i] = (int)listNode.get(i);
                        }
                        writer.writeInts(node.getName(), ints);
                        break;
                    case "long[]":
                        long[] longs = new long[listNode.getLength()];

                        for(int i = 0; i < listNode.getLength(); i++){
                            longs[i] = (byte)listNode.get(i);
                        }
                        writer.writeLongs(node.getName(), longs);
                        break;
                }

                return;
            }


            if(listNode.getContentType() == null)
                return;

            writer.beginList(node.getName(), listNode.getContentType());

            for(int i = 0; i < listNode.getLength(); i++){
                if(listNode.get(i) instanceof NBTNode nbtNode)
                    saveNode(nbtNode, writer);
                else  {
                    NBTValueNode valueNode =  new NBTValueNode();
                    valueNode.setName("");
                    valueNode.setValue(listNode.get(i));
                    valueNode.setType(listNode.getContentType());

                    writeValue(valueNode, writer);
                }

            }

            writer.endList();

        } else if(node instanceof NBTValueNode valueNode){
            writeValue(valueNode, writer);
        }
    }


    public final static ImageIcon compoundIcon = loadIcon("icons/compound.png");
    public final static ImageIcon byteIcon = loadIcon("icons/byte.png");
    public final static ImageIcon doubleIcon = loadIcon("icons/double.png");
    public final static ImageIcon floatIcon = loadIcon("icons/float.png");
    public final static ImageIcon intIcon = loadIcon("icons/int.png");
    public final static ImageIcon longIcon = loadIcon("icons/long.png");
    public final static ImageIcon shortIcon = loadIcon("icons/short.png");
    public final static ImageIcon stringIcon = loadIcon("icons/string.png");
    public final static ImageIcon listIcon = loadIcon("icons/list.png");
    public final static ImageIcon arrayIcon = loadIcon("icons/array.png");
    public final static ImageIcon valueIcon = loadIcon("icons/value.png");

    private static ImageIcon loadIcon(String path){
        URL url = ClassLoader.getSystemClassLoader().getResource(path);

        if(url != null)
            return new ImageIcon(url);

        throw new RuntimeException("Invalid icon path " + path);
    }


    static class NBTTreeRenderer extends DefaultTreeCellRenderer {

        private Font displayFont;

        public NBTTreeRenderer() {


            try {

                InputStream fontStream = ClassLoader.getSystemClassLoader().getResourceAsStream("fonts/opensans.ttf");
                assert fontStream != null;
                displayFont = Font.createFont(Font.TRUETYPE_FONT, fontStream);
                displayFont = displayFont.deriveFont(Font.PLAIN, 16);


            } catch (FontFormatException | IOException e) {
                throw new RuntimeException(e);
            }


        }



        public Component getTreeCellRendererComponent( JTree tree, Object value, boolean bSelected, boolean bExpanded, boolean bLeaf, int iRow, boolean bHasFocus ) {
            DefaultMutableTreeNode node = (DefaultMutableTreeNode)value;

            if(!(node.getUserObject() instanceof NBTNode)) {
                setText(node.getUserObject().toString());
                setIcon(valueIcon);
                setFont(displayFont);
                selected = false;
                setBorder(null);

                if(bSelected) {
                    editor.nodeMenu.setEnabled(false);
                    editor.addChildOption.setEnabled(false);
                    editor.removeChildOption.setEnabled(false);
                }

                return this;
            }


            NBTNode nbtNode = (NBTNode)node.getUserObject();


            String label = nbtNode.getName();
            setFont(displayFont);

            if(nbtNode instanceof NBTCompoundNode) {
                setIcon(compoundIcon);
            } else if(nbtNode instanceof NBTListNode<?>){
                setIcon(listIcon);

                if(nbtNode.getType().endsWith("[]")){
                    setIcon(arrayIcon);
                }

            } else if(nbtNode instanceof NBTValueNode valueNode){

                if(nbtNode.getName() == null || nbtNode.getName().isEmpty()){
                    label = valueNode.getValue().toString();
                    node.removeAllChildren();
                }

                switch (valueNode.getType()) {
                    case "byte":
                        setIcon(byteIcon);
                        break;
                    case "double":
                        setIcon(doubleIcon);
                        break;
                    case "float":
                        setIcon(floatIcon);
                        break;
                    case "int":
                        setIcon(intIcon);
                        break;
                    case "long":
                        setIcon(longIcon);
                        break;
                    case "short":
                        setIcon(shortIcon);
                        break;
                    case "string":
                        setIcon(stringIcon);
                        break;
                    default:
                        setIcon(super.leafIcon);
                        break;
                }

            } else {
                setIcon(super.leafIcon);
            }

            if(bSelected){

                TreePath path = tree.getSelectionPath();

                setBorder(BorderFactory.createLineBorder(Color.GRAY, 1));
                editor.nodeMenu.setEnabled(true);

                editor.currentNode = nbtNode;
                editor.currentSelection = node;

                if(nbtNode.getType().endsWith("[]")) {
                    editor.addChildOption.setEnabled(false);
                    editor.setValueOption.setEnabled(false);
                } else if(nbtNode instanceof NBTListNode<?>) {
                    editor.addChildOption.setEnabled(true);
                    editor.setValueOption.setEnabled(false);
                } else {
                    editor.addChildOption.setEnabled(false);
                    editor.setValueOption.setEnabled(true);
                }

                DefaultMutableTreeNode parent = (DefaultMutableTreeNode)node.getParent();
                if(parent != null && parent.getUserObject() instanceof NBTNode parentNode) {
                    editor.removeChildOption.setEnabled(parentNode.getType().equals("list") || parentNode.getType().equals("compound"));
                } else {
                    editor.removeChildOption.setEnabled(false);
                }

                editor.frame.revalidate();
                editor.frame.repaint();

            } else {
                setBorder(null);
            }

            setToolTipText(nbtNode.getType());
            setText(label);

            return this;
        }
    }

    private NBTNode currentNode;
    private DefaultMutableTreeNode currentSelection;

    private NBTNode queryNode() {

        String finalType = queryType();

        if(finalType == null)
            return null;

        System.out.println("Creating " + finalType + " node");

        String name = JOptionPane.showInputDialog("Name");

        if(!finalType.endsWith("[]") && !finalType.equals("compound") && !finalType.equals("list")){
            NBTValueNode valueNode = new NBTValueNode();
            valueNode.setName(name);
            valueNode.setType(finalType);
            setValue(valueNode);

            return valueNode;
        } else if(finalType.equals("compound")) {

            NBTCompoundNode node = new NBTCompoundNode();
            node.setType(finalType);
            node.setName(name);

            return node;

        } else if(finalType.equals("list")) {

            NBTListNode listNode = new NBTListNode();
            listNode.setName(name);
            listNode.setType(finalType);
            listNode.setContentType(queryType());

            return listNode;

        }

        return null;
    }

    private String queryType() {

        String types[] = {
                "byte",
                "double",
                "int",
                "float",
                "long",
                "short",
                "string",
                "compound",
                "array",
                "list"
        };

        String finalType = "";

        String selection = (String) JOptionPane.showInputDialog(frame, "Choose type", "Choose type", JOptionPane.QUESTION_MESSAGE, null, types, types[0]);

        if(Objects.equals(selection, "array")) {

            String[] arrayTypes = {
                    "byte",
                    "int",
                    "long",
            };

            String arrayType = (String)JOptionPane.showInputDialog(frame, "Choose array type", "Choose type", JOptionPane.QUESTION_MESSAGE, null, arrayTypes, arrayTypes[0]);
            finalType = arrayType + "[]";
        } else {
            finalType = selection;
        }

        return finalType;
    }

    private void setValue() {
        NBTValueNode valueNode = (NBTValueNode)currentNode;
        setValue(valueNode);

        if(currentSelection.getChildCount() > 0)
            ((DefaultMutableTreeNode)currentSelection.getChildAt(0)).setUserObject(valueNode.getValue());

        editor.frame.revalidate();
        editor.frame.repaint();
    }

    private void setValue(NBTValueNode valueNode) {
        String stringValue = JOptionPane.showInputDialog("Enter value", valueNode.getValue() != null ? valueNode.getValue() : "");

        if(stringValue == null || stringValue.isEmpty() || stringValue.isBlank())
            return;

        switch (valueNode.getType()) {
            case "byte":
                ((NBTValueNode<Byte>)valueNode).setValue(Byte.parseByte(stringValue));
                break;
            case "double":
                ((NBTValueNode<Double>)valueNode).setValue(Double.parseDouble(stringValue));
                break;
            case "float":
                ((NBTValueNode<Float>)valueNode).setValue(Float.parseFloat(stringValue));
                break;
            case "int":
                ((NBTValueNode<Integer>)valueNode).setValue(Integer.parseInt(stringValue));
                break;
            case "long":
                ((NBTValueNode<Long>)valueNode).setValue(Long.parseLong(stringValue));
                break;
            case "short":
                ((NBTValueNode<Short>)valueNode).setValue(Short.parseShort(stringValue));
                break;
            case "string":
                ((NBTValueNode<String>)valueNode).setValue(stringValue);
                break;
        }
    }

    private void setName() {
        String name = JOptionPane.showInputDialog("Enter name", currentNode.getName());

        if(name == null || name.isEmpty() || name.isBlank())
            return;

        currentNode.setName(name);

    }

    private void addChild() {

        if(currentNode instanceof NBTCompoundNode compoundNode) {
            NBTNode newNode = queryNode();

            System.out.println("Adding child");

            compoundNode.add(newNode);

            assert newNode != null;
            addNodes(newNode, currentSelection);

            rebuildNodeTree(currentRoot);


        } else if(currentNode instanceof NBTListNode listNode){

            NBTValueNode node = new NBTValueNode();
            node.setType(listNode.getContentType());

            setValue(node);

            listNode.add(node);

            assert node != null;
            addNodes(node, currentSelection);

            rebuildNodeTree(currentRoot);

        }


    }

    private void removeChild() {
        DefaultMutableTreeNode parent = (DefaultMutableTreeNode)currentSelection.getParent();
        NBTListNode parentNode = (NBTListNode) parent.getUserObject();

        parentNode.getData().remove(currentNode);

        rebuildNodeTree(currentRoot);

    }

    private void newFile() {
        currentFile = null;
        frame.setTitle("NBTEdit");

        rebuildNodeTree(Objects.requireNonNull(queryNode()));

        noFileLabel.setEnabled(false);
    }

    static class MenuItemListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            System.out.println("ACTION: " + e.getActionCommand());
            switch (e.getActionCommand()) {
                case "open" -> editor.openFile();
                case "save" -> editor.saveFile();
                case "saveAs" -> editor.saveFileAs();
                case "help" -> new HelpWindow();
                case "setValue" -> editor.setValue();
                case "setName" -> editor.setName();
                case "addChild" -> editor.addChild();
                case "removeChild" -> editor.removeChild();
                case "new" -> editor.newFile();
                case "info" -> JOptionPane.showMessageDialog(editor.frame, "NBTEditor by khhs\nVersion 1.0\nLicensed under the MIT License v2\nCopyright(c) khhs 2022", "NBTEditor Info", JOptionPane.INFORMATION_MESSAGE);
            }
        }
    }
}
