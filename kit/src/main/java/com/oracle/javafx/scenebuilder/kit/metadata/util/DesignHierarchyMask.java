/*
 * Copyright (c) 2016, 2019, Gluon and/or its affiliates.
 * Copyright (c) 2012, 2014, Oracle and/or its affiliates.
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
 *  - Neither the name of Oracle Corporation nor the names of its
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
package com.oracle.javafx.scenebuilder.kit.metadata.util;

import com.oracle.javafx.scenebuilder.kit.editor.images.ImageUtils;
import com.oracle.javafx.scenebuilder.kit.fxom.FXOMCollection;
import com.oracle.javafx.scenebuilder.kit.fxom.FXOMInstance;
import com.oracle.javafx.scenebuilder.kit.fxom.FXOMIntrinsic;
import com.oracle.javafx.scenebuilder.kit.fxom.FXOMObject;
import com.oracle.javafx.scenebuilder.kit.fxom.FXOMProperty;
import com.oracle.javafx.scenebuilder.kit.fxom.FXOMPropertyC;
import com.oracle.javafx.scenebuilder.kit.metadata.Metadata;
import com.oracle.javafx.scenebuilder.kit.metadata.klass.ComponentClassMetadata;
import com.oracle.javafx.scenebuilder.kit.metadata.property.ComponentPropertyMetadata;
import com.oracle.javafx.scenebuilder.kit.metadata.property.ValuePropertyMetadata;
import com.oracle.javafx.scenebuilder.kit.metadata.util.access.Accessory;
import com.oracle.javafx.scenebuilder.kit.metadata.util.access.DefaultAccessories;
import com.oracle.javafx.scenebuilder.kit.util.Deprecation;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ServiceLoader;

import javafx.geometry.Orientation;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Labeled;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuButton;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ScrollBar;
import javafx.scene.control.Separator;
import javafx.scene.control.Slider;
import javafx.scene.control.SplitPane;
import javafx.scene.control.Tab;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TextInputControl;
import javafx.scene.control.TitledPane;
import javafx.scene.control.ToolBar;
import javafx.scene.control.Tooltip;
import javafx.scene.control.TreeTableColumn;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.text.Text;
import javafx.stage.Stage;

/**
 *
 */
public class DesignHierarchyMask {

    private final FXOMObject fxomObject;
    private Map<PropertyName, ComponentPropertyMetadata> propertyMetadataMap; // Initialized lazily
    private final Collection<ExternalDesignHierarchyMaskProvider> providers;

    public DesignHierarchyMask(FXOMObject fxomObject) {
        assert fxomObject != null;
        this.fxomObject = fxomObject;
        this.providers = getExternalDesignHierarchyMaskProviders();
    }

    public FXOMObject getFxomObject() {
        return fxomObject;
    }

    public FXOMObject getParentFXOMObject() {
        return fxomObject.getParentObject();
    }

    public boolean isFxNode() {
        return fxomObject.getSceneGraphObject() instanceof Node;
    }

    public FXOMObject getClosestFxNode() {
        FXOMObject result = fxomObject;
        DesignHierarchyMask mask = this;

        while ((result != null) && (mask.isFxNode() == false)) {
            result = mask.getParentFXOMObject();
            mask = (result == null) ? null : new DesignHierarchyMask(result);
        }

        return result;
    }

