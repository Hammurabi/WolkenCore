package org.wolkenproject.utils;

import org.wolkenproject.serialization.SerializableI;

import java.util.*;

public class PriorityHashQueue<T extends SerializableI & Comparable<T>> implements Queue<T> {
    private Map<byte[], Entry<T>>   entryMap;
    private Queue<Entry<T>>         queue;

    public PriorityHashQueue() {
        this(new DefaultComparator<>());
    }

    public PriorityHashQueue(Comparator<Entry<T>> comparator) {
        queue       = new PriorityQueue<Entry<T>>(comparator);
        entryMap    = new HashMap<>();
    }

    @Override
    public int size() {
        return queue.size();
    }

    @Override
    public boolean isEmpty() {
        return queue.isEmpty();
    }

    @Override
    public boolean contains(Object o) {
        return entryMap.containsKey(((Hashable) o).getHash160());
    }

    @Override
    public Iterator<T> iterator() {
        return null;
    }

    @Override
    public Object[] toArray() {
        return new Object[0];
    }

    @Override
    public <T1> T1[] toArray(T1[] t1s) {
        return null;
    }

    @Override
    public boolean add(T t) {
        return false;
    }

    @Override
    public boolean remove(Object o) {
        return false;
    }

    @Override
    public boolean containsAll(Collection<?> collection) {
        return false;
    }

    @Override
    public boolean addAll(Collection<? extends T> collection) {
        return false;
    }

    @Override
    public boolean removeAll(Collection<?> collection) {
        return false;
    }

    @Override
    public boolean retainAll(Collection<?> collection) {
        return false;
    }

    @Override
    public void clear() {

    }

    @Override
    public boolean offer(T t) {
        return false;
    }

    @Override
    public T remove() {
        return null;
    }

    @Override
    public T poll() {
        Entry<T> entry = queue.poll();
        entryMap.remove(entry.hash);

        return ;
    }

    @Override
    public T element() {
        return null;
    }

    @Override
    public T peek() {
        return queue.peek().element;
    }

    private static class DefaultComparator<T extends SerializableI & Comparable<T>> implements Comparator<Entry<T>> {
        @Override
        public int compare(Entry<T> a, Entry<T> b) {
            return a.compareTo(b);
        }
    }

    private static class Entry<T extends SerializableI & Comparable<T>> implements Comparable<Entry<T>> {
        private T       element;
        private byte[]  hash;

        @Override
        public int compareTo(Entry<T> b) {
            return element.compareTo(b.element);
        }
    }
}
