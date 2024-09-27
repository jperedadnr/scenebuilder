/*
 * Copyright (c) 2019, 2024 Gluon and/or its affiliates.
 * All rights reserved. Use is subject to license terms.
 *
 * This file is available and licensed under the following license:
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 *  - Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 *  - Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in
 *    the documentation and/or other materials provided with the distribution.
 *  - Neither the name of Gluon nor the names of its
 *    contributors may be used to endorse or promote products derived
 *    from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
 * LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR
 * A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT
 * OWNER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
 * DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
 * THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package com.gluonhq.scenebuilder.components;

import com.gluonhq.charm.glisten.control.BottomNavigation;
import com.gluonhq.charm.glisten.control.DropdownButton;
import com.gluonhq.charm.glisten.control.ExpansionPanel;
import com.gluonhq.charm.glisten.control.ToggleButtonGroup;
import com.gluonhq.scenebuilder.components.hierarchy.HierarchyItemExpandedPanel;
import com.gluonhq.scenebuilder.components.hierarchy.HierarchyItemExpansionPanel;
import com.oracle.javafx.scenebuilder.kit.editor.panel.hierarchy.HierarchyItem;
import com.oracle.javafx.scenebuilder.kit.fxom.FXOMObject;
import com.oracle.javafx.scenebuilder.kit.metadata.util.DesignHierarchyMask;
import com.oracle.javafx.scenebuilder.kit.metadata.util.ExternalDesignHierarchyMaskProvider;
import com.oracle.javafx.scenebuilder.kit.metadata.util.PropertyName;
import com.oracle.javafx.scenebuilder.kit.metadata.util.access.Accessory;
import com.oracle.javafx.scenebuilder.kit.metadata.util.access.DefaultAccessories;
import javafx.scene.control.TreeItem;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GluonDesignHierarchyMaskProvider implements ExternalDesignHierarchyMaskProvider {

    public enum AccessoryEx implements ExternalDesignHierarchyMaskProvider.Accessible {
        // ExpansionPanel
        EXPANDED_CONTENT,
        COLLAPSED_CONTENT,
        // ExpandedPanel
        EX_CONTENT {
            @Override
            public String toString() { return "CONTENT"; }
        }
    }

    private static final PropertyName contentName = new PropertyName("content");
    // ExpansionPanel
    private static final PropertyName expandedContentName = new PropertyName("expandedContent");
    private static final PropertyName collapsedContentName = new PropertyName("collapsedContent");

    private final Map<FXOMObject, Boolean> treeItemsExpandedMapProperty = new HashMap<>();
    private DesignHierarchyMask designHierarchyMask;

    @Override
    public List<Class<?>> getResizableItems() {
        return Arrays.asList(
                ExpansionPanel.ExpandedPanel.class,
                DropdownButton.class,
                BottomNavigation.class,
                ExpansionPanel.CollapsedPanel.class,
                ToggleButtonGroup.class);
    }

    @Override
    public void setDesignHierarchyMask(DesignHierarchyMask designHierarchyMask) {
        this.designHierarchyMask = designHierarchyMask;
    }

    @Override
    public boolean isAcceptingAccessory(Object sceneGraphObject, Accessible accessory) {
        if (accessory instanceof Accessory) {
            switch (DefaultAccessories.byName(((Accessory) accessory).getName()).getName()) {
                case "GRAPHIC":
                    if (sceneGraphObject instanceof ExpansionPanel.ExpandedPanel) {
                        return false;
                    }
                    break;
                case "DP_GRAPHIC":
                    if (sceneGraphObject instanceof ExpansionPanel.ExpandedPanel) {
                        return false;
                    }
                    break;
                case "EXPANDABLE_CONTENT":
                    if (!(sceneGraphObject instanceof ExpansionPanel)) {
                        return false;
                    }
                    break;
                case "EXTERNAL":
                    switch ((AccessoryEx) accessory) {
                        case COLLAPSED_CONTENT:
                            if (!(sceneGraphObject instanceof ExpansionPanel)) {
                                return false;
                            }
                            break;
                        case EX_CONTENT:
                            if (!(sceneGraphObject instanceof ExpansionPanel.ExpandedPanel)) {
                                return false;
                            }
                            break;
                    }
                    break;
            }
        }
        return true;
    }

    @Override
    public Class<?> getClassForAccessory(Accessible accessory) {
        // TODO:
//        switch (accessoryEx) {
//            case EX_CONTENT:
//            case EXPANDED_CONTENT:
//            case COLLAPSED_CONTENT:
                return javafx.scene.Node.class;
//            }
//        return null;
    }

    public PropertyName getPropertyNameForAccessory(AccessoryEx accessory) {
        final PropertyName result;

        switch (accessory) {
            case EX_CONTENT:
                result = contentName;
                break;
            case EXPANDED_CONTENT:
                result = expandedContentName;
                break;
            case COLLAPSED_CONTENT:
                result = collapsedContentName;
                break;
            default: result = null;
            }
            return result;
        }

     public Map<Accessory, FXOMObject> checkExternalAccesories() {
        Map<Accessory, FXOMObject> map = new HashMap();
        // Gluon ExpansionPanel
        for (AccessoryEx accessory : new AccessoryEx[]{
                AccessoryEx.EXPANDED_CONTENT,
                AccessoryEx.COLLAPSED_CONTENT
        }) {
//            if (isAcceptingAccessory(accessory)) {
//                final FXOMObject value = designHierarchyMask.getAccessory(accessory);
//                map.put(accessory, value);
////                treeItem.getChildren().add(makeTreeItemExpansionPanel(mask, value, accessory));
//            }
        }

//        // Gluon ExpandedPanel
//        if (mask.isAcceptingAccessory(Accessory.EX_CONTENT)) {
//            final FXOMObject value = designHierarchyMask.getAccessory(AccessoryEx.EX_CONTENT);
//            map.put(AccessoryEx.EX_CONTENT, value);
////            treeItem.getChildren().add(makeTreeItemExpandedPanel(mask, value));
//        }
        return map;
    }


    private TreeItem<HierarchyItem> makeTreeItemExpansionPanel(
            final DesignHierarchyMask owner,
            final FXOMObject fxomObject,
            final Accessory accessory) {
        final HierarchyItemExpansionPanel item = new HierarchyItemExpansionPanel(owner, fxomObject, accessory);
        final TreeItem<HierarchyItem> treeItem = new TreeItem<>(item);
        // Set back the TreeItem expanded property if any
        Boolean expanded = treeItemsExpandedMapProperty.get(fxomObject);
        if (expanded != null) {
            treeItem.setExpanded(expanded);
        }
        // Mask may be null for empty place holder
        if (item.getMask() != null) {
//            updateTreeItem(treeItem);
        }
        return treeItem;
    }

    private TreeItem<HierarchyItem> makeTreeItemExpandedPanel(final DesignHierarchyMask owner, final FXOMObject fxomObject) {
        final HierarchyItemExpandedPanel item = new HierarchyItemExpandedPanel(owner, fxomObject);
        final TreeItem<HierarchyItem> treeItem = new TreeItem<>(item);
        // Set back the TreeItem expanded property if any
        Boolean expanded = treeItemsExpandedMapProperty.get(fxomObject);
        if (expanded != null) {
            treeItem.setExpanded(expanded);
        }
        // Mask may be null for empty place holder
        if (item.getMask() != null) {
//            updateTreeItem(treeItem);
        }
        return treeItem;
    }
}