    public URL getClassNameIconURL() {
        final Object sceneGraphObject;
        
        // For FXOMIntrinsic, we use the source sceneGraphObject
        if (fxomObject instanceof FXOMIntrinsic) {
            sceneGraphObject = ((FXOMIntrinsic) fxomObject).getSourceSceneGraphObject();
        } else {
            sceneGraphObject = fxomObject.getSceneGraphObject();
        }

        if (sceneGraphObject == null) {
            // For now, handle icons for scenegraph objects only
            return null;
        }
        final URL url;
        if (sceneGraphObject instanceof Separator) {
            // Separator orientation
            final Separator obj = (Separator) sceneGraphObject;
            if (Orientation.HORIZONTAL.equals(obj.getOrientation())) {
                url = ImageUtils.getNodeIconURL("Separator-h.png"); //NOI18N
            } else {
                url = ImageUtils.getNodeIconURL("Separator-v.png"); //NOI18N
            }
        } else if (sceneGraphObject instanceof ScrollBar) {
            // ScrollBar orientation
            final ScrollBar obj = (ScrollBar) sceneGraphObject;
            if (Orientation.HORIZONTAL.equals(obj.getOrientation())) {
                url = ImageUtils.getNodeIconURL("ScrollBar-h.png"); //NOI18N
            } else {
                url = ImageUtils.getNodeIconURL("ScrollBar-v.png"); //NOI18N
            }
        } else if (sceneGraphObject instanceof Slider) {
            // Slider orientation
            final Slider obj = (Slider) sceneGraphObject;
            if (Orientation.HORIZONTAL.equals(obj.getOrientation())) {
                url = ImageUtils.getNodeIconURL("Slider-h.png"); //NOI18N
            } else {
                url = ImageUtils.getNodeIconURL("Slider-v.png"); //NOI18N
            }
        } else if (sceneGraphObject instanceof SplitPane) {
            // SplitPane orientation
            final SplitPane obj = (SplitPane) sceneGraphObject;
            if (Orientation.HORIZONTAL.equals(obj.getOrientation())) {
                url = ImageUtils.getNodeIconURL("SplitPane-h.png"); //NOI18N
            } else {
                url = ImageUtils.getNodeIconURL("SplitPane-v.png"); //NOI18N
            }
        } else {
            // Default
            String fileName = sceneGraphObject.getClass().getSimpleName();
            url = ImageUtils.getNodeIconURL(fileName + ".png"); //NOI18N
        }
        return url;
    }

    public Image getClassNameIcon() {
        final URL resource = getClassNameIconURL();
        return ImageUtils.getImage(resource);
    }

    public String getClassNameInfo() {
        final Object sceneGraphObject;
        final String classNameInfo;
        String prefix = "", suffix = ""; //NOI18N

        // For FXOMIntrinsic, we use the source sceneGraphObject
        if (fxomObject instanceof FXOMIntrinsic) {
            final FXOMIntrinsic fxomIntrinsic = (FXOMIntrinsic) fxomObject;
            sceneGraphObject = fxomIntrinsic.getSourceSceneGraphObject();
            if (fxomIntrinsic.getType() == FXOMIntrinsic.Type.FX_INCLUDE) {
                // Add FXML prefix for included FXML file
                prefix += "FXML "; //NOI18N
            }
        } else {
            sceneGraphObject = fxomObject.getSceneGraphObject();
        }

        if (sceneGraphObject == null) {
            classNameInfo = prefix + fxomObject.getGlueElement().getTagName() + suffix;
        } else {
            if (sceneGraphObject instanceof Node) {
                final Node node = (Node) sceneGraphObject;

                // GridPane : add num rows x num columns
                if (node instanceof GridPane) {
                    int columnsSize = getColumnsSize();
                    int rowsSize = getRowsSize();
                    suffix += " (" + columnsSize + //NOI18N
                            " x " + rowsSize + ")"; //NOI18N
                }

                // GridPane children : add child positionning within the GridPane
                final FXOMObject parentFxomObject = fxomObject.getParentObject();
                if (parentFxomObject != null) {
                    final Object parentSceneGraphObject = parentFxomObject.getSceneGraphObject();
                    if (parentSceneGraphObject instanceof GridPane) {
                        int columnIndex = getColumnIndex();
                        int rowIndex = getRowIndex();
                        suffix += " (" + columnIndex + ", " + rowIndex + ")"; //NOI18N
                    }
                }
            }
            classNameInfo = prefix + sceneGraphObject.getClass().getSimpleName() + suffix;
        }

        return classNameInfo;
    }

    /**
     * Returns the string value for this FXOM object description property.
     * If the value is internationalized, the returned value is the resolved one.
     *
     * @return
     */
    public String getDescription() {
        if (hasDescription()) { // (1)
            final PropertyName propertyName = getPropertyNameForDescription();
            assert propertyName != null; // Because of (1)
            assert fxomObject instanceof FXOMInstance; // Because of (1)
            final FXOMInstance fxomInstance = (FXOMInstance) fxomObject;
            final ValuePropertyMetadata vpm
                    = Metadata.getMetadata().queryValueProperty(fxomInstance, propertyName);
            final Object description = vpm.getValueInSceneGraphObject(fxomInstance); // resolved value
            return description == null ? null : description.toString();
        }
        return null;
    }

