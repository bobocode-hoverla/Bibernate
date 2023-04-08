package org.hoverla.bibernate.collection;

import java.util.*;
import java.util.function.Supplier;

public class LazyList<T> implements List<T> {
    private List<T> delagateList;
    Supplier<List<?>> supplierDelegate;

    public LazyList(Supplier<List<?>> supplierDelegate) {
        this.supplierDelegate = supplierDelegate;
    }

    public List<T> getProxyList() {
        if(delagateList == null) {
            delagateList = (List<T>) supplierDelegate.get();
        }
        return delagateList;
    }

    @Override
    public int size() {
        return getProxyList().size();
    }

    @Override
    public boolean isEmpty() {
        return getProxyList().isEmpty();
    }

    @Override
    public boolean contains(Object o) {
        return getProxyList().contains(o);
    }

    @Override
    public Iterator<T> iterator() {
        return getProxyList().iterator();
    }

    @Override
    public Object[] toArray() {
        return getProxyList().toArray();
    }

    @Override
    public <T1> T1[] toArray(T1[] a) {
        return getProxyList().toArray(a);
    }

    @Override
    public boolean add(T t) {
        return getProxyList().add(t);
    }

    @Override
    public boolean remove(Object o) {
        return getProxyList().remove(o);
    }

    @Override
    public boolean containsAll(Collection<?> c) {
        return getProxyList().containsAll(c);
    }

    @Override
    public boolean addAll(Collection<? extends T> c) {
        return getProxyList().addAll(c);
    }

    @Override
    public boolean addAll(int index, Collection<? extends T> c) {
        return getProxyList().addAll(index, c);
    }

    @Override
    public boolean removeAll(Collection<?> c) {
        return getProxyList().removeAll(c);
    }

    @Override
    public boolean retainAll(Collection<?> c) {
        return getProxyList().retainAll(c);
    }

    @Override
    public void clear() {
        getProxyList().clear();
    }

    @Override
    public T get(int index) {
        return getProxyList().get(index);
    }

    @Override
    public T set(int index, T element) {
        return getProxyList().set(index, element);
    }

    @Override
    public void add(int index, T element) {
        getProxyList().add(index, element);
    }

    @Override
    public T remove(int index) {
        return getProxyList().remove(index);
    }

    @Override
    public int indexOf(Object o) {
        return getProxyList().indexOf(o);
    }

    @Override
    public int lastIndexOf(Object o) {
        return getProxyList().lastIndexOf(o);
    }

    @Override
    public ListIterator<T> listIterator() {
        return getProxyList().listIterator();
    }

    @Override
    public ListIterator<T> listIterator(int index) {
        return getProxyList().listIterator(index);
    }

    @Override
    public List<T> subList(int fromIndex, int toIndex) {
        return getProxyList().subList(fromIndex, toIndex);
    }
}