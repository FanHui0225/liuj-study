package com.stereo.study.rpc.utils;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReferenceArray;

/**
 * 原子引用列表
 * 
 * @param <T>
 */
public final class FreeList<T> {
	private final AtomicReferenceArray<T> _freeStack;
	private final AtomicInteger _top = new AtomicInteger();

	public FreeList(int size) {
		_freeStack = new AtomicReferenceArray(size);
	}

	public T allocate() {
		int top = _top.get();

		if (top > 0 && _top.compareAndSet(top, top - 1))
			return _freeStack.getAndSet(top - 1, null);
		else
			return null;
	}

	public boolean free(T obj) {
		int top = _top.get();

		if (top < _freeStack.length()) {
			boolean isFree = _freeStack.compareAndSet(top, null, obj);

			_top.compareAndSet(top, top + 1);

			return isFree;
		} else
			return false;
	}

	public boolean allowFree(T obj) {
		return _top.get() < _freeStack.length();
	}

	public void freeCareful(T obj) {
		if (checkDuplicate(obj))
			throw new IllegalStateException("tried to free object twice: "
					+ obj);

		free(obj);
	}

	public boolean checkDuplicate(T obj) {
		int top = _top.get();

		for (int i = top - 1; i >= 0; i--) {
			if (_freeStack.get(i) == obj)
				return true;
		}

		return false;
	}
}