    /**
     * Returns a single line description for this FXOM object.
     *
     * @return
     */
    public String getSingleLineDescription() {
        String result = getDescription();
        if (result != null && containsLineFeed(result)) {
            result = result.substring(0, result.indexOf('\n')) + "..."; //NOI18N
        }
        return result;
    }

    /**
     * Returns the object value for this FXOM object node id property.
     *
     * @return
     */
    public Object getNodeIdValue() {
        Object result = null;
        if (fxomObject instanceof FXOMInstance) {
            final FXOMInstance fxomInstance = (FXOMInstance) fxomObject;
            final PropertyName propertyName = new PropertyName("id"); //NOI18N
            final ValuePropertyMetadata vpm
                    = Metadata.getMetadata().queryValueProperty(fxomInstance, propertyName);
            result = vpm.getValueObject(fxomInstance);
        }
        return result;
    }

    /**
     * Returns the string value for this FXOM object node id property.
     *
     * @return
     */
    public String getNodeId() {
        final Object value = getNodeIdValue();
        String result = null;
        if (value != null) {
            result = value.toString();
        }
        return result;
    }

    public String getFxId() {
        String result = null;
        if (fxomObject instanceof FXOMInstance) { // Can be null for place holder items
            final FXOMInstance fxomInstance = (FXOMInstance) fxomObject;
            final String fxId = fxomInstance.getFxId();
            result = fxId == null ? "" : fxId; //NOI18N
        }
        return result;
    }

    public boolean hasDescription() {
        final Object sceneGraphObject = fxomObject.getSceneGraphObject();
        if (sceneGraphObject == null) {
            // For now, handle display label for scenegraph objects only
            return false;
        }
        return sceneGraphObject instanceof ComboBox
                || sceneGraphObject instanceof Labeled
                || sceneGraphObject instanceof Menu
                || sceneGraphObject instanceof MenuItem
                || sceneGraphObject instanceof Tab
                || sceneGraphObject instanceof TableColumn
                || sceneGraphObject instanceof Text
                || sceneGraphObject instanceof TextInputControl
                || sceneGraphObject instanceof TitledPane
                || sceneGraphObject instanceof Tooltip
                || sceneGraphObject instanceof TreeTableColumn
                || sceneGraphObject instanceof Stage;
    }
    
    public boolean isResourceKey() {
        if (hasDescription()) { // (1)
            // Retrieve the unresolved description
            final PropertyName propertyName = getPropertyNameForDescription();
            assert propertyName != null; // Because of (1)
            assert fxomObject instanceof FXOMInstance; // Because of (1)
            final FXOMInstance fxomInstance = (FXOMInstance) fxomObject;
            final ValuePropertyMetadata vpm
                    = Metadata.getMetadata().queryValueProperty(fxomInstance, propertyName);
            final Object description = vpm.getValueObject(fxomInstance); // unresolved value
            final PrefixedValue pv = new PrefixedValue(description.toString());
            return pv.isResourceKey();
        }
        return false;
    }

    public boolean isFreeChildPositioning() {
        boolean result = false;
        if (fxomObject instanceof FXOMInstance) {
            final FXOMInstance fxomInstance = (FXOMInstance) fxomObject;
            final Class<?> componentClass = fxomInstance.getDeclaredClass();
            result = componentClass == AnchorPane.class
                    || componentClass == Group.class
                    || componentClass == Pane.class;
        }
        return result;
    }

    public boolean isAcceptingAccessory(Accessory accessory) {
        final PropertyName propertyName = getPropertyNameForAccessory(accessory);
        final Class<?> valueClass = getClassForAccessory(accessory);

        final Object sceneGraphObject = fxomObject.getSceneGraphObject();

        boolean accepting = DefaultAccessories.isAcceptingAccessory(accessory, sceneGraphObject);
        if (! accepting) {
            return false;
        }
        // TODO
//            case EXTERNAL:
//                for (ExternalDesignHierarchyMaskProvider provider : providers) {
//                    if (! provider.isAcceptingAccessory(sceneGraphObject, accessory)) {
//                        return false;
//                    }
//                }
//                break;
        return isAcceptingProperty(propertyName, valueClass);
    }

