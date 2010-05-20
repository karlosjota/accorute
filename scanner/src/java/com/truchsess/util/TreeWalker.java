package com.truchsess.util;

public interface TreeWalker<E> {

    public void doWalk(Tree<E> tree, Walk<E> callback) throws AbortProcessingException;

    public interface Walk<F> {
        public void doDown(TreeCursor<F> cursor) throws AbortProcessingException;

        public void doUp(TreeCursor<F> cursor) throws AbortProcessingException;

        public void doNext(TreeCursor<F> cursor) throws AbortProcessingException;
    }

    public static class AbortProcessingException extends Exception {
        private static final long serialVersionUID = 2835928358243526435L;

        public AbortProcessingException() {
            super();
        }

        public AbortProcessingException(Exception e) {
            super(e);
        }
    }
}

