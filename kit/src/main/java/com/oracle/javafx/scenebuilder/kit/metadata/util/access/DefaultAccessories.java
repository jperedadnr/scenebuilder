package com.oracle.javafx.scenebuilder.kit.metadata.util.access;

import javafx.scene.control.DialogPane;

import java.util.Arrays;
import java.util.List;

public class DefaultAccessories {

    public static List<Accessory> ACCESSORIES = Arrays.asList(
            new Accessory("PLACEHOLDER", javafx.scene.Node.class, "placeholder"),
            new Accessory("TOOLTIP", javafx.scene.control.Tooltip.class, "tooltip"),
            new Accessory("CONTEXT_MENU", javafx.scene.control.ContextMenu.class, "contextMenu"),
            new Accessory("CLIP", javafx.scene.Node.class, "clip"),
            new Accessory("GRAPHIC", javafx.scene.Node.class, "graphic"),
            new Accessory("CONTENT", javafx.scene.Node.class, "content"),
            new Accessory("ROOT", javafx.scene.Node.class, "root"),
            new Accessory("SCENE", javafx.scene.Scene.class, "scene"),
            new Accessory("TOP", javafx.scene.Node.class, "top"),
            new Accessory("BOTTOM", javafx.scene.Node.class, "bottom"),
            new Accessory("LEFT", javafx.scene.Node.class, "left"),
            new Accessory("RIGHT", javafx.scene.Node.class, "right"),
            new Accessory("CENTER", javafx.scene.Node.class, "center"),
            new Accessory("XAXIS", javafx.scene.chart.Axis.class, "xAxis"),
            new Accessory("YAXIS", javafx.scene.chart.Axis.class, "yAxis"),
            new Accessory("TREE_COLUMN", javafx.scene.control.TreeTableColumn.class, "treeColumn"),
            new Accessory("EXPANDABLE_CONTENT", javafx.scene.Node.class, "expandableContent"),
            new Accessory("HEADER", javafx.scene.Node.class, "header"),
            new Accessory("DP_GRAPHIC", javafx.scene.Node.class, "graphic") {
                @Override
                public String toString() {
                    return "GRAPHIC"; // NOI18N
                }
            },
            new Accessory("DP_CONTENT", javafx.scene.Node.class, "content") {
                @Override
                public String toString() {
                    return "CONTENT"; // NOI18N
                }
            }
        );

    public static Accessory byName(String name) {
        for (Accessory a : ACCESSORIES) {
            if (name.equals(a.getName())) {
                return a;
            }
        }
        return null;
    }

    public static boolean isAcceptingAccessory(Accessory accessory, Object sceneGraphObject) {
        String name = accessory.getName();
        if ("CONTENT".equals(name) || "GRAPHIC".equals(name)) {
            return !(sceneGraphObject instanceof DialogPane);
        } else if ("DP_CONTENT".equals(name) || "DP_GRAPHIC".equals(name)) {
            return sceneGraphObject instanceof DialogPane;
        }
        return true;
    }
}