    /**
     * Returns true if this mask accepts the specified fxomObject as accessory.
     *
     * @param accessory
     * @param fxomObject
     * @return
     */
    public boolean isAcceptingAccessory(final Accessory accessory, final FXOMObject fxomObject) {
        final Object sceneGraphObject;
        if (fxomObject instanceof FXOMIntrinsic) {
            sceneGraphObject = ((FXOMIntrinsic) fxomObject).getSourceSceneGraphObject();
        } else {
            sceneGraphObject = fxomObject.getSceneGraphObject();
        }
        final Class<?> accessoryClass = getClassForAccessory(accessory);
        return isAcceptingAccessory(accessory)
                && accessoryClass.isInstance(sceneGraphObject);
    }

    private Class<?> getClassForAccessory(Accessory accessory) {
//            case EXTERNAL: // TODO. For now assume only Node class
//                for (ExternalDesignHierarchyMaskProvider provider : providers) {
                // this won't work if more than one provider with different classes...
//                    result = provider.getClassForAccessory(accessory);
//                    break;
//                }
        Class<?> clazz = accessory.getClazz();
        if (clazz == null) {
            throw new IllegalStateException("Unexpected accessory " + accessory);
        }
        return clazz;
    }

    public FXOMObject getAccessory(Accessory accessory) {
        assert isAcceptingAccessory(accessory);
        assert fxomObject instanceof FXOMInstance;

        final FXOMInstance fxomInstance = (FXOMInstance) fxomObject;
        final PropertyName propertyName = getPropertyNameForAccessory(accessory);
        final FXOMProperty fxomProperty = fxomInstance.getProperties().get(propertyName);
        final FXOMObject result;

        if (fxomProperty instanceof FXOMPropertyC) {
            final FXOMPropertyC fxomPropertyC = (FXOMPropertyC) fxomProperty;
            assert fxomPropertyC.getValues().size() >= 1 : "accessory=" + accessory;
            result = fxomPropertyC.getValues().get(0);
        } else {
            result = null;
        }

        return result;
    }

    public boolean isAcceptingSubComponent() {
        final PropertyName propertyName = getSubComponentPropertyName();
        return propertyName != null;
    }

    /**
     * Returns true if this mask accepts the specified sub component.
     *
     * @param obj
     * @return
     */
    public boolean isAcceptingSubComponent(FXOMObject obj) {
        final boolean result;

        assert obj != null;

        final PropertyName propertyName = getSubComponentPropertyName();
        if (propertyName == null) {
            result = false;
        } else {
            queryPropertyMetadata();
            final ComponentPropertyMetadata subComponentMetadata
                    = propertyMetadataMap.get(propertyName);
            assert subComponentMetadata != null;
            final Class<?> subComponentClass
                    = subComponentMetadata.getClassMetadata().getKlass();
            final Object sceneGraphObject;
            if (obj instanceof FXOMIntrinsic) {
                sceneGraphObject = ((FXOMIntrinsic) obj).getSourceSceneGraphObject();
            } else {
                sceneGraphObject = obj.getSceneGraphObject();
            }
            result = subComponentClass.isInstance(sceneGraphObject);
        }

        return result;
    }

    /**
     * Returns true if this mask accepts the specified sub components.
     *
     * @param fxomObjects
     * @return
     */
    public boolean isAcceptingSubComponent(final Collection<FXOMObject> fxomObjects) {
        final PropertyName propertyName = getSubComponentPropertyName();
        if (propertyName != null) {
            queryPropertyMetadata();
            final ComponentPropertyMetadata subComponentMetadata
                    = propertyMetadataMap.get(propertyName);
            assert subComponentMetadata != null;
            final Class<?> subComponentClass
                    = subComponentMetadata.getClassMetadata().getKlass();
            for (FXOMObject obj : fxomObjects) {
                final Object sceneGraphObject;
                if (obj instanceof FXOMIntrinsic) {
                    final FXOMIntrinsic intrinsicObj = (FXOMIntrinsic) obj;
                    sceneGraphObject = intrinsicObj.getSourceSceneGraphObject();
                } else {
                    sceneGraphObject = obj.getSceneGraphObject();
                }
                if (!subComponentClass.isInstance(sceneGraphObject)) {
                    return false;
                }
            }
            return true;
        }
        return false;
    }

