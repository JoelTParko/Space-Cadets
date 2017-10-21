import java.util.*;


public class PositionableIterator implements Iterator<String>{

    private List<String> list;
    private Iterator<String> iterator;

    public PositionableIterator(List<String> list){
        this.list = list;
        this.iterator = list.iterator();
    }
    public void moveTo(int index){
        this.iterator = list.listIterator(index);
    }

    @Override
    public boolean hasNext() {
        return iterator.hasNext();
    }

    @Override
    public String next() {
        return iterator.next();
    }
}
