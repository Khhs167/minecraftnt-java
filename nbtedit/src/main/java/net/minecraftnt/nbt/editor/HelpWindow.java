package net.minecraftnt.nbt.editor;

import javax.swing.*;
import java.awt.*;

public class HelpWindow {


    public HelpWindow() {

        JDialog window = new JDialog(NBTEdit.editor.frame, "NBTEdit help");
        window.setSize(300, 500);
        window.setResizable(false);
        window.setVisible(true);
        window.getContentPane().setLayout(new GridLayout(13, 1));
        window.add(new JLabel("NBTEdit - A simple NBT editor"));
        window.add(new JLabel("Node Types:"));
        window.add(new JLabel("- Array, fixed size primitive list.", NBTEdit.arrayIcon, SwingConstants.LEFT));
        window.add(new JLabel("- Byte, signed 8 bit integer.", NBTEdit.byteIcon, SwingConstants.LEFT));
        window.add(new JLabel("- Compound, contains other nodes", NBTEdit.compoundIcon, SwingConstants.LEFT));
        window.add(new JLabel("- Double, double precision decimal.", NBTEdit.doubleIcon, SwingConstants.LEFT));
        window.add(new JLabel("- Float, floating point decimal", NBTEdit.floatIcon, SwingConstants.LEFT));
        window.add(new JLabel("- Int, signed 32 bit integer.", NBTEdit.intIcon, SwingConstants.LEFT));
        window.add(new JLabel("- List, variable size container for single type.", NBTEdit.listIcon, SwingConstants.LEFT));
        window.add(new JLabel("- Long, signed 64 bit integer.", NBTEdit.longIcon, SwingConstants.LEFT));
        window.add(new JLabel("- Short, signed 16 bit integer.", NBTEdit.shortIcon, SwingConstants.LEFT));
        window.add(new JLabel("- String, set of characters.", NBTEdit.stringIcon, SwingConstants.LEFT));
        window.add(new JLabel("- Value, the value of a node.", NBTEdit.valueIcon, SwingConstants.LEFT));
    }

}