    public PropertyName getSubComponentPropertyName() {
        final Object sceneGraphObject = fxomObject.getSceneGraphObject();
        final PropertyName result;

        if (fxomObject instanceof FXOMCollection) {
            result = null;
        } else if (sceneGraphObject == null) {
            // An unresolved has no subcomponent
            result = null;
        } else {
            final Class<?> componentClass = sceneGraphObject.getClass();
            final ComponentClassMetadata componentClassMedadata
                    = Metadata.getMetadata().queryComponentMetadata(componentClass);
            assert componentClassMedadata != null;
            result = componentClassMedadata.getSubComponentProperty();
        }

        return result;
    }

    public int getSubComponentCount() {
        final PropertyName name = getSubComponentPropertyName();
        return (name == null) ? 0 : getSubComponents().size();
    }

    public FXOMObject getSubComponentAtIndex(int i) {
        assert 0 <= i;
        assert i < getSubComponentCount();
        assert getSubComponentPropertyName() != null;

        return getSubComponents().get(i);
    }
    
    public List<FXOMObject> getSubComponents() {

        assert getSubComponentPropertyName() != null;
        assert fxomObject instanceof FXOMInstance;

        final PropertyName subComponentPropertyName = getSubComponentPropertyName();
        final FXOMInstance fxomInstance = (FXOMInstance) fxomObject;
        final FXOMProperty fxomProperty
                = fxomInstance.getProperties().get(subComponentPropertyName);

        final List<FXOMObject> result;
        if (fxomProperty instanceof FXOMPropertyC) {
            result = ((FXOMPropertyC) fxomProperty).getValues();
        } else {
            result = Collections.emptyList();
        }

        return result;
    }

    public PropertyName getPropertyNameForDescription() {
        final Object sceneGraphObject = fxomObject.getSceneGraphObject();
        if (sceneGraphObject == null) {
            return null;
        }
        PropertyName propertyName = null;
        if (sceneGraphObject instanceof ComboBox) {
            propertyName = new PropertyName("promptText");
        } else if (sceneGraphObject instanceof Labeled
                || sceneGraphObject instanceof Menu
                || sceneGraphObject instanceof MenuItem
                || sceneGraphObject instanceof Tab
                || sceneGraphObject instanceof TableColumn
                || sceneGraphObject instanceof TextInputControl
                || sceneGraphObject instanceof TitledPane
                || sceneGraphObject instanceof Text
                || sceneGraphObject instanceof Tooltip
                || sceneGraphObject instanceof TreeTableColumn) {
            propertyName = new PropertyName("text");
        } else if (sceneGraphObject instanceof Stage) {
            propertyName = new PropertyName("title");
        }
        return propertyName;
    }

    public PropertyName getPropertyNameForAccessory(Accessory accessory) {
        String propertyName = accessory.getPropertyName();
        if (propertyName == null) {
            throw new IllegalStateException("Unexpected accessory " + accessory);
        }
        //case EXTERNAL: // TODO
        return new PropertyName(propertyName);
    }

    /*
     * Private
     */
    private boolean isAcceptingProperty(PropertyName propertyName, Class<?> valueClass) {
        final ComponentPropertyMetadata cpm;
        final boolean result;

        queryPropertyMetadata();
        cpm = propertyMetadataMap.get(propertyName);
        if (cpm == null) {
            result = false;
        } else {
            result = valueClass.isAssignableFrom(cpm.getClassMetadata().getKlass());
        }

        return result;
    }

    public FXOMPropertyC getAccessoryProperty(Accessory accessory) {

        assert getPropertyNameForAccessory(accessory) != null;
        assert fxomObject instanceof FXOMInstance;

        final PropertyName accessoryPropertyName = getPropertyNameForAccessory(accessory);
        final FXOMInstance fxomInstance = (FXOMInstance) fxomObject;
        final FXOMProperty result
                = fxomInstance.getProperties().get(accessoryPropertyName);

        assert (result == null) || (result instanceof FXOMPropertyC);

        return (FXOMPropertyC) result;
    }

