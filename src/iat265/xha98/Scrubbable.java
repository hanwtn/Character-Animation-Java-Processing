    
package iat265.xha98;

import java.util.Iterator;

public interface Scrubbable {
    String getName();

    void setName(String parameter);

    String[] getProperties();

    void setParameter(String parameter, float value);

    float getParameter(String parameter);

    void draw();

    Scrubbable pick(int x, int y);

    Iterator<Scrubbable> createIterator();
}
