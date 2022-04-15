package tools;

import java.io.Serializable;

public class Quadruple<E, F, G, H> implements Serializable
{
    private static final long serialVersionUID = 9179541993413749999L;
    public final E one;
    public final F two;
    public final G three;
    public final H four;
    
    public Quadruple(final E one, final F two, final G three, final H four) {
        this.one = one;
        this.two = two;
        this.three = three;
        this.four = four;
    }
    
    public E getOne() {
        return this.one;
    }
    
    public F getTwo() {
        return this.two;
    }
    
    public G getThree() {
        return this.three;
    }
    
    public H getFour() {
        return this.four;
    }
    
    @Override
    public String toString() {
        return this.one.toString() + ":" + this.two.toString() + ":" + this.three.toString() + ":" + this.four.toString();
    }
    
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = 31 * result + ((this.one == null) ? 0 : this.one.hashCode());
        result = 31 * result + ((this.two == null) ? 0 : this.two.hashCode());
        result = 31 * result + ((this.three == null) ? 0 : this.three.hashCode());
        result = 31 * result + ((this.four == null) ? 0 : this.four.hashCode());
        return result;
    }
    
    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (this.getClass() != obj.getClass()) {
            return false;
        }
        final Quadruple other = (Quadruple)obj;
        if (this.one == null) {
            if (other.one != null) {
                return false;
            }
        }
        else if (!this.one.equals(other.one)) {
            return false;
        }
        if (this.two == null) {
            if (other.two != null) {
                return false;
            }
        }
        else if (!this.two.equals(other.two)) {
            return false;
        }
        if (this.three == null) {
            if (other.three != null) {
                return false;
            }
        }
        else if (!this.three.equals(other.three)) {
            return false;
        }
        if (this.four == null) {
            if (other.four != null) {
                return false;
            }
        }
        else if (!this.four.equals(other.four)) {
            return false;
        }
        return true;
    }
}