    private void queryPropertyMetadata() {
        if (propertyMetadataMap == null) {
            propertyMetadataMap = new HashMap<>();
            if (fxomObject instanceof FXOMInstance) {
                final FXOMInstance fxomInstance = (FXOMInstance) fxomObject;
                if (fxomInstance.getSceneGraphObject() != null) {
                    final Class<?> componentClass = fxomInstance.getSceneGraphObject().getClass();
                    for (ComponentPropertyMetadata cpm : Metadata.getMetadata().queryComponentProperties(componentClass)) {
                        propertyMetadataMap.put(cpm.getName(), cpm);
                    }
                }
            }
        }

        assert propertyMetadataMap != null;
    }

    /**
     * Returns the number of columns constraints for this GridPane mask.
     *
     * @return the number of columns constraints
     */
    public int getColumnsConstraintsSize() {
        assert fxomObject instanceof FXOMInstance;
        final FXOMInstance fxomInstance = (FXOMInstance) fxomObject;
        assert fxomInstance.getSceneGraphObject() instanceof GridPane;

        final PropertyName propertyName = new PropertyName("columnConstraints"); //NOI18N
        final FXOMProperty fxomProperty = fxomInstance.getProperties().get(propertyName);
        
        final int result;
        if (fxomProperty == null) {
            result = 0;
        } else {
            assert fxomProperty instanceof FXOMPropertyC; // ie cannot be written as an XML attribute
            final FXOMPropertyC fxomPropertyC = (FXOMPropertyC) fxomProperty;
            result = fxomPropertyC.getValues().size();
        }
        
        return result;
    }

    /**
     * Returns the number of rows constraints for this GridPane mask.
     *
     * @return the number of rows constraints
     */
    public int getRowsConstraintsSize() {
        assert fxomObject instanceof FXOMInstance;
        final FXOMInstance fxomInstance = (FXOMInstance) fxomObject;
        assert fxomInstance.getSceneGraphObject() instanceof GridPane;

        final PropertyName propertyName = new PropertyName("rowConstraints"); //NOI18N
        final FXOMProperty fxomProperty = fxomInstance.getProperties().get(propertyName);
        
        final int result;
        if (fxomProperty == null) {
            result = 0;
        } else {
            assert fxomProperty instanceof FXOMPropertyC; // ie cannot be written as an XML attribute
            final FXOMPropertyC fxomPropertyC = (FXOMPropertyC) fxomProperty;
            result = fxomPropertyC.getValues().size();
        }
        
        return result;
    }

    /**
     * Returns the number of columns for this GridPane mask.
     * The number of columns for a GridPane is the max of :
     * - the number of column constraints
     * - the max column index defined in this GridPane children + 1
     *
     * @return the number of columns
     */
    public int getColumnsSize() {
        final Object sceneGraphObject;
        // For FXOMIntrinsic, we use the source sceneGraphObject
        if (fxomObject instanceof FXOMIntrinsic) {
            sceneGraphObject = ((FXOMIntrinsic) fxomObject).getSourceSceneGraphObject();
        } else {
            sceneGraphObject = fxomObject.getSceneGraphObject();
        }
        assert sceneGraphObject instanceof GridPane;
        return Deprecation.getGridPaneColumnCount((GridPane) sceneGraphObject);
    }

    /**
     * Returns the number of rows for this GridPane mask.
     * The number of rows for a GridPane is the max of :
     * - the number of row constraints
     * - the max row index defined in this GridPane children + 1
     *
     * @return the number of rows
     */
    public int getRowsSize() {
        final Object sceneGraphObject;
        // For FXOMIntrinsic, we use the source sceneGraphObject
        if (fxomObject instanceof FXOMIntrinsic) {
            sceneGraphObject = ((FXOMIntrinsic) fxomObject).getSourceSceneGraphObject();
        } else {
            sceneGraphObject = fxomObject.getSceneGraphObject();
        }
        assert sceneGraphObject instanceof GridPane;
        return Deprecation.getGridPaneRowCount((GridPane) sceneGraphObject);
    }
    
    public List<FXOMObject> getColumnContentAtIndex(int index) {
        assert 0 <= index;
        assert fxomObject instanceof FXOMInstance;
        final FXOMInstance fxomInstance = (FXOMInstance) fxomObject;
        assert fxomInstance.getSceneGraphObject() instanceof GridPane;

        final List<FXOMObject> result = new ArrayList<>();
        for (int i = 0, count = getSubComponentCount(); i < count; i++) {
            final FXOMObject childObject = getSubComponentAtIndex(i);
            final DesignHierarchyMask childMask = new DesignHierarchyMask(childObject);
            if (childMask.getColumnIndex() == index) {
                result.add(childObject);
            }
        }
        return result;
    }

