package net.BlazeLoader.api.reflect;

import java.lang.reflect.Field;

/**
 * Class to bind a Field to a containing Object.
 */
public class FieldInstance {
    protected Field boundField;
    protected Object boundObject;

    /**
     * Creates a new FieldInstance.
     *
     * @param field  The field to bind to.  Can not be null.
     * @param object The object containing the bound field.  Can be null for static fields.
     */
    public FieldInstance(Field field, Object object) {
        if (field == null) {
            throw new IllegalArgumentException("The bound field cannot be null!");
        }
        this.boundField = field;
        this.boundObject = object;
    }

    /**
     * Gets the bound Field.
     *
     * @return Return the bound Field.
     */
    public Field getField() {
        return boundField;
    }

    /**
     * Gets the bound Object.
     *
     * @return Return the bound Object.
     */
    public Object getObject() {
        return boundObject;
    }
}
