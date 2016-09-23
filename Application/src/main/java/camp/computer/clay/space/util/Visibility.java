package camp.computer.clay.space.util;

public class Visibility {

    private Visibility reference = null;

    private Value value = Value.VISIBLE;

    public enum Value {

        INVISIBLE,
        VISIBLE;

        Value()
        {
        }
    }

    public Visibility()
    {
    }

    public Visibility(Visibility.Value value)
    {
        this.value = value;
    }

    public void setValue(Visibility.Value value)
    {
        this.value = value;
    }

    public Visibility.Value getValue()
    {
        if (reference != null) {
            return reference.getValue();
        } else {
            return this.value;
        }
    }

    public void setReference(Visibility visibility)
    {
        this.reference = visibility;
    }

    public Visibility getReference()
    {
        return this.reference;
    }

    public boolean isVisible()
    {
        return this.value == Visibility.Value.VISIBLE;
    }
}