    public List<FXOMObject> getRowContentAtIndex(int index) {
        assert 0 <= index;
        assert fxomObject instanceof FXOMInstance;
        final FXOMInstance fxomInstance = (FXOMInstance) fxomObject;
        assert fxomInstance.getSceneGraphObject() instanceof GridPane;

        final List<FXOMObject> result = new ArrayList<>();
        for (int i = 0, count = getSubComponentCount(); i < count; i++) {
            final FXOMObject childObject = getSubComponentAtIndex(i);
            final DesignHierarchyMask childMask = new DesignHierarchyMask(childObject);
            if (childMask.getRowIndex() == index) {
                result.add(childObject);
            }
        }
        return result;
    }

    public FXOMObject getColumnConstraintsAtIndex(int index) {

        assert 0 <= index;
        assert fxomObject instanceof FXOMInstance;
        final FXOMInstance fxomInstance = (FXOMInstance) fxomObject;
        assert fxomInstance.getSceneGraphObject() instanceof GridPane;

        FXOMObject result = null;

        // Retrieve the constraints property
        final PropertyName propertyName = new PropertyName("columnConstraints"); //NOI18N
        final FXOMProperty constraintsProperty
                = fxomInstance.getProperties().get(propertyName);

        if (constraintsProperty != null) {
            assert constraintsProperty instanceof FXOMPropertyC;
            final List<FXOMObject> constraintsValues
                    = ((FXOMPropertyC) constraintsProperty).getValues();
            if (index < constraintsValues.size()) {
                result = constraintsValues.get(index);
            }
        }

        return result;
    }

    public FXOMObject getRowConstraintsAtIndex(int index) {

        assert 0 <= index;
        assert fxomObject instanceof FXOMInstance;
        final FXOMInstance fxomInstance = (FXOMInstance) fxomObject;
        assert fxomInstance.getSceneGraphObject() instanceof GridPane;

        FXOMObject result = null;

        // Retrieve the constraints property
        final PropertyName propertyName = new PropertyName("rowConstraints"); //NOI18N
        final FXOMProperty constraintsProperty
                = fxomInstance.getProperties().get(propertyName);

        if (constraintsProperty != null) {
            assert constraintsProperty instanceof FXOMPropertyC;
            final List<FXOMObject> constraintsValues
                    = ((FXOMPropertyC) constraintsProperty).getValues();
            if (index < constraintsValues.size()) {
                result = constraintsValues.get(index);
            }
        }

        return result;
    }

    /**
     * Returns the column index for this GridPane child mask.
     *
     * @return the column index
     */
    public int getColumnIndex() {
        int result = 0;
        if (fxomObject instanceof FXOMInstance) {
            assert fxomObject.getSceneGraphObject() != null;
            final FXOMInstance fxomInstance = (FXOMInstance) fxomObject;
            result = getIndexFromGrid(fxomInstance, "columnIndex");
        } else if(fxomObject instanceof FXOMIntrinsic) {
            FXOMIntrinsic fxomIntrinsic = (FXOMIntrinsic) fxomObject;
            FXOMInstance fxomInstance = fxomIntrinsic.createFxomInstanceFromIntrinsic();
            result = getIndexFromGrid(fxomInstance, "columnIndex");
        }
        return result;
    }

    /**
     * Returns the row index for this GridPane child mask.
     *
     * @return the row index
     */
    public int getRowIndex() {
        int result = 0;
        if (fxomObject instanceof FXOMInstance) {
            assert fxomObject.getSceneGraphObject() != null;
            final FXOMInstance fxomInstance = (FXOMInstance) fxomObject;
            result = getIndexFromGrid(fxomInstance, "rowIndex");
        } else if(fxomObject instanceof FXOMIntrinsic) {
            FXOMIntrinsic fxomIntrinsic = (FXOMIntrinsic) fxomObject;
            FXOMInstance fxomInstance = fxomIntrinsic.createFxomInstanceFromIntrinsic();
            result = getIndexFromGrid(fxomInstance, "rowIndex");
        }
        return result;
    }

    private int getIndexFromGrid(final FXOMInstance fxomInstance, final String columnOrRow) {
        int result;
        final FXOMObject parentFxomObject = fxomInstance.getParentObject();
        assert parentFxomObject.getSceneGraphObject() instanceof GridPane;

        final PropertyName propertyName
                = new PropertyName(columnOrRow, GridPane.class); //NOI18N
        final ValuePropertyMetadata vpm
                = Metadata.getMetadata().queryValueProperty(fxomInstance, propertyName);
        final Object value = vpm.getValueObject(fxomInstance);
        // TODO : when DTL-5920 will be fixed, the null check will become unecessary
        if (value == null) {
            result = 0;
        } else {
            assert value instanceof Integer;
            result = ((Integer) value);
        }
        return result;
    }


    // Should be in a shared Utils class ?
    public static boolean containsLineFeed(String str) {
        // LF (\n) is used for files generated on UNIX
        // CR+LF (\r\n) is used for files generated on WINDOWS
        // So in both cases, a file containing multi lines will contain LF
        if (str == null) {
            return false;
        }
        return str.contains("\n"); //NOI18N
    }
    
    /**
     * 
     * @return true if the mask deserves a resizing while used as top element of
     * the layout.
     */
    public boolean needResizeWhenTopElement() {
        Object sceneGraphObject = fxomObject.getSceneGraphObject();
        return (this.isAcceptingSubComponent()
                || this.isAcceptingAccessory(DefaultAccessories.byName("CONTENT"))
                || this.isAcceptingAccessory(DefaultAccessories.byName("ROOT"))
                || this.isAcceptingAccessory(DefaultAccessories.byName("SCENE"))
                || this.isAcceptingAccessory(DefaultAccessories.byName("CENTER"))
                || this.isAcceptingAccessory(DefaultAccessories.byName("TOP"))
                || this.isAcceptingAccessory(DefaultAccessories.byName("RIGHT"))
                || this.isAcceptingAccessory(DefaultAccessories.byName("BOTTOM"))
                || this.isAcceptingAccessory(DefaultAccessories.byName("LEFT")))
                && ! (sceneGraphObject instanceof MenuButton
                        || sceneGraphObject instanceof MenuBar
                        || sceneGraphObject instanceof ToolBar
                        || isExternalResizable(sceneGraphObject));
    }

    public Map<Accessory, FXOMObject> checkExternalAccesories() {
        Map<Accessory, FXOMObject> map = new HashMap();
//        // Gluon ExpansionPanel
//        for (Accessory accessory: new Accessory[]{
//                Accessory.EXPANDED_CONTENT,
//                Accessory.COLLAPSED_CONTENT
//        }) {
//            if (isAcceptingAccessory(accessory)) {
//                final FXOMObject value = getAccessory(accessory);
//                map.put(accessory, value);
////                treeItem.getChildren().add(makeTreeItemExpansionPanel(mask, value, accessory));
//            }
//        }
//
//        // Gluon ExpandedPanel
//        if (mask.isAcceptingAccessory(Accessory.EX_CONTENT)) {
//            final FXOMObject value = getAccessory(Accessory.EX_CONTENT);
//            map.put(Accessory.EX_CONTENT, value);
////            treeItem.getChildren().add(makeTreeItemExpandedPanel(mask, value));
//        }
        return map;
    }

    private boolean isExternalResizable(Object object) {
        for (ExternalDesignHierarchyMaskProvider provider : providers) {
            for (Class<?> item : provider.getResizableItems()) {
                if (item.isInstance(object)) {
                    return true;
                }
            }
        }
        return false;
    }

    private Collection<ExternalDesignHierarchyMaskProvider> getExternalDesignHierarchyMaskProviders() {
        ServiceLoader<ExternalDesignHierarchyMaskProvider> loader = ServiceLoader.load(ExternalDesignHierarchyMaskProvider.class);
        Collection<ExternalDesignHierarchyMaskProvider> providers = new ArrayList<>();
        loader.iterator().forEachRemaining(provider -> {
            provider.setDesignHierarchyMask(this);
            providers.add(provider);
        });
        return providers;
    }
}